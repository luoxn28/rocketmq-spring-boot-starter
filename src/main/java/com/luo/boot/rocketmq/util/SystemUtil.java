package com.luo.boot.rocketmq.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author xiangnan
 * date 2018/8/9 10:46
 */
public class SystemUtil {

    public static String localIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

    public static String localHostName() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostName();
    }

}
