package Assignment2;

import java.io.Serializable;
import java.security.Timestamp;

/**
 * Created by walker on 2015/12/14.
 */
public class Message implements Serializable{

    private int timeStamp;

    private PortInfo source;

    private MessageType type;

    private int data;

    public int getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(int timeStamp) {
        this.timeStamp = timeStamp;
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

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }
}

enum MessageType {
    REQUEST, REPLY, INFORM_ENTER, INFORM_EXIT, UPDATE, UPDATE_REPLY
}
