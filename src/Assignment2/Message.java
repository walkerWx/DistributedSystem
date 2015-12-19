package Assignment2;

import java.security.Timestamp;

/**
 * Created by walker on 2015/12/14.
 */
public class Message {

    private int clock;

    private PortInfo source;

    private MessageType type;

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public PortInfo getSource() {
        return source;
    }

    public void setSource(PortInfo source) {
        this.source = source;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }
}

enum MessageType {
    REQUEST, REPLY
}
