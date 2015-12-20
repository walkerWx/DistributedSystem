package Assignment2;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

/**
 * Created by walker on 2015/12/19.
 */
public class EntranceMessageHandler implements Runnable {

    private Entrance entrance;

    public EntranceMessageHandler(Entrance entrance) {
        this.entrance = entrance;
    }

    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Here is Entrance at " + entrance.getInfo());
            System.out.println("Now has occupied places number : " + entrance.getOccupiedNum());
            System.out.println("Now has enter car number : " + entrance.getEnterNum());
            if (entrance.hasMessage()) {
                System.out.println("Has message!");
                entrance.tick();
                Message message = entrance.getAndRemoveEarliestMessage();
                switch (message.getType()) {

                    case REQUEST: {
                        entrance.handleRequestFrom(message.getSource());
                    }
                    break;

                    case REPLY: {
                        entrance.handleReplyFrom(message.getSource());
                    }
                    break;

                    case INFORM_ENTER: {
                        entrance.handleInformEnterFrom(message.getSource());
                    }

                    case UPDATE: {
                        entrance.updatePorts();
                    }
                }


            }
        }
    }
}

