package Assignment2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by walker on 15/12/18.
 */
public class Config {

    private static int PARKING_SPACE_NUM;
    private static int ENTRANCE_NUM;
    private static int EXIT_NUM;

    private static List<PortInfo> entrances;
    private static List<PortInfo> exits;
    private static String path = "./src/Configuration.txt";


    static {

        entrances = new ArrayList<>();
        exits = new ArrayList<>();

        File configFile = new File(path);

        try {

            if (!configFile.exists()) {

                // 若配置文件不存在，则通过控制台输入配置，并将其写入到配置文件中
                System.out.println("Can not find configuration file in current directory : " + configFile.getCanonicalPath());
                System.out.println("Creating...");
                configFile.createNewFile();
                Scanner scanner = new Scanner(System.in);
                System.out.println("Please enter the total parking space number: ");
                PARKING_SPACE_NUM = scanner.nextInt();
                ENTRANCE_NUM = 0;
                EXIT_NUM = 0;
                scanner.close();

                PrintWriter writer = new PrintWriter(configFile, "UTF-8");
                writer.println(PARKING_SPACE_NUM);
                writer.println(ENTRANCE_NUM);
                writer.println(EXIT_NUM);
                writer.close();

            } else {

                // 若配置文件存在则，读取配置文件
                System.out.println("Configuration file found!");
                BufferedReader br = new BufferedReader(new FileReader(configFile));
                PARKING_SPACE_NUM = Integer.parseInt(br.readLine());
                ENTRANCE_NUM = Integer.parseInt(br.readLine());
                for (int i = 0; i < ENTRANCE_NUM; ++i) {
                    String info = br.readLine();
                    String ip = info.split(":")[0];
                    int port = Integer.parseInt(info.split(":")[1]);
                    entrances.add(new PortInfo(ip, port));
                }
                EXIT_NUM = Integer.parseInt(br.readLine());
                for (int i = 0; i < EXIT_NUM; ++i) {
                    String info = br.readLine();
                    String ip = info.split(":")[0];
                    int port = Integer.parseInt(info.split(":")[1]);
                    exits.add(new PortInfo(ip, port));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getParkingSpaceNum() {
        return PARKING_SPACE_NUM;
    }

    public static List<PortInfo> getEntranceList() {
        return entrances;
    }

    public static List<PortInfo> getExitList() {
        return exits;
    }

    public static void addPort(PortInfo info, PortType type) {
        switch (type) {

            case ENTRANCE: {
                entrances.add(info);
                update();
            }
            break;

            case EXIT: {
                exits.add(info);
                update();
            }
            break;

        }
    }

    public static void show() {
        entrances.forEach(e -> System.out.println(e));
        exits.forEach(e -> System.out.println(e));
    }

    public static void main(String[] args) {
        PortInfo info = new PortInfo("127.0.0.1", 24011);
        addPort(info, PortType.ENTRANCE);
    }

    private static void update() {
        try {
            PrintWriter writer = new PrintWriter(new File(path), "UTF-8");
            writer.println(PARKING_SPACE_NUM);
            writer.println(entrances.size());
            for (PortInfo info : entrances) {
                writer.println(info);
            }
            writer.println(exits.size());
            for (PortInfo info : exits) {
                writer.println(info);
            }
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
