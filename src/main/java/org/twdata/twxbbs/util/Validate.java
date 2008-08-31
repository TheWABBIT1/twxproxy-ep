package org.twdata.twxbbs.util;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by IntelliJ IDEA.
 * User: mrdon
 * Date: 24/08/2008
 * Time: 1:27:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Validate {
    public static void notNull(Object o) {
        notNull(o, "Argument cannot be null");
    }

    public static void notNull(Object o, String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static boolean isNotEmpty(String val) {
        return val != null && val.trim().length() > 0;
    }

    public static boolean isPortFree(String port) {
        return isPortFree(Integer.parseInt(port));
    }

    public static boolean isPortFree(int port) {
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close socket", e);
                }
            }
        }
    }

    public static boolean canConnect(String host, String port) {
        return canConnect(host, Integer.parseInt(port));
    }
    public static boolean canConnect(String host, int port) {
        Socket socket = null;
        try {
            socket = new Socket(host, port);
            return true;
        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    throw new RuntimeException("Unable to close socket", e);
                }
            }
        }
    }

    public static boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    public static boolean isIntegerInRange(String value, int low, int high) {
        try {
            int val = Integer.parseInt(value);
            if (val >= low && val <= high) {
                return true;
            }
        } catch (NumberFormatException ex) {
            // treat as false
        }
        return false;
    }
}
