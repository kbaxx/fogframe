package at.ac.tuwien.infosys.sharedstorage.impl;

import at.ac.tuwien.infosys.model.exception.DBException;
import at.ac.tuwien.infosys.util.PropertyService;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 * Concrete Redis database service enabling the communication with a running Redis database.
 */
@Service
@Slf4j
public class SharedRedisService {

    @Autowired
    private PropertyService propertyService;

    private StatefulRedisConnection<String, String> connection;
    private RedisClient client;

    @Value("${fog.redis.shared.port}")
    private int port;

    /**
     * Starts a redis server on construction of this bean.
     * @throws IOException if something happens with the port or the creation of the server
     */
    @PostConstruct
    private void startRedis() throws IOException, DBException {
        String redisHost = propertyService.getIp();
        String hostString = "redis://"+redisHost+":"+port;
        this.client = RedisClient.create(hostString);
        this.connection = client.connect();
    }

    /**
     * Stops the redis server when this bean is destroyed.
     */
    @PreDestroy
    private void stopRedis() {
        connection.close();
        this.client.shutdown();
    }


    /**
     * #########################################################################################################
     * #########################################################################################################
     */


    /**
     * Retrieves a value from the db according to a passed key
     * @param key key of the key/value pair
     * @return the value of the passed key
     */
    public String getValue(String key){
        RedisCommands<String, String> sync = connection.sync();
        return sync.get(key);
    }

    /**
     * Returns all stored keys of the Redis db
     * @return set with string keys
     */
    public Set<String> getKeys(){
        RedisCommands<String, String> sync = connection.sync();
        return new HashSet<String>(sync.keys("*"));
    }

    /**
     * Returns all key/value pair stored in the db
     * @return key/value map
     */
    public Map<String, String> getAll(){
        RedisCommands<String, String> sync = connection.sync();
        Set<String> keys = getKeys();
        Map<String, String> map = new HashMap<String, String>();
        for(String key : keys){
            map.put(key, sync.get(key));
        }
        return map;
    }

    /**
     * Creates a new key/value pair in the database
     * @param key key of the pair
     * @param value value of the pair
     */
    public void setValue(String key, String value){
        RedisCommands<String, String> sync = connection.sync();
        sync.set(key, value);
    }

    /**
     * Removes a key/value pair according to the passed key
     * @param key key of the key/value pair to remove
     */
    public void deleteKey(String key){
        RedisCommands<String, String> sync = connection.sync();
        sync.del(key);
    }
}
