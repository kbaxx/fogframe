package at.ac.tuwien.infosys;


import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.database.impl.RedisService;
import at.ac.tuwien.infosys.fogactioncontrol.IFogActionControlService;
import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class StatusController {

    @Autowired
    private IDatabaseService dbService;

    @Autowired
    private IFogActionControlService fogActionControlService;

    @PostConstruct
    public void init(){    }

    /**
     * requests the status of the Service
     *
     * @return a String with HTML code for the display of availability
     */
    @RequestMapping(method = RequestMethod.GET)
    public String getPage(){
        String html = "<html><head></head><body style='background: white; color: black; font-family: Verdana'>" +
                "<h1>Fog Cell Status-Page</h1>";

        html+="<p>  LOCAL DB  ------------------------------------------------------------------------------------------------------------------------</p>";
        html+="<ul>";
        html+="<li><b>Id:</b> "+dbService.getDeviceId()+"</li>";
        html+="<li><b>Device Type:</b> "+dbService.getDeviceType()+"</li>";
        Location loc = dbService.getLocation();
        html+="<li><b>Location:</b> ("+loc.getLatitude()+"/"+loc.getLongitude()+")</li>";
        html+="<li><b>Utilization:</b> "+dbService.getUtilization()+"</li>";
        html+="<li><b>IP:Port:</b> "+dbService.getIp()+":"+dbService.getPort()+"</li>";
        html+="<li><b>Cloud:</b> "+dbService.getCloudIp()+":"+dbService.getCloudPort()+"</li>";
        Fogdevice parent = dbService.getParent();
        if(parent != null)
            html+="<li><b>Parent:</b> "+parent.getIp()+":"+parent.getPort()+", Type:"+ parent.getType()+"</li>";
        html+="<li><b>Service Types:</b> "+dbService.getServiceTypes()+"</li>";
        html+="</ul>";

        html+="<hr/>";
        html+="<p>  SERVICES  ------------------------------------------------------------------------------------------------------------------------</p>";
        Set<DockerContainer> createdContainers = fogActionControlService.getCreatedContainers();
        html+="<p>Count: "+createdContainers.size()+"</p>";
        html+="<ol>";
        for(DockerContainer c : createdContainers){
            html+="<li>"+c+"</li>";
        }
        html+="</ol>";

        html += "</body></html>";
        return html;
    }
}



