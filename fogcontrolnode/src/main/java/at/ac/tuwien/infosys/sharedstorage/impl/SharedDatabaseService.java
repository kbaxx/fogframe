package at.ac.tuwien.infosys.sharedstorage.impl;

import at.ac.tuwien.infosys.model.DockerImage;
import at.ac.tuwien.infosys.sharedstorage.ISharedDatabaseService;
import at.ac.tuwien.infosys.util.JSONHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 11/11/2016.
 */
@Service
@Slf4j
public class SharedDatabaseService implements ISharedDatabaseService {

    @Autowired
    private SharedRedisService redisService;

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

    public DockerImage getServiceImage(String serviceKey){
        String jsonString = redisService.getValue(serviceKey);
        DockerImage img = JSONHelper.toObject(jsonString, DockerImage.class);
        return img==null ? null: img;
//        return redisService.getValue(serviceKey);
    }

    public boolean setServiceImage(DockerImage image){
        String jsonString = JSONHelper.toJsonString(image);
        if(getServiceImage(image.getServiceKey()) != null) return false;
        redisService.setValue(image.getServiceKey(), jsonString);
        return true;
    }
}
