package cn.edw.mynetty.util;

/**
 * 网络相关的utils
 * @author taoxu.xu
 * @date 9/3/2021 4:53 PM
 */
public class NetUtil {
    /**
     * 端口号最大值
     * */
    private static final int MAX_PORT = 65535;

    /**
     * 端口检查
     * */
    public static void validatePort(int port){
        if (port < 1 || port > MAX_PORT) {
            throw new IllegalArgumentException("The port is illegal: " + port);
        }
    }
}
