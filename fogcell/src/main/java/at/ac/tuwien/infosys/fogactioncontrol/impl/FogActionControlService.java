package at.ac.tuwien.infosys.fogactioncontrol.impl;

import at.ac.tuwien.infosys.fogactioncontrol.IContainerService;
import at.ac.tuwien.infosys.fogactioncontrol.IFogActionControlService;
import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.model.DockerImage;
import com.spotify.docker.client.messages.Container;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 28/10/2016.
 */
@Service
@Slf4j
public class FogActionControlService implements IFogActionControlService {

    @Autowired
    private IContainerService dockerService;

    private Set<DockerContainer> createdContainers = new HashSet<DockerContainer>();

    @Value("${fog.docker.startup.stop}")
    private boolean dockerStartupStop;

    @PostConstruct
    public void init(){
//        loadRunningServices();
        if(dockerStartupStop)
            stopRunningContainers();
    }

    @PreDestroy
    public void destruct(){
        stopCreatedContainers();
    }

    public DockerContainer deploy(String serviceKey){
        log.info("--- Deploy Service with serviceKey "+serviceKey+" ---");
        DockerContainer container = dockerService.startContainer(serviceKey);
        if(container != null && container.getContainerId() != null){
            createdContainers.add(container);
            log.info("- Successfully created container with ID :"+container.getContainerId() + " -");
            log.info(container.toString());
        }
        return container;
    }

    private void stopRunningContainers(){
        List<Container> containers = dockerService.getRunningContainers();
        for(Container c : containers){
            String name = c.names().get(0).split("/")[1];
            if(name != null && !(name.contains("fogcell") || name.contains("fogcontrolnode") ||
                    name.contains("redis") )){
                dockerService.stopContainer(c.id());
            }
        }
    }

    public void stopAllRunningDockerContainers(){
        dockerService.stopRunningContainers();
    }

    public void stopContainerByService(String serviceKey){
        dockerService.stopContainerByService(serviceKey);
    }

    public void stopContainerById(String dockerId){
        dockerService.stopContainerById(dockerId);
        removeContainerById(dockerId);
    }

    private void removeContainerById(String containerId){
        Iterator<DockerContainer> iterator = createdContainers.iterator();
        while(iterator.hasNext()){
            DockerContainer c = iterator.next();
            if(c.getContainerId().equals(containerId)){
                iterator.remove();
            }
        }
    }

    private void stopCreatedContainers(){
        for(DockerContainer c : createdContainers){
            dockerService.stopContainerById(c.getContainerId());
        }
    }

    public void loadRunningServices(){
        List<Container> containers = dockerService.getRunningContainers();
        for(Container c : containers){
            String name = c.names().get(0).split("/")[1];
            if(name != null && !(name.contains("fogcell") || name.contains("fogcontrolnode") ||
                    name.contains("redis") )){
                String ports = c.portsAsString();
                int port = Integer.valueOf(ports.split(":")[1].substring(0, 4));
                DockerImage img = new DockerImage(name, "", "", false, new String[]{String.valueOf(port)});
                DockerContainer container = new DockerContainer(c.id(), name, port, name, img);
                createdContainers.add(container);
            }
        }
    }

    public List<Container> getRunningServices(){
        dockerService.getRunningContainers();
        return null;
    }

    public Set<DockerContainer> getCreatedContainers(){
        return this.createdContainers;
    }
}
