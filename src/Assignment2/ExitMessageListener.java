package Assignment2;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by walker on 2015/12/20.
 */
public class ExitMessageListener implements Runnable{

    private Exit exit;

    public ExitMessageListener(Exit exit) {
        this.exit = exit;
    }

    public void run(){
        Socket socket = null;
        ObjectInputStream ois = null;
        Message message = null;

        while (true) {
            try {

                TimeUnit.MILLISECONDS.sleep(500);
                socket = this.exit.getServerSocket().accept();
                ois = new ObjectInputStream(socket.getInputStream());
                message = (Message) ois.readObject();
                exit.receiveMessage(message);
                System.out.print("Get message from " + message.getSource());
                switch (message.getType()) {
                    case REPLY:
                        System.out.println(". Message type is REPLY");
                        break;
                    case REQUEST:
                        System.out.println(". Message type is REQUEST");
                        break;
                    case INFORM_ENTER:
                        System.out.println(". Message type is INFORM ENTER");
                        break;
                    case INFORM_EXIT:
                        System.out.println(". Message type is INFORM EXIT");
                        break;
                    case UPDATE:
                        System.out.println(". Message type is UPDATE");
                        break;
                    case UPDATE_REPLY:
                        System.out.println(". Message type is UPDATE REPLY");
                        break;
                }
                ois.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
