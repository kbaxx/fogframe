package at.ac.tuwien.infosys.watchdog;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.database.IDatabaseService;
import at.ac.tuwien.infosys.model.Fogdevice;
import at.ac.tuwien.infosys.model.Utilization;
import at.ac.tuwien.infosys.reasoner.IReasonerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 * Service watching over the whole systems utilization, throwing overload events in case the defined rules are violated.
 */
@Service
@Slf4j
public class WatchdogService {

    @Autowired
    private IDatabaseService dbService;

    @Autowired
    private IReasonerService reasonerService;

    @Autowired
    private ICommunicationService commService;

    private Set<Rule> rules = new HashSet<Rule>();

    @PostConstruct
    public void init(){    }

    public WatchdogService(){
        // add some basic rules for testing reasons
        addRule("cpu", RuleOperator.SMALLER, 80);
        addRule("ram", RuleOperator.SMALLER, 99.5);
        addRule("storage", RuleOperator.BIGGER, 5);
    }

    public void addRule(String identifier, RuleOperator operator, double value){
        rules.add(new Rule(identifier, operator, value));
    }

    public boolean checkRules(Utilization u){
        for(Rule r : rules){
            double value = Integer.MAX_VALUE;
            if(r.getIdentifier().equals("cpu")){
                value = u.getCpu();
            } else if(r.getIdentifier().equals("ram")){
                value = u.getRam();
            } else if(r.getIdentifier().equals("storage")){
                value = u.getStorage();
            }
            if(!r.checkRule(value)){
                log.warn(r+" is violated. DbValue="+value);
                return false;
            }
        }
        return true;
    }




    @Scheduled(fixedDelayString = "${fog.watchdog.delay}", initialDelay = 5*1000)
    private void scheduledCheck(){
        // only the root fog control node runs the watchdog
        if(reasonerService.isRootFCN())
            watchdogCheck();
    }

    public void watchdogCheck(){
        if(dbService == null) return;

        try {
            Set<Fogdevice> children = reasonerService.getTopologyChildren();
            for (Fogdevice fd : children) {
                Utilization u = commService.getChildUtilization(fd);
                if (u != null && u.getCpu() > 0.0) {
                    if (!checkRules(u)) {
                        reasonerService.deviceOverloadedEvent(fd);
                    }
                }
            }
        } catch(Exception e){
            log.warn("Watchdog error occurred");
        }
    }
}
