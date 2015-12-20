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
                entrance.receiveMessage(message);
                System.out.println("Get message from " + message.getSource());
                switch (message.getType()) {
                    case REPLY:
                        System.out.println(". Message type is reply");
                        break;
                    case REQUEST:
                        System.out.println(". Message type is request");
                        break;
                    case INFORM_ENTER:
                        System.out.println(". Message type is inform enter");
                        break;
                    case UPDATE:
                        System.out.println(". Message type is update");
                        break;
                }
                ois.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
