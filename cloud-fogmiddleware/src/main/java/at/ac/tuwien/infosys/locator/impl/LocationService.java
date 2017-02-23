package at.ac.tuwien.infosys.locator.impl;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.locator.ILocationService;
import at.ac.tuwien.infosys.model.Fogcontrolnode;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Location;
import at.ac.tuwien.infosys.model.LocationRange;
import at.ac.tuwien.infosys.util.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 */
@Service
@Slf4j
public class LocationService implements ILocationService {

    @Autowired
    private IDatabaseService dbService;

    @Autowired
    private ICommunicationService commService;

    public Fogdevice getResponsibleParent(long latitude, long longitude){
        Set<Fogcontrolnode> possibleParents = new HashSet<Fogcontrolnode>();

//        Set<Fogcontrolnode> parents = dbService.getParents(); // static parents
        // not just get the children of the cloud but as well all the fog control nodes
        Set<Fogcontrolnode> parents = new HashSet<Fogcontrolnode>();
        Set<Fogcontrolnode> directChildren = dbService.getChildren();
        for(Fogcontrolnode directChild : directChildren){
            if(directChild.getType().equals(DeviceType.FOG_CONTROL_NODE)){
                parents.add(directChild);
                // send get children request
                Set<Fogdevice> childChildren = commService.getChildrenOfChild(directChild);
                for(Fogdevice fd: childChildren){
                    if(fd.getType().equals(DeviceType.FOG_CONTROL_NODE)){
                        // convert fd to fcn and get location of them
                        LocationRange lr = commService.getLocationRange(fd);
                        Fogcontrolnode fcn = new Fogcontrolnode(fd.getId(), fd.getType(), fd.getIp(), fd.getPort(), fd.getLocation(),
                                fd.getParent(), fd.getServiceTypes(), lr);
                        parents.add(fcn);
                    }
                }
            }
        }

        for(Fogcontrolnode f : parents){
            if((f.getLocation().getLatitude() != latitude || f.getLocation().getLongitude() != longitude) &&
                    f.getLocationRange().isInside(new Location(latitude, longitude))){
                // check that it is not the caller itself and it fits into the location range
                possibleParents.add(f);
            }
        }
        if(possibleParents.size()==1) return (Fogdevice) possibleParents.toArray()[0];
        if(possibleParents.size()>1){
            return findClosestParent(latitude, longitude, possibleParents);
        }
        // if no matching parent is found the cloud-fog middleware is returned
        Fogdevice cfm = new Fogdevice(dbService.getDeviceId(), DeviceType.CLOUD_FOG_MIDDLEWARE, dbService.getIp(),
                dbService.getPort(), dbService.getLocation(), null);
        return cfm;
    }

    public Fogdevice findClosestParent(long latitude, long longitude, Set<Fogcontrolnode> parents){
        double distance = Integer.MAX_VALUE;
        Fogcontrolnode parent = null;

        for(Fogcontrolnode f : parents){
            long tempLat = f.getLocation().getLatitude();
            long tempLong = f.getLocation().getLongitude();
            // calculate the distance between the locations by calculating the sqrt(x^2+y^2)
            double tempDistance = Math.hypot((double)(tempLong-longitude), (double)(tempLat-latitude));
            if(tempDistance < distance) {
                distance = tempDistance;
                parent = f;
            }
        }
        return parent;
    }
}
