package de.itech.netcalc.net;

/**
 * Provides static methods to format IP addresses and Networks.
 */
public class Format {
    /**
     * Specifies, how an IPv6 Address can be formatted.
     */
    public enum IPv6Format {
        NORMAL,
        HIDELEADINGZEROS,
        SHORTHAND,
        BINARY
    }

    /**
     * Format an IPv6 address with the given parameters.
     * @param address the IPv6 address to format
     * @param format the notation to use
     * @return the IPv6 address string representation
     */
    static String format(IPv6Address address, IPv6Format format) {
        long networkId = address.getNetworkId();
        long interfaceId = address.getInterfaceId();

        if(format == IPv6Format.BINARY) {
            String[] elements = (String.format("%64s", Long.toBinaryString(networkId)).replace(' ', '0') +
                    String.format("%64s", Long.toBinaryString(interfaceId)).replace(' ', '0')).split("(?<=\\G.{8})");
            return String.join(" ", elements);
        }

        Short[] segments = new Short[8];

        for(int i = 0; i < 4; i++){
            segments[i] = (short) (networkId >> (3-i)*16);
            segments[i + 4] = (short) (interfaceId >> (3-i)*16);
        }

        StringBuilder resultBuilder = new StringBuilder();
        resultBuilder.append(format == IPv6Format.SHORTHAND || format == IPv6Format.HIDELEADINGZEROS
                ? Long.toString(Short.toUnsignedLong(segments[0]), 16)
                : String.format("%04X", Short.toUnsignedLong(segments[0])));

        for(int i = 1; i < segments.length; i++){
            resultBuilder.append(format == IPv6Format.SHORTHAND || format == IPv6Format.HIDELEADINGZEROS
                    ? ":" + Long.toString(Short.toUnsignedLong(segments[i]), 16)
                    : ":" + String.format("%04X", Short.toUnsignedLong(segments[i])));
        }

        if(format == IPv6Format.SHORTHAND){
            resultBuilder = new StringBuilder(resultBuilder.toString().replaceAll("((?::0\\b){2,}):?(?!\\S*\\b\\1:0\\b)(\\S*)", "::$2"));
            if(resultBuilder.toString().equals("0::")) resultBuilder = new StringBuilder("::");
        }
        return resultBuilder.toString().toLowerCase();
    }
}