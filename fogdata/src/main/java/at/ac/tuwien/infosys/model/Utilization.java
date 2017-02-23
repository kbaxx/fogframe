package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Kevin Bachmann on 18/11/2016.
 */
@Data
public class Utilization implements Serializable {

    private double cpu = 0;
    private double ram = 0;
    private double storage = 0;

    public Utilization(double cpu, double ram, double storage){
        this.cpu = cpu;
        this.ram = ram;
        this.storage = storage;
    }

    public Utilization(){}

    @Override
    public String toString() {
        return "Utilization{" +
                "cpu=" + cpu +
                ", ram=" + ram +
                ", storage=" + storage +
                '}';
    }
}
