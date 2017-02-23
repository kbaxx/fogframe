package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by Kevin Bachmann on 20/11/2016.
 * Controller enabling the REST communication between different device types by exposing the following specified
 * REST endpoints to pair, ping, and request other information.
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
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
}
