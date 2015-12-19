package Assignment2;

import java.io.ObjectInputStream;
import java.net.Socket;

/**
 * Created by walker on 15/12/18.
 */
public class EntranceListener implements Runnable {

    private Socket socket;

    public EntranceListener(Socket socket) {
        this.socket = socket;
    }

    public void run() {
        ObjectInputStream ois = null;
        Message message = null;
        while (true) {
            try {
                ois = new ObjectInputStream(socket.getInputStream());
                message = (Message) ois.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
