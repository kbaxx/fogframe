package at.ac.tuwien.infosys.util;

import at.ac.tuwien.infosys.database.impl.DatabaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
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

    @Value("${fog.device.type}")
    private String deviceType;

    @Value("${fog.device.ip}")
    private String ip;

    @Value("${server.port}")
    private int port;


    @PostConstruct
    public void init(){}

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        // save the basic stuff into the embedded local database
        String id = dbService.getDeviceId();
        if(id == null || id.isEmpty()) dbService.setDeviceId(UUID.randomUUID().toString());
        dbService.setDeviceType(DeviceType.valueOf(deviceType));
        dbService.setIp(ip);
        dbService.setPort(port);

        dbService.removeChildren();
    }
}
