package at.ac.tuwien.infosys.model;

import at.ac.tuwien.infosys.util.DeviceType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Data
public class Fogcontrolnode extends Fogcell{

    private Set<Fogcell> children = new HashSet<Fogcell>();
    private LocationRange locationRange;

    public Fogcontrolnode(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent,
                          String serviceType, Utilization utilization, Set<Fogcell> children,
                          LocationRange locationRange) {
        super(id, type, ip, port, loc, parent, serviceType, utilization);
        this.children = children;
        this.locationRange = locationRange;
    }

    public Fogcontrolnode(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent,
                          String serviceType, Utilization utilization, LocationRange locationRange) {
        super(id, type, ip, port, loc, parent, serviceType, utilization);
        this.locationRange = locationRange;
    }

    public Fogcontrolnode(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent,
                          ArrayList<String> serviceTypes, LocationRange locationRange) {
        super(id, type, ip, port, loc, parent, serviceTypes, new Utilization());
        this.locationRange = locationRange;
    }

    public Fogcontrolnode(){}

    public void addChild(Fogcell fogcell){
        children.add(fogcell);
    }
}
