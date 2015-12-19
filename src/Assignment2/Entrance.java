package Assignment2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

/**
 * Created by walker on 2015/12/14.
 */
public class Entrance implements PortI, Runnable {

    private PortInfo info;

    private int clock;

    private List<PortInfo> ports;

    private PortType type;

    private PortStatus status;

    private ServerSocket serverSocket;

    private int parkingSpaceNum;

    private int occupiedNum;

    private int enterNum;

    private PriorityQueue<Message> requests;

    public Entrance(PortInfo info, int parkingSpaceNum) {
        this.info = info;
        this.clock = 0;
        this.ports = new ArrayList<>();
        this.type = PortType.ENTRANCE;
        this.status = PortStatus.RELEAS;
        this.parkingSpaceNum = parkingSpaceNum;
        this.occupiedNum = 0;
        this.enterNum = 0;
        this.requests = new PriorityQueue<Message>(new Comparator<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                if (o1.getClock() < o2.getClock()) {
                    return -1;
                } else if (o1.getClock() > o2.getClock()) {
                    return 1;
                } else {
                    return 0;
                }
            }
        });
        try {
            serverSocket = new ServerSocket(info.port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void enterRequest() {

        Message msg = new Message();
        msg.setClock(this.clock);
        msg.setSource(this.info);
        msg.setType(MessageType.REQUEST);
        sendMsg(msg);

    }

    public void sendMsg(Message msg) {
        for (PortInfo port : ports) {
            if (port == this.info) {
                requests.add(msg);
            }
            try {
                Socket socket = new Socket(port.host, port.port);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {

        Socket socket = null;
        ObjectInputStream ois = null;
        Message message = null;

        while (true) {
            try {

                System.out.print("Here is Entrance with host " + info.host + " and port " + info.port);
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
                    case REQUEST:{

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        int portNum = Integer.parseInt(args[0]); // 通过命令行参数获取端口号
        PortInfo entranceInfo = new PortInfo("localhost", portNum);

        Entrance entrance = new Entrance(entranceInfo, Config.PARKING_SPACE_NUM);
        Thread t = new Thread(entrance, "Entrance " + portNum);
        t.start();

        // 持续地获取用户输入,当收到enter请求时,给其余的进出口发送Request
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String cmd = scanner.next();
            if (cmd.equalsIgnoreCase("enter")) {
                entrance.enterRequest();
            }
        }

    }

}
