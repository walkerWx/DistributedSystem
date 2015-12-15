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

    private int totalNum;

    private int occupiedNum;

    private int enterNum;

    private PriorityQueue<Message> requests;

    public Entrance(PortInfo info, int totalNum) {
        this.info = info;
        this.clock = 0;
        this.ports = new ArrayList<>();
        this.type = PortType.ENTRANCE;
        this.status = PortStatus.RELEAS;
        this.totalNum = totalNum;
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
        msg.setSource(this.info.id);
        msg.setType(MessageType.REQUEST);
        sendMsg(msg);

    }

    public void sendMsg(Message msg) {
        for (PortInfo port : ports) {
            if (port.id == this.info.id) {
                requests.add(msg);
            }
            try {
                Socket socket = new Socket(port.host, port.id);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
                oos.close();
                socket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        while (true) {
            try {

                System.out.print("Here is Entrance " + info.id + ": ");
                System.out.println("there are total " + totalNum + " positions in the parking lot, " + occupiedNum + " occupied, " + (totalNum - occupiedNum) + " empty left.");
                System.out.print("Is there a car?[y/n]");

                Thread.sleep(1000);

                Scanner sc = new Scanner(System.in);
                if (sc.hasNext()) {
                    if (sc.next().equalsIgnoreCase("y")) {
                        // There is a car entering
                        enterRequest();
                    } else if (sc.next().equalsIgnoreCase("n")) {
                        // No car is entering
                        System.out.println("No car is entering! Continue...");
                    } else {
                        // Invalid input
                        System.out.println("Unrecognized input! Continue...");
                    }
                } else {
                    System.out.println("No input detected! Continue...");
                }

                Socket socket = serverSocket.accept();
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                Message msg = (Message) ois.readObject();

                switch (msg.getType()) {
                    case REPLY: {

                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
