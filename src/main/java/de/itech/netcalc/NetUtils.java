package de.itech.netcalc;

public class NetUtils {
    public static Ipv4Address getMaskFromSuffix(int suffix) {
        if(suffix < 0 || suffix >= 31) throw new IllegalArgumentException("Illegal suffix '" + suffix + "'");

        int mask = suffix == 0 ? 0 : -1 << (32 - suffix);

        return new Ipv4Address(mask);
    }

    public static boolean isInSubnet(Ipv4Address network, Ipv4Address subnet, Ipv4Address host) {
        int valIp = host.getValue();
        int valSub = subnet.getValue();
        int valNet = network.getValue();
        return (valIp & valSub) == (valNet & valSub);
    }

    public static Ipv4Address addSuffixToMask(Ipv4Address mask, int suffix) {
        return getMaskFromSuffix(getSuffixFromMask(mask) + suffix);
    }

    public static int getSuffixFromMask(Ipv4Address mask) {
        return mask.toBinary().indexOf("0");
    }

    public static long getLengthBetweenNetworks(NetworkBase n1 , NetworkBase n2){
        return getLengthBetweenIpAddresses(n1.getBroadcastAddress(), n2.getAddress());
    }

    public static long getLengthBetweenIpAddresses(Ipv4Address ip1, Ipv4Address ip2){
        if(ip1.isGreaterThan(ip2)){
            Ipv4Address temp = ip1;
            ip1 = ip2;
            ip2 = temp;
        }
        return ip2.getLValue() - ip1.getLValue();
    }
}