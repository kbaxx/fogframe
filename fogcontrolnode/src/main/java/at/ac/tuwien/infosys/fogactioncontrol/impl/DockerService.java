package at.ac.tuwien.infosys.fogactioncontrol.impl;

import at.ac.tuwien.infosys.fogactioncontrol.IContainerService;
import at.ac.tuwien.infosys.model.DockerContainer;
import at.ac.tuwien.infosys.model.DockerImage;
import at.ac.tuwien.infosys.sharedstorage.ISharedDatabaseService;
import at.ac.tuwien.infosys.util.Utils;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.ProgressHandler;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Kevin Bachmann on 28/10/2016.
 */
@Service
@Slf4j
public class DockerService implements IContainerService {

    @Autowired
    private ISharedDatabaseService sharedDb;

    private DockerClient docker = null;

    private HashSet<String> portSet = new HashSet<String>();

    @Value("${fog.docker}")
    private boolean DOCKER;

    @PostConstruct
    public void init(){
        try {
            createDockerClient();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    /**
     * Starts the docker client if it not yet is started
     * @throws DockerCertificateException certificate exception in case the client cant be started
     */
    private void createDockerClient() throws DockerCertificateException {
        if(docker == null){
            docker = DefaultDockerClient.fromEnv().build();
        }
    }

    /**
     * Stop the docker client
     */
    private void closeDockerClient(){
        docker.close();
    }

    public String createImageFromDockerfile(String dockerfile, String imageName) throws DockerCertificateException {
        // create dockerfile and save it temporarily
        URL url = this.getClass().getClassLoader().getResource("docker");
        if(url == null) return null;
        String dockerFilePath = url.getPath();
        if(DOCKER) dockerFilePath = "/home";
        try{
            PrintWriter writer = new PrintWriter(dockerFilePath+"/Dockerfile", "UTF-8");
            writer.print(dockerfile);
            writer.close();
        } catch (Exception e) {
            // do something
            log.error("create dockerfile error: "+e.getMessage());
        }

        createDockerClient();
        DockerClient.BuildParam[] params = { DockerClient.BuildParam.quiet() };
        String returnedImageId = null;
        try {
            final AtomicReference<String> imageIdFromMessage = new AtomicReference<String>();
            returnedImageId = docker.build(
                Paths.get(dockerFilePath), imageName, new ProgressHandler() {
                    @Override
                    public void progress(ProgressMessage message) throws DockerException {
                        final String imageId = message.buildImageId();
                        if (imageId != null) {
                            imageIdFromMessage.set(imageId);
                        }
                    }
                });
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return returnedImageId;
    }

    public DockerContainer startContainer(String serviceKey){
        // Create a client based on DOCKER_HOST and DOCKER_CERT_PATH env vars
        try {
            createDockerClient();

            // 0. get dockerimage object from shared storage
            DockerImage image = sharedDb.getServiceImage(serviceKey);
            // 1. get dockerfile
            String dockerfile = image.getDockerfile();
            // 2. build image
            String imageId = createImageFromDockerfile(dockerfile, serviceKey);
            log.info("Created image with imageID="+imageId);

            String randomPort = Utils.generateRandomPort(portSet);
            String[] ports = {randomPort};
            String[] exposedPorts = image.getExposedPorts();
            Map<String, List<PortBinding>> portBindings = new HashMap<String, List<PortBinding>>();
            for (String port : ports) {
                List<PortBinding> hostPorts = new ArrayList<PortBinding>();
                hostPorts.add(PortBinding.of("0.0.0.0", port));
                portBindings.put(image.getExposedPorts()[0], hostPorts);
            }

            HostConfig hostConfig = null;
            if(!image.getVolumes().equals("")){
                hostConfig = HostConfig.builder().
                        portBindings(portBindings).
                        privileged(image.isPrivileged()).
                        binds(image.getVolumes()).
                        build();
            } else {
                hostConfig = HostConfig.builder().
                        portBindings(portBindings).
                        privileged(image.isPrivileged()).
                        build();
            }

            final ContainerConfig containerConfig = ContainerConfig.builder()
                    .hostConfig(hostConfig)
                    .image(serviceKey)
                    .exposedPorts(exposedPorts)
                    .build();

            String uuid = UUID.randomUUID().toString();
            String containerName = image.getName()+"_"+uuid;
            final ContainerCreation creation = docker.createContainer(containerConfig, containerName);
            final String id = creation.id();

            // Start container
            docker.startContainer(id);
            return new DockerContainer(id, serviceKey, Integer.valueOf(ports[0]), containerName, image);
        } catch (DockerCertificateException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        } catch (DockerException e) {
            e.printStackTrace();
            return null;
        }
    }



    public void stopContainer(String dockerId){
        try {
            createDockerClient();
            // Kill container
            docker.killContainer(dockerId);
            log.info("Killing container with Id: "+dockerId);
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerException e) {
            e.printStackTrace();
        }
    }

    public void removeContainer(String dockerId){
        try {
            createDockerClient();
            // Kill container
            docker.removeContainer(dockerId);
            log.info("Removing container with Id: "+dockerId);
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerException e) {
            e.printStackTrace();
        }
    }

    public void stopRunningContainers(){
        try {
            createDockerClient();
            List<Container> containers = docker.listContainers();
            for(Container c : containers){
                stopContainer(c.id());
                removeContainer(c.id());
            }
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public void stopContainerByService(String serviceKey){
        try {
            createDockerClient();
            List<Container> containers = docker.listContainers();
            for(Container c : containers){
                if(c.image().equals(serviceKey)){
                    stopContainer(c.id());
                    removeContainer(c.id());
                }
            }
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
    }

    public void stopContainerById(String dockerId){
        stopContainer(dockerId);
        removeContainer(dockerId);
    }

    public List<Container> getRunningContainers(){
        try {
            createDockerClient();
            return docker.listContainers();
        } catch (DockerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (DockerCertificateException e) {
            e.printStackTrace();
        }
        return new ArrayList<Container>();
    }
}



