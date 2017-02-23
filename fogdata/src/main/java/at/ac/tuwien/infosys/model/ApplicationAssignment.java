package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kevin Bachmann on 15/12/2016.
 */
@Data
public class ApplicationAssignment {

    private String id;
    private List<TaskAssignment> assignedTasks;
    private List<TaskRequest> openRequests;

    public ApplicationAssignment(){
        this.id = UUID.randomUUID().toString();
        this.assignedTasks = new ArrayList<TaskAssignment>();
        this.openRequests = new ArrayList<TaskRequest>();
    }

    public ApplicationAssignment(String id, List<TaskAssignment> assignedTasks, List<TaskRequest> openRequests){
        this.id = id;
        this.assignedTasks = assignedTasks;
        this.openRequests = openRequests;
    }

    public ApplicationAssignment(List<TaskAssignment> assignedTasks){
        this.id = UUID.randomUUID().toString();
        this.assignedTasks = assignedTasks;
        this.openRequests = new ArrayList<TaskRequest>();
    }
}
