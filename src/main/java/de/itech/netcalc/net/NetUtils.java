package de.itech.netcalc.net;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NetUtils {

    /**
     * creates an netmask from prefix
     * @param prefix network prefix
     * @return The netmask
     */
    public static IPv4Address prefixToMask(int prefix) {
        if(prefix < 0 || prefix > 32) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");

        int mask = prefix == 0 ? 0 : -1 << (32 - prefix);

        return new IPv4Address(mask);
    }

    /**
     *
     * @param networkId parent networkID
     * @param networkMask parent netmask
     * @param host
     * @return If the hostIP ist inside networkId/netmask combination
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
     * @return true, if the host is located in the network
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
     *
     * @param mask netmask
     * @param prefix prefix that should be added to mask
     * @return Networkmask with added prefix
     */
    static IPv4Address addPrefixToMask(IPv4Address mask, int prefix) {
        return prefixToMask(maskToPrefix(mask) + prefix);
    }

    static long ipv6PrefixLengthToValue(int prefix) {
        if(prefix < 0 || prefix > 64) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");
        return (long) (prefix == 0 ? 0 : -1 << (32 - prefix));
    }

    /**
     *
     * @param mask IPv4 netmask
     * @return Integer representation of netmask
     */
    public static int maskToPrefix(IPv4Address mask) {
        int index = mask.toBinary().indexOf("0");
        return index == -1 ? 32 : index;
    }

    /**
     *
     * @param n1 Network 1
     * @param n2 Network 2
     * @return space between the given networks
     */
    static long getLengthBetweenNetworks(Network n1, Network n2){
        return getLengthBetweenIpAddresses(n1.getBroadcastAddress(), n2.getNetworkIdV4());
    }

    /**
     *
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