package de.itech.netcalc;

public class NetUtils {
    public static IpAddress getMaskFromPrefix(int prefix) {
        if(prefix < 0 || prefix >= 31) throw new IllegalArgumentException("Illegal prefix '" + prefix + "'");

        int mask = prefix == 0 ? 0 : -1 << (32 - prefix);

        return new IpAddress(mask);
    }

    public boolean isInSubnet(IpAddress network, IpAddress subnet, IpAddress host) {
        int valip = host.getValue();
        int valsub = subnet.getValue();
        int valnet = network.getValue();
        return (valip & valsub) == (valnet & valsub);
    }

    public static int getPrefixFromMask(IpAddress mask) {
        return mask.toBinary().indexOf("0");
    }
}
