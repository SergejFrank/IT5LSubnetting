package de.itech.netcalc.net;

class NetUtils {
    static IPv4Address getMaskFromPrefix(int prefix) {
        if(prefix < 0 || prefix > 32) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");

        int mask = prefix == 0 ? 0 : -1 << (32 - prefix);

        return new IPv4Address(mask);
    }

    static boolean isInSubnet(IPv4Address network, IPv4Address subnet, IPv4Address host) {
        int valIp = host.getValue();
        int valSub = subnet.getValue();
        int valNet = network.getValue();
        return (valIp & valSub) == (valNet & valSub);
    }

    static IPv4Address addPrefixToMask(IPv4Address mask, int prefix) {
        return getMaskFromPrefix(getPrefixFromMask(mask) + prefix);
    }

    static int getPrefixFromMask(IPv4Address mask) {
        int index = mask.toBinary().indexOf("0");
        return index == -1 ? 32 : index;
    }

    static long getLengthBetweenNetworks(NetworkBase n1, NetworkBase n2){
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