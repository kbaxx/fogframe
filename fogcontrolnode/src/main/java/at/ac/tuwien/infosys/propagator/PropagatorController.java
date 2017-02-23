package at.ac.tuwien.infosys.propagator;

import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.ServiceData;
import at.ac.tuwien.infosys.reasoner.IReasonerService;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.util.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 * Controller that enables the recursive propagation of service data and task requests by exposing a standardized
 * propagation API endpoint.
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class PropagatorController {

    @Autowired
    private IPropagatorService propagatorService;

    @Autowired
    private IDatabaseService dbService;

    @Autowired
    private IReasonerService reasonerService;

    /**
     * Propagates the received service data to upper device layers or initiates the resource provisioning, in case this
     * very device is the root device of the topology. If this is the root fcn the task requests are extracted of the
     * passed data and the resource provisioning and service placement is started.
     * @param o
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_PROPAGATE)
    public void propagate(@RequestBody List<ServiceData> o) {
        if(o.size() < 1) return;
        ServiceData s = o.get(0);

        // check if it needs to be propagated or not
        Fogdevice parent = dbService.getParent();
        if(parent.getType() != null && parent.getType().equals(DeviceType.CLOUD_FOG_MIDDLEWARE) && s != null && s.isReasoningPurpose()){
            // this fog control node is the root node and therefore needs to handle the task scheduling
            try {
                reasonerService.handleTaskRequests(s.getApplication());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
//            log.info("---- Propagating ----");
            propagatorService.propagate(o);
        }
    }
}
