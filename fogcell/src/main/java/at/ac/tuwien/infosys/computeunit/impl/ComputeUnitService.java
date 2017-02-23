package at.ac.tuwien.infosys.computeunit.impl;

import at.ac.tuwien.infosys.communication.ICommunicationService;
import at.ac.tuwien.infosys.computeunit.IComputeUnitService;
import at.ac.tuwien.infosys.model.ServiceData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Kevin Bachmann on 25/11/2016.
 */
@Service
@Slf4j
public class ComputeUnitService implements IComputeUnitService {

    @Autowired
    private ICommunicationService commService;

    @Override
    public void serviceData(List<ServiceData> data) {
        commService.propagateData(data);
    }
}
