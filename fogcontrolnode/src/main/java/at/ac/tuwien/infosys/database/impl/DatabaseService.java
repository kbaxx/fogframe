package at.ac.tuwien.infosys.database.impl;

import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.model.Fogcell;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Location;
import at.ac.tuwien.infosys.model.Utilization;
import at.ac.tuwien.infosys.util.DeviceType;
import at.ac.tuwien.infosys.util.JSONHelper;
import at.ac.tuwien.infosys.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Service
@Slf4j
public class DatabaseService implements IDatabaseService {

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

    public DeviceType getDeviceType(){
        return DeviceType.valueOf(redisService.getValue(RedisKeys.DEVICE_TYPE.getKey()).toUpperCase());
    }

    public String getDeviceId(){
        return redisService.getValue(RedisKeys.DEVICE_ID.getKey());
    }

    public String getIp(){
        return redisService.getValue(RedisKeys.IP.getKey());
    }

    public int getPort(){
        return Integer.valueOf(redisService.getValue(RedisKeys.PORT.getKey()));
    }

    public Fogdevice getParent(){
        String jsonString = redisService.getValue(RedisKeys.PARENT.getKey());
        Fogdevice fc = JSONHelper.toObject(jsonString, Fogcell.class);
        return fc==null ? new Fogdevice() : fc;
    }

    public Set<Fogdevice> getChildren(){
        String jsonString = redisService.getValue(RedisKeys.CHILDREN.getKey());
        Set<Fogdevice> children = JSONHelper.toCollection(jsonString, HashSet.class, Fogdevice.class);
        return children==null ? new HashSet<Fogdevice>() : children;
    }

    public Location getLocation(){
        String jsonString = redisService.getValue(RedisKeys.LOCATION.getKey());
        Location loc = JSONHelper.toObject(jsonString, Location.class);
        return loc==null ? new Location() : loc;
    }

    public String getCloudIp(){
        return redisService.getValue(RedisKeys.CLOUD_IP.getKey());
    }

    public int getCloudPort(){
        return Integer.valueOf(redisService.getValue(RedisKeys.CLOUD_PORT.getKey()));
    }

    public ArrayList<String> getServiceTypes(){
        String jsonString = redisService.getValue(RedisKeys.SERVICE_TYPES.getKey());
        ArrayList<String> values = JSONHelper.toCollection(jsonString, ArrayList.class, String.class);
        return values==null ? new ArrayList<String>() : values;
    }

    public Fogdevice getDeviceInformation(){
        String devId = getDeviceId();
        DeviceType devType = getDeviceType();
        String ip = getIp();
        int port = getPort();
        Location loc = getLocation();
        Fogdevice parent = getParent();
        ArrayList<String> serviceTypes = getServiceTypes();

        return new Fogdevice(devId, devType, ip, port, loc, (Fogcell) parent, serviceTypes);
    }


    /**
     * SETTERS
     */

    public void setUtilization(Utilization utilization){
        String jsonString = JSONHelper.toJsonString(utilization);
        redisService.setValue(RedisKeys.UTILIZATION.getKey(), jsonString);
    }

    public void setDeviceType(DeviceType value){
        redisService.setValue(RedisKeys.DEVICE_TYPE.getKey(), value.getValue());
    }

    public void setDeviceId(String value){
        redisService.setValue(RedisKeys.DEVICE_ID.getKey(), value);
    }

    public void setPort(int value){
        redisService.setValue(RedisKeys.PORT.getKey(), String.valueOf(value));
    }

    public void setIp(String value){
        redisService.setValue(RedisKeys.IP.getKey(), value);
    }

    public void setParent(Fogdevice parent){
        String jsonString = JSONHelper.toJsonString(parent);
        redisService.setValue(RedisKeys.PARENT.getKey(), jsonString);
    }

    public void setChildren(Set<Fogdevice> children){
        String jsonString = JSONHelper.toJsonString(children);
        redisService.setValue(RedisKeys.CHILDREN.getKey(), jsonString);
    }

    public void addChild(Fogdevice child){
        Set<Fogdevice> children = getChildren();
//        log.info("Current children:\n"+children);
        if(children == null){
            children = new HashSet<Fogdevice>();
        }
        children.add(child);
//        log.info("New children:\n"+children);
        setChildren(children);
    }

    public void removeChild(Fogdevice child){
        Set<Fogdevice> children = getChildren();
        if(children == null) return;
        children.remove(child);
        setChildren(children);
    }

    public void removeChildren(){
        setChildren(new HashSet<Fogdevice>());
    }

    public void setLocation(Location loc){
        String jsonString = JSONHelper.toJsonString(loc);
        redisService.setValue(RedisKeys.LOCATION.getKey(), jsonString);
    }

    public void setCloudIp(String value){
        redisService.setValue(RedisKeys.CLOUD_IP.getKey(), value);
    }

    public void setCloudPort(int value){
        redisService.setValue(RedisKeys.CLOUD_PORT.getKey(), String.valueOf(value));
    }

    public void setServiceTypes(ArrayList<String> values){
        String jsonString = JSONHelper.toJsonString(values);
        redisService.setValue(RedisKeys.SERVICE_TYPES.getKey(), jsonString);
    }
}
