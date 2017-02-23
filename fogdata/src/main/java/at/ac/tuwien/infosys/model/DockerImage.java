package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by Kevin Bachmann on 08/12/2016.
 */
@Data
public class DockerImage implements Serializable {
    private String name;
    private String serviceKey;
    private String dockerfile;
    private String volumes;
    private String[] exposedPorts;
    private boolean privileged;

    public DockerImage() { }

    public DockerImage(String serviceKey, String dockerfile){
        this.name = serviceKey.replace("/","-");
        this.serviceKey = serviceKey;
        this.dockerfile = dockerfile;
        this.volumes = "";
        this.exposedPorts = new String[]{"8100"};
        this.privileged = false;
    }

    public DockerImage(String serviceKey, String[] exposedPorts){
        this.name = serviceKey.replace("/","-");
        this.serviceKey = serviceKey;
        this.dockerfile = "";
        this.volumes = "";
        this.exposedPorts = exposedPorts;
        this.privileged = false;
    }

    public DockerImage(String serviceKey, String dockerfile, String volumes, boolean privileged){
        this.name = serviceKey.replace("/","-");
        this.serviceKey = serviceKey;
        this.dockerfile = dockerfile;
        this.volumes = volumes;
        this.exposedPorts = new String[]{"8100"};
        this.privileged = privileged;
    }

    public DockerImage(String serviceKey, String dockerfile, String volumes, boolean privileged, String[]exposedPorts){
        this.name = serviceKey.replace("/","-");
        this.serviceKey = serviceKey;
        this.dockerfile = dockerfile;
        this.volumes = volumes;
        this.exposedPorts = exposedPorts;
        this.privileged = privileged;
    }
}
