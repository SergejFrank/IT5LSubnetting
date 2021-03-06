package de.itech.netcalc.net;

import java.util.ArrayList;
import java.util.Objects;

/**
 * The IPAddress represents a base class for IPv4 and IPv6 and provides validation and parsing methods for those.
 */
public abstract class IPAddress {
    private static final String validV6Regex ="(([0-9a-fA-F]{1,4}:){7,7}[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]{1,}|::(ffff(:0{1,4}){0,1}:){0,1}((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9])\\.){3,3}(25[0-5]|(2[0-4]|1{0,1}[0-9]){0,1}[0-9]))";
    private static final String validV4Regex = "^((25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

    /**
     * Validates against the IPv4 Address specification
     * @param value the String input
     * @return true, if the input String is a valid IPv4 Address
     */
    public static boolean isValidIPv4(String value) {
        return value != null && value.matches(validV4Regex);
    }

    /**
     * Parses an IPv4 Address based on the IPv4 Address specification
     * @param value the String input
     * @return the parsed IPv4Address
     */
    public static IPv4Address parseIPv4(String value) {
        if(!IPAddress.isValidIPv4(value))
            throw new IllegalArgumentException("'value' is not a valid IPv4 Address.");
        String[] segments = value.split("\\.");
        return new IPv4Address(Integer.parseInt(segments[0]),Integer.parseInt(segments[1]),Integer.parseInt(segments[2]),Integer.parseInt(segments[3]));
    }

    /**
     * Validates an String input against the IPv6 Address specification.
     * @param value the String input
     * @return true, if the input String is a valid IPv6 Address
     */
    public static Boolean isValidIPv6(String value) {
        return value != null && value.matches(validV6Regex);
    }

    /**
     * Validates an String input against the IPv6 Address specification. Requires an valid Prefix.
     * @param value the String input
     * @param minValue the minimum value the prefix can have
     * @param maxValue the maximum value the prefix can have
     * @return true, if the input String is a valid IPv6 Address
     */
    public static Boolean isValidIPv6WithPrefix(String value, Integer minValue, Integer maxValue) {
        if(value == null || !value.contains("/")) return false;
        String[] splitted = value.split("/");
        if(splitted.length != 2) return false;
        try {
            Integer prefix = Integer.valueOf(splitted[1]);
            if(prefix < minValue || prefix > maxValue || (prefix % 4) != 0)
                return false;
        } catch(Exception e) {
            return false;
        }
        return splitted[0].matches(validV6Regex);
    }

    /**
     * Parses an IPv6 Address based on the IPv6 Address specification
     * @param value the String input
     * @return the parsed IPv6Address
     */
    public static IPv6Address parseIPv6(String value) {
        try {
            if(value.contains("/"))
            {
                if(!isValidIPv6WithPrefix(value, 0, 128))
                    throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
                value = value.split("/")[0];
            }
            if (!isValidIPv6(value))
                throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
            ArrayList<Integer> parsedSegments = new ArrayList<>();

            int expectedLength = 15;
            String[] split = value.split("((?<=[:])|(?=[:]))");
            //String[] split = value.split("((?<=[:.])|(?=[:.]))");
            int missingSegments = 0;
            if (split.length != expectedLength)
                missingSegments = (expectedLength - split.length + 1) / 2;
            if(value.startsWith("::"))
                missingSegments++;
            if(value.endsWith("::"))
                missingSegments++;

            String currSegment;
            for (int i = 0; i < split.length; i++) {
                currSegment = split[i];
                if ((Objects.equals(currSegment, ":")) && i != (split.length - 1) && (Objects.equals(split[i + 1], ":"))) { //Evtl nächstes Zeichen noch ein Trennzeichen?
                    i++;
                    for (int j = 0; j < missingSegments; j++) {
                        parsedSegments.add(0);
                    }
                } else if ((Objects.equals(currSegment, ":")) && i == (split.length - 1)) //Spezialfall letzter Block ist Trennzeichen
                    parsedSegments.add(0);
                else if(!(Objects.equals(currSegment, ":"))) //normales Segment
                {
                    if((i != 0 && Objects.equals(split[i - 1], ".")) || (i != split.length -1 && Objects.equals(split[i + 1], ".")))
                        parsedSegments.add(Integer.parseInt(currSegment));
                    else
                        parsedSegments.add(Integer.parseInt(currSegment, 16));
                }
            }

            long networkId = ((long) parsedSegments.get(0) << 48) +
                    ((long) parsedSegments.get(1) << 32) +
                    ((long) parsedSegments.get(2) << 16) +
                    ((long) parsedSegments.get(3));

            long interfaceId = ((long) parsedSegments.get(4) << 48) +
                    ((long) parsedSegments.get(5) << 32) +
                    ((long) parsedSegments.get(6) << 16) +
                    ((long) parsedSegments.get(7));

            return new IPv6Address(networkId, interfaceId);

        }
        catch(Exception e) {
            System.out.println("Can not parse IPv6Address (" + e.toString() + ")");
            throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
        }
    }

    /**
     * Parses an IPv6 Address with Prefix and returns the prefix
     * @param value the input value to parse
     * @return the prefix of the input
     */
    public static int parseIPv6Prefix(String value) {
        if(!isValidIPv6WithPrefix(value, 0, 128))
            throw new IllegalArgumentException("'value' is not a valid IPv6 Address.");
        String[] splitted = value.split("/");
        if(splitted.length != 2) {
            throw new IllegalArgumentException("Invalid IPv6Address");
        }
        return Integer.valueOf(splitted[1]);
    }
}