package at.ac.tuwien.infosys;


import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.database.impl.RedisService;
import at.ac.tuwien.infosys.fogactioncontrol.IFogActionControlService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.reasoner.IReasonerService;
import at.ac.tuwien.infosys.sharedstorage.impl.SharedRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.List;
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
    private SharedRedisService sharedRedisService;

    @Autowired
    private IReasonerService reasonerService;

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
                "<h1>Fog Control Node Status-Page</h1>";

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
        html+="<li><b>Child Devices:</b><ul>";
        for(Fogdevice c : dbService.getChildren()){
            html+="<li>"+c+"</li>";
        }
        html+="</ul></li></ul>";

        html+="<hr/>";
        html+="<p>  SHARED DB  ------------------------------------------------------------------------------------------------------------------------</p>";
        html+="<ul>";
        for (Map.Entry<String,String> entry : sharedRedisService.getAll().entrySet()) {
            html += "<li>"+entry.getKey()+": "+entry.getValue()+"</li>";
        }
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

        if(reasonerService.isRootFCN()) {
            html += "<hr/>";
            html += "<p>  APPLICATIONS  ------------------------------------------------------------------------------------------------------------------------</p>";
            List<ApplicationAssignment> asslist = reasonerService.getApplicationAssignments();
            html += "<p>Count: " + asslist.size() + "</p>";
            html += "<ol>";
            for (ApplicationAssignment a : asslist) {
                html += "<li>";
                html+="<ul>";
                for(TaskAssignment t : a.getAssignedTasks()) {
                    html += "<li>";
                    html += t.getFogdevice().getIp()+":"+t.getFogdevice().getPort()+" |  "
                            +t.getTaskRequest().getServiceKey()+":"+t.getTaskRequest().getServiceType()+"  |  "
                            +t.getContainer().getContainerId().substring(0,6);
                    html += "</li>";
                }
                html+="</ul>";
                html+= "</li>";
            }
            html += "</ol>";

            html += "<hr/>";
            html += "<p>  EVALUATION  ------------------------------------------------------------------------------------------------------------------------</p>";
            html += "<p>" + reasonerService.getEvaluationSummary() + "</p>";

        }
        html += "</body></html>";
        return html;
    }
}



