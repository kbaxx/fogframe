package at.ac.tuwien.infosys.util;

import at.ac.tuwien.infosys.communication.impl.CommunicationService;
import at.ac.tuwien.infosys.database.impl.DatabaseService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.sharedstorage.impl.SharedDatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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
    private SharedDatabaseService sharedDb;

    @Autowired
    private CommunicationService commService;

    @Autowired
    private PropertyService propertyService;

    @Value("${server.port}")
    private int port;

    @Value("${fog.device.type}")
    private String deviceType;

    @Value("${fog.docker}")
    private boolean DOCKER;

    @PostConstruct
    public void init(){}

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        dbService.removeChildren();

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

        String testDockerfile =
                "FROM sander85/rpi-busybox\n" +
                "CMD [\"sh\", \"-c\", \"while :; do echo i am busy; sleep 1 ; done\"]";
        DockerImage img1 = new DockerImage(Constants.IMG_BUSYBOX_KEY, testDockerfile);
        sharedDb.setServiceImage(img1);

        String temp_hum_service =
                "FROM jonasbonno/rpi-grovepi\n" +
                "RUN pip install requests\n" +
                "RUN git clone https://github.com/keyban/fogservice.git #update\n" +
                "ENTRYPOINT [\"python\"]\n" +
                "CMD [\"fogservice/service.py\"]";
        DockerImage img2 = new DockerImage(Constants.IMG_TEMPHUM_KEY, temp_hum_service, "/dev/i2c-1:/dev/i2c-1", true);
        sharedDb.setServiceImage(img2);


        Fogdevice parent = null;
        try {
            parent = commService.requestParent(location);
        } catch(Exception e){
            log.warn("Cloud is not reachable.");
        }
        Fogdevice fallbackParent = new Fogdevice("parentId", DeviceType.CLOUD_FOG_MIDDLEWARE, propertyService.getParentIp(),
                propertyService.getParentPort(), new Location(), null);
        if(parent == null || (parent.getIp().equals(propertyService.getIp()) && parent.getPort()==port)){
            // if the cloud-fog middleware does not answer a standard parent from the application file is used
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
            log.info("---- FALLBACK TO CLOUD-FOG MIDDLEWARE PARENT ----");
            // could not pair with parent -> pair with fallback parent (cloud-fog middleware)
            dbService.setParent(fallbackParent);
            try {
                commService.sendPairRequest();
            } catch(Exception e){
                log.warn("Fallback Parent is not reachable.");
            }
        }

//        dbService.removeChildren();
//        Fogdevice child = new Fogdevice("child1", DeviceType.FOG_CELL, "128.131.172.118", 8081, null);
//        dbService.addChild(child);
    }
}
