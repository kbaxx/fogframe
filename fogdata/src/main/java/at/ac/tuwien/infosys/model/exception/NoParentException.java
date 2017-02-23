package at.ac.tuwien.infosys.model.exception;

/**
 * Created by Kevin Bachmann on 14/11/2016.
 */
public class NoParentException extends Exception {
    public NoParentException(){
        super("The device tries to communicate with its parent but does not have one associated.");
    }
}
