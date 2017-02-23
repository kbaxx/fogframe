package at.ac.tuwien.infosys.cloud.impl;

import at.ac.tuwien.infosys.cloud.ICloudProviderService;
import at.ac.tuwien.infosys.cloud.ICloudService;
import at.ac.tuwien.infosys.communication.IRequestService;
import at.ac.tuwien.infosys.model.*;
import at.ac.tuwien.infosys.util.Constants;
import at.ac.tuwien.infosys.util.DeviceType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Kevin Bachmann on 13/12/2016.
 * Service to start and stop VMs in the cloud and start, stop and migrate containers on these started cloud VMs.
 */
@Service
@Slf4j
public class CloudService implements ICloudService {

    @Autowired
    private IRequestService requestService;

    @Autowired
    private ICloudProviderService openStackService;

    /**
     * Map that maps the VM hosts with the list of running service assignments (containers)
     */
    private Map<DockerHost, List<ServiceAssignment>> vmMappings = new HashMap<DockerHost, List<ServiceAssignment>>();


    public void sendDataToDeployedCloudService(List<ServiceData> data) {
        if(data.size() == 0){
            log.warn("---- Received empty service data list");
            return;
        }
        int size = vmMappings.size();
        if(size == 0){
            log.warn("---- No cloud service deployed to send the propagated data to");
            return;
        } else if (size > 1){
            size = size -1;
        }

        // select a random host
        Random r = new Random();
        int rand = r.nextInt(size);
        DockerHost host = (DockerHost) new ArrayList(vmMappings.keySet()).get(rand);

        ServiceData temp = data.get(0);
        List<ServiceAssignment> assignments = vmMappings.get(host);
        List<ServiceAssignment> suitableContainers = new ArrayList<ServiceAssignment>();
        // create a list of suitable containers
        for(ServiceAssignment s: assignments){
            if(s.getImage().getServiceKey().equals(temp.getReceiverServiceKey())){
//                port = s.getContainer().getPort();
                suitableContainers.add(s);
            }
        }
        int port = -1;
        if(suitableContainers.size() > 0){
            // randomly select a container
            rand = r.nextInt(suitableContainers.size());
            ServiceAssignment s = suitableContainers.get(rand);
            port = s.getContainer().getPort();
        }

        if(port == -1){
            outerloop:
            // the randomly selected host does not have an appropriate service running -> check all others and take the first fitting one
            for(Map.Entry e : vmMappings.entrySet()){
                DockerHost dh = (DockerHost) e.getKey();
                List<ServiceAssignment> list = (List<ServiceAssignment>) e.getValue();
                for(ServiceAssignment ass : list){
                    if(ass.getImage().getServiceKey().equals(temp.getReceiverServiceKey())){
                        port = ass.getContainer().getPort();
                        host = dh;
                        break outerloop;
                    }
                }
            }
            if(port == -1){
                // there is no appropriate service running
                log.warn("No fitting cloud service found for: "+data);
                return;
            }
        }

        Fogdevice cloudService = new Fogdevice(host.getName(), DeviceType.CLOUD_SERVICE, host.getUrl(),
                port, null, null);

        for(ServiceData sd: data){
            String parentkey = sd.getKey();
            for(HashMap<?,?> m : sd.getData()){
                for(Map.Entry<?,?> e: m.entrySet()){
                    String key = parentkey + "_" + String.valueOf(e.getKey());
                    double v = Double.parseDouble(String.valueOf(e.getValue()));
                    String value = String.format("%.2f", new BigDecimal(v)).replace(".",",");
                    requestService.sendRequest(cloudService, "/db/" + key + "/" + value, HttpMethod.POST, null,
                            new ParameterizedTypeReference<Object>() {});
                }
            }
        }
    }

    /**
     * Starts a new VM and deploys a container with the passed image on it.
     * @param image docker image to deploy on the VM
     * @return the task assignment host, image, and container
     * @throws Exception throws an exception if something goes wrong
     */
    private TaskAssignment deployVMAndContainer(DockerImage image) throws Exception {
        DockerHost deployedHost = openStackService.startVM(new DockerHost(image.getName(), "m1.micro"));
        if(image.getExposedPorts() != null && image.getExposedPorts().length < 1){
            image.setExposedPorts(new String[]{String.valueOf(Constants.PORT_CLOUD_SERVICE)});
        }
        vmMappings.put(deployedHost, new ArrayList<ServiceAssignment>());

        // start the container
        DockerContainer container = startDockerContainerOnVM(deployedHost.getUrl(), image);

        Fogdevice fd = new Fogdevice(deployedHost.getName(), DeviceType.CLOUD_SERVICE, deployedHost.getUrl(),
                Constants.PORT_CLOUD_SERVICE, null, null);
        return new TaskAssignment(fd, null, container, true);
    }

    /**
     * Deploys a container on a already running VM with the passed url.
     * @param url url of the host VM
     * @param image image to deploy on the VM
     * @return returns the resulting docker container
     * @throws Exception throws an exception if something goes wrong
     */
    private DockerContainer startDockerContainerOnVM(String url, DockerImage image) throws Exception {
        DockerContainer container = openStackService.startDockerContainer(url, image);
        if(container == null || container.getContainerId() == null || container.getContainerId() == ""){
            throw new Exception("Container of image "+image.getName()+" couldnt be deployed.");
        }

        // add to mapping
        DockerHost h = getDockerHostFromMap(url);
        if(h==null)
            throw new Exception("Could not find VM with that URL in VM mapping.");
        List<ServiceAssignment> list = vmMappings.get(h);
        list.add(new ServiceAssignment(h, image, container));

        return container;
    }

    /**
     * Returns the docker host in the vm mappings according to the passed url.
     * @param url url of the required host object
     * @return wanted docker host object with all its additional information
     */
    private DockerHost getDockerHostFromMap(String url){
        for(DockerHost h : vmMappings.keySet()){
            if(h.getUrl().equals(url)) return h;
        }
        return null;
    }

    /**
     * Returns the amount of containers by docker host in the vm mappings
     * @param dh docker host (key)
     * @return amount of containers on the passed host
     */
    private int getContainersPerVM(DockerHost dh){
        return vmMappings.get(dh).size();
    }

    public TaskAssignment deployService(DockerImage image) throws Exception {
        DockerHost host = null;
        for(Map.Entry e : vmMappings.entrySet()) {
            DockerHost dh = (DockerHost) e.getKey();
            List<ServiceAssignment> list = (List<ServiceAssignment>) e.getValue();
            if(list.size() < Constants.MAX_CONTAINERS){
                host = dh;
            }
        }
        if(host == null){
            // no VM running or all fully loaded -> deploy one and run the container on it
            return deployVMAndContainer(image);
        }
        // create docker container on selected VM
        DockerContainer container = startDockerContainerOnVM(host.getUrl(), image);

        Fogdevice fd = new Fogdevice(host.getName(), DeviceType.CLOUD_SERVICE, host.getUrl(),
                Constants.PORT_CLOUD_SERVICE, null, null);
        return new TaskAssignment(fd, null, container, true);
    }

    /**
     * Stops the VM running on the passed docker host
     * @param dh docker host to stop
     * @return message indicating the status of the stopping
     */
    private Message stopVM(DockerHost dh){
        openStackService.stopDockerHost(dh.getName());
        vmMappings.remove(dh);
        log.info("Stopping VM: "+dh.getUrl());
        return new Message("Cloud.StopVM", true);
    }

    /**
     * Stops the container with the passed container id running on the host with the passed url.
     * @param url url the container is running on
     * @param containerId id of the container to stop
     * @return message indicating the status of the stopping
     */
    private Message stopContainer(String url, String containerId){
        openStackService.stopDockerContainer(url, containerId);

        // remove container from mapping
        removeContainerMapping(url, containerId);
        return new Message("Cloud.StopContainer", true);
    }

    public void stopService(String containerId){
        String url = "";
        for(Map.Entry e : vmMappings.entrySet()){
            DockerHost dh = (DockerHost) e.getKey();
            List<ServiceAssignment> list = (List<ServiceAssignment>) e.getValue();
            for(ServiceAssignment sa: list) {
                if (sa.getContainer().getContainerId().equals(containerId)) {
                    url = dh.getUrl();
                }
            }
        }
        if(!url.isEmpty()) {
            stopContainer(url, containerId);
        }
        stopEmptyVMs();
    }

    /**
     * Stops all the VMs that do not have any containers deployed
     */
    private void stopEmptyVMs(){
        // if the vm does not have any containers, stop VM
        // TODO: check concurrent mod exception
        for(Map.Entry e : vmMappings.entrySet()){
            DockerHost dh = (DockerHost) e.getKey();
            List<ServiceAssignment> list = (List<ServiceAssignment>) e.getValue();
            if(list.size() == 0){
                stopVM(dh);
            }
        }
    }

    /**
     * Removes the container mapping from the vm assignments
     * @param url url the host is running on
     * @param containerId id of the container to remove
     */
    private void removeContainerMapping(String url, String containerId){
        DockerHost dh = getDockerHostFromMap(url);
        List<ServiceAssignment> l = vmMappings.get(dh);
        Iterator<ServiceAssignment> iterator = l.iterator();
        while(iterator.hasNext()){
            ServiceAssignment s = iterator.next();
            if(s.getContainer().getContainerId().equals(containerId)){
                iterator.remove();
            }
        }
    }

    public Map<DockerHost, List<ServiceAssignment>> getVMMappings(){
        return this.vmMappings;
    }
}
