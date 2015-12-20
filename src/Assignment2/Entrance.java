package Assignment2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

/**
 * Created by walker on 2015/12/14.
 */
public class Entrance {

    private PortInfo info;

    private int timeStamp;

    private List<PortInfo> entrances;

    private List<PortInfo> exits;

    private PortType type;

    private PortStatus status;

    private ServerSocket serverSocket;

    private int parkingSpaceNum;

    private int occupiedNum;

    private int enterNum;

    private Set<PortInfo> repliedPorts;

    public PriorityQueue<Message> messages;

    public Entrance(PortInfo info, int parkingSpaceNum) {

        this.info = info;

        this.timeStamp = 0;

        this.entrances = new ArrayList<>();

        this.exits = new ArrayList<>();

        this.type = PortType.ENTRANCE;

        this.status = PortStatus.RELEAS;

        this.parkingSpaceNum = parkingSpaceNum;

        this.occupiedNum = 0;

        this.enterNum = 0;

        this.messages = new PriorityQueue<Message>(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                if (o1.getTimeStamp() < o2.getTimeStamp()) {
                    return -1;
                } else if (o1.getTimeStamp() > o2.getTimeStamp()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });

        try {
            this.serverSocket = new ServerSocket(info.port);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void enterRequest() {

        Message request = new Message();
        this.tick();
        request.setTimeStamp(this.timeStamp);
        request.setSource(this.info);
        request.setType(MessageType.REQUEST);
        sendMsgToAll(request);

    }

    public void sendMsgToAll(Message msg) {
        for (PortInfo port : entrances) {
            if (port == this.info) {
                messages.add(msg);
            } else {
                try {
                    Socket socket = new Socket(port.getIp(), port.getPort());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void receiveRequestFrom(PortInfo port) {

        Message reply = new Message();
        reply.setTimeStamp(this.getTimeStamp());
        reply.setSource(this.getInfo());
        reply.setType(MessageType.REPLY);
        try {

            Socket socket = new Socket(this.getInfo().getIp(), this.getInfo().getPort());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(reply);
            oos.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void receiveReplyFrom(PortInfo port) {
        this.repliedPorts.add(port);
        if (this.repliedPorts.containsAll(entrances)) {
            // 收到所有出入口的回复，允许车辆进入
            System.out.print("车辆进入停车场入口" + this.info.getPort());
            this.enterNum++;
            this.informEnterEvent();
            this.repliedPorts.clear();
        }
    }

    public void receiveInformEnterFrom(PortInfo port) {
        this.occupiedNum++;
    }

    // 通知其他所有出入口车辆进入的信息
    public void informEnterEvent() {
        Message informMessage = new Message();
        informMessage.setSource(this.info);
        this.tick();
        informMessage.setTimeStamp(this.timeStamp);
        informMessage.setType(MessageType.INFORM_ENTER);
        this.sendMsgToAll(informMessage);
    }

    public static void main(String[] args) {

        int portNum = Integer.parseInt(args[0]);
        PortInfo entranceInfo = new PortInfo("127.0.0.1", portNum);

        Entrance entrance = new Entrance(entranceInfo, Config.getParkingSpaceNum());
        System.out.println(entrance.getServerSocket() == null);

        (new Thread(new EntranceMessageListener(entrance))).start();
        (new Thread(new EntranceMessageHandler(entrance))).start();
        (new Thread(new CommandLineListener(entrance))).start();

    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public synchronized void receiveMessage(Message message) {
        this.timeStamp = Math.max(message.getTimeStamp(), this.timeStamp); // 收到消息时更新本地时钟
        messages.add(message);
    }

    public boolean hasMessage() {
        return messages.size() != 0;
    }

    public Message getAndRemoveEarliestMessage() {
        synchronized (this) {
            return messages.poll();
        }
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public synchronized void tick() {
        this.timeStamp++;
    }

    public PortInfo getInfo() {
        return this.info;
    }
}
