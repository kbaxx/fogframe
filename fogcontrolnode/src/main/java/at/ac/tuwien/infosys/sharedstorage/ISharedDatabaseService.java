package at.ac.tuwien.infosys.sharedstorage;

import at.ac.tuwien.infosys.model.DockerImage;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 * Database service to retrieve and persist information in the shared database
 */
public interface ISharedDatabaseService {
    /**
     * GENERAL DB METHODS
     * The following three methods allow the user to delete, set, get values from the shared database
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
     * The following getter methods return the specified resources from the shared running database
     */

    /**
     * Get all key/value pairs stored in the db
     * @return key/value map
     */
    Map<String, String> getAll();

    /**
     * Returns the docker image with the unique passed service key
     * @param serviceKey unique service key of the docker image
     * @return docker image with the according key
     */
    DockerImage getServiceImage(String serviceKey);

    /**
     * Sets the passed docker image with the including key. If the service key already exists, false is returned and the
     * image is not persisted.
     * @param image image to persist
     * @return true if it is persisted, false if the service key is already used
     */
    boolean setServiceImage(DockerImage image);
}