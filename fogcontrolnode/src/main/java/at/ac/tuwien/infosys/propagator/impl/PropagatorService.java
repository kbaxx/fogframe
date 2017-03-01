package at.ac.tuwien.infosys.propagator.impl;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.model.ServiceData;
import at.ac.tuwien.infosys.model.TaskAssignment;
import at.ac.tuwien.infosys.model.TaskRequest;
import at.ac.tuwien.infosys.propagator.IPropagatorService;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 */
@Service
@Slf4j
public class PropagatorService implements IPropagatorService {

    @Autowired
    private ICommunicationService commService;

    public void propagate(List<ServiceData> o) {
        ServiceData s = o.get(0);
        if(s != null && s.getSender() != null) //log.info("Propagating from "+ s.getSender().getIp()+ ":"+s.getSender().getPort());
        try {
            commService.sendToParent(Constants.URL_PROPAGATE, HttpMethod.POST, new HttpEntity(o),
                    new ParameterizedTypeReference<Object>() {});
        }catch (Exception ex){
            log.warn("Data propagation failed. Either the parent isn't available or not correctly configured.");
        }
    }

    public List<TaskAssignment> propagateTaskRequests(List<TaskRequest> requests){
        List<TaskAssignment> list = new ArrayList<TaskAssignment>();
        try {
            list = commService.sendToParent(Constants.URL_PROPAGATE_TASK_REQUESTS, HttpMethod.POST,
                    new HttpEntity(requests), new ParameterizedTypeReference<List<TaskAssignment>>() {});
        } catch(Exception ex){
            log.warn("Task request propagation failed. Either the parent isn't available or not correctly configured.");
        }
        return list;
    }
}
