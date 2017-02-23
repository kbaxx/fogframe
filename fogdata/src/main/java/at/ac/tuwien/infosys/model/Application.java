package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by Kevin Bachmann on 15/12/2016.
 */
@Data
public class Application {

    private String id;
    private List<TaskRequest> requests;
    private int duration; // duration in seconds

    public Application(){
        this.id = UUID.randomUUID().toString();
        this.requests = new ArrayList<TaskRequest>();
        this.duration = -1; // -1 means infinite
    }

    public Application(List<TaskRequest> requests){
        this.id = UUID.randomUUID().toString();
        this.requests = requests;
        this.duration = -1; // -1 means infinite
    }

    public Application(List<TaskRequest> requests, int duration){
        this.id = UUID.randomUUID().toString();
        this.requests = requests;
        this.duration = duration;
    }

    public Application(String id, List<TaskRequest> requests){
        this.id = id;
        this.requests = requests;
        this.duration = -1; // -1 means infinite
    }
}
