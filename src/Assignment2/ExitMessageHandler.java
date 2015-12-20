package Assignment2;

import java.util.concurrent.TimeUnit;

/**
 * Created by walker on 2015/12/20.
 */
public class ExitMessageHandler implements Runnable {
    private Exit exit;

    public ExitMessageHandler(Exit exit) {
        this.exit = exit;
    }

    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
//            System.out.println("Here is Exit at " + exit.getInfo());
//            System.out.println("Now has occupied places number : " + exit.getOccupiedNum());
//            System.out.println("Now has exit car number : " + exit.getExitNum());
            if (exit.hasMessage()) {
                exit.tick();
                Message message = exit.getAndRemoveEarliestMessage();
                switch (message.getType()) {

                    case REQUEST: {

                    }
                    break;

                    case REPLY: {

                    }
                    break;

                    case INFORM_ENTER: {
                        exit.handleInformEnterFrom(message.getSource());
                    }
                    break;

                    case INFORM_EXIT:{
                        exit.handleInformExitFrom(message.getSource());
                    }
                    break;

                    case UPDATE: {
                        exit.handleUpdateFrom(message.getSource());
                    }
                    break;

                    case UPDATE_REPLY:{
                        exit.handleUpdateReply(message.getData());
                    }
                    break;
                }


            }
        }
    }
}
