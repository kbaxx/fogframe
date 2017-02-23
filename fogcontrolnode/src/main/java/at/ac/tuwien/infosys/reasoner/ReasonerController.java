package at.ac.tuwien.infosys.reasoner;

import at.ac.tuwien.infosys.model.Application;
import at.ac.tuwien.infosys.model.Message;
import at.ac.tuwien.infosys.model.TaskRequest;
import at.ac.tuwien.infosys.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 * Controller that receives the task requests for further processing by the reasoner service and resource provisioning
 * approach.
 */
@RestController
@CrossOrigin(origins = "*")
@Slf4j
public class ReasonerController {

    @Autowired
    private IReasonerService reasonerService;


    /**
     * Handles the incoming task requests and executes the implemented resource provisioning approach. Furthermore,
     * this method creates an application task mapping to know where every service is running.
     * @param application application consisting of several task requests to deploy in the system
     * @return a message with deployment time and status if the deployment worked out
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_TASK_REQUESTS)
    public Message handleTaskRequests(@RequestBody Application application) {
        return reasonerService.handleTaskRequests(application);
    }

    /**
     * Endpoint to stop all running applications in the whole environment
     */
    @RequestMapping(method = RequestMethod.POST, value=Constants.URL_STOP_APPS)
    public void stopAllApplications() {
        reasonerService.stopAllApplications();
    }

    /**
     * Testing method to simplify the evaluation
     * @param countT1 amount of services of type t1
     * @param countT2 amount of services of type t2
     * @param countT3 amount of services of type t3
     * @param countT4 amount of services of type t4
     * @param minutes duration of the application
     * @return
     */
    @RequestMapping(method = RequestMethod.POST, value="reasoner/test/{countT1}/{countT2}/{countT3}/{countT4}/{minutes}")
    public Message testTaskHandlingAdaptable(@PathVariable int countT1, @PathVariable int countT2,
                                             @PathVariable int countT3, @PathVariable int countT4,
                                             @PathVariable int minutes){

        Set<TaskRequest> testRequests = new HashSet<TaskRequest>();
        // t1 = temperature humidity sensor (only fog)
        // t2 = busy box (cloud and fog)
        // t3 = busy box (cloud and fog)
        // t4 = cloud db service (only cloud)
        for(int i = 0; i < countT1; i++){
            TaskRequest t = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
            testRequests.add(t);
        }
        for(int i = 0; i < countT2; i++){
            TaskRequest t = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
            testRequests.add(t);
        }
        for(int i = 0; i < countT3; i++){
            TaskRequest t = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
            testRequests.add(t);
        }
        for(int i = 0; i < countT4; i++){
            TaskRequest t = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
            testRequests.add(t);
        }

        int time = minutes*60;
//        if(minutes == -1) time = -1;
        Application application = new Application(new ArrayList<TaskRequest>(testRequests), time);
        return reasonerService.handleTaskRequests(application);
    }






    /**
     * OTHER TEST METHODS BELOW
     * -----------------------------------------------------------------------------------------------------------------
     */

    @RequestMapping(method = RequestMethod.POST, value="reasoner/test/{minutes}")
    public Message testTaskHandling(@PathVariable int minutes){
        Set<TaskRequest> testRequests = new HashSet<TaskRequest>();
        // t1 = temperature humidity sensor (only fog)
        // t2 = busy box (cloud and fog)
        // t3 = busy box (cloud and fog)
        // t4 = cloud db service (only cloud)
        TaskRequest t1 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t2 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t3 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);

        TaskRequest t4 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);

        TaskRequest t5 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t6 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t7 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t8 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t9 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t10 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);

        TaskRequest t11 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t12 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t13 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t14 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);

//        testRequests.add(t10);
        testRequests.add(t4);
        testRequests.add(t1);
        testRequests.add(t6);
        testRequests.add(t2);
        testRequests.add(t5);
        testRequests.add(t8);
        testRequests.add(t9);
        testRequests.add(t3);
        testRequests.add(t7);
        testRequests.add(t11);
//        testRequests.add(t12);
//        testRequests.add(t13);
//        testRequests.add(t14);

        int time = minutes*60;
//        if(minutes == -1) time = -1;
        Application application = new Application(new ArrayList<TaskRequest>(testRequests), time);
        return reasonerService.handleTaskRequests(application);
    }

    @RequestMapping(method = RequestMethod.POST, value="reasoner/test1")
    public Message testTaskHandling1(){
        Set<TaskRequest> testRequests = new HashSet<TaskRequest>();
        // t1 = temperature humidity sensor (only fog)
        // t2 = busy box (cloud and fog)
        // t3 = busy box (cloud and fog)
        // t4 = cloud db service (only cloud)
        TaskRequest t1 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t2 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t3 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);

        TaskRequest t4 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);

        TaskRequest t5 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t6 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t7 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t8 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t9 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t10 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);

        TaskRequest t11 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t12 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t13 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t14 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);

//        testRequests.add(t10);
        testRequests.add(t4);
        testRequests.add(t1);
        testRequests.add(t6);
        testRequests.add(t2);
        testRequests.add(t5);
        testRequests.add(t8);
        testRequests.add(t9);
        testRequests.add(t3);
        testRequests.add(t7);
        testRequests.add(t11);
//        testRequests.add(t12);
//        testRequests.add(t13);
//        testRequests.add(t14);

        Application application = new Application(new ArrayList<TaskRequest>(testRequests), 1*60);
        return reasonerService.handleTaskRequests(application);
    }

    @RequestMapping(method = RequestMethod.POST, value="reasoner/test3")
    public Message testTaskHandling3(){
        Set<TaskRequest> testRequests = new HashSet<TaskRequest>();
        // t1 = temperature humidity sensor (only fog)
        // t2 = busy box (cloud and fog)
        // t3 = busy box (cloud and fog)
        // t4 = cloud db service (only cloud)
        TaskRequest t1 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t2 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t3 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);

        TaskRequest t4 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);

        TaskRequest t5 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t6 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t7 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t8 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t9 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t10 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);

        TaskRequest t11 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t12 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t13 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t14 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);

//        testRequests.add(t10);
        testRequests.add(t4);
        testRequests.add(t1);
        testRequests.add(t6);
        testRequests.add(t2);
        testRequests.add(t5);
        testRequests.add(t8);
        testRequests.add(t9);
        testRequests.add(t3);
        testRequests.add(t7);
//        testRequests.add(t11);
//        testRequests.add(t12);
//        testRequests.add(t13);
//        testRequests.add(t14);

        Application application = new Application(new ArrayList<TaskRequest>(testRequests), 3*60);
        return reasonerService.handleTaskRequests(application);
    }

    @RequestMapping(method = RequestMethod.POST, value="reasoner/test5")
    public Message testTaskHandling5(){
        Set<TaskRequest> testRequests = new HashSet<TaskRequest>();
        // t1 = temperature humidity sensor (only fog)
        // t2 = busy box (cloud and fog)
        // t3 = busy box (cloud and fog)
        // t4 = cloud db service (only cloud)
        TaskRequest t1 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t2 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t3 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);

        TaskRequest t4 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);

        TaskRequest t5 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t6 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t7 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t8 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t9 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t10 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);

        TaskRequest t11 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t12 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t13 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t14 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);

//        testRequests.add(t10);
        testRequests.add(t4);
        testRequests.add(t1);
        testRequests.add(t6);
        testRequests.add(t2);
        testRequests.add(t5);
        testRequests.add(t8);
        testRequests.add(t9);
        testRequests.add(t3);
        testRequests.add(t7);
//        testRequests.add(t11);
//        testRequests.add(t12);
//        testRequests.add(t13);
//        testRequests.add(t14);

        Application application = new Application(new ArrayList<TaskRequest>(testRequests), 5*60);
        return reasonerService.handleTaskRequests(application);
    }

    @RequestMapping(method = RequestMethod.POST, value="reasoner/test550")
    public Message testTaskHandling5_50(){
        Set<TaskRequest> testRequests = new HashSet<TaskRequest>();
        // t1 = temperature humidity sensor (only fog)
        // t2 = busy box (cloud and fog)
        // t3 = busy box (cloud and fog)
        // t4 = cloud db service (only cloud)

        // 15 times t1
        TaskRequest t1 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t2 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t3 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t4 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t5 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t6 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t7 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t8 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t9 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t10 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t11 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t12 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t13 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t14 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);
        TaskRequest t15 = new TaskRequest(Constants.IMG_TEMPHUM_KEY, "t1", true, false);

        // 12 times t2
        TaskRequest t16 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t17 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t18 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t19 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t20 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t21 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t22 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t23 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t24 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t25 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t26 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);
        TaskRequest t27 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t2", false, false);

        // 18 times t3
        TaskRequest t28 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t29 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t30 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t31 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t32 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t33 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t34 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t35 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t36 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t37 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t38 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t39 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t40 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t41 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t42 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t43 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t44 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);
        TaskRequest t45 = new TaskRequest(Constants.IMG_BUSYBOX_KEY, "t3", false, false);

        // 5 times t4
        TaskRequest t46 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t47 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t48 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t49 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);
        TaskRequest t50 = new TaskRequest(Constants.IMG_CLOUDDB_KEY, "t4", false, true);


        testRequests.add(t10);testRequests.add(t4);testRequests.add(t1);testRequests.add(t6);testRequests.add(t2);
        testRequests.add(t5);testRequests.add(t8);testRequests.add(t9);testRequests.add(t3);testRequests.add(t7);
        testRequests.add(t11);testRequests.add(t12);testRequests.add(t13);testRequests.add(t14);

        testRequests.add(t15);testRequests.add(t22);testRequests.add(t30);testRequests.add(t37);testRequests.add(t44);
        testRequests.add(t16);testRequests.add(t23);testRequests.add(t31);testRequests.add(t38);testRequests.add(t45);
        testRequests.add(t17);testRequests.add(t24);testRequests.add(t32);testRequests.add(t39);testRequests.add(t46);
        testRequests.add(t18);testRequests.add(t25);testRequests.add(t33);testRequests.add(t40);testRequests.add(t47);
        testRequests.add(t19);testRequests.add(t26);testRequests.add(t34);testRequests.add(t41);testRequests.add(t48);
        testRequests.add(t20);testRequests.add(t27);testRequests.add(t35);testRequests.add(t42);testRequests.add(t49);
        testRequests.add(t21);testRequests.add(t28);testRequests.add(t36);testRequests.add(t43);testRequests.add(t50);
                              testRequests.add(t29);

        Application application = new Application(new ArrayList<TaskRequest>(testRequests), 5*60);
        return reasonerService.handleTaskRequests(application);
    }
}
