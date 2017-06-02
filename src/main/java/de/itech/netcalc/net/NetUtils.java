package de.itech.netcalc.net;

public class NetUtils {
    static IPv4Address prefixToMask(int prefix) {
        if(prefix < 0 || prefix > 32) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");

        int mask = prefix == 0 ? 0 : -1 << (32 - prefix);

        return new IPv4Address(mask);
    }

    static boolean isInSubnet(IPv4Address networkId, IPv4Address networkMask, IPv4Address host) {
        int valIp = host.getValue();
        int valSub = networkMask.getValue();
        int valNet = networkId.getValue();
        return (valIp & valSub) == (valNet & valSub);
    }

    public static boolean isInSubnet(IPv6Address subnet, IPv6Address host){
        return subnet.getNetworkId() == host.getNetworkId();
    }

    static IPv4Address addPrefixToMask(IPv4Address mask, int prefix) {
        return prefixToMask(maskToPrefix(mask) + prefix);
    }

    public static int maskToPrefix(IPv4Address mask) {
        int index = mask.toBinary().indexOf("0");
        return index == -1 ? 32 : index;
    }

    static long getLengthBetweenNetworks(Network n1, Network n2){
        return getLengthBetweenIpAddresses(n1.getBroadcastAddress(), n2.getNetworkIdV4());
    }

    static long getLengthBetweenIpAddresses(IPv4Address ip1, IPv4Address ip2){
        if(ip1.isGreaterThan(ip2)){
            IPv4Address temp = ip1;
            ip1 = ip2;
            ip2 = temp;
        }
        return ip2.getLValue() - ip1.getLValue();
    }
}