package at.ac.tuwien.infosys.reasoner.impl;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.database.impl.DatabaseService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.propagator.IPropagatorService;
import at.ac.tuwien.infosys.reasoner.IReasonerService;
import at.ac.tuwien.infosys.reasoner.events.IDeviceAccedence;
import at.ac.tuwien.infosys.reasoner.events.IDeviceFailure;
import at.ac.tuwien.infosys.reasoner.events.IDeviceOverload;
import at.ac.tuwien.infosys.reasoner.resourceProvisioning.IResourceProvisioning;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.util.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;

/**
 * Created by Kevin Bachmann on 13/12/2016.
 */
@Service
@Slf4j
public class ReasonerService implements IReasonerService {

    @Autowired
    private DatabaseService dbService;

    @Autowired
    private IPropagatorService propagatorService;

    @Autowired
    private ICommunicationService commService;


    /**
     * REPLACEABLE POLICIES
     */
    @Autowired
    private IResourceProvisioning resourceProvisioning;

    @Autowired
    private IDeviceAccedence deviceAccedence;

    @Autowired
    private IDeviceFailure deviceFailure;

    @Autowired
    private IDeviceOverload deviceOverload;


    /**
     * Application assignments to keep track of the running applications and their services
     */
    private List<ApplicationAssignment> applicationAssignments = new ArrayList<ApplicationAssignment>();
    public List<ApplicationAssignment> getApplicationAssignments() {
        return applicationAssignments;
    }

    /**
     * Old children to realize a change in the topology and raise events accordingly
     */
    private Set<Fogdevice> oldChildren = new HashSet<Fogdevice>();

    /**
     * Flag indicating if the device is currently replanning
     */
    private boolean isReplanning = false;

    // EVALUATION METRICS
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    private int totalRequestCount = 0;
    private int cloudRequestCount = 0;
    private HashMap<Integer, Double> deploymentTimes = new HashMap<Integer, Double>();
    private HashMap<Integer, Double> fogdeploymentTimes = new HashMap<Integer, Double>();
    private HashMap<Integer, Double> clouddeploymentTimes = new HashMap<Integer, Double>();
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // EVALUATION METRICS



    @Override
    public Message handleTaskRequests(Application application) {
        Set<TaskRequest> requests = new HashSet<TaskRequest>(application.getRequests());

        // check if root. otherwise send it to the parent
        if(isRootFCN()){
            // this fog control node is the root node and therefore needs to handle the task scheduling
            try {
                // get children of the whole fog colony (recursively)
                Set<Fogdevice> children = getTopologyChildren();

                // START TIME TRACKING
                // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                long startTime = System.nanoTime();
                int requestCount = requests.size();
                this.totalRequestCount += requestCount;


                final ApplicationAssignment assignment = deployServices(requests, children);
                if(assignment == null){
                    return new Message(Constants.URL_TASK_REQUESTS, false);
                }

                // save the application assignment
                applicationAssignments.add(assignment);

                // END TIME TRACKING
                // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
                long stopTime = System.nanoTime();
                long duration = stopTime - startTime;
                double seconds = ((double)duration / 1000000000);
                deploymentTimes.put(requestCount, seconds);
                String timeStr = new DecimalFormat("#.##########").format(seconds);
                log.info("total service deployment time : " + timeStr + " Seconds for " + requestCount+ " services");


                int applicationDuration = application.getDuration()*1000;
                if(applicationDuration > 0) {
                    final String assignmentId = assignment.getId();
                    // add timertask to the map in order to be able to stop it after the execution
                    TimerTask t = new TimerTask() {
                        @Override
                        public void run() {
                            log.info("--------- STOPPING APPLICATION ---------");
//                            stopFailedApplicationServices(assignment);
                            stopApplicationById(assignmentId);
                            log.info("--------- SUCCESSFULLY STOPPED ---------");
                        }
                    };
                    // start the timertask
                    new Timer().schedule(t, applicationDuration);
                }

                return new Message(Constants.URL_TASK_REQUESTS, timeStr, true);
            } catch (Exception e) {
                log.error("ERROR while reasoning: "+e.getMessage());
                return new Message(Constants.URL_TASK_REQUESTS, false);
            }
        } else {
            Fogdevice parent = dbService.getParent();
            if(parent != null)
                log.info("-- Propagating task requests to parent "+parent.getIp()+":"+parent.getPort());
            return propagateTaskRequestsToParent(application);
        }
    }

    /**
     * Method to deploy a service with the implemented resource provisioning approach. In case the services cant be
     * deployed in the fog the rest of the requests is deployed in the cloud (if the services can be deployed there). If
     * at least one request of an application fails, the whole application is stopped.
     * @param requests task requests to deploy
     * @param children children to deploy the services to
     * @return an application assignment of the passed requests, children, deployed containers and open requestss
     */
    private ApplicationAssignment deployServices(Set<TaskRequest> requests, Set<Fogdevice> children){
        // split in fog and cloud tasks (due to different processor architecture (ARM, Intel))
        Set<TaskRequest> fogRequests = new HashSet<TaskRequest>();
        Set<TaskRequest> cloudRequests = new HashSet<TaskRequest>();
        for(TaskRequest request: requests){
            if(request.isCloudTask()) {
                cloudRequests.add(request);
            } else {
                fogRequests.add(request);
            }
        }

        // resource provisioning in the fog
        // ---------------------------------------------------------------------------------------------------

        // START FOG TIME TRACKING
        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        long startFogTime = System.nanoTime();
        int fogrequestCount = requests.size();

        ApplicationAssignment assignment = new ApplicationAssignment();
        try {
            assignment = resourceProvisioning.handleTaskRequests(children, fogRequests);
        } catch(Exception e) {
            log.error("ERROR: A resource provisioning error occurred. ERROR: " + e.getMessage());
            // TODO: stop the services that are already deployed
            return null;
        }

        // check if all dedicated fog services could be deployed
        if(!checkFogDeployment(assignment)){
            stopFailedApplicationServices(assignment);
            log.error("ERROR: One or more fog bound services couldnt be deployed");
            return null;
        }
        fogrequestCount -= (assignment.getOpenRequests().size()+cloudRequests.size());

        // END FOG TIME TRACKING
        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        long stopFogTime = System.nanoTime();
        long fogduration = stopFogTime - startFogTime;
        double seconds = ((double)fogduration / 1000000000);
        fogdeploymentTimes.put(fogrequestCount, seconds);
        String timeStr = new DecimalFormat("#.##########").format(seconds);
        log.info("fog service deployment time : " + timeStr + " Seconds for "+fogrequestCount+ " services");



        // resource provisioning in the cloud
        // ---------------------------------------------------------------------------------------------------
        // if some requests couldnt be deployed in the fog the rest of the services are deployed in the cloud

        // START CLOUD TIME TRACKING
        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        long startCloudTime = System.nanoTime();

        cloudRequests.addAll(assignment.getOpenRequests());
        int cloudrequestCount = cloudRequests.size();
        if(cloudRequests.size() > 0){
            log.info("------- Sending task requests to the cloud -------");
            cloudRequestCount += cloudRequests.size();
            List<TaskAssignment> cloudAssignments = sendTaskRequestToCloud(cloudRequests);
            if(cloudAssignments.size() == 0){
                stopFailedApplicationServices(assignment);
                log.error("ERROR: Failed deploying cloud tasks");
                return  null;
            }
            List<TaskAssignment> l = assignment.getAssignedTasks();
            l.addAll(cloudAssignments);
            assignment.setAssignedTasks(l);
            log.info("------- Finished cloud service deployment -------");
        }

        // END CLOUD TIME TRACKING
        // ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        long stopCloudTime = System.nanoTime();
        long cloudduration = stopCloudTime - startCloudTime;
        seconds = ((double)cloudduration / 1000000000);
        clouddeploymentTimes.put(cloudrequestCount, seconds);
        timeStr = new DecimalFormat("#.##########").format(seconds);
        log.info("cloud service deployment time : " + timeStr + " Seconds for "+cloudrequestCount+ " services");

        return assignment;
    }


    public Set<Fogdevice> getTopologyChildren(){
        Set<Fogdevice> directChildren = dbService.getChildren();
        Set<Fogdevice> children = new HashSet<Fogdevice>();
        for(Fogdevice child : directChildren){
            children.add(child); // HINT: only if fog control nodes should deploy stuff as well
            if(child.getType().equals(DeviceType.FOG_CONTROL_NODE)){
                // send get children request
                Set<Fogdevice> childChildren = commService.getChildrenOfChild(child);
                children.addAll(childChildren);
            } else {
                children.add(child);
            }
        }
        return children;
    }

    /**
     * Propagate the requests of the passed application to the parent.
     * @param application application to propagate
     * @return message indicating whether it worked
     */
    private Message propagateTaskRequestsToParent(Application application){
        List<ServiceData> data = new ArrayList<ServiceData>();
        ServiceData sd = new ServiceData();
        sd.setReasoningPurpose(true);
        sd.setKey("taskRequests");
        sd.setApplication(application);
//        sd.setRequests(requests);
        data.add(sd);
        propagatorService.propagate(data);
        return new Message(Constants.URL_PROPAGATE, true);
    }

    /**
     * Check whether all fog tasks could be deployed
     * @param assignment assignment to check
     * @return true if all fog tasks are deployed
     */
    private boolean checkFogDeployment(ApplicationAssignment assignment){
        // check if all dedicated fog services could be deployed
        List<TaskRequest> openRequests = assignment.getOpenRequests();
        for(TaskRequest r : openRequests){
            if(r.isFogTask()){
                // if there is a open fog request the whole application failed and needs to be stopped again
                return false;
            }
        }
        return true;
    }

    /**
     * Sends the set of task requests to the cloud for deployment
     * @param cloudRequests requests to deloy in the cloud
     * @return a list with task assignments to add to the application assignment
     */
    private List<TaskAssignment> sendTaskRequestToCloud(Set<TaskRequest> cloudRequests){
        // requests need to be sent to the cloud
        List<TaskAssignment> assignments = propagatorService.propagateTaskRequests(new ArrayList<TaskRequest>(cloudRequests));
        // check if all the requests are now deployed
        if(cloudRequests.size() != assignments.size()){
            // not all requests were assigned/deployed
            log.error("-- Cloud deployment error. Not all requests were assigned successfully");
            return new ArrayList<TaskAssignment>();
        }

        cloudRequestCount += cloudRequests.size();
        return assignments;
    }

    public void stopFailedApplicationServices(ApplicationAssignment assignment){
        log.info("-- Stopping Application with "+assignment.getAssignedTasks().size()+" services");
        // stop all services of the assignment
        for(TaskAssignment ass: assignment.getAssignedTasks()){
            stopService(ass);
        }
        removeApplicationAssignment(assignment);
    }

    /**
     * Stops an application by its unique uuid
     * @param id uuid of the application
     */
    private void stopApplicationById(String id){
        ApplicationAssignment tostop = null;
        for(ApplicationAssignment a : applicationAssignments){
            if(a.getId().equals(id)){
                tostop = a;
                break;
            }
        }
        if(tostop != null)
            stopFailedApplicationServices(tostop);
    }

    /**
     * Stop a specific service of the passed task assignment
     * @param ass assignment to stop
     */
    private void stopService(TaskAssignment ass){
        if(ass.isInCloud()){
            Fogdevice parent = dbService.getParent();
            commService.sendCloudServiceStopRequest(parent, ass.getContainer().getContainerId());
        } else {
            if(ass.getFogdevice() != null && ass.getContainer() != null) {
                commService.sendServiceStopRequest(ass.getFogdevice(), ass.getContainer().getContainerId());
            }
        }
    }

    /**
     * Remove the passed application assignment from the total mapping
     * @param assignment assignment to remove
     */
    private void removeApplicationAssignment(ApplicationAssignment assignment){
        Iterator<ApplicationAssignment> iterator = applicationAssignments.iterator();
        while(iterator.hasNext()){
            ApplicationAssignment ass = iterator.next();
            if(ass.getId().equals(assignment.getId())){
                iterator.remove();
            }
        }
    }

    public boolean isRootFCN(){
        Fogdevice parent = dbService.getParent();
        return parent != null && parent.getPort() == Constants.PORT_CFM;
    }

    /**
     * Compare the passed sets of children
     * @param oldChildren set of children1m
     * @param newChildren set of children2
     * @return true if they are equal, false otherwise
     */
    private boolean compareChildren(Set<Fogdevice> oldChildren, Set<Fogdevice> newChildren){
        // check if the children set is the same
        if(oldChildren.size() != newChildren.size())
            return false;
        return oldChildren.equals(newChildren);
    }

    /**
     * Adapt the old application assignments with the new task assignments, e.g., used after the assignments have
     * changed after migration.
     * @param old old application assignment to be updated by the new task assignments
     * @param newAssignments new task assignments to be used for the update of the old application assignment
     */
    private void adaptApplicationAssignment(ApplicationAssignment old, List<TaskAssignment> newAssignments){
        List<TaskAssignment> oldAssignments = old.getAssignedTasks();

        for(TaskAssignment oldAss: oldAssignments){
            for(TaskAssignment newAss: newAssignments){
                if(oldAss.getTaskRequest().equals(newAss.getTaskRequest())){
                    // just override the old values with the new ones
                    oldAss.setContainer(newAss.getContainer());
                    oldAss.setFogdevice(newAss.getFogdevice());
                    oldAss.setInCloud(newAss.isInCloud());
                }
            }
        }
        // remove the old app assignment from the list
        removeApplicationAssignment(old);

        // create new one and add it to the list
//        ApplicationAssignment newAppAss = new ApplicationAssignment();
        old.setOpenRequests(new ArrayList<>());
        old.setAssignedTasks(oldAssignments);
        applicationAssignments.add(old);
    }

    /**
     * Stops all services and applications where the passed fog device takes place (has a service deployed)
     * @param fd affected fog device
     */
    private void stopDeviceApplications(Fogdevice fd){
        List<ApplicationAssignment> assignmentsToStop = new ArrayList<>();
        for(ApplicationAssignment ass : applicationAssignments){
            for(TaskAssignment tass : ass.getAssignedTasks()){
                Fogdevice temp = tass.getFogdevice();
                if(fd.getIp().equals(temp.getIp()) && fd.getPort()==temp.getPort()){
                    assignmentsToStop.add(ass);
                    continue;
                }
            }
        }
        for(ApplicationAssignment ass : assignmentsToStop){
            stopFailedApplicationServices(ass);
        }
    }

    public void stopAllApplications(){
        log.info("--- MANUAL STOPPING APPLICATIONS ---");
        Iterator<ApplicationAssignment> appIter = applicationAssignments.iterator();
        while(appIter.hasNext()){
//            stopFailedApplicationServices(iterator.next());
            ApplicationAssignment a = appIter.next();
            Iterator<TaskAssignment> taskIter = a.getAssignedTasks().iterator();
            while(taskIter.hasNext()){
                stopService(taskIter.next());
            }
            // remove applicationassignment from application assignment list
            appIter.remove();
        }
        log.info("--- MANUAL STOPPING FINISHED ---");
    }

    /**
     *
     * MONITORING AND REPLANNING
     * ################################################################################################################
     *
     */

    /**
     * Fog landscape monitoring method. This method monitors the devices in the total fog colony and compares them
     * periodically. In case a new device appears or a connected device is lost, the corresponding events/methods are
     * executed. It also works for multiple incoming and leaving devices.
     */
    @Scheduled(fixedDelay = 5*1000, initialDelay = 2*1000)
    private void fogLandscapeMonitoring(){
        // make sure only the root fcn does monitor
        if(!isRootFCN() || isReplanning || applicationAssignments.size() < 1) return;

        // scheduled task that checks the whole fog landscape and reacts on new devices and gone devices
        Set<Fogdevice> newChildren = getTopologyChildren();

        // check the devices
        if(!compareChildren(oldChildren, newChildren) && oldChildren.size() != 0){
            if(oldChildren.size() < newChildren.size()){
                // find the new connected children
                Set<Fogdevice> temp = new HashSet<>(newChildren);
                for(Fogdevice oldFd : oldChildren){
                    Iterator<Fogdevice> iterator = temp.iterator();
                    while(iterator.hasNext()){
                        Fogdevice fd = iterator.next();
                        if(oldFd.getIp().equals(fd.getIp()) && oldFd.getPort()==fd.getPort()){
                            iterator.remove();
                        }
                    }
                }
                if(temp.size() >= 1){
                    for(Fogdevice fd : temp){
                        log.info("new device "+fd.getIp());
                        isReplanning = true;
                        try {
                            newDeviceReplanning(fd);
                        } catch(Exception e){
                            // HINT: not 100 percent correct! possibly more than one device is affected
                            stopDeviceApplications(fd);
                            log.info("ERROR while new device replanning. Exception: "+e.getMessage());
                        }
                        isReplanning = false;
                    }
                }

            } else {
                // one or more children left
                Set<Fogdevice> temp = new HashSet<>(oldChildren);
                for(Fogdevice newFd : newChildren){
                    Iterator<Fogdevice> iterator = temp.iterator();
                    while(iterator.hasNext()){
                        Fogdevice fd = iterator.next();
                        if(newFd.getIp().equals(fd.getIp()) && newFd.getPort()==fd.getPort()){
                            iterator.remove();
                        }
                    }
                }
                if(temp.size() >= 1){
                    for(Fogdevice fd : temp) {
                        log.info("left device " + fd.getIp());
                        isReplanning = true;
                        try {
                            lostDeviceReplanning(fd);
                        } catch (Exception e) {
                            // HINT: not 100 percent correct! possibly more than one device is affected
                            stopDeviceApplications(fd);
                            log.info("ERROR while lost device replanning. Exception: " + e.getMessage());
                        }
                        isReplanning = false;
                    }
                }
            }
        }
        oldChildren = newChildren;
    }

    /**
     * Event in case a new device appears. The basic approach currently implemented is the following. All compatible
     * services running on other services are checked and the containers having the maximum amount of services are taken
     * away services until the new device is filled by 50% of its maximum amount of containers. In case some compatible
     * services are running in the cloud, the cloud services are definitely deployed on the new device to save cloud
     * costs and resources.
     * @param fd newly detected device
     */
    private void newDeviceReplanning(Fogdevice fd) {
        if (applicationAssignments.size() < 1) {
            return;
        }

        // replan all applications
        for(ApplicationAssignment assignment : applicationAssignments){
            EventResult result = deviceAccedence.handleDeviceAccedence(fd, assignment);
            if(result == null) continue;

            List<TaskAssignment> servicesToMigrate = result.getServicesToMigrate();
            Set<TaskRequest> requestsToMigrate = result.getRequestsToMigrate();

            log.info("--- Starting service migration due to new device: " + fd.getIp() + ":" + fd.getPort());
            // deploy all new services and then stop the old ones
            ApplicationAssignment applicationAssignment = deployServices(requestsToMigrate, getTopologyChildren());
            if (applicationAssignment == null) {
                stopFailedApplicationServices(assignment);
                log.error("--- Failed migrating services");
                return;
            }
            // after deploying the new services stop the old ones
            for (TaskAssignment ass : servicesToMigrate) {
                stopService(ass);
            }

            // correct the applicationassignment
            adaptApplicationAssignment(assignment, applicationAssignment.getAssignedTasks());
        }

        log.info("--- Service migration successful");
    }


    /**
     * Lost device event. All the services of the lost device are migrated to other devices or the cloud if possible. If
     * not all services can be migrated the whole application fails and is stopped.
     * @param fd lost device
     */
    private void lostDeviceReplanning(Fogdevice fd){
        if(applicationAssignments.size() < 1) {
            return;
        }

        EventResult result = deviceFailure.handleDeviceFailure(fd, applicationAssignments);
        if(result == null) return;

        Set<TaskRequest> requestsToMigrate = result.getRequestsToMigrate();
        Set<ApplicationAssignment> affectedAssignments = result.getAffectedAssignments();

        // deploy the services of this device somewhere else
        ApplicationAssignment newAssignments = deployServices(requestsToMigrate, getTopologyChildren());

        // if something failed stop all involved applications
        if(newAssignments == null){
            for(ApplicationAssignment ass : affectedAssignments){
                stopFailedApplicationServices(ass);
            }
        }

        // update all application assignments
        for(ApplicationAssignment ass : affectedAssignments){
            adaptApplicationAssignment(ass, newAssignments.getAssignedTasks());
        }
    }



    /**
     *
     * OVERLOAD EVENT
     * ################################################################################################################
     *
     */

    public void deviceOverloadedEvent(Fogdevice fd){
        EventResult result = deviceOverload.handleDeviceOverload(fd, applicationAssignments);
        if(result == null) return;

        List<TaskAssignment> servicesToMigrate = result.getServicesToMigrate();
        Set<TaskRequest> requestsToMigrate = result.getRequestsToMigrate();
        ApplicationAssignment affectedAssignment = result.getFirstApplicationAssignment();

        // remove the overloaded device from children list
        Set<Fogdevice> children = getTopologyChildren();
        children.remove(fd);

        // deploy new services and then stop the old ones
        ApplicationAssignment applicationAssignment = deployServices(requestsToMigrate, children);
        if (applicationAssignment == null) {
            stopFailedApplicationServices(affectedAssignment);
            log.error("--- Failed migrating services");
            return;
        }
        // after deploying the new services stop the old ones
        for (TaskAssignment ass : servicesToMigrate) {
            stopService(ass);
        }

        // correct the applicationassignment
        adaptApplicationAssignment(affectedAssignment, applicationAssignment.getAssignedTasks());
    }



    /**
     *
     * EVALUATION
     * ################################################################################################################
     *
     */

    public String getEvaluationSummary(){
        String output = "";

        // REQUEST COUNT ANALYSIS
        // -------------------------------------------------------------------------------------------------------------
        output += "---- REQUEST COUNT ----<br>";
        output += "total request count: "+totalRequestCount;
        output += "<br>cloud request count: "+cloudRequestCount;
        output += "<br>fog request count: "+(totalRequestCount-cloudRequestCount);


        // TOTAL DEPLOYMENT TIME ANALYSIS
        // -------------------------------------------------------------------------------------------------------------
        output += "<br><br>---- TOTAL DEPLOYMENT TIME ----<br>";
        double maxDuration = -1;
        double minDuration = Integer.MAX_VALUE;
        double durationPerRequest;
        double durationPerOrder;
        double sumDuration = 0;
        for(Map.Entry e : deploymentTimes.entrySet()){
            // deploymentTime: avg per request, avg general, stdev per request, stdev general, min, max
            int reqCount = (int) e.getKey();
            double duration = (double) e.getValue();
            double preq = duration/(reqCount*1.0);
            output += reqCount+" services deployed in "+duration+" ("+preq+"s/Req) || ";

            if(duration > maxDuration) maxDuration = duration;
            if(duration < minDuration) minDuration = duration;

            sumDuration += duration;
        }
        output += "<br>max:"+maxDuration+", min: "+minDuration;

        if(deploymentTimes.size() > 0) {
            durationPerOrder = sumDuration / deploymentTimes.size();
            output += "<br>per application: " + durationPerOrder;
        }
        if(totalRequestCount > 0) {
            durationPerRequest = sumDuration / totalRequestCount;
            output += "<br>per service: " + durationPerRequest;
        }


        // FOG DEPLOYMENT TIME ANALYSIS
        // -------------------------------------------------------------------------------------------------------------
        output += "<br><br>---- FOG DEPLOYMENT TIME ----<br>";
        int fogRequestCount = totalRequestCount - cloudRequestCount;
        double fogmaxDuration = -1;
        double fogminDuration = Integer.MAX_VALUE;
        double fogdurationPerRequest;
        double fogdurationPerOrder;
        double fogsumDuration = 0;
        for(Map.Entry e : fogdeploymentTimes.entrySet()){
            // deploymentTime: avg per request, avg general, stdev per request, stdev general, min, max
            int reqCount = (int) e.getKey();
            double duration = (double) e.getValue();
            double preq = duration/(reqCount*1.0);
            output += reqCount+" fog services deployed in "+duration+" ("+preq+"s/Req) || ";

            if(duration > fogmaxDuration) fogmaxDuration = duration;
            if(duration < fogminDuration) fogminDuration = duration;

            fogsumDuration += duration;
        }
        output += "<br>max: "+fogmaxDuration+", min: "+fogminDuration;

        if(deploymentTimes.size() > 0) {
            fogdurationPerOrder = fogsumDuration / fogdeploymentTimes.size();
            output += "<br>per application: " + fogdurationPerOrder;
        }
        if(fogRequestCount > 0) {
            fogdurationPerRequest = fogsumDuration / fogRequestCount;
            output += "<br>per fog service: " + fogdurationPerRequest;
        }


        // CLOUD DEPLOYMENT TIME ANALYSIS
        // -------------------------------------------------------------------------------------------------------------
        output += "<br><br>---- CLOUD DEPLOYMENT TIME ----<br>";
        double cloudmaxDuration = -1;
        double cloudminDuration = Integer.MAX_VALUE;
        double clouddurationPerRequest;
        double clouddurationPerOrder;
        double cloudsumDuration = 0;
        for(Map.Entry e : clouddeploymentTimes.entrySet()){
            // deploymentTime: avg per request, avg general, stdev per request, stdev general, min, max
            int reqCount = (int) e.getKey();
            double duration = (double) e.getValue();
            double preq = duration/(reqCount*1.0);
            output += reqCount+" cloud services deployed in "+duration+" ("+preq+"s/Req) || ";

            if(duration > cloudmaxDuration) cloudmaxDuration = duration;
            if(duration < cloudminDuration) cloudminDuration = duration;

            cloudsumDuration += duration;
        }
        output += "<br>max: "+cloudmaxDuration+", min: "+cloudminDuration;

        if(clouddeploymentTimes.size() > 0) {
            clouddurationPerOrder = cloudsumDuration / clouddeploymentTimes.size();
            output += "<br>per application: " + clouddurationPerOrder;
        }
        if(cloudRequestCount > 0) {
            clouddurationPerRequest = cloudsumDuration / cloudRequestCount;
            output += "<br>per cloud service: " + clouddurationPerRequest;
        }

        return  output;
    }
}