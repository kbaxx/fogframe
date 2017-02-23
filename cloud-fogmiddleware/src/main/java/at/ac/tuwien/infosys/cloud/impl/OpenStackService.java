package at.ac.tuwien.infosys.cloud.impl;


import at.ac.tuwien.infosys.cloud.ICloudProviderService;
import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.model.DockerHost;
import at.ac.tuwien.infosys.model.DockerImage;
import at.ac.tuwien.infosys.util.Constants;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerCreation;
import com.spotify.docker.client.messages.HostConfig;
import com.spotify.docker.client.messages.PortBinding;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeService;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.Template;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.javax.annotation.Nullable;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v2_0.domain.FloatingIP;
import org.jclouds.openstack.nova.v2_0.extensions.FloatingIPApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.util.*;

@Service
@Slf4j
public class OpenStackService implements ICloudProviderService {

    @Value("${cloud.dockerhost.image}")
    private String dockerhostImage;

    @Value("${cloud.openstack.publicip}")
    private Boolean PUBLICIPUSAGE;


    private String OPENSTACK_AUTH_URL;
    private String OPENSTACK_USERNAME;
    private String OPENSTACK_PASSWORD;
    private String OPENSTACK_TENANT_NAME;
    private String OPENSTACK_KEYPAIR_NAME;

    private Map<String, Hardware> hardwareProfiles = new HashMap<>();
    private Map<String, Image> imageProfiles = new HashMap<>();

    private NovaApi novaApi;
    private ComputeService compute;

    public void setup() {
        Properties prop = new Properties();
        try {
            prop.load(getClass().getClassLoader().getResourceAsStream("cloud/credential.properties"));
        } catch (IOException e) {
            log.error("Could not load properties.", e);
        }

        OPENSTACK_AUTH_URL = prop.getProperty("os.auth.url");
        OPENSTACK_USERNAME = prop.getProperty("os.username");
        OPENSTACK_PASSWORD = prop.getProperty("os.password");
        OPENSTACK_TENANT_NAME = prop.getProperty("os.tenant.name");
        OPENSTACK_KEYPAIR_NAME = prop.getProperty("os.keypair.name");

        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());
        novaApi = ContextBuilder.newBuilder("openstack-nova")
                .endpoint(OPENSTACK_AUTH_URL)
                .credentials(OPENSTACK_TENANT_NAME + ":" + OPENSTACK_USERNAME, OPENSTACK_PASSWORD)
                .modules(modules)
                .buildApi(NovaApi.class);

        ComputeServiceContext context = ContextBuilder.newBuilder("openstack-nova")
                .endpoint(OPENSTACK_AUTH_URL)
                .credentials(OPENSTACK_TENANT_NAME + ":" + OPENSTACK_USERNAME, OPENSTACK_PASSWORD)
                .modules(modules)
                .buildView(ComputeServiceContext.class);
        compute = context.getComputeService();

        loadOpenStackData();

        log.info("Successfully connected to " + OPENSTACK_AUTH_URL + " on tenant " + OPENSTACK_TENANT_NAME + " with user " + OPENSTACK_USERNAME);
    }


    public DockerHost startVM(DockerHost dh) {
        setup();

        String cloudInit = "";
        try {
            cloudInit = IOUtils.toString(getClass().getClassLoader().getResourceAsStream("docker-config/cloud-init"), "UTF-8");
        } catch (IOException e) {
            log.error("Could not load cloud init file");
        }

        TemplateOptions options = NovaTemplateOptions.Builder
                .userData(cloudInit.getBytes())
                .keyPairName(OPENSTACK_KEYPAIR_NAME)
                .securityGroups("default");
        Hardware hardware = hardwareProfiles.get(dh.getFlavor());

        Template template = compute.templateBuilder()
                .locationId("myregion")
                .options(options)
                .fromHardware(hardware)
                .fromImage(imageProfiles.get(dockerhostImage))
                .build();

        Set<? extends NodeMetadata> nodes = null;
        try {
            nodes = compute.createNodesInGroup(dh.getName(), 1, template);
        } catch (RunNodesException e) {
            log.error("Could not start Dockerhost." + e.getMessage());
        }

        NodeMetadata nodeMetadata = nodes.iterator().next();
        String ip = nodeMetadata.getPrivateAddresses().iterator().next();

        if (PUBLICIPUSAGE) {
            FloatingIPApi floatingIPs = novaApi.getFloatingIPApi("myregion").get();
            String publicIP = null;
            for (FloatingIP floatingIP : floatingIPs.list()) {
                if (floatingIP.getInstanceId() == null) {
                    publicIP = floatingIP.getIp();
                    floatingIPs.addToServer(publicIP, nodeMetadata.getProviderId());
                    break;
                }
            }
            if (publicIP == null) {
                publicIP = floatingIPs.allocateFromPool("cloud").getIp();
                floatingIPs.addToServer(publicIP, nodeMetadata.getProviderId());
            }
            ip = publicIP;
        }
        dh.setName(nodeMetadata.getHostname());
        dh.setUrl(ip);

        log.info("Server with id: " + dh.getName() + " and IP " + ip + " was started.");
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


    public final void stopDockerHost(final String name) {
        Set<? extends NodeMetadata> nodeMetadatas = compute.destroyNodesMatching(new Predicate<NodeMetadata>() {
            @Override
            public boolean apply(@Nullable NodeMetadata input) {
                boolean contains = input.getName().contains(name);
                return contains;
            }
        });
        for (NodeMetadata nodeMetadata : nodeMetadatas) {
            log.info("DockerHost terminated " + nodeMetadata.getName());
        }
    }

    private String generateRandomPort(){
        // generate a random number that is used from the outside of the container
        // inside of the container the port is fixed to the port 8100
        Random r = new Random();
        int Low = 8201;
        int High = 50000;
        return String.valueOf(r.nextInt(High-Low) + Low);
    }

    public DockerContainer startDockerContainer(String url, DockerImage image) {
        final DockerClient docker = getDockerClient(url);
        try {
            String serviceKey = image.getServiceKey();
            // tweak to get the cloud service pull the images
            if(!serviceKey.startsWith(Constants.IMAGE_PREFIX)) serviceKey = Constants.IMAGE_PREFIX+serviceKey;
            docker.pull(serviceKey);

            String[] ports = {generateRandomPort()};
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

    protected void loadOpenStackData() {
        Set<? extends Hardware> profiles = compute.listHardwareProfiles();
        for (Hardware profile : profiles) {
            hardwareProfiles.put(profile.getName(), profile);
        }
        Set<? extends org.jclouds.compute.domain.Image> images = compute.listImages();
        for (org.jclouds.compute.domain.Image image : images) {
            imageProfiles.put(image.getProviderId(), image);
        }
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

    public void stopDockerContainer(String url, String containerId){
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
