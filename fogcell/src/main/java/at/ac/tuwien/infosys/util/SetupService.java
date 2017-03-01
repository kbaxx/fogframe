package at.ac.tuwien.infosys.util;

import at.ac.tuwien.infosys.communication.impl.CommunicationService;
import at.ac.tuwien.infosys.database.impl.DatabaseService;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Location;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.model.Utilization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Kevin Bachmann on 03/11/2016.
 * Service to setup the basic features of a component, e.g., basic database content, etc.
 */
@Service
@Slf4j
public class SetupService implements ApplicationRunner {

    @Autowired
    private DatabaseService dbService;

    @Autowired
    private PropertyService propertyService;

    @Autowired
    private CommunicationService commService;

    @Value("${server.port}")
    private int port;

    @Value("${fog.device.type}")
    private String deviceType;

    @Value("${fog.docker}")
    private boolean DOCKER;


    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        // save the basic stuff into the embedded local database
        dbService.setUtilization(new Utilization(0,0,0));
        String id = dbService.getDeviceId();
        if(id == null || id.isEmpty()) dbService.setDeviceId(UUID.randomUUID().toString());
        dbService.setDeviceType(DeviceType.valueOf(deviceType));
        dbService.setIp(propertyService.getIp());
        dbService.setPort(port);
        dbService.setCloudIp(propertyService.getCloudIp());
        dbService.setCloudPort(propertyService.getCloudPort());
        dbService.setServiceTypes(new ArrayList<String>(propertyService.getServiceTypes()));

        Location location = new Location(propertyService.getLatitude(), propertyService.getLongitude());
        dbService.setLocation(location);

        Fogdevice parent = null;
        Fogdevice fallbackParent = null;
        Fogdevice fallbackGrandparent = null;
        boolean cloudFailed = false;
        try {
            parent = commService.requestParent(location);
        } catch(Exception e){
            log.warn("Cloud is not reachable.");
            cloudFailed = true;
        }
        if(parent == null || (parent.getIp().equals(propertyService.getIp()) && parent.getPort()==port)){
            // if the cloud-fog middleware does not answer a standard parent from the application file is used
            int parentPort = propertyService.getParentPort();
            fallbackParent = new Fogdevice("parentId", Utils.getDeviceTypeFromPort(parentPort),
                    propertyService.getParentIp(), parentPort, new Location(), null);
            parent = fallbackParent;
        }
        dbService.setParent(parent);

        // pair with parent
        Message m = null;
        try {
            m = commService.sendPairRequest();
        } catch(Exception e){
            log.warn("Parent is not reachable.");
        }
        if(m == null) {
            log.info("---- FALLBACK TO GRAND PARENT ----");
            // could not pair with parent -> pair with fallback parent (cloud-fog middleware)
            if(cloudFailed){
                dbService.setParent(fallbackParent);
            } else {
                int grandParentPort = propertyService.getGrandParentPort();
                fallbackGrandparent = new Fogdevice("grandparentId", Utils.getDeviceTypeFromPort(grandParentPort),
                        propertyService.getGrandParentIp(), grandParentPort, new Location(), null);
                dbService.setParent(fallbackGrandparent);
            }
            try {
                commService.sendPairRequest();
            } catch(Exception e){
                log.warn("Fallback Parent is not reachable.");
            }
        }
    }
}
