package de.itech.netcalc.gui;

import de.itech.netcalc.net.IPv4Address;
import de.itech.netcalc.net.NetUtils;
import de.itech.netcalc.net.Network;

import javax.swing.*;

class GuiUtils {
    static String getInitialSubnetString(Network network) {
        IPv4Address networkId = network.getNetworkIdV4();
        Integer prefix = NetUtils.maskToPrefix(network.getNetworkMaskV4());
        if(prefix >= 24)
            return networkId.getOct1() + "." + networkId.getOct2() + "." + networkId.getOct3() + ".";
        if(prefix >= 16)
            return networkId.getOct1() + "." + networkId.getOct2() + ".";
        if(prefix >= 8)
            return networkId.getOct1() + ".";
        return "";
    }

    static boolean confirmation(String title, String message) {
        int input = JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.OK_CANCEL_OPTION);
        return input == JOptionPane.OK_OPTION;
    }
}