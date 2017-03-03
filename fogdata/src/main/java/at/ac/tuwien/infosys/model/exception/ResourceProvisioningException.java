package at.ac.tuwien.infosys.model.exception;

import at.ac.tuwien.infosys.model.ApplicationAssignment;

/**
 * Created by Kevin Bachmann on 03/03/2017.
 */
public class ResourceProvisioningException extends Exception {

    private Exception cause;
    private ApplicationAssignment assignment;

    public ResourceProvisioningException(String message, ApplicationAssignment ass, Exception cause){
        super(message);
        this.cause = cause;
        this.assignment = ass;
    }

    @Override
    public Exception getCause() {
        return cause;
    }

    public void setCause(Exception cause) {
        this.cause = cause;
    }

    public ApplicationAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(ApplicationAssignment assignment) {
        this.assignment = assignment;
    }
}
