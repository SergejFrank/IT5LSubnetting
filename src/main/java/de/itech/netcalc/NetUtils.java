package de.itech.netcalc;

public class NetUtils {
    public static IpAddress getMaskFromSuffix(int suffix) {
        if(suffix < 0 || suffix >= 31) throw new IllegalArgumentException("Illegal suffix '" + suffix + "'");

        int mask = suffix == 0 ? 0 : -1 << (32 - suffix);

        return new IpAddress(mask);
    }

    public static boolean isInSubnet(IpAddress network, IpAddress subnet, IpAddress host) {
        int valIp = host.getValue();
        int valSub = subnet.getValue();
        int valNet = network.getValue();
        return (valIp & valSub) == (valNet & valSub);
    }

    public static IpAddress addSuffixToMask(IpAddress mask, int suffix) {
        return getMaskFromSuffix(getSuffixFromMask(mask) + suffix);
    }

    public static int getSuffixFromMask(IpAddress mask) {
        return mask.toBinary().indexOf("0");
    }
}