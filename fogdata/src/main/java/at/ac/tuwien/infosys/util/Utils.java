package at.ac.tuwien.infosys.util;

import java.util.HashSet;
import java.util.Random;

/**
 * Created by Kevin Bachmann on 23/02/2017.
 */
public class Utils {

    /**
     * Generate a random port the deployed service is going to expose
     * @return a random port string between 8100 and 10000
     */
    public static String generateRandomPort(HashSet<String> portSet){
        // generate a random number that is used from the outside of the container
        // inside of the container the port is fixed to the port 8100
        String port = null;
        Random r = new Random();
        int Low = 8100;
        int High = 50000;
        do {
            String tempPort = String.valueOf(r.nextInt(High - Low) + Low);
            if(!portSet.contains(tempPort)){
                port = tempPort;
                portSet.add(port);
            }
        } while(port == null);
        return port;
    }

    public static DeviceType getDeviceTypeFromPort(int port){
        switch(port){
            case Constants.PORT_CFM:
                return DeviceType.CLOUD_FOG_MIDDLEWARE;
            case Constants.PORT_FC:
                return DeviceType.FOG_CELL;
            case Constants.PORT_FCN:
                return DeviceType.FOG_CONTROL_NODE;
            default:
                // should not happen
                System.err.println("ERROR: UNKNOWN PORT IN PORT TO DEVICE TYPE CONVERSION");
                return null;
        }
    }
}
