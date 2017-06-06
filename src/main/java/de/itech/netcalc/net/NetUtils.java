package de.itech.netcalc.net;

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
     *
     * @param subnet IPv6 address
     * @param host IPv6 address
     * @return If the subnet is inside host
     */
    public static boolean isInSubnet(IPv6Address subnet, IPv6Address host){
        return subnet.getNetworkId() == host.getNetworkId();
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