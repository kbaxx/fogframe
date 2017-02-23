package at.ac.tuwien.infosys.util;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
public class Constants {

    public static final int SERVICE_STARTUP_SLEEP = 1 * 1000;
    public static final int PROVISIONING_ROUNDS = 2;
    public static final int MAX_CONTAINERS = 10;

    public static final String IMAGE_PREFIX = "fogframe/";


    public static final String IP_HOST = "192.168.1.101";
    public static final String IP_CLOUD = IP_HOST;
    public static final String IP_FCN1 = "192.168.1.105";
    public static final String IP_FCN2 = "192.168.1.106";
    public static final String IP_FCN3 = "192.168.1.107";
    public static final String IP_FC1 = "192.168.1.110";
    public static final String IP_FC2 = "192.168.1.111";
    public static final String IP_FC3 = "192.168.1.112";

    public static final int PORT_CFM = 8082;
    public static final int PORT_FC = 8081;
    public static final int PORT_FCN = 8080;
    public static final int PORT_CLOUD_SERVICE = 8200;

    public static final String IMG_BUSYBOX_KEY = "busy-image";
    public static final String IMG_TEMPHUM_KEY = "temp-hum";
    public static final String IMG_CLOUDDB_KEY = "cloud-service";

    // URLs
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // fog action control
    public static final String URL_SERVICE = "/services/";
    public static final String URL_SERVICE_DEPLOY = URL_SERVICE + "deploy/";
    public static final String URL_SERVICE_STOP = URL_SERVICE + "stop/";
    public static final String URL_SERVICE_STOPBYID = URL_SERVICE + "stopById/";
    public static final String URL_SERVICE_STOPALL = URL_SERVICE + "stopAll"; // no slash at the end as nothing follows
    public static final String URL_SERVICE_STOPCREATED = URL_SERVICE + "stopCreated";
    public static final String URL_SERVICE_GET_CREATED = URL_SERVICE + "containers"; // no slash at the end as nothing follows

    // local database
    public static final String URL_DB = "/localdb/";
    public static final String URL_DB_GETALL= URL_DB;
    public static final String URL_DB_UTILIZATION = URL_DB + "utilization";
    public static final String URL_DB_IP= URL_DB + "ip";
    public static final String URL_DB_CHILD= URL_DB + "child";
    public static final String URL_DB_DEVICETYPE= URL_DB + "deviceType";
    public static final String URL_DB_DEVICEID= URL_DB + "deviceId";
    public static final String URL_DB_PARENT= URL_DB + "parent";
    public static final String URL_DB_CHILDREN= URL_DB + "children";

    // reasoner
    public static final String URL_REASONER = "/reasoner/";
    public static final String URL_REASON = URL_REASONER + "reason/";
    public static final String URL_TASK_REQUESTS = URL_REASONER + "taskRequests/";
    public static final String URL_STOP_APPS = URL_REASONER + "stopApps";

    // shared database
    public static final String URL_SHARED = "/shareddb/";
    public static final String URL_SHARED_GETALL = URL_SHARED;
    public static final String URL_SHARED_GETIMAGE = URL_SHARED + "image/";
    public static final String URL_SHARED_REGISTERIMAGE = URL_SHARED + "register";

    // communication between devices (api)
    public static final String URL_COMMUNICATION = "/comm/";
    public static final String URL_PAIR_REQUEST = URL_COMMUNICATION + "pairRequest/";
    public static final String URL_MANUAL_PAIR_REQUEST = URL_COMMUNICATION + "manualPair";
    public static final String URL_PING_CHILDREN = URL_COMMUNICATION + "pairChildren/";
    public static final String URL_PING = URL_COMMUNICATION + "ping/";
    public static final String URL_CHILDREN_UTIL = URL_COMMUNICATION + "childrenUtil/";
    public static final String URL_LOCATION_RANGE = URL_COMMUNICATION + "locationRange/";


    // propagator
    public static final String URL_PROPAGATOR = "/propagator/";
    public static final String URL_PROPAGATE = URL_PROPAGATOR +"propagate";
    public static final String URL_PROPAGATE_TASK_REQUESTS = URL_PROPAGATOR +"propagateTaskRequests";

    // compute unit
    public static final String URL_COMPUNIT = "/compunit/";
    public static final String URL_COMPUNIT_SERVICEDATA = URL_COMPUNIT +"serviceData";

    // cloud-fog middleware
    public static final String URL_LOCATOR = "/locator/";
    public static final String URL_REQUEST_PARENT = URL_LOCATOR + "parent/";

    public static final String URL_CLOUD = "/cloud/";
    public static final String URL_CLOUD_STOP_VM = URL_CLOUD + "stopVM/";
    public static final String URL_CLOUD_STOP_SERVICE = URL_CLOUD + "stopService/";
}
