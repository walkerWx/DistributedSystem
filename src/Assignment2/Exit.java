package Assignment2;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by walker on 2015/12/14.
 */
public class Exit {

    private PortInfo info;

    private PortType type;

    private int timeStamp;

    private List<PortInfo> entrances;

    private List<PortInfo> exits;

    private ServerSocket serverSocket;

    private int parkingSpaceNum;

    private int occupiedNum;

    private int exitNum;

    private int repliedPortNum;

    public PriorityQueue<Message> messages;

    public Exit(PortInfo info) {

        this.info = info;

        this.timeStamp = 0;

        this.entrances = Config.getEntranceList();

        this.exits = Config.getExitList();

        this.type = PortType.EXIT;

        this.parkingSpaceNum = Config.getParkingSpaceNum();

        this.occupiedNum = 0;

        this.exitNum = 0;

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

        // 通知所有的出入口增加了新的出口
        Message update = new Message();
        update.setSource(this.info);
        update.setTimeStamp(this.timeStamp);
        update.setType(MessageType.UPDATE);
        sendMsgToAll(update);
    }

    public void exitRequest() {

        if (occupiedNum == 0) {
            System.out.println("ERROR: There is NO CAR in the parking place!");
            return;
        } else {
            this.exitNum++;
            System.out.println("INFO: Letting a car out...");
        }
        // 通知其他出入口有车辆驶出停车场
        informExitEvent();
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

    public void sendMsgToExits(Message msg) {

        for (PortInfo exit : exits) {
            if (exit == this.info) {
                messages.add(msg);
            } else {
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

    }

    public void sendMsgToAll(Message msg) {
        sendMsgToEntrances(msg);
        sendMsgToExits(msg);
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

    // 通知其他所有出入口车辆出去的信息
    public void informExitEvent() {
//        System.out.print("Now informing other ports exiting a car");
        Message informMessage = new Message();
        informMessage.setSource(this.info);
        this.tick();
        informMessage.setTimeStamp(this.timeStamp);
        informMessage.setType(MessageType.INFORM_EXIT);
        this.sendMsgToAll(informMessage);
    }

    public static void main(String[] args) {

        int portNum = Integer.parseInt(args[0]);
        PortInfo exitInfo = new PortInfo("127.0.0.1", portNum);

        // 通过全局的配置文件添加一个出口
        Config.addPort(exitInfo, PortType.EXIT);
        Exit exit = new Exit(exitInfo);

        (new Thread(new ExitMessageListener(exit))).start();
        (new Thread(new ExitMessageHandler(exit))).start();
        (new Thread(new ExitCmdListener(exit))).start();

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

    public int getExitNum() {
        return this.exitNum;
    }

    public void showUsage() {
        System.out.print("Please enter [ EXIT ] for leaving a car");
        System.out.println(" or [ SHOW ] to show the status of this port");
    }

    public void showStatus() {
        System.out.println("This is an Exit at " + info);
        System.out.println("Current time is (as an integer) " + timeStamp);
        System.out.println("There are " + messages.size() + " messages to be handled");
        System.out.println("Total parking spaces : " + parkingSpaceNum);
        System.out.println("Occupied parking spaces : " + occupiedNum);
        System.out.println("Total exited cars : " + exitNum);
    }
}
