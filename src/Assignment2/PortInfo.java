package Assignment2;

import java.io.Serializable;

/**
 * Created by walker on 2015/12/14.
 */
public class PortInfo implements Serializable {

    String ip;
    int port;

    public PortInfo(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    @Override
    public boolean equals(Object that) {
        if (!(that instanceof PortInfo)) {
            return false;
        }

        return (this.ip == (((PortInfo) that).ip)) && (this.ip == (((PortInfo) that).ip));

    }
}
