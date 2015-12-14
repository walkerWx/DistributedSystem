package Assignment2;

/**
 * Created by walker on 2015/12/14.
 */
public interface PortI {

    void sendMsg(Message msg);

}

enum PortType {
    ENTRANCE, EXIT
}

enum PortStatus {
    RELEAS,
    BLOCK,
    MUTEX,
}
