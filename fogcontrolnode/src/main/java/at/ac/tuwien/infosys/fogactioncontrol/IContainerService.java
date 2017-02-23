package at.ac.tuwien.infosys.fogactioncontrol;

import at.ac.tuwien.infosys.model.DockerContainer;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.messages.Container;

import java.util.List;

/**
 * Created by Kevin Bachmann on 28/10/2016.
 * Service to pull image objects, build images locally, deploy, stop, and remove containers.
 */
public interface IContainerService {

    /**
     * Creates a docker image from the passed dockerfile with passed image name
     * @param dockerfile dockerfile construction plan to build the image
     * @param imageName name of the image to create
     * @return returns the image id of the created image
     * @throws DockerCertificateException throws a certificate exception in case the creation does not work
     */
    String createImageFromDockerfile(String dockerfile, String imageName) throws DockerCertificateException;

    /**
     * Starts a new container with the specified service key. In case the docker image of the service key is not locally
     * available, the method pulls the image information, creates the docker image first, and then deploys it.
     * @param serviceKey unique service key of the deployable service
     * @return returns the container information in case it worked, null otherwise
     */
    DockerContainer startContainer(String serviceKey);

    /**
     * Stops the container with the passed container id
     * @param dockerId container id of the service to stop
     */
    void stopContainer(String dockerId);

    /**
     * Removes a already created container from the local docker environment
     * @param dockerId container id of the container to remove
     */
    void removeContainer(String dockerId);

    /**
     * Stops all running docker containers
     */
    void stopRunningContainers();

    /**
     * Stop a container by service key
     * @param serviceKey service key of the containers to stop
     */
    void stopContainerByService(String serviceKey);

    /**
     * Stops and removes a container with the specified docker id
     * @param dockerId id of the container to stop
     */
    void stopContainerById(String dockerId);

    /**
     * Returns all running docker containers in the docker environment
     * @return list with running containers
     */
    List<Container> getRunningContainers();
}



