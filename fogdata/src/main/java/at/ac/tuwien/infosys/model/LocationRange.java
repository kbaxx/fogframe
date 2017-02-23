package at.ac.tuwien.infosys.model;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 */
@Data
public class LocationRange implements Serializable {

    private Location location1;
    private Location location2;

    public LocationRange(Location location1, Location location2){
        this.location1 = location1;
        this.location2 = location2;
    }

    public LocationRange() {}

    public boolean isInside(Location loc){
        long maxLat=0, minLat=0, maxLong=0, minLong=0;
        long loc1Lat = location1.getLatitude();
        long loc1Long = location1.getLongitude();
        long loc2Lat = location2.getLatitude();
        long loc2Long = location2.getLongitude();

        if(loc1Lat > loc2Lat){
            maxLat = loc1Lat;
            minLat = loc2Lat;
        } else {
            maxLat = loc2Lat;
            minLat = loc1Lat;
        }
        if(loc1Long > loc2Long){
            maxLong = loc1Long;
            minLong = loc2Long;
        } else {
            maxLong = loc2Long;
            minLong = loc1Long;
        }

        long lat = loc.getLatitude();
        long lo = loc.getLongitude();
        if(lat >= minLat && lat <= maxLat && lo >= minLong && lo <= maxLong){
            return true;
        }
        return false;
    }
}
