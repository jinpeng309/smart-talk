package com.capslock.im.commons.util;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by capslock1874.
 */
public class NetUtils {
    public static String getLocalHost() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
