package de.itech.netcalc.net;

/**
 * The NetUtils class provides helper methods for different task in network calculation
 */
public class NetUtils {
    /**
     * Converts an IPv4 prefix to an IPv4 network mask
     * @param prefix network prefix
     * @return the network mask
     */
    public static IPv4Address prefixToMask(int prefix) {
        if(prefix < 0 || prefix > 32) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");

        int mask = prefix == 0 ? 0 : -1 << (32 - prefix);

        return new IPv4Address(mask);
    }

    /**
     * Calculates if an IPv4 address is located in an IPv4 network
     * @param networkId the network Id
     * @param networkMask the network mask
     * @param host the host address
     * @return true, if the host address is located in the network
     */
    static boolean isInSubnet(IPv4Address networkId, IPv4Address networkMask, IPv4Address host) {
        int valIp = host.getValue();
        int valSub = networkMask.getValue();
        int valNet = networkId.getValue();
        return (valIp & valSub) == (valNet & valSub);
    }

    /**
     * Determinate if an IPv6 address is located in an IPv6 network
     * @param subnet IPv6 network address
     * @param prefixLength IPv6 network prefix length (0-128)
     * @param host IPv6 host address
     * @return true, if the host address is located in the network
     */
    public static boolean isInSubnet(IPv6Address subnet, int prefixLength, IPv6Address host){
        if(prefixLength < 0 || prefixLength > 128) throw new IllegalArgumentException("'" + prefixLength + "' is not a valid prefix length.");
        if(prefixLength <= 64) {
            return (subnet.getNetworkId() & ipv6PrefixLengthToValue(prefixLength)) == (host.getNetworkId() & ipv6PrefixLengthToValue(prefixLength));
        } else {
            long prefix = ipv6PrefixLengthToValue(prefixLength - 64);
            return  (subnet.getNetworkId() == host.getNetworkId())
                    && ((subnet.getInterfaceId() & prefix) == (host.getNetworkId() & prefix));
        }
    }

    /**
     * Adds a prefix value to a network mask
     * @param mask the network mask
     * @param prefix the prefix to add
     * @return the new network mask
     */
    static IPv4Address addPrefixToMask(IPv4Address mask, int prefix) {
        return prefixToMask(maskToPrefix(mask) + prefix);
    }

    /**
     * Converts an IPv6 prefix to a long value, that can be used for calculation
     * @param prefix the prefix to convert
     * @return the converted value
     */
    static long ipv6PrefixLengthToValue(int prefix) {
        if(prefix < 0 || prefix > 64) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");
        return (long) (prefix == 0 ? 0 : -1 << (32 - prefix));
    }

    /**
     * Converts an IPv4 network mask to a IPv4 prefix
     * @param mask the IPv4 network mask
     * @return the IPv4 network prefix
     */
    public static int maskToPrefix(IPv4Address mask) {
        int index = mask.toBinary().indexOf("0");
        return index == -1 ? 32 : index;
    }

    /**
     * Calculates the IPv4 address range between two networks
     * @param n1 Network 1
     * @param n2 Network 2
     * @return the amount of IPv4 addresses in between
     */
    static long getLengthBetweenNetworks(Network n1, Network n2){
        return getLengthBetweenIpAddresses(n1.getBroadcastAddress(), n2.getNetworkIdV4());
    }

    /**
     * Calculates the IPv4 address range between two ip addresses
     * @param ip1 IPv4 address
     * @param ip2 IPv4 address
     * @return space between the given IP addresses
     */
    static long getLengthBetweenIpAddresses(IPv4Address ip1, IPv4Address ip2){
        if(ip1.isGreaterThan(ip2)){
            IPv4Address temp = ip1;
            ip1 = ip2;
            ip2 = temp;
        }
        return ip2.getLValue() - ip1.getLValue();
    }
}