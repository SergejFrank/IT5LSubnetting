package de.itech.netcalc;

import java.net.InetAddress;
import java.net.UnknownHostException;

public abstract class IPAddress {

    public static IPAddress parse(String host) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(host);
        return null;
    }

    public static boolean isValidIPv4(String value) {
        return value != null && value.matches("^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$");
    }

    public static IPv4Address parseIPv4(String value) {
        if(!IPAddress.isValidIPv4(value))
            throw new IllegalArgumentException("'value' is not a valid IPv4 Address.");
        String[] segments = value.split("\\.");
        return new IPv4Address(Integer.parseInt(segments[0]),Integer.parseInt(segments[1]),Integer.parseInt(segments[2]),Integer.parseInt(segments[3]));
    }
}
