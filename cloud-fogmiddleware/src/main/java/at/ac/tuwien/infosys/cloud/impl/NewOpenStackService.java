package at.ac.tuwien.infosys.cloud.impl;

import at.ac.tuwien.infosys.cloud.ICloudProviderService;
import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.model.DockerHost;
import at.ac.tuwien.infosys.model.DockerImage;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.util.Utils;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.internal.util.Base64;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.*;
import org.openstack4j.openstack.OSFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Service("OpenStackNew")
@Slf4j
public class NewOpenStackService implements ICloudProviderService {

    @Value("${cloud.dockerhost.image}")
    private String dockerhostImage;

    @Value("${cloud.openstack.publicip}")
    private Boolean PUBLICIPUSAGE;

    private String OPENSTACK_KEYPAIR_NAME;

    private OSClient.OSClientV2 os;

    private HashSet<String> portSet = new HashSet<String>();

    public void setup() {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("cloud/credential.properties"));
        } catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        String OPENSTACK_AUTH_URL = prop.getProperty("os.auth.url");
        String OPENSTACK_USERNAME = prop.getProperty("os.username");
        String OPENSTACK_PASSWORD = prop.getProperty("os.password");
        String OPENSTACK_TENANT_NAME = prop.getProperty("os.tenant.name");
        OPENSTACK_KEYPAIR_NAME = prop.getProperty("os.keypair.name");

        os = OSFactory.builderV2()
                .endpoint(OPENSTACK_AUTH_URL)
                .credentials(OPENSTACK_USERNAME,OPENSTACK_PASSWORD)
                .tenantName(OPENSTACK_TENANT_NAME)
                .authenticate();

        // ToDo: the new V3 authenticator should be used to successfully stop VMs. Currently this version is not
        // supported by the OpenStack implementation!
//        os = OSFactory.builderV3()
//                .endpoint(OPENSTACK_AUTH_URL)
//                .credentials(OPENSTACK_USERNAME,OPENSTACK_PASSWORD)
//                .authenticate();

        log.info("Successfully connected to " + OPENSTACK_AUTH_URL + " on tenant " + OPENSTACK_TENANT_NAME + " with user " + OPENSTACK_USERNAME);
    }


    public DockerHost startVM(DockerHost dh) throws Exception {
        setup();

        String cloudInit = "";
        try {
            cloudInit = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("docker-config/cloud-init"), "UTF-8");
        } catch (IOException e) {
            log.error("Could not load cloud init file");
        }
        Flavor flavor = os.compute().flavors().get(dh.getFlavor());

        //TODO check if the flavors can be retrieved in future releases
        for (Flavor f : os.compute().flavors().list()) {
            if (f.getName().equals(dh.getFlavor())) {
                flavor = f;
                break;
            }
        }
        ServerCreate sc = Builders.server()
                .name(dh.getName())
                .flavor(flavor)
                .image(dockerhostImage)
                .userData(Base64.encodeAsString(cloudInit))
                .keypairName(OPENSTACK_KEYPAIR_NAME)
                .addSecurityGroup("default")
                .build();

        Server server = os.compute().servers().boot(sc);
        String uri = server.getAccessIPv4();

        if (PUBLICIPUSAGE) {
            org.openstack4j.model.compute.FloatingIP freeIP = null;
            List<? extends FloatingIP> list = os.compute().floatingIps().list();
            for (org.openstack4j.model.compute.FloatingIP ip : list) {
                if (ip.getFixedIpAddress() == null) {
                    freeIP = ip;
                    break;
                }
            }
            if (freeIP == null) {
                throw new Exception("No more floating IPs available");
            }
            ActionResponse ipresponse = os.compute().floatingIps().addFloatingIP(server, freeIP.getFloatingIpAddress());
            if (!ipresponse.isSuccess()) {
                throw new Exception("Dockerhost could not be started. Error: "+ipresponse.getFault());
            }
            uri = freeIP.getFloatingIpAddress();
        }
        dh.setName(server.getId());
        dh.setUrl(uri);

        log.info("Server with id: " + dh.getName() + " and IP " + uri + " was started.");
        //wait until the dockerhost is available
        Boolean connection = false;
        while (!connection) {
            try {
                Thread.sleep(1000);
                final DockerClient docker = getDockerClient(dh.getUrl(), 60000);
                docker.ping();
                connection = true;
            } catch (DockerException ex) {
                log.info("Dockerhost is not available yet.");
            } catch (InterruptedException e) {
                log.info("Dockerhost is not available yet.");
            }
        }
        return dh;
    }

    public final void stopDockerHost(String name) {
        // ToDo: if the next line throws a session exception the new V3 keystone authentication is needed and the
        // current openstack implementation does not support this version.
        ActionResponse r = os.compute().servers().action(name, Action.STOP);

        if (!r.isSuccess()) {
            log.error("Dockerhost could not be stopped", r.getFault());
        } else {
            log.info("DockerHost terminated " + name);
        }
    }

    public DockerContainer startDockerContainer(String url, DockerImage image) {
        final DockerClient docker = getDockerClient(url);
        try {
            String serviceKey = image.getServiceKey();
            // tweak to get the cloud service pull the images
            if(!serviceKey.startsWith(Constants.IMAGE_PREFIX)) serviceKey = Constants.IMAGE_PREFIX+serviceKey;
            docker.pull(serviceKey);

            String[] ports = {Utils.generateRandomPort(portSet)};
            String[] exposedPorts = image.getExposedPorts();
            Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>();
            for (String port : ports) {
                List<PortBinding> hostPorts = new ArrayList<PortBinding>();
                hostPorts.add(PortBinding.of("0.0.0.0", port));
                portBindings.put(image.getExposedPorts()[0], hostPorts);
            }

            final HostConfig expected = HostConfig.builder()
                    .portBindings(portBindings)
                    .privileged(image.isPrivileged())
                    .build();

            final ContainerConfig containerConfig = ContainerConfig.builder()
                    .image(serviceKey)
                    .hostConfig(expected)
                    .exposedPorts(exposedPorts)
                    .build();

            String uuid = UUID.randomUUID().toString();
            String containerName = serviceKey.replace("/","-")+"_"+uuid;
            final ContainerCreation creation = docker.createContainer(containerConfig, containerName);
            final String id = creation.id();

            docker.startContainer(id);

            return new DockerContainer(id, serviceKey, Integer.valueOf(ports[0]), containerName, image);
        } catch (DockerException e) {
            log.error("Could not start container", e);
        } catch (InterruptedException e) {
            log.error("Could not start container", e);
        }
        return new DockerContainer();
    }

    private DockerClient getDockerClient(String url, int connectTimeout){
        return DefaultDockerClient.builder().
                uri(URI.create("http://" + url + ":2375")).
                connectTimeoutMillis(connectTimeout).
                build();
    }

    private DockerClient getDockerClient(String url){
        return getDockerClient(url, 3000000);
    }

    public void stopDockerContainer(String url, String containerId) {
        final DockerClient docker = getDockerClient(url);
        try {
            // Kill container
            docker.killContainer(containerId);
            log.info("Killing container with Id: "+containerId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerException e) {
            e.printStackTrace();
        }
    }
}
