package Assignment2;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by walker on 15/12/18.
 */
public class Config {
    public static final int PARKING_SPACE_NUM = 10;
    public static final int ENTRANCE_NUM = 3;
    public static final int EXIT_NUM = 2;
    public static List<Integer> availablePorts;

    // 配置可用的端口号
    static {
        int start = 24000;
        int num = ENTRANCE_NUM + EXIT_NUM;
        availablePorts = new ArrayList<>();
        for (int i = 0; i < num; ++i) {
            availablePorts.add(start + i);
        }
    }

}
