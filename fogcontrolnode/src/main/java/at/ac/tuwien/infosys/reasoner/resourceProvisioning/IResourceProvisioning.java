package at.ac.tuwien.infosys.reasoner.resourceProvisioning;

import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.TaskRequest;

import java.util.Set;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 * Resource provisioning service handling the service placement according to the passed fog devices and the set of
 * task requests.
 */
public interface IResourceProvisioning {

    /**
     * Handles the service placement of the passed task requests on the passed fog devices and returns the resulting
     * successful task assignments and the open requests that could not be deployed with the passed children.
     * @param children children fog devices to place the services on
     * @param requests task requests resulting in deployed service containers
     * @return returns the application assignment with successful taskassignments and open task requests
     * @throws Exception exception if anything happens during the placement
     */
    ApplicationAssignment handleTaskRequests(Set<Fogdevice> children, Set<TaskRequest> requests) throws Exception;
}
