package shared;

import java.io.Serial;
import java.io.Serializable;

public class Message implements Serializable {
    private MessageType type;
    private String payload;

    public Message(MessageType type, String payload) {
        this.type = type;
        this.payload = payload;
    }

    public MessageType getType() {return type;}

    public String getPayload() {return payload;}
}
