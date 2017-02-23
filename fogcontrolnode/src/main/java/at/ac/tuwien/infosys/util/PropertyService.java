package at.ac.tuwien.infosys.util;

import at.ac.tuwien.infosys.model.Location;
import at.ac.tuwien.infosys.model.LocationRange;
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
    private LocationRange locationRange;

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
            if(!serviceTypeString.isEmpty())
                this.serviceTypes = new HashSet<String>(Arrays.asList(serviceTypeString.split(",")));
            else
                this.serviceTypes = new HashSet<String>();
            // location range
            String[] lowerBoundStr = props.getProperty("fog.location.range.lower").split(",");
            long lowerBoundLat = Long.valueOf(lowerBoundStr[0]);
            long lowerBoundLong = Long.valueOf(lowerBoundStr[1]);
            String[] upperBoundStr = props.getProperty("fog.location.range.upper").split(",");
            long upperBoundLat = Long.valueOf(upperBoundStr[0]);
            long upperBoundLong = Long.valueOf(upperBoundStr[1]);
            this.locationRange = new LocationRange(new Location(lowerBoundLat, lowerBoundLong),
                    new Location(upperBoundLat, upperBoundLong));
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

    public LocationRange getLocationRange() {
        return locationRange;
    }

    public String getCloudIp() {
        return cloudIp;
    }

    public int getCloudPort() {
        return cloudPort;
    }
}
