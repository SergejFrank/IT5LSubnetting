package de.itech.netcalc.gui;

import de.itech.netcalc.net.*;

import javax.swing.*;

class GuiUtils {
    static void error(String msg){
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

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

    static String getInitialSubnetV6String(IPv6Address networkId, int networkPrefix) {
        IPv6Address initialAddress;
        if(networkPrefix <= 64) {
            initialAddress =  new IPv6Address((networkId.getNetworkId() & NetUtils.ipv6PrefixLengthToValue(networkPrefix)), 0);
        } else {
            long interfacePrefix = NetUtils.ipv6PrefixLengthToValue(networkPrefix - 64);
            initialAddress = new IPv6Address(networkId.getNetworkId(), (networkId.getInterfaceId() & interfacePrefix));
        }
        String value = initialAddress.toString(Format.IPv6Format.SHORTHAND);
        if(value.endsWith("::")) value = value.replace("::", ":");
        return value;
    }

    static boolean confirmation(String title, String message) {
        int input = JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.OK_CANCEL_OPTION);
        return input == JOptionPane.OK_OPTION;
    }

    static IPv6Address iPv6AddressDialog(String title, String message, String initialValue) {
        Object input = JOptionPane.showInputDialog(null, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
        if(input == null) return null;
        String inputString = input.toString();
        if(!IPAddress.isValidIPv6(inputString)) {
            error("Die angegebene IPv6 Adresse ist ungültig.");
            return iPv6AddressDialog(title, message, inputString);
        } else {
            try {
                return IPAddress.parseIPv6(inputString);
            } catch(Exception e) {
                error("Fehler beim Umwandeln der IPv6 Adresse.");
                return iPv6AddressDialog(title, message, inputString);
            }
        }
    }

    static IPv4Address iPv4AddressDialog(String title, String message, String initialValue) {
        Object input = JOptionPane.showInputDialog(null, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
        if(input == null) return null;
        String inputString = input.toString();
        if(!IPAddress.isValidIPv4(inputString)) {
            error("Die angegebene IPv4 Adresse ist ungültig.");
            return iPv4AddressDialog(title, message, inputString);
        } else {
            try {
                return IPAddress.parseIPv4(inputString);
            } catch(Exception e) {
                error("Fehler beim Umwandeln der IPv4 Adresse.");
                return iPv4AddressDialog(title, message, inputString);
            }
        }
    }
}