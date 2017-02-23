package at.ac.tuwien.infosys.reasoner.events.impl;

import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.reasoner.events.IDeviceAccedence;
import at.ac.tuwien.infosys.reasoner.events.IDeviceOverload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 20/01/2017.
 */
@Service
@Slf4j
public class DeviceOverload implements IDeviceOverload {

    @Override
    public EventResult handleDeviceOverload(Fogdevice fd, List<ApplicationAssignment> applicationAssignments) {
        // basic overload policy to fix the overload: just migrate one random container from the affected device
        log.warn("----- OVERLOAD: Device "+fd.getIp()+":"+fd.getPort()+" is overloaded.");

        List<TaskAssignment> servicesToMigrate = new ArrayList<TaskAssignment>();
        ApplicationAssignment affectedAssignment = null;
        outerloop:
        for(ApplicationAssignment assignment : applicationAssignments){
            for (TaskAssignment ass : assignment.getAssignedTasks()) {
                if (ass.getFogdevice().equals(fd)) {
                    // just add one service to the list of services to migrate
                    servicesToMigrate.add(ass);
                    affectedAssignment = assignment;
                    break outerloop;
                }
            }
        }
        if (servicesToMigrate.size() < 1) {
            log.info("--- No services to migrate. The device is overloaded without deployed services. Check the device log.");
            return null;
        }

        return new EventResult(servicesToMigrate, affectedAssignment);
    }
}
