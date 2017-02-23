package at.ac.tuwien.infosys.sharedstorage;


import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.model.DockerImage;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.reasoner.IReasonerService;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.util.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class SharedDatabaseController {

    @Autowired
    private ISharedDatabaseService dbService;

    @Autowired
    private IReasonerService reasonerService;

    @Autowired
    private ICommunicationService communicationService;


    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_SHARED_GETALL)
    public Map<String, String> getAll(){
        return dbService.getAll();
    }

    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_SHARED_GETIMAGE+"{serviceKey}")
    public DockerImage getServiceImage(@PathVariable String serviceKey){
        return dbService.getServiceImage(serviceKey);
    }


    /**
     * SETTERS
     */

    /**
     * Registers a service image at the shared database and distributes/sends it to all children shared databases in the
     * topology
     * @param image image to save
     * @param response http response to set the status code 201
     * @return message indicating the success
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_SHARED_REGISTERIMAGE)
    public Message registerServiceImage(@RequestBody DockerImage image, HttpServletResponse response){
        if(image.getName() == null || image.getName().isEmpty()){
            image.setName(image.getServiceKey().replace("/","-"));
        }
        boolean status = dbService.setServiceImage(image);
        if (status) {
            response.setStatus(HttpServletResponse.SC_CREATED);

            if(reasonerService.isRootFCN()){
                // send the docker image to all child fog control nodes
                Set<Fogdevice> children = reasonerService.getTopologyChildren();
                for (Fogdevice child : children) {
                    if (child.getType().equals(DeviceType.FOG_CONTROL_NODE)) {
                        communicationService.distributeDockerImage(child, image);
                    }
                }
                log.info("Successfully saved and distributed the docker image with key=" + image.getServiceKey());
            } else {
                log.info("Successfully saved the docker image with key=" + image.getServiceKey());
            }
        } else {
            log.warn("Docker image with key="+image.getServiceKey()+" already exists.");
        }
        return new Message(Constants.URL_SHARED_REGISTERIMAGE, status);
    }
}