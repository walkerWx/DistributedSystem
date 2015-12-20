package Assignment2;

import java.util.Scanner;

/**
 * Created by walker on 2015/12/19.
 */
public class EntranceCmdListener implements Runnable {

    private Entrance entrance;

    public EntranceCmdListener(Entrance entrance) {
        this.entrance = entrance;
    }

    public void run() {
        Scanner scanner = null;
        String command = null;
        while (true) {
            try {
                entrance.showUsage();
                scanner = new Scanner(System.in);
                command = scanner.next();
                if (command.equalsIgnoreCase("enter")) {
                    entrance.enterRequest();
                } else if (command.equalsIgnoreCase("show")) {
                    entrance.showStatus();
                } else {
                    System.err.println("Unrecognized command : " + command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
