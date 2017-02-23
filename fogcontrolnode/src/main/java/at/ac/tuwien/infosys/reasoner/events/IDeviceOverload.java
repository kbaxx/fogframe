package at.ac.tuwien.infosys.reasoner.events;

import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.EventResult;
import at.ac.tuwien.infosys.model.Fogdevice;

import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 20/01/2017.
 */
public interface IDeviceOverload {

    /**
     * Device overload event handler. This event handles the case when a fogdevice is overloaded.
     * @param fd overloaded fog device
     * @param applicationAssignments affected application assignments to handle
     * @return event result object with the services to migrate and the affected application assignments
     */
    EventResult handleDeviceOverload(Fogdevice fd, List<ApplicationAssignment> applicationAssignments);
}
