package at.ac.tuwien.infosys.database.impl;

import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.util.DeviceType;
import at.ac.tuwien.infosys.util.JSONHelper;
import at.ac.tuwien.infosys.util.RedisKeys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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

    public Set<Fogcontrolnode> getChildren(){
        String jsonString = redisService.getValue(RedisKeys.CHILDREN.getKey());
        Set<Fogcontrolnode> children = JSONHelper.toCollection(jsonString, HashSet.class, Fogcontrolnode.class);
        return children==null ? new HashSet<Fogcontrolnode>() : children;
    }

    public Location getLocation(){
        String jsonString = redisService.getValue(RedisKeys.LOCATION.getKey());
        Location loc = JSONHelper.toObject(jsonString, Location.class);
        return loc==null ? new Location() : loc;
    }

    public Set<Fogcontrolnode> getParents(){
        String jsonString = redisService.getValue(RedisKeys.PARENTS.getKey());
        Set<Fogcontrolnode> parents = JSONHelper.toCollection(jsonString, HashSet.class, Fogcontrolnode.class);
        return parents==null ? new HashSet<Fogcontrolnode>() : parents;
    }

    public Fogdevice getDeviceInformation(){
        String devId = getDeviceId();
        DeviceType devType = getDeviceType();
        String ip = getIp();
        int port = getPort();
        Location loc = getLocation();
        Fogdevice parent = getParent();
//        ArrayList<String> serviceTypes = getServiceTypes();

        return new Fogdevice(devId, devType, ip, port, loc, (Fogcell) parent);
    }

    public List<ServiceData> getServiceData(){
        String jsonString = redisService.getValue(RedisKeys.SERVICE_DATA.getKey());
        List<ServiceData> data = JSONHelper.toCollection(jsonString, List.class, ServiceData.class);
        return data==null ? new ArrayList<ServiceData>() : data;
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

    public void setChildren(Set<Fogcontrolnode> children){
        String jsonString = JSONHelper.toJsonString(children);
        redisService.setValue(RedisKeys.CHILDREN.getKey(), jsonString);
    }

    public void addChild(Fogcontrolnode child){
        Set<Fogcontrolnode> children = getChildren();
        if(children == null){
            children = new HashSet<Fogcontrolnode>();
        }
        children.add(child);
        setChildren(children);
    }

    public void removeChild(Fogcontrolnode child){
        Set<Fogcontrolnode> children = getChildren();
        if(children == null) return;
        children.remove(child);
        setChildren(children);
    }

    public void removeChildren(){
        setChildren(new HashSet<Fogcontrolnode>());
    }

    public void setLocation(Location loc){
        String jsonString = JSONHelper.toJsonString(loc);
        redisService.setValue(RedisKeys.LOCATION.getKey(), jsonString);
    }

    public void setParents(Set<Fogcontrolnode> parents){
        String jsonString = JSONHelper.toJsonString(parents);
        redisService.setValue(RedisKeys.PARENTS.getKey(), jsonString);
    }

    public void addParent(Fogcontrolnode parent){
        Set<Fogcontrolnode> parents = getParents();
        if(parents == null){
            parents = new HashSet<Fogcontrolnode>();
        }
        parents.add(parent);
        setParents(parents);
    }

    public void removeParent(Fogcontrolnode parent){
        Set<Fogcontrolnode> parents = getParents();
        if(parents == null) return;
        parents.remove(parent);
        setParents(parents);
    }

    public void removeParents(){
        setParents(new HashSet<Fogcontrolnode>());
    }

    public void setServiceData(List<ServiceData> data){
        String jsonString = JSONHelper.toJsonString(data);
        redisService.setValue(RedisKeys.SERVICE_DATA.getKey(), jsonString);
    }
}
