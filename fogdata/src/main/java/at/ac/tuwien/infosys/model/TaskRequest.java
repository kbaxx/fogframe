package at.ac.tuwien.infosys.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Data
@EqualsAndHashCode
public class TaskRequest implements Comparable<TaskRequest>, Serializable {

    private String id;
    private String serviceKey;
    private String serviceType;
    private boolean fogTask;
    private boolean cloudTask;

    public TaskRequest() {
        this.id = UUID.randomUUID().toString();
        fogTask = false;
        cloudTask = false;
    }

    public TaskRequest(String serviceKey, String serviceType, boolean fogTask, boolean cloudTask){
        this.id = UUID.randomUUID().toString();
        this.serviceKey = serviceKey;
        this.serviceType = serviceType;
        this.fogTask = fogTask;
        this.cloudTask = cloudTask;
    }

    @Override
    public int compareTo(TaskRequest o) {
        return this.getServiceType().compareTo(o.getServiceType());
    }

    @Override
    public String toString() {
        return "TaskRequest{" +
                "serviceKey='" + serviceKey + '\'' +
                ", serviceType='" + serviceType + '\'' +
                ", fogTask=" + fogTask +
                ", cloudTask=" + cloudTask +
                '}';
    }
}
