package Assignment2;

import java.util.Scanner;

/**
 * Created by walker on 2015/12/20.
 */
public class ExitCmdListener implements Runnable {

    private Exit exit;

    public ExitCmdListener(Exit exit) {
        this.exit = exit;
    }

    public void run() {
        Scanner scanner = null;
        String command = null;
        while (true) {
            try {

                exit.showUsage();
                scanner = new Scanner(System.in);
                command = scanner.next();

                if (command.equalsIgnoreCase("exit")) {
                    exit.exitRequest();
                }else if(command.equalsIgnoreCase("show")) {
                    exit.showStatus();
                } else {
                    System.err.println("Unrecognized command : " + command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
