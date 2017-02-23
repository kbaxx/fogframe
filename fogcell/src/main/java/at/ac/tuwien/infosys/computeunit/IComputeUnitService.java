package at.ac.tuwien.infosys.computeunit;

import at.ac.tuwien.infosys.model.ServiceData;

import java.util.List;

/**
 * Created by Kevin Bachmann on 25/11/2016.
 * Service to handle the received service data from deployed service instances of the according device.
 */
public interface IComputeUnitService {

    /**
     * Receives, handles, and propagates the data coming from running services.
     * @param data service data list with data readings to propagate or process
     */
    void serviceData(List<ServiceData> data);
}

