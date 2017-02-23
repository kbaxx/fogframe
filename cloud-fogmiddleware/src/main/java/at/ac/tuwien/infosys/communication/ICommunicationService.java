package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.LocationRange;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.model.ServiceData;

import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 03/11/2016.
 * Service enabling the communication between the different components/devices.
 */
public interface ICommunicationService {
    /**
     * Pair request endpoint method that adds the passed fog device to the connected children
     * @param fd new paired child
     * @return message indicating whether the pair was successful or not
     */
    Message pair(Fogdevice fd);

    /**
     * Saves the persisted data in a cloud db
     * @param data data to persist
     */
    void saveServiceData(List<ServiceData> data);

    /**
     * Requests the location range of a passed fog device for location assessment reasons
     * @param fd fog device to send the request to
     * @return location range of the device
     */
    LocationRange getLocationRange(Fogdevice fd);

    /**
     * Requests the children of the passed child fog device fd.
     * @param fd fog device whos children are requested
     * @return set of children fog devices of the passed fog device
     */
    Set<Fogdevice> getChildrenOfChild(Fogdevice fd);
}
