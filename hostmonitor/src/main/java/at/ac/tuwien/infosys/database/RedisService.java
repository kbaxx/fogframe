package at.ac.tuwien.infosys.database;

import at.ac.tuwien.infosys.model.exception.DBException;
import com.lambdaworks.redis.RedisClient;
import com.lambdaworks.redis.api.StatefulRedisConnection;
import com.lambdaworks.redis.api.sync.RedisCommands;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.*;

/**
 * Created by Kevin Bachmann on 27/10/2016.
 */
@Service
public class RedisService {

    private StatefulRedisConnection<String, String> connection;
    private RedisClient client;

    /**
     * Starts an embedded redis server on construction of this bean.
     * @throws IOException if something happens with the port or the creation of the server
     */
    @PostConstruct
    private void startRedis() throws IOException, DBException {
        String redisHost = getHostIp();
        int port = 6380;
        if(redisHost.endsWith("105") || redisHost.endsWith("106") || redisHost.endsWith("107")) {
            port = 6380;
        }
        else {
            port = 6381;
        }
        System.out.println("--------- REDISHOST: "+redisHost+":"+port+" ---------");

        String hostString = "redis://"+redisHost+":"+port;
        this.client = RedisClient.create(hostString);
        this.connection = client.connect();
    }

    private String getHostIp(){
        String ip = "";
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                // filters out 127.0.0.1 and inactive interfaces
                if (iface.isLoopback() || !iface.isUp())
                    continue;

                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while(addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    String tempip = addr.getHostAddress();
                    System.out.println(iface.getDisplayName() + " " + tempip);
                    if(tempip.contains("192.168.1.")) ip = tempip;
                }
            }
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        return ip;
    }

    /**
     * #########################################################################################################
     * #########################################################################################################
     */


    public String getValue(String key){
        RedisCommands<String, String> sync = connection.sync();
        return sync.get(key);
    }

    public Set<String> getKeys(){
        RedisCommands<String, String> sync = connection.sync();
        return new HashSet<String>(sync.keys("*"));
    }

    public Map<String, String> getAll(){
        RedisCommands<String, String> sync = connection.sync();
        Set<String> keys = getKeys();
        Map<String, String> map = new HashMap<String, String>();
        for(String key : keys){
            map.put(key, sync.get(key));
        }
        return map;
    }

    public void setValue(String key, String value){
        RedisCommands<String, String> sync = connection.sync();
        sync.set(key, value);
    }

    public void deleteKey(String key){
        RedisCommands<String, String> sync = connection.sync();
        sync.del(key);
    }
}
