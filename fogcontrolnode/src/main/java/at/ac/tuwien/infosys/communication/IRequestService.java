package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.Fogdevice;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

/**
 * Created by Kevin Bachmann on 22/11/2016.
 * Service to send REST requests to a specified url, method, entity, and response type
 */
public interface IRequestService {

    /**
     * Send request to parent stored in the database with the passed url
     * @param url API url
     * @param method HTTP method (e.g., POST)
     * @param requestEntity wrapper including the entity to send in the request body, null otherwise
     * @param type expected type of the response
     * @param <T> response type
     * @return response of the REST API call on the specified url
     */
    <T> T sendRequest(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type);

    /**
     * Send request to the passed fog device fd
     * @param fd fog device to send the request to
     * @param method HTTP method (e.g., POST)
     * @param requestEntity wrapper including the entity to send in the request body, null otherwise
     * @param type expected type of the response
     * @param <T> response type
     * @return response of the REST API call on the specified url
     */
    <T> T sendRequest(Fogdevice fd, String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type);
}
