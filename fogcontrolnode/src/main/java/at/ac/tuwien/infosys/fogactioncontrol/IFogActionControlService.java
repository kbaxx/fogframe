package at.ac.tuwien.infosys.fogactioncontrol;

import at.ac.tuwien.infosys.model.DockerContainer;
import com.spotify.docker.client.messages.Container;

import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 24/11/2016.
 * Service to deploy and release services and prepare and configure the service environment at startup
 */
public interface IFogActionControlService {

    /**
     * Deploys a new container with the docker image according the passed service key
     * @param serviceKey service key of the docker image to deploy
     * @return the docker container if the service could be deployed, null otherwise
     */
    DockerContainer deploy(String serviceKey);

    /**
     * Stops all running docker containers that have been started by this very service
     */
    void stopAllRunningDockerContainers();

    /**
     * Stops the running containers by service key
     * @param serviceKey the service key of the containers to stop
     */
    void stopContainerByService(String serviceKey);

    /**
     * Stops a running container with the passed docker id
     * @param dockerId id of the container to stop
     */
    void stopContainerById(String dockerId);

    /**
     * Returns all the running services of the whole docker environment of this device
     * @return list of containers
     */
    List<Container> getRunningServices();

    /**
     * Returns all the started containers of this very service
     * @return list of docker containers
     */
    Set<DockerContainer> getCreatedContainers();
}
