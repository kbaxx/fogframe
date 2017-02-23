package at.ac.tuwien.infosys.database;


import at.ac.tuwien.infosys.model.Utilization;
import at.ac.tuwien.infosys.util.JSONHelper;
import at.ac.tuwien.infosys.util.RedisKeys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Service
public class DatabaseService {

    @Autowired
    private RedisService redisService;

    public void delete(String key){
        redisService.deleteKey(key);
    }

    public Set<String> getKeys(){
        return redisService.getKeys();
    }

    public String getValue(String key){
        return redisService.getValue(key);
    }

    public void setValue(String key, String value){
        redisService.setValue(key, value);
    }




    public Map<String, String> getAll(){
        return redisService.getAll();
    }

    public Utilization getUtilization(){
        String jsonString = redisService.getValue(RedisKeys.UTILIZATION.getKey());
        Utilization u = JSONHelper.toObject(jsonString, Utilization.class);
        return u==null ? new Utilization() : u;
    }

    /**
     * SETTERS
     */

    public void setUtilization(Utilization utilization){
        String jsonString = JSONHelper.toJsonString(utilization);
        redisService.setValue(RedisKeys.UTILIZATION.getKey(), jsonString);
    }
}
