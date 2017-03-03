package at.ac.tuwien.infosys.model;

import at.ac.tuwien.infosys.util.Constants;
import lombok.Data;

import java.util.*;

/**
 * Created by Kevin Bachmann on 25/11/2016.
 */
@Data
public class ServiceData {

    private Fogdevice sender;
    private String receiverServiceKey;
    private Application application;
    private String key;
    private List<HashMap> data = new ArrayList<HashMap>();
    private boolean reasoningPurpose;

    public ServiceData(){
        this.receiverServiceKey = Constants.IMG_CLOUDDB_KEY;
    }

    public String toString(){
        String output = "ServiceData{";
        if(application != null && application.getRequests() != null)
            output += "requests: "+application.getRequests().toString();
        if(data != null)
            output += key+": " +data.toString();
        output += "}";
        return output;
    }


}
