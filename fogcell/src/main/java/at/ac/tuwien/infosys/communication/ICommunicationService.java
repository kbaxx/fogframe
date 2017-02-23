package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.*;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.List;

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
    <T> T sendToParent(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type );

    /**
     * Send request to the cloud stored in the database
     * @param url API url
     * @param method HTTP method (e.g., POST)
     * @param requestEntity wrapper including the entity to send in the request body, null otherwise
     * @param type expected type of the response
     * @param <T> response type
     * @return response of the REST API call on the specified url
     */
    <T> T sendToCloud(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type );

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
     * Requests the docker image with the passed unique service key
     * @param serviceKey unique service key defining a docker image
     * @return docker image to build and deploy as docker container
     */
    DockerImage getServiceImage(String serviceKey);

    /**
     * Propagates Data to upper layers
     * @param data data to propagate
     */
    void propagateData(List<ServiceData> data);
}
