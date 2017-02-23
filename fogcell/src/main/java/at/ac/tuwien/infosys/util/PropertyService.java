package at.ac.tuwien.infosys.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 29/11/2016.
 * Service to read properties from the additional "main.properties" file
 */
@Service
@Slf4j
public class PropertyService {

    @Value("${fog.docker}")
    private boolean DOCKER;

    private String cloudIp;
    private int cloudPort;
    private String ip;
    private String parentIp;
    private int parentPort;
    private long latitude;
    private long longitude;
    private Set<String> serviceTypes;

    @PostConstruct
    public void init(){
        readProperties();
    }

    private void readProperties(){
        Properties props = new Properties();
        FileInputStream file = null;
        String path = "./main.properties";
        if(!DOCKER) {
            URL url = this.getClass().getClassLoader().getResource("main.properties");
            path = url.getPath();
        }
        try {
            file = new FileInputStream(path);
            //load properties
            props.load(file);
            file.close();
            //retrieve property
            this.cloudIp = props.getProperty("cloud.fog.middleware.ip");
            this.cloudPort = Integer.valueOf(props.getProperty("cloud.fog.middleware.port"));
            this.ip = props.getProperty("fog.device.ip");
            this.parentIp = props.getProperty("fog.parent.ip");
            this.parentPort = Integer.valueOf(props.getProperty("fog.parent.port"));
            this.latitude = Long.valueOf(props.getProperty("fog.location.latitude"));
            this.longitude = Long.valueOf(props.getProperty("fog.location.longitude"));
            String serviceTypeString = props.getProperty("fog.service.types");
            this.serviceTypes = new HashSet<String>(Arrays.asList(serviceTypeString.split(",")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isDOCKER() {
        return DOCKER;
    }

    public String getIp() {
        return ip;
    }

    public String getParentIp() {
        return parentIp;
    }

    public int getParentPort() {
        return parentPort;
    }

    public long getLatitude() {
        return latitude;
    }

    public long getLongitude() {
        return longitude;
    }

    public Set<String> getServiceTypes() {
        return serviceTypes;
    }

    public String getCloudIp() {
        return cloudIp;
    }

    public int getCloudPort() {
        return cloudPort;
    }
}
