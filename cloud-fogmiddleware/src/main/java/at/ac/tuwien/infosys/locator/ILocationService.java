package at.ac.tuwien.infosys.locator;

import at.ac.tuwien.infosys.model.Fogcontrolnode;
import at.ac.tuwien.infosys.model.Fogdevice;

import java.util.Set;

/**
 * Created by Kevin Bachmann on 24/11/2016.
 * Service to assess the responsible parent according to the location and the location range of the fog control nodes
 */
public interface ILocationService {
    /**
     * Returns the responsible parent according to the passed location and the location ranges of the fog control nodes
     * in the whole topology.
     * @param latitude location latitude
     * @param longitude location longitude
     * @return responsible parent
     */
    Fogdevice getResponsibleParent(long latitude, long longitude);

    /**
     * Returns the closest parent according to the location of both devices
     * @param latitude location latitude of the child
     * @param longitude location longitude of the child
     * @param parents set of parents to look for the closest one
     * @return returns the closes parent
     */
    Fogdevice findClosestParent(long latitude, long longitude, Set<Fogcontrolnode> parents);
}
