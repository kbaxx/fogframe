package at.ac.tuwien.infosys.communication.impl;

import at.ac.tuwien.infosys.communication.IRequestService;
import at.ac.tuwien.infosys.model.Fogdevice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 */
@Service
@Slf4j
public class RequestService implements IRequestService {

    @Value("${fog.connection.timeout}")
    private int CONNECT_TIMEOUT;

    public <T> T sendRequest(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type){
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(CONNECT_TIMEOUT);

        ResponseEntity<T> response = null;
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        try {
            response = restTemplate.exchange(url, method, requestEntity, type);
        } catch(RestClientException e){
//            log.error("RequestService Error (most likely timeout): "+e.getMessage());
//            System.out.print(" || "+ url);
            System.out.print(".");
            return null;
        }

        return response.getBody();
    }

    public <T> T sendRequest(Fogdevice fd, String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type ){
        T result = sendRequest("http://"+fd.getIp()+":"+fd.getPort() + url, method, requestEntity, type);
        return result;
    }
}
