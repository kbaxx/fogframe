package at.ac.tuwien.infosys.monitor;

import at.ac.tuwien.infosys.database.DatabaseService;
import at.ac.tuwien.infosys.model.Utilization;
import com.sun.management.OperatingSystemMXBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.lang.management.ManagementFactory;
import java.util.Date;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 */
@Service
public class MonitorService {

    @Autowired
    private DatabaseService dbService;

    private OperatingSystemMXBean operatingSystemMXBean;

    @PostConstruct
    public void init(){
        operatingSystemMXBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
    }


    @Scheduled(fixedDelayString = "${fog.monitor.delay}")
    private void monitorUtilization() {
        long freeRam = operatingSystemMXBean.getFreePhysicalMemorySize();
        long totalRam = operatingSystemMXBean.getTotalPhysicalMemorySize();
        double storage = new File("/").getFreeSpace()/Math.pow(10,9); // in gigabytes
        double ram = (((totalRam-freeRam)*1.0)/(totalRam*1.0))*100.0;
        double cpu = operatingSystemMXBean.getSystemCpuLoad()*100.0;

        System.out.println(new Date()+": CPU="+cpu+", RAM="+ram+", Storage="+storage);

        try {
            // save to db
            dbService.setUtilization(new Utilization(cpu, ram, storage));
        } catch(Exception e){
            System.out.println("----- ERROR HEAD -----");
            System.out.println(e.getMessage());
            System.out.println("----- ERROR TAIL -----");
        }
    }
}
