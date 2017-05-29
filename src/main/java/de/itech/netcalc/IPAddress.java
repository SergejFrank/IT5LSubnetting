package de.itech.netcalc;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

public abstract class IPAddress {
    private static final String validV6Regex ="(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";
    private static final String validV4Regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    public static boolean isValidIPv4(String value) {
        return value != null && value.matches(validV4Regex);
    }

    public static IPv4Address parseIPv4(String value) {
        if(!IPAddress.isValidIPv4(value))
            throw new IllegalArgumentException("'value' is not a valid IPv4 Address.");
        String[] segments = value.split("\\.");
        return new IPv4Address(Integer.parseInt(segments[0]),Integer.parseInt(segments[1]),Integer.parseInt(segments[2]),Integer.parseInt(segments[3]));
    }

    public static Boolean isValidIPv6(String value) {
        return value != null && value.matches(validV6Regex);
    }

    public static IPv6Address parseIPv6(String value) {
        try {

            if (!isValidIPv6(value))
                throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
            ArrayList<Integer> parsedSegments = new ArrayList<>();

            int expectedLength = value.contains(".") ? 19 : 15;
            String[] splitted = value.split("((?<=[:.])|(?=[:.]))");
            int missingSegments = 0;
            if (splitted.length != expectedLength)
                missingSegments = (expectedLength - splitted.length + 1) / 2;
            if(splitted.length == 3)
                missingSegments = 7;
            if(splitted.length == 2)
                missingSegments = 8;

            String currSegment;
            for (int i = 0; i < splitted.length; i++) {
                currSegment = splitted[i];
                if (isDelimiter(currSegment) && i != (splitted.length - 1) && isDelimiter(splitted[i+1])) { //Evtl nächstes Zeichen noch ein Trennzeichen?
                    i++;
                    for (int j = 0; j < missingSegments; j++) {
                        parsedSegments.add(0);
                    }
                } else if (isDelimiter(currSegment) && i == (splitted.length - 1)) //Spezialfall letzter Block ist Trennzeichen
                    parsedSegments.add(0);
                else if(!isDelimiter(currSegment)) //normales Segment
                {
                    if((i != 0 && Objects.equals(splitted[i - 1], ".")) || (i != splitted.length -1 && Objects.equals(splitted[i + 1], ".")))
                        parsedSegments.add(Integer.parseInt(currSegment));
                    else
                        parsedSegments.add(Integer.parseInt(currSegment, 16));
                }
            }
            return new IPv6Address(parsedSegments.stream().mapToInt(i -> i).toArray());
        }
        catch(Exception e) {
            System.out.println("Can not parse IPv6Address (" + e.toString() + ")");
            throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
        }
    }

    private static boolean isDelimiter(String value) {
        return Objects.equals(value, ":") || Objects.equals(value, ".");
    }
}
