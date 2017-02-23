package at.ac.tuwien.infosys.model;

import at.ac.tuwien.infosys.util.DeviceType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Data
@EqualsAndHashCode(exclude={"parent", "type", "location", "serviceTypes"})
public class Fogdevice implements Serializable, Comparable<Fogdevice> {

    private String id;
    private DeviceType type;
    private String ip;
    private int port;
    private Location location;
    private Fogcell parent;
    private ArrayList<String> serviceTypes = new ArrayList<String>();

    public Fogdevice(){}

    public Fogdevice(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent, String serviceType){
        this.id = id;
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.location = loc;
        this.parent = parent;
        this.serviceTypes.add(serviceType);
    }

    public Fogdevice(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent, ArrayList<String> serviceTypes){
        this.id = id;
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.location = loc;
        this.parent = parent;
        this.serviceTypes = serviceTypes;
    }

    public Fogdevice(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent){
        this.id = id;
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.location = loc;
        this.parent = parent;
    }

    @Override
    public int compareTo(Fogdevice o) {
        Collections.sort(o.getServiceTypes()); Collections.sort(this.serviceTypes);
        String firstElem = this.serviceTypes.get(0);
        String firstElemOther = o.getServiceTypes().get(0);
        return firstElem.compareTo(firstElemOther);
    }

    @Override
    public String toString() {
        return "Fogdevice{" +
                "type=" + type +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                ", location=" + location +
                ", parent=" + (parent == null ? "" : ("{type="+parent.getType()+", ip="+parent.getIp()+", "+parent.getPort()) )+
                ", serviceTypes=" + serviceTypes +
                '}';
    }
}
