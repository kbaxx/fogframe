package at.ac.tuwien.infosys.fogactioncontrol;

import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 28/10/2016.
 * Controller publishing the API endpoints to deploy and stop services on the respective device.
 */
@RestController
@CrossOrigin(origins = "*")
public class ServiceController {

    @Autowired
    private IFogActionControlService fogActionControlService;


    @PostConstruct
    public void init(){    }

    /**
     * Deploys a new container with the docker image according the passed service key
     * @param serviceKey service key of the docker image to deploy
     * @return the docker container if the service could be deployed, null otherwise
     */
    @RequestMapping(method = RequestMethod.POST, value= Constants.URL_SERVICE_DEPLOY+"{serviceKey}")
    public DockerContainer deploy(@PathVariable String serviceKey){
        return fogActionControlService.deploy(serviceKey);
    }

    /**
     * Stops all running docker containers that have been started by this very service
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_SERVICE_STOPALL)
    public void stopAll(){
        fogActionControlService.stopAllRunningDockerContainers();
    }

    /**
     * Stops the running containers by service key
     * @param serviceKey the service key of the containers to stop
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_SERVICE_STOP+"{serviceKey}")
    public void stopService(@PathVariable String serviceKey){
        fogActionControlService.stopContainerByService(serviceKey);
    }

    /**
     * Stops a running container with the passed docker id
     * @param dockerId id of the container to stop
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_SERVICE_STOPBYID+"{dockerId}")
    public void stopContainer(@PathVariable String dockerId){
        fogActionControlService.stopContainerById(dockerId);
    }

    /**
     * Returns all the started containers of this very service
     * @return list of docker containers
     */
    @RequestMapping(method = RequestMethod.GET, value=Constants.URL_SERVICE_GET_CREATED)
    public Set<DockerContainer> getCreatedContainers(){
        return fogActionControlService.getCreatedContainers();
    }

    /**
     * Stops all created containers
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_SERVICE_STOPCREATED)
    public void stopCreatedContainers(){
        for(DockerContainer c : getCreatedContainers()){
            fogActionControlService.stopContainerById(c.getContainerId());
        }
    }
}