package Assignment2;

import java.util.Scanner;

/**
 * Created by walker on 2015/12/19.
 */
public class CommandLineListener implements Runnable {

    private Entrance entrance;

    public CommandLineListener(Entrance entrance) {
        this.entrance = entrance;
    }

    public void run() {
        Scanner scanner = null;
        String command = null;
        while (true) {
            try {
                System.out.println("This console for Entrance at " + entrance.getInfo().getIp() + ":" + entrance.getInfo().getPort());
                System.out.println("Current time is " + entrance.getTimeStamp());
                System.out.println("There is " + entrance.messages.size() + " to handle");
                System.out.println("Please enter [ request ] to query for a parking place...");
                scanner = new Scanner(System.in);
                command = scanner.next();
                if (command.equalsIgnoreCase("request")) {
                    entrance.enterRequest();
                } else {
                    System.err.println("Unrecognized command : " + command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
