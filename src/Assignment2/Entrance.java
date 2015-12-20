package Assignment2;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by walker on 2015/12/14.
 */
public class Entrance {

    private PortInfo info;

    private int timeStamp;

    private List<PortInfo> entrances;

    private List<PortInfo> exits;

    private PortType type;

    private ServerSocket serverSocket;

    private int parkingSpaceNum;

    private int occupiedNum;

    private int enterNum;

    private int repliedPortNum;

    public PriorityQueue<Message> messages;

    public Entrance(PortInfo info) {

        this.info = info;

        this.timeStamp = 0;

        this.entrances = Config.getEntranceList();

        this.exits = Config.getExitList();

        this.type = PortType.ENTRANCE;

        this.parkingSpaceNum = Config.getParkingSpaceNum();

        this.occupiedNum = 0;

        this.enterNum = 0;

        this.repliedPortNum = 0;

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

        // 通知所有的出入口增加了新的入口
        Message update = new Message();
        update.setSource(this.info);
        update.setTimeStamp(this.timeStamp);
        update.setType(MessageType.UPDATE);
        sendMsgToAll(update);
    }

    public void enterRequest() {

        if(occupiedNum == parkingSpaceNum) {
            System.out.println("ERROR: There is no space left for cars!");
            return;
        }

        Message request = new Message();
        this.tick();
        request.setTimeStamp(this.timeStamp);
        request.setSource(this.info);
        request.setType(MessageType.REQUEST);
        sendMsgToAll(request);

    }

    private void sendMsgTo(Message msg, PortInfo port) {
        try {
            Socket socket = new Socket(port.getIp(), port.getPort());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(msg);
            oos.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void sendMsgToEntrances(Message msg) {
        for (PortInfo entrance : entrances) {
            if (entrance == this.info) {
                messages.add(msg);
            } else {
                try {
                    Socket socket = new Socket(entrance.getIp(), entrance.getPort());
                    ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                    oos.writeObject(msg);
                    oos.close();
                    socket.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void sendMsgToExits(Message msg) {
        for (PortInfo exit : exits) {
            try {
                Socket socket = new Socket(exit.getIp(), exit.getPort());
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.writeObject(msg);
                oos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMsgToAll(Message msg) {
        sendMsgToEntrances(msg);
        sendMsgToExits(msg);
    }

    public void handleRequestFrom(PortInfo port) {

//        System.out.println("Handle Request from " + port);
        Message reply = new Message();
        reply.setTimeStamp(this.getTimeStamp());
        reply.setSource(this.getInfo());
        reply.setType(MessageType.REPLY);
        try {

            Socket socket = new Socket(port.getIp(), port.getPort());
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(reply);
            oos.close();
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void handleReplyFrom(PortInfo port) {
//        System.out.println("Handle reply from " + port);
        this.repliedPortNum++;
        if (this.repliedPortNum == entrances.size()) {
            // 收到所有出入口的回复，允许车辆进入
            System.out.println("INFO: A car is ENTERING the parking place.");
            this.enterNum++;
            this.informEnterEvent();
            this.repliedPortNum = 0;
        }
    }

    public synchronized void handleInformEnterFrom(PortInfo port) {
//        System.out.println("Handle inform enter from " + port);
        this.occupiedNum++;
    }

    public synchronized void handleInformExitFrom(PortInfo port) {
//        System.out.println("Handle inform exit from " + port);
        this.occupiedNum--;
    }

    public synchronized void handleUpdateFrom(PortInfo port) {
//        System.out.println("Update ports");
        this.entrances = Config.getEntranceList();
        this.exits = Config.getExitList();
        Message reply = new Message();
        reply.setSource(this.info);
        tick();
        reply.setTimeStamp(this.timeStamp);
        reply.setType(MessageType.UPDATE_REPLY);
        reply.setData(this.occupiedNum);
        sendMsgTo(reply, port);
    }

    // 新增出入口时需要通过回复的方式获取当前已经被使用的停车位
    public void handleUpdateReply(int occupiedNum) {
        this.occupiedNum = occupiedNum;
    }

    // 通知其他所有出入口车辆进入的信息
    public void informEnterEvent() {
//        System.out.print("Now informing other ports entering a car");
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

        // 通过全局的配置文件添加一个入口
        Config.addPort(entranceInfo, PortType.ENTRANCE);
        Entrance entrance = new Entrance(entranceInfo);

        (new Thread(new EntranceMessageListener(entrance))).start();
        (new Thread(new EntranceMessageHandler(entrance))).start();
        (new Thread(new EntranceCmdListener(entrance))).start();

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

    public int getOccupiedNum() {
        return this.occupiedNum;
    }

    public int getEnterNum() {
        return this.enterNum;
    }

    public void showUsage() {
        System.out.print("Please enter [ ENTER ] for entering a car");
        System.out.println(" or [ SHOW ] to show the status of this port");
    }

    public void showStatus() {
        System.out.println("This is an Entrance at " + info);
        System.out.println("Current time is (as an integer) " + timeStamp);
        System.out.println("There are " + messages.size() + " messages to be handled");
        System.out.println("Total parking spaces : " + parkingSpaceNum);
        System.out.println("Occupied parking spaces : " + occupiedNum);
        System.out.println("Total entered cars : " + enterNum);
    }
}
