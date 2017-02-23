package at.ac.tuwien.infosys.model;

import lombok.Data;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by Kevin Bachmann on 02/11/2016.
 */
@Data
public class Message implements Serializable {

    private String id;
    private String header;
    private String payload;
    private String timestamp;
    private boolean status;

    public Message() { }

    public Message(String header, String payload, boolean status) {
        this.id = UUID.randomUUID().toString();
        this.header = header;
        this.payload = payload;
        this.status = status;
        this.timestamp = new DateTime(DateTimeZone.UTC).toString();
    }

    public Message(String header, String payload) {
        this.id = UUID.randomUUID().toString();
        this.header = header;
        this.payload = payload;
        this.status = true;
        this.timestamp = new DateTime(DateTimeZone.UTC).toString();
    }

    public Message(String header) {
        this.id = UUID.randomUUID().toString();
        this.header = header;
        this.payload = null;
        this.status = true;
        this.timestamp = new DateTime(DateTimeZone.UTC).toString();
    }

    public Message(String header, boolean status) {
        this.id = UUID.randomUUID().toString();
        this.header = header;
        this.payload = null;
        this.status = status;
        this.timestamp = new DateTime(DateTimeZone.UTC).toString();
    }
}
