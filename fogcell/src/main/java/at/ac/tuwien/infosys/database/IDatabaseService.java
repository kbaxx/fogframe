package at.ac.tuwien.infosys.database;

import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Location;
import at.ac.tuwien.infosys.model.Utilization;
import at.ac.tuwien.infosys.util.DeviceType;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 * Database service to retrieve and persist information in the local database
 */
public interface IDatabaseService {
    /**
     * GENERAL DB METHODS
     * The following three methods allow the user to delete, set, get values from the local database
     */

    void delete(String key);
    String getValue(String key);
    void setValue(String key, String value);

    /**
     * Returns all the available keys of the redis database
     * @return set with string keys
     */
    Set<String> getKeys();

    /**
     * GETTER
     * The following getter methods return the specified resources from the local running database
     */

    /**
     * Get all key/value pairs stored in the db
     * @return key/value map
     */
    Map<String, String> getAll();
    Utilization getUtilization();
    DeviceType getDeviceType();
    String getDeviceId();
    String getIp();
    int getPort();
    Fogdevice getParent();
    Set<Fogdevice> getChildren();
    Location getLocation();
    String getCloudIp();
    int getCloudPort();
    ArrayList<String> getServiceTypes();
    Fogdevice getDeviceInformation();

    /**
     * SETTER
     * The following setter methods define or override the specified resources to the local running database
     */

    void setUtilization(Utilization utilization);
    void setDeviceType(DeviceType value);
    void setDeviceId(String value);
    void setPort(int value);
    void setIp(String value);
    void setParent(Fogdevice parent);
    void setChildren(Set<Fogdevice> children);
    void addChild(Fogdevice child);
    void removeChild(Fogdevice child);
    void removeChildren();
    void setLocation(Location loc);
    void setCloudIp(String value);
    void setCloudPort(int value);
    void setServiceTypes(ArrayList<String> values);
}