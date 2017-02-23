package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 */
@Data
public class Location implements Serializable {

    private long latitude;
    private long longitude;

    public Location(){}

    public Location(long latitude, long longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
