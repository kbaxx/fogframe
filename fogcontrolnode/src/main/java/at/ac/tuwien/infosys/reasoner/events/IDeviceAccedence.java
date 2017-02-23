package at.ac.tuwien.infosys.reasoner.events;

import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.EventResult;
import at.ac.tuwien.infosys.model.Fogdevice;

/**
 * Created by Kevin Bachmann on 20/01/2017.
 */
public interface IDeviceAccedence {

    /**
     * Event handler in case a new device appears.
     * @param fd newly detected device
     * @param applicationAssignment current application assignment to handle
     * @return event result object with the services to migrate and the affected application assignments
     */
    EventResult handleDeviceAccedence(Fogdevice fd, ApplicationAssignment applicationAssignment);
}
