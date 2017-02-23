package at.ac.tuwien.infosys.reasoner.events.impl;

import at.ac.tuwien.infosys.model.ApplicationAssignment;
import at.ac.tuwien.infosys.model.EventResult;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.TaskAssignment;
import at.ac.tuwien.infosys.reasoner.events.IDeviceAccedence;
import at.ac.tuwien.infosys.reasoner.events.IDeviceFailure;
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
public class DeviceFailure implements IDeviceFailure {
    @Override
    public EventResult hanldeDeviceFailure(Fogdevice fd, List<ApplicationAssignment> applicationAssignments) {
        Set<ApplicationAssignment> affectedAssignments = new HashSet<ApplicationAssignment>();
        List<TaskAssignment> servicesToMigrate = new ArrayList<TaskAssignment>();

        for(ApplicationAssignment appAss: applicationAssignments){
            for(TaskAssignment ass : appAss.getAssignedTasks()){
                if(ass.getFogdevice().getIp().equals(fd.getIp())){
                    affectedAssignments.add(appAss);
                    servicesToMigrate.add(ass);
                }
            }
        }
        if(servicesToMigrate.size() == 0){
            return null;
        }
        return new EventResult(servicesToMigrate, affectedAssignments);
    }
}
