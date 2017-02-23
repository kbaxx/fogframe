package at.ac.tuwien.infosys.model;

import lombok.Data;

/**
 * Created by Kevin Bachmann on 15/12/2016.
 */
@Data
public class TaskAssignment {

    private Fogdevice fogdevice;
    private TaskRequest taskRequest;
    private DockerContainer container;
    private boolean inCloud;

    public TaskAssignment() {}

    public TaskAssignment(Fogdevice fd, TaskRequest req, DockerContainer container, boolean inCloud) {
        this.fogdevice = fd;
        this.taskRequest = req;
        this.container = container;
        this.inCloud = inCloud;
    }
}
