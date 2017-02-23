package at.ac.tuwien.infosys.reasoner.events;

import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.EventResult;
import at.ac.tuwien.infosys.model.Fogdevice;

import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 20/01/2017.
 */
public interface IDeviceFailure {

    /**
     * Lost device event handler in case a device fails.
     * @param fd lost device
     * @param applicationAssignments affected application assignments to handle
     * @return event result object with the services to migrate and the affected application assignments
     */
    EventResult hanldeDeviceFailure(Fogdevice fd, List<ApplicationAssignment> applicationAssignments);
}
