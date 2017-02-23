package at.ac.tuwien.infosys.propagator;

import at.ac.tuwien.infosys.model.ServiceData;
import at.ac.tuwien.infosys.model.TaskAssignment;
import at.ac.tuwien.infosys.model.TaskRequest;

import java.util.List;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 * Service to propagate the service data or task requests to upper layers.
 */
public interface IPropagatorService {

    /**
     * Propagates data received and created by services to upper levels for further data processing.
     * @param o list with service data objects
     */
    void propagate(List<ServiceData> o);

    /**
     * Propagates task requests to upper levels in case a fog control node in the middle receives task requests
     * from outside the environment or the root fcn propagates it to the cloud.
     * @param requests list of task requests
     * @return returns a list of task assignments that assigns the request a device and a container after deployment
     */
    List<TaskAssignment> propagateTaskRequests(List<TaskRequest> requests);
}
