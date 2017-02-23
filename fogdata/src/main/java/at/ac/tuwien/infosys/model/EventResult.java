package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 20/01/2017.
 */
@Data
public class EventResult {

    private List<TaskAssignment> servicesToMigrate;
    private Set<ApplicationAssignment> affectedAssignments;

    public EventResult(){}

    public EventResult(List<TaskAssignment> servicesToMigrate){
        this.servicesToMigrate = servicesToMigrate;
        this.affectedAssignments = new HashSet<ApplicationAssignment>();
    }

    public EventResult(List<TaskAssignment> servicesToMigrate, ApplicationAssignment assignment){
        this.servicesToMigrate = servicesToMigrate;
        this.affectedAssignments = new HashSet<ApplicationAssignment>();
        this.affectedAssignments.add(assignment);
    }

    public EventResult(List<TaskAssignment> servicesToMigrate, Set<ApplicationAssignment> affectedAssignments){
        this.servicesToMigrate = servicesToMigrate;
        this.affectedAssignments = affectedAssignments;
    }

    public Set<TaskRequest> getRequestsToMigrate(){
        HashSet<TaskRequest> requestsToMigrate = new HashSet<TaskRequest>();
        for(TaskAssignment ass : this.servicesToMigrate){
            requestsToMigrate.add(ass.getTaskRequest());
        }
        return requestsToMigrate;
    }

    public ApplicationAssignment getFirstApplicationAssignment(){
        return new ArrayList<ApplicationAssignment>(this.affectedAssignments).get(0);
    }
}
