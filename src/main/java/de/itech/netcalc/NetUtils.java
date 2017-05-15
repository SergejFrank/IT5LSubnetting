package de.itech.netcalc;

public class NetUtils {
    public static IpAddress getMaskFromSuffix(int suffix) {
        if(suffix < 0 || suffix >= 31) throw new IllegalArgumentException("Illegal suffix '" + suffix + "'");

        int mask = suffix == 0 ? 0 : -1 << (32 - suffix);

        return new IpAddress(mask);
    }

    public boolean isInSubnet(IpAddress network, IpAddress subnet, IpAddress host) {
        int valip = host.getValue();
        int valsub = subnet.getValue();
        int valnet = network.getValue();
        return (valip & valsub) == (valnet & valsub);
    }

    public static int getSuffixFromMask(IpAddress mask) {
        return mask.toBinary().indexOf("0");
    }
}
