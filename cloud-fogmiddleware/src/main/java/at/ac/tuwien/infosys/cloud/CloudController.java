package at.ac.tuwien.infosys.cloud;

import at.ac.tuwien.infosys.communication.impl.CommunicationService;
import at.ac.tuwien.infosys.model.DockerImage;
import at.ac.tuwien.infosys.model.ServiceData;
import at.ac.tuwien.infosys.model.TaskAssignment;
import at.ac.tuwien.infosys.model.TaskRequest;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Bachmann on 13/12/2016.
 * Controller exposing endpoints to start and stop VMs and containers in the cloud and to propagate data to save it in
 * a cloud database for further data analysis.
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class CloudController {

    @Autowired
    private ICloudService cloudService;

    @Autowired
    private CommunicationService commService;

    /**
     * Propagation endpoint for the service data propagation to cloud databases
     * @param o list consisting of service data to persist
     */
    @RequestMapping(method = RequestMethod.POST, value= Constants.URL_PROPAGATE)
    public void propagate(@RequestBody List<ServiceData> o) {
        if(o.size() >= 1) {
            // send data to the database
            cloudService.sendDataToDeployedCloudService(o);
        }
    }

    /**
     * Task propagation endpoint enabling the root fog control node to propagate task requests to be deployed in the
     * cloud.
     * @param requests requests to be deployed in the cloud
     * @return list of task assignments
     */
    @RequestMapping(method = RequestMethod.POST, value= Constants.URL_PROPAGATE_TASK_REQUESTS)
    public List<TaskAssignment> receiveTaskRequests(@RequestBody List<TaskRequest> requests) {
        List<TaskAssignment> assignments = new ArrayList<TaskAssignment>();
        for(TaskRequest req : requests){
            DockerImage image = new DockerImage(req.getServiceKey(), new String[]{String.valueOf(Constants.PORT_CLOUD_SERVICE)});

            TaskAssignment ass = null;
            try {
                ass = cloudService.deployService(image);
                ass.setTaskRequest(req);
            } catch(Exception e){
                log.error("------ A deployment Error occurred: "+e.getMessage());
                return new ArrayList<TaskAssignment>();
            }
            if(ass == null){
                log.error("------ A deployment Error occurred. The assignment is null");
                return new ArrayList<TaskAssignment>();
            }
            assignments.add(ass);
        }
        return assignments;
    }

    /**
     * Stop the service with the passed container id
     * @param containerId id of the container to stop
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_CLOUD_STOP_SERVICE+"{containerId}")
    public void stopService(@PathVariable String containerId) {
        cloudService.stopService(containerId);
    }

    /**
     * Testing endpoint to test the cloud deployment
     * @return the resulting task assignment
     * @throws Exception throws an exception if something fails
     */
    @RequestMapping(method = RequestMethod.POST, value="/cloud/testvm")
    public TaskAssignment test_vm_deployment() throws Exception {
        DockerImage image = new DockerImage(Constants.IMG_CLOUDDB_KEY, new String[]{String.valueOf(Constants.PORT_CLOUD_SERVICE)});
        return cloudService.deployService(image);
    }
}
