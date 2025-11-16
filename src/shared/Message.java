package shared;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Message implements Serializable {
    private LocalDateTime timeStamp;
    private MessageType type;
    private String payload;

    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }

    public Message(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
        this.timeStamp = LocalDateTime.now();
    }

    public MessageType getType() {return type;}

    public String getPayload() {return payload;}
}
