package at.ac.tuwien.infosys.database;


import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Utilization;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.util.DeviceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 * Controller to retrieve and persist information in the local database according to selected REST endpoints
 */
@RestController
@CrossOrigin(origins = "*")
public class DatabaseController {

    @Autowired
    private IDatabaseService dbService;

    @PostConstruct
    public void init(){    }


    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_GETALL)
    public Map<String, String> getAll(){
        return dbService.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_DEVICETYPE)
    public DeviceType getDeviceType(){
        return dbService.getDeviceType();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_DEVICEID)
    public String getDeviceId(){
        return dbService.getDeviceId();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_IP)
    public String getIp(){
        return dbService.getIp();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_PARENT)
    public Fogdevice getParent(){
        return dbService.getParent();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_CHILDREN)
    public Set<Fogdevice> getChildren(){
        return dbService.getChildren();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_DB_UTILIZATION)
    public Utilization getUtilization() {
        return dbService.getUtilization();
    }


    /**
     * SETTERS
     */

    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_DB_UTILIZATION+"{value}")
    public void setUtilization(@PathVariable Utilization value){
        dbService.setUtilization(value);
    }

    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_DB_IP+"{value}")
    public void setIp(@PathVariable String value){
        dbService.setIp(value);
    }

    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_DB_PARENT)
    public void setParent(@RequestBody Fogdevice parent){
        dbService.setParent(parent);
    }

    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_DB_CHILDREN)
    public void setChildren(@RequestBody Set<Fogdevice> children){
        dbService.setChildren(children);
    }

    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_DB_CHILD)
    public void addChild(@RequestBody Fogdevice child){
        dbService.addChild(child);
    }

    @RequestMapping(method = RequestMethod.DELETE, value=Constants.URL_DB_CHILD)
    public void removeChild(@RequestBody Fogdevice child){
        dbService.removeChild(child);
    }
}