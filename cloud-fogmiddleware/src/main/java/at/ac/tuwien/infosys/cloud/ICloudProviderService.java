package at.ac.tuwien.infosys.cloud;

import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.model.DockerHost;
import at.ac.tuwien.infosys.model.DockerImage;

/**
 * Created by Kevin Bachmann on 30/11/2016.
 * A Service representing the minimum methods that need to be implemented by every cloud provider implementation. These
 * methods enable the setup of the cloud environment, start, stop VMs and docker containers.
 */
public interface ICloudProviderService {
    /**
     * Setup the cloud environment, read the required cloud properties, and authenticate the user and project.
     */
    void setup();

    /**
     * Start a new VM with the passed docker host information
     * @param dh docker host just containing of some information like name, ..
     * @return returns the filled docker host object with url and others
     */
    DockerHost startVM(DockerHost dh) throws Exception;

    /**
     * Stops the VM with the passed name
     * @param name unique name of the VM to stop
     */
    void stopDockerHost(final String name);

    /**
     * Starts a docker container with the passed image on the passed url of the VM
     * @param url url the container is started one
     * @param image image to deploy on the VM
     * @return returns the deployed docker container information
     */
    DockerContainer startDockerContainer(String url, DockerImage image);

    /**
     * Stop docker container running on the passed url with the passed container id
     * @param url VM url the container is running on
     * @param containerId id of the container to stop
     */
    void stopDockerContainer(String url, String containerId);
}
