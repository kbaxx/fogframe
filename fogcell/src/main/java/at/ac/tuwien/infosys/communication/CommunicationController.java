package at.ac.tuwien.infosys.communication;

import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;

/**
 * Created by Kevin Bachmann on 03/11/2016.
 */
@RestController
@CrossOrigin(origins = "*")
public class CommunicationController {

    @Autowired
    private ICommunicationService communicationService;

    @PostConstruct
    public void init(){ }

    @RequestMapping(method = RequestMethod.GET, value="/comm/sendPairRequest")
    public Message test_sendPairRequest(){
        return communicationService.sendPairRequest();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_PING)
    public Message ping(){
        return communicationService.ping();
    }

    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_MANUAL_PAIR_REQUEST)
    public Message manualPairing(@RequestBody Fogdevice fd){
        return communicationService.sendManualPairRequest(fd);
    }
}
