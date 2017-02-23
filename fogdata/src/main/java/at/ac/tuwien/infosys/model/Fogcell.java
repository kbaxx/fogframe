package at.ac.tuwien.infosys.model;

import at.ac.tuwien.infosys.util.DeviceType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Data
public class Fogcell extends Fogdevice {

    private Utilization utilization;

    public Fogcell(){}

    public Fogcell(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent, String serviceType, Utilization utilization){
        super(id, type, ip, port, loc, parent, serviceType);
        this.utilization = utilization;
    }

    public Fogcell(String id, DeviceType type, String ip, int port, Location loc, Fogcell parent,
                   ArrayList<String> serviceTypes, Utilization utilization){
        super(id, type, ip, port, loc, parent, serviceTypes);
        this.utilization = utilization;
    }
}
