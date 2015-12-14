package Assignment2;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadFactory;

/**
 * Created by walker on 2015/12/14.
 */
public class Entrance implements PortI, Runnable {

    private int id;

    private int clock;

    private List<PortInfo> ports;

    private PortType type;

    private PortStatus status;

    private ServerSocket serverSocket;

    private int totalNum;

    private int occupiedNum;

    private int enterNum;

    public void enterRequest() {

        Message msg = new Message();
        msg.setClock(this.clock);
        msg.setSource(this.id);
        msg.setType(MessageType.REQUEST);
        sendMsg(msg);

    }

    public void sendMsg(Message msg) {
        for (PortInfo port : ports) {
            if (port.id == this.id) continue;
            try {
                Socket socket = new Socket(port.host, port.id);
                ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        while (true) {
            try {

                System.out.print("Here is Entrance " + id + ": ");
                System.out.println("there are total " + totalNum + " positions in the parking lot, " + occupiedNum + " occupied, " + (totalNum - occupiedNum) + " empty left.");
                System.out.print("Is there a car?[y/n]");

                Thread.sleep(1000);

                Scanner sc = new Scanner(System.in);
                if (sc.hasNext()) {
                    if (sc.next().equalsIgnoreCase("y")) {
                        // There is a car entering

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
