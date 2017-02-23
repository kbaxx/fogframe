package at.ac.tuwien.infosys.reasoner.resourceProvisioning.impl;

import at.ac.tuwien.infosys.communication.impl.CommunicationService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.reasoner.resourceProvisioning.IResourceProvisioning;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.watchdog.WatchdogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 */
@Service
@Slf4j
public class ResourceProvisioning implements IResourceProvisioning {

    @Autowired
    private CommunicationService commService;

    @Autowired
    private WatchdogService watchdogService;

    private String logstarter = "--- RESPROV: ";


    // sort incoming services according to service-types and then deploy them until the threshold is nearly exceeded
    public ApplicationAssignment handleTaskRequests(Set<Fogdevice> children, Set<TaskRequest> requests) throws Exception {

        ApplicationAssignment ass = new ApplicationAssignment();
        List<TaskAssignment> taskAssignments = new ArrayList<TaskAssignment>();

        // 1. sort requests according to service-type (comparable interface)
        ArrayList<TaskRequest> sortedRequests = new ArrayList<TaskRequest>(requests);
        Collections.sort(sortedRequests);
        Iterator<TaskRequest> requestIt = sortedRequests.iterator();
        log.info(logstarter+"sorted task requests: "+sortedRequests);

        // 2. sort children according to service-type (comparable interface)
        ArrayList<Fogdevice> sortedChildren = new ArrayList<Fogdevice>(children);
        Collections.sort(sortedChildren);
        Iterator<Fogdevice> childIt = sortedChildren.iterator();

        log.info(logstarter+"sorted children: "+sortedChildren.toString());

        int round = 0;
        // 3. assign requests to children
        while (childIt.hasNext()) {
            Fogdevice fd = childIt.next();
            log.info(logstarter+"------- child: "+fd.getIp()+" with types:"+fd.getServiceTypes()+ " -------");

            for(String childType : fd.getServiceTypes()){

                requestIt = sortedRequests.iterator();
                while (requestIt.hasNext()) {
                    TaskRequest req = requestIt.next();
                    String reqType = req.getServiceType();

                    if(childType.equals(reqType)){
                        // check utilization
                        Utilization u = null;
                        do {
                            u = commService.getChildUtilization(fd);
                            if(u == null || u.getStorage() == 0 || u.getCpu() == 0 || u.getRam() == 0)
                                Thread.sleep(1000);
                        } while(u == null || u.getStorage() == 0 || u.getCpu() == 0 || u.getRam() == 0);
                        log.info(logstarter+"util of " + fd.getIp() + ": "+u.toString());

                        Set<DockerContainer> deployedContainers =commService.requestDeployedContainers(fd);
                        int containerCount = 0;
                        if(deployedContainers != null) {
                            containerCount = deployedContainers.size();
                        }
                        log.info(logstarter+containerCount+" already deployed containers");
                        if(containerCount >= Constants.MAX_CONTAINERS){
                            break;
                        }
                        if(watchdogService.checkRules(u)){
                            // assign it to the child
                            log.info(logstarter+"send deployment request to " + fd.getIp() + ": "+req);
                            DockerContainer container = commService.sendServiceDeploymentRequest(fd, req);

                            TaskAssignment taskAssignment = new TaskAssignment(fd, req, container, false);
                            taskAssignments.add(taskAssignment);

                            // remove request from list that it does not get assigned anymore
                            requestIt.remove();
                        }

                    } else {
                        // do nothing
                    }
                }
            }

            // retry if the task requests are not all deployed and the iterator is empty
            if(!childIt.hasNext() && sortedRequests.size() > 0 && round < Constants.PROVISIONING_ROUNDS){
                round++;
                childIt = sortedChildren.iterator();
                log.info(logstarter+"-------------- ROUND "+(round)+" --------------");
            }
        }
        if(sortedRequests.size() > 0) {
            log.info(logstarter+"The following task requests could not be deployed\n" + sortedRequests+"\n----------------------------------------");
        }
        log.info(logstarter+"finished the resource provisioning of the fog tasks");

        ass.setAssignedTasks(taskAssignments);
        ass.setOpenRequests(sortedRequests);
        return ass;
    }
}
