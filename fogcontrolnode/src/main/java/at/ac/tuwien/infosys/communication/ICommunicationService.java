package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Set;

/**
 * Created by Kevin Bachmann on 03/11/2016.
 * Service enabling the communication between the different components/devices.
 */
public interface ICommunicationService {

    /**
     * Send request to parent stored in the database
     * @param url API url
     * @param method HTTP method (e.g., POST)
     * @param requestEntity wrapper including the entity to send in the request body, null otherwise
     * @param type expected type of the response
     * @param <T> response type
     * @return response of the REST API call on the specified url
     */
    <T> T sendToParent(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type);

    /**
     * Send request to the cloud stored in the database
     * @param url API url
     * @param method HTTP method (e.g., POST)
     * @param requestEntity wrapper including the entity to send in the request body, null otherwise
     * @param type expected type of the response
     * @param <T> response type
     * @return response of the REST API call on the specified url
     */
    <T> T sendToCloud(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type);


    /**
     * Sends a pair request to the specified parent stored in the database.
     * @return a message object with a status that indicates whether it was successful or not
     */
    Message sendPairRequest();

    /**
     * Sends a manual pair request to the passed fog device fd.
     * @param fd device to pair to
     * @return a message object with a status that indicates whether it was successful or not
     */
    Message sendManualPairRequest(Fogdevice fd);



    /**
     * Requests a suitable parent at the cloud-fog middleware specified in the local database.
     * @param loc proprietary location
     * @return responsible parent device to pair to
     */
    Fogdevice requestParent(Location loc);

    /**
     * Answers a ping request with a message
     * @return returns a message object with "ping" in the header
     */
    Message ping();

    /**
     * Pings all the connected children
     */
    void pingChildren();

    /**
     * Sends a ping to the passed fog device fd.
     * @param fd fog device to send the ping to
     * @return message with a flag indicating the status of the ping
     */
    Message sendPing(Fogdevice fd);

    /**
     * Pair request endpoint method that adds the passed fog device to the connected children
     * @param fd new paired child
     * @return message indicating whether the pair was successful or not
     */
    Message pair(Fogdevice fd);


    /**
     * Request the utilization of the passed fog device fd.
     * @param fd fog device of the wanted utilization
     * @return utilization object consisting of cpu, ram, and storage
     */
    Utilization getChildUtilization(Fogdevice fd);

    /**
     * Request the utilization of the connected children.
     * @return utilization set consisting of utilization objects
     */
    Set<Utilization> getChildrenUtilization();

    /**
     * Requests the children of the passed child fog device fd.
     * @param fd fog device whos children are requested
     * @return set of children fog devices of the passed fog device
     */
    Set<Fogdevice> getChildrenOfChild(Fogdevice fd);


    /**
     * Sends a service deployment request to the passed fog device fd with the required task to deploy ts
     * @param fd fog device to deploy the service on
     * @param ts task request to deploy
     * @return if the service is deployed successfully the request returns docker container data otherwise null
     */
    DockerContainer sendServiceDeploymentRequest(Fogdevice fd, TaskRequest ts);

    /**
     * Sends a service stop request requesting the stopping of a container with the passed dockerId at the passed
     * fog device fd
     * @param fd fog device the service is running on
     * @param dockerId id of the deployed docker container
     */
    void sendServiceStopRequest(Fogdevice fd, String dockerId);

    /**
     * Request the deployed and running containers on the passed fog device fd.
     * @param fd the fog device on which the containers are running
     * @return a set of docker container objects running on fd
     */
    Set<DockerContainer> requestDeployedContainers(Fogdevice fd);

    /**
     * Returns the location range specified by in the property file
     * @return location range object with upper and lower range
     */
    LocationRange getLocationRange();

    /**
     * Sends a service stop request to the specified cloud-fog middleware with the VM data wrapped in a fog device
     * object and the docker container id as the second parameter.
     * @param fd the VM data where the service is running on
     * @param dockerId the docker id of the service to stop
     */
    void sendCloudServiceStopRequest(Fogdevice fd, String dockerId);

    /**
     * Distributes a docker image to the passed fog device for registration purposes
     * @param fd fog device to send the docker image to
     * @param image image to register at the receiving fog device
     */
    void distributeDockerImage(Fogdevice fd, DockerImage image);
}
