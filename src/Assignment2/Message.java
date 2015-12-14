package Assignment2;

import java.security.Timestamp;

/**
 * Created by walker on 2015/12/14.
 */
public class Message {

    private int clock;

    private int source; // source port id

    private MessageType type;

    public int getClock() {
        return clock;
    }

    public void setClock(int clock) {
        this.clock = clock;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
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
