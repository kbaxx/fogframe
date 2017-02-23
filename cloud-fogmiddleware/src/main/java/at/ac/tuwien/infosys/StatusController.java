package at.ac.tuwien.infosys;

import at.ac.tuwien.infosys.cloud.ICloudService;
import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.database.impl.RedisService;
import at.ac.tuwien.infosys.model.DockerHost;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Location;
import at.ac.tuwien.infosys.model.ServiceAssignment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

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
    private ICloudService cloudService;

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
                "<h1>Cloud-Fog Middleware Status-Page</h1>";
        html+="<p>  LOCAL DB  ------------------------------------------------------------------------------------------------------------------------</p>";
        html+="<ul>";
        html+="<li><b>Id:</b> "+dbService.getDeviceId()+"</li>";
        html+="<li><b>Device Type:</b> "+dbService.getDeviceType()+"</li>";
        html+="<li><b>IP:Port:</b> "+dbService.getIp()+":"+dbService.getPort()+"</li>";
        html+="<li><b>Child Devices:</b><ul>";
        for(Fogdevice c : dbService.getChildren()){
            html+="<li>"+c+"</li>";
        }
        html+="</ul></li></ul>";

        html+="<hr/>";
        html+="<p>  SERVICES  ------------------------------------------------------------------------------------------------------------------------</p>";
        html+="<ol>";
        for(Map.Entry e : cloudService.getVMMappings().entrySet()){
            DockerHost h = (DockerHost) e.getKey();
            List<ServiceAssignment> l = (List<ServiceAssignment>) e.getValue();
            html += "<li><p>Host:"+h+"</p>";
            for(ServiceAssignment s : l){
                html += "<p>"+s.getContainer()+"</p>";
            }
            html += "</li>";
        }
        html+="</ol>";
        html+="<hr/>";
        html += "</body></html>";
        return html;
    }
}



