package Assignment2;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * Created by walker on 15/12/18.
 */
public class EntranceMessageListener implements Runnable {

    private Entrance entrance;

    public EntranceMessageListener(Entrance entrance) {
        this.entrance = entrance;
    }

    public void run() {

        Socket socket = null;
        ObjectInputStream ois = null;
        Message message = null;

        while (true) {
            try {

                TimeUnit.MILLISECONDS.sleep(500);
                socket = this.entrance.getServerSocket().accept();
                ois = new ObjectInputStream(socket.getInputStream());
                message = (Message) ois.readObject();
                entrance.addMessage(message);
                System.out.println("Get message " + entrance.messages.size());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
