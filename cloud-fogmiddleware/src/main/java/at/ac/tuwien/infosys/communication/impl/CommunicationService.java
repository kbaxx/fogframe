package at.ac.tuwien.infosys.communication.impl;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.communication.IRequestService;
import at.ac.tuwien.infosys.database.impl.DatabaseService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 20/11/2016.
 */
@Service
@Slf4j
public class CommunicationService implements ICommunicationService {

    @Autowired
    private IRequestService requestService;

    @Autowired
    private DatabaseService dbService;

    public Message pair(Fogdevice fd){
        // fog device that wants to pair requests with its own information. this device sends back a ok
        LocationRange lr = getLocationRange(fd);
        Fogcontrolnode fcn = new Fogcontrolnode(fd.getId(), fd.getType(), fd.getIp(), fd.getPort(), fd.getLocation(),
                                fd.getParent(), fd.getServiceTypes(), lr);
        dbService.addChild(fcn);
        log.info("----- PAIR FROM IP="+ fd.getIp() +":"+fd.getPort()+" -----");
        return new Message("pair");
    }

    public LocationRange getLocationRange(Fogdevice fd){
        return requestService.sendRequest(fd, Constants.URL_LOCATION_RANGE, HttpMethod.GET, null,
                new ParameterizedTypeReference<LocationRange>() {});
    }

    public void saveServiceData(List<ServiceData> data){
        dbService.setServiceData(data);
    }

    public Set<Fogdevice> getChildrenOfChild(Fogdevice fd) {
        return requestService.sendRequest(fd, Constants.URL_DB_CHILDREN, HttpMethod.GET, null,
                new ParameterizedTypeReference<Set<Fogdevice>>() {});
    }
}