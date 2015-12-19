package Assignment2;

import java.io.ObjectOutputStream;
import java.net.Socket;

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
            if (entrance.hasMessage()) {
                entrance.tick();
                Message message = entrance.getAndRemoveEarliestMessage();
                switch (message.getType()) {

                    case REQUEST: {
                        entrance.receiveRequestFrom(message.getSource());
                    }
                    break;

                    case REPLY: {
                        entrance.receiveReplyFrom(message.getSource());
                    }
                    break;

                    case INFORM_ENTER:{
                        entrance.receiveInformEnterFrom(message.getSource());
                    }
                }


            }
        }
    }
}

