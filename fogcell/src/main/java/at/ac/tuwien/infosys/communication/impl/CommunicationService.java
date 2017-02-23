package at.ac.tuwien.infosys.communication.impl;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.communication.IRequestService;
import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Kevin Bachmann on 03/11/2016.
 */
@Service
@Slf4j
public class CommunicationService implements ICommunicationService {

    @Autowired
    private IRequestService requestService;

    @Autowired
    private IDatabaseService dbService;

    private String getParentUrl(){
        Fogdevice parent = dbService.getParent();
        if(parent == null) log.warn("No parent set for this Fog Device");
        return "http://" + parent.getIp() + ":" + parent.getPort();
    }

    private String getCloudUrl(){
        return "http://" + dbService.getCloudIp() + ":" + dbService.getCloudPort();
    }

    public <T> T sendToParent(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type ){
        String parentUrl = getParentUrl();
        T result = requestService.sendRequest(parentUrl + url, method, requestEntity, type);
        return result;
    }

    public <T> T sendToCloud(String url, HttpMethod method, HttpEntity requestEntity, ParameterizedTypeReference<T> type ){
        String cloudUrl = getCloudUrl();
        T result = requestService.sendRequest(cloudUrl + url, method, requestEntity, type);
        return result;
    }

    public Message sendPairRequest(){
        // get information about this device
        Fogdevice fc = dbService.getDeviceInformation();

        Message m = sendToParent(Constants.URL_PAIR_REQUEST, HttpMethod.POST, new HttpEntity(fc),
                new ParameterizedTypeReference<Message>(){});
        if(m!=null && m.isStatus()){
            Fogdevice parent = dbService.getParent();
            log.info("Sucessfully paired with "+parent.getIp()+":"+parent.getPort());
        } else {
            log.warn("Pairing error");
        }
        return m;
    }

    public Message sendManualPairRequest(Fogdevice fd) {
        // get information about this devicec
        Fogdevice fc = dbService.getDeviceInformation();
        Message m = requestService.sendRequest(fd, Constants.URL_PAIR_REQUEST, HttpMethod.POST, new HttpEntity(fc),
                new ParameterizedTypeReference<Message>(){});
        if(m!=null && m.isStatus()){
            // if the manual request was successful, save the newly paired parent
            dbService.setParent(fd);
        }
        return m;
    }

    public Fogdevice requestParent(Location loc){
        Fogdevice parent = sendToCloud(Constants.URL_REQUEST_PARENT +loc.getLatitude()+"/"+loc.getLongitude(), HttpMethod.GET,
                null, new ParameterizedTypeReference<Fogdevice>(){});
        return parent;
    }

    public Message ping(){
        return new Message("ping");
    }

    public DockerImage getServiceImage(String serviceKey){
        return sendToParent(Constants.URL_SHARED_GETIMAGE+serviceKey, HttpMethod.GET, null,
                new ParameterizedTypeReference<DockerImage>() {});
    }

    public void propagateData(List<ServiceData> data) {
        sendToParent(Constants.URL_PROPAGATE, HttpMethod.POST, new HttpEntity(data),
                new ParameterizedTypeReference<Object>(){});
    }
}