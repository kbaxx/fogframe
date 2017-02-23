package at.ac.tuwien.infosys.reasoner;

import at.ac.tuwien.infosys.model.Application;
import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Message;

import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 13/12/2016.
 * Service that handles the reasoning of the whole fog landscape. This includes the task request handling, resource
 * provisioning, topology monitoring and event creating, amongst others. Most of the decisions and plans made in the
 * total environment are made in this service, e.g., service placement in the cloud or fog are started, etc.
 */
public interface IReasonerService {
    /**
     * Handles the incoming task requests and executes the implemented resource provisioning approach. Furthermore,
     * this method creates an application task mapping to know where every service is running.
     * @param application application consisting of several task requests to deploy in the system
     * @return a message with deployment time and status if the deployment worked out
     */
    Message handleTaskRequests(Application application);

    /**
     * Returns a string with some metrics used to evaluate the system.
     * @return evaluation string
     */
    String getEvaluationSummary();

    /**
     * Returns the children / devices of the whole environment (recursively)
     * @return set with topology children (all)
     */
    Set<Fogdevice> getTopologyChildren();

    /**
     * Checks if this very device is the root fog control node
     * @return true if it is the root fcn, false otherwise
     */
    boolean isRootFCN();

    /**
     * Trigger for the device overload event used by the watchdog.
     * @param fd overloaded fog device
     */
    void deviceOverloadedEvent(Fogdevice fd);

    /**
     * Stops all applications running in the total fog computing framework (cloud and fog services)
     */
    void stopAllApplications();

    /**
     * Returns the currently running application assignments
     * @return list with application assignments
     */
    List<ApplicationAssignment> getApplicationAssignments();
}
