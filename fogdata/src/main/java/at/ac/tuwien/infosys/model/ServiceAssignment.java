package at.ac.tuwien.infosys.model;

import lombok.Data;

/**
 * Created by Kevin Bachmann on 14/12/2016.
 */
@Data
public class ServiceAssignment {
    private DockerHost host;
    private DockerImage image;
    private DockerContainer container;

    public ServiceAssignment() {}

    public ServiceAssignment(DockerHost host, DockerImage image, DockerContainer container) {
        this.host = host;
        this.image = image;
        this.container = container;
    }
}
