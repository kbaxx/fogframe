package at.ac.tuwien.infosys.reasoner.events.impl;

import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.EventResult;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.TaskAssignment;
import at.ac.tuwien.infosys.reasoner.events.IDeviceAccedence;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by Kevin Bachmann on 20/01/2017.
 */
@Service
@Slf4j
public class DeviceAccedence implements IDeviceAccedence {

    @Override
    public EventResult handleDeviceAccedence(Fogdevice fd, ApplicationAssignment applicationAssignment) {

        // calculate a map with fogdevice and its deployed container count
        HashMap<Fogdevice, Integer> containerCounterMap = new HashMap<Fogdevice, Integer>();
        for (TaskAssignment ass : applicationAssignment.getAssignedTasks()) {
            if (containerCounterMap.size() > 0 && containerCounterMap.containsKey(ass.getFogdevice())) {
                int counter = containerCounterMap.get(ass.getFogdevice());
                containerCounterMap.put(ass.getFogdevice(), counter + 1);
            } else {
                containerCounterMap.put(ass.getFogdevice(), 1);
            }
        }

        List<TaskAssignment> servicesToMigrate = new ArrayList<TaskAssignment>();
        for (Map.Entry e : containerCounterMap.entrySet()) {
            Fogdevice tempDevice = (Fogdevice) e.getKey();
            int containerCount = (int) e.getValue();

            for (TaskAssignment ass : applicationAssignment.getAssignedTasks()) {
                // go through the map and migrate a container from the ones who have the maximum container count deployed
                // (or it is a service deployed in the cloud -> save cloud resources / costs)
                if (ass.getFogdevice().equals(tempDevice) && fd.getServiceTypes().contains(ass.getTaskRequest().getServiceType())
                        && (containerCount == Constants.MAX_CONTAINERS || ass.isInCloud())) {

                    // stop after migrating MAX_CONTAINERS/2 (or it is a service deployed in the cloud)
                    if (servicesToMigrate.size() <= Math.floor(Constants.MAX_CONTAINERS / 2) || ass.isInCloud()) {
                        servicesToMigrate.add(ass);

                        // only leave after one service is migrated when the service is not from the cloud
                        // reason: all cloud services should be migrated to the fog if possible
                        if(!ass.isInCloud())
                            break;
                    }
                }
            }
        }
        if (servicesToMigrate.size() < 1) {
            log.info("--- No services to migrate as no device is fully loaded or the services do not fit the new device.");
            return null;
        }

        return new EventResult(servicesToMigrate);
    }
}
