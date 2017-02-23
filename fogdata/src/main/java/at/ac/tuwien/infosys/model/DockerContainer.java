package at.ac.tuwien.infosys.model;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by Kevin Bachmann on 28/10/2016.
 */
@Data
public class DockerContainer implements Serializable {

    private String containerId;
    private String serviceKey;
    private int port;
    private String status;
    private String createdAt;
    private String name;
    private DockerImage image;

    public DockerContainer() {
    }

    public DockerContainer(String containerId, String serviceKey, int port, String name, DockerImage image){
        this.containerId = containerId;
        this.serviceKey = serviceKey;
        this.port = port;
        this.status = "running";
        this.createdAt = new Date().toString();
        this.name = name;
        this.image = image;
    }

    @Override
    public String toString() {
        return "DockerContainer{" +
                "containerId='" + containerId + '\'' +
                ", serviceKey='" + serviceKey + '\'' +
                ", port=" + port +
                ", createdAt='" + createdAt + '\'' +
                '}';
    }
}
