package at.ac.tuwien.infosys.cloud;

import at.ac.tuwien.infosys.model.*;

import java.util.List;
import java.util.Map;

/**
 * Created by Kevin Bachmann on 13/12/2016.
 * Service to start and stop VMs in the cloud and start, stop and migrate containers on these started cloud VMs.
 */
public interface ICloudService {

    /**
     * Deploys a service based on the passed image that is pulled from dockerhub beforehand. In case no VM is running,
     * the service starts up a new VM in the cloud.
     * @param image docker image to be deployed to a service
     * @return returns the resulting task assignment with VM and container information
     * @throws Exception throws an exception in case an error occurred
     */
    TaskAssignment deployService(DockerImage image) throws Exception;

    /**
     * Stops the cloud service with the passed container id
     * @param containerId the container id of the service to stop
     */
    void stopService(String containerId);

    /**
     * Sends the propagated service data to a randomly selected suitable service to save it in a cloud database for
     * further processing/analysis.
     * @param data service data to persist
     */
    void sendDataToDeployedCloudService(List<ServiceData> data);

    /**
     * Returns the vm mappings for evaluation reasons
     * @return vm mappings
     */
    Map<DockerHost, List<ServiceAssignment>> getVMMappings();
}
