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
                Message message = entrance.getAndRemoveEarliestMessage();
                switch (message.getType()) {

                    case REQUEST: {
                        Message reply = new Message();
                        reply.setTimeStamp(entrance.getTimeStamp());
                        reply.setSource(entrance.getInfo());
                        reply.setType(MessageType.REPLY);
                        try {

                            Socket socket = new Socket(entrance.getInfo().getIp(), entrance.getInfo().getPort());
                            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                            oos.writeObject(reply);
                            oos.close();
                            socket.close();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        entrance.tick(); // 发送消息成功后更新本地时钟
                        break;
                    }

                    case REPLY: {

                    }

                }
            }
        }
    }
}

