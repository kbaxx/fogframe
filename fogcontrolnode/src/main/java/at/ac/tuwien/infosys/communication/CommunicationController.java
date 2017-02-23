package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.LocationRange;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.model.Utilization;
import at.ac.tuwien.infosys.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * Created by Kevin Bachmann on 03/11/2016.
 * Controller enabling the REST communication between different device types by exposing the following specified
 * REST endpoints to pair, ping, and request other information.
 */
@RestController
@CrossOrigin(origins = "*")
public class CommunicationController {

    @Autowired
    private ICommunicationService commService;

    /**
     * Sends a pair request to the specified parent stored in the database.
     * @return a message object with a status that indicates whether it was successful or not
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_PAIR_REQUEST)
    public Message pairRequest(@RequestBody Fogdevice fogdevice){
        return commService.pair(fogdevice);
    }

    /**
     * Answers a ping request with a message
     * @return returns a message object with "ping" in the header
     */
    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_PING)
    public Message ping(){
        return commService.ping();
    }

    /**
     * Request the utilization of the connected children.
     * @return utilization set consisting of utilization objects
     */
    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_CHILDREN_UTIL)
    public Set<Utilization> getChildrenUtilization(){
        return commService.getChildrenUtilization();
    }

    /**
     * Returns the location range specified by in the property file
     * @return location range object with upper and lower range
     */
    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_LOCATION_RANGE)
    public LocationRange getLocationRange(){
        return commService.getLocationRange();
    }

    /**
     * Sends a manual pair request to the passed fog device fd.
     * @param fd device to pair to
     * @return a message object with a status that indicates whether it was successful or not
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_MANUAL_PAIR_REQUEST)
    public Message manualPairing(@RequestBody Fogdevice fd){
        return commService.sendManualPairRequest(fd);
    }
}
