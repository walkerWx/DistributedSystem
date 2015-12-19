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
public class Entrance implements Runnable {

    private PortInfo info;

    private int timeStamp;

    private List<PortInfo> ports;

    private PortType type;

    private PortStatus status;

    private ServerSocket serverSocket;

    private int parkingSpaceNum;

    private int occupiedNum;

    private int enterNum;

    private Set<PortInfo> repliedPorts;

    public PriorityQueue<Message> messages;

    public Entrance() {
    }

    public Entrance(PortInfo info, int parkingSpaceNum) {
        this.info = info;
        this.timeStamp = 0;
        this.ports = new ArrayList<>();
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

        Message msg = new Message();
        this.tick();
        msg.setTimeStamp(this.timeStamp);
        msg.setSource(this.info);
        msg.setType(MessageType.REQUEST);
        sendMsgToAll(msg);

    }

    public void sendMsgToAll(Message msg) {
        for (PortInfo port : ports) {
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
        if (this.repliedPorts.containsAll(ports)) {
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

    public void run() {

        Socket socket = null;
        ObjectInputStream ois = null;
        Message message = null;

        while (true) {
            try {

                System.out.print("Here is Entrance with host " + info.getIp() + " and port " + info.getPort());
                System.out.println("there are total " + parkingSpaceNum + " positions in the parking lot, " + occupiedNum + " occupied, " + (parkingSpaceNum - occupiedNum) + " empty left.");
                System.out.print("Is there a car?[y/n]");

                Thread.sleep(1000);

//                Scanner sc = new Scanner(System.in);
//                if (sc.hasNext()) {
//                    if (sc.next().equalsIgnoreCase("y")) {
//                        // There is a car entering
//                        enterRequest();
//                    } else if (sc.next().equalsIgnoreCase("n")) {
//                        // No car is entering
//                        System.out.println("No car is entering! Continue...");
//                    } else {
//                        // Invalid input
//                        System.out.println("Unrecognized input! Continue...");
//                    }
//                } else {
//                    System.out.println("No input detected! Continue...");
//                }

                socket = serverSocket.accept();
                ois = new ObjectInputStream(socket.getInputStream());
                message = (Message) ois.readObject();
                switch (message.getType()) {
                    case REQUEST: {

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

//        int portNum = Integer.parseInt(args[0]); // 通过命令行参数获取端口号
        int portNum = 26490;
        PortInfo entranceInfo = new PortInfo("localhost", portNum);

        Entrance entrance = new Entrance(entranceInfo, Config.PARKING_SPACE_NUM);
        System.out.println(entrance.getServerSocket() == null);
//        Thread t = new Thread(entrance, "Entrance " + portNum);
//        t.start();
//
//        // 持续地获取用户输入,当收到enter请求时,给其余的进出口发送Request
//        Scanner scanner = new Scanner(System.in);
//        while (true) {
//            String cmd = scanner.next();
//            if (cmd.equalsIgnoreCase("enter")) {
//                entrance.enterRequest();
//            }
//        }

        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.submit(new EntranceMessageListener(entrance));
        executor.shutdown();

    }

    public void test() {
        ExecutorService executor = Executors.newFixedThreadPool(2);
        IntStream.range(0, 10000).forEach(i -> executor.submit(this::increment));
        executor.shutdown();
        System.out.print(this.occupiedNum);
    }

    public int increment() {
//        synchronized (this) {
        occupiedNum++;
        return occupiedNum;
//        }
    }

    public int decrement() {
//        synchronized (this) {
        occupiedNum--;
        return occupiedNum;
//        }
    }

    public ServerSocket getServerSocket() {
        return this.serverSocket;
    }

    public synchronized void receiveMessage(Message message) {
        this.timeStamp = Math.max(message.getTimeStamp(), this.timeStamp) + 1; // 收到消息时更新本地时钟
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
