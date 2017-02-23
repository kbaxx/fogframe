package at.ac.tuwien.infosys.computeunit;

import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.ServiceData;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Kevin Bachmann on 25/11/2016.
 * Controller to handle the received service data from deployed service instances of the according device.
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ComputeUnitController {

    @Autowired
    private IComputeUnitService computeUnitService;

    @Autowired
    private IDatabaseService dbService;

    /**
     * Receives, handles, and propagates the data coming from running services.
     * @param data service data list with data readings to propagate or process
     */
    @RequestMapping(method = RequestMethod.POST, value= Constants.URL_COMPUNIT_SERVICEDATA)
    public void serviceData(@RequestBody List<HashMap> data){
        log.info("Received data from service");
        Fogdevice dev = dbService.getDeviceInformation();
        if(dev == null){
            log.info("No device information found in database");
            return;
        }
        List<ServiceData> serviceData = new ArrayList<ServiceData>();
        for(HashMap m : data){
            ServiceData sd = new ServiceData();
            sd.setKey((String) m.get("key"));
            sd.setData((List<HashMap>) m.get("data"));
            sd.setSender(dev);
            sd.setReasoningPurpose(false);
            serviceData.add(sd);
            log.info(sd.toString());
        }
        computeUnitService.serviceData(serviceData);
    }
}
