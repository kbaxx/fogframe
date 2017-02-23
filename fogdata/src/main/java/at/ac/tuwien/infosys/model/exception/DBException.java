package at.ac.tuwien.infosys.model.exception;

/**
 * Created by Kevin Bachmann on 10/11/2016.
 */
public class DBException extends Exception {
    public DBException(){
        super("DB Exception: Local redis database could not be started or connected. Check the path to the executable and the port.");
    }
}
