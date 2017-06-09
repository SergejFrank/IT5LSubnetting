package de.itech.netcalc.gui;

import de.itech.netcalc.net.Format;
import de.itech.netcalc.net.IPv6Address;

import javax.swing.tree.DefaultMutableTreeNode;

class NetworkTreeRoot extends DefaultMutableTreeNode {
    private IPv6Address globalPrefix;
    private int globalPrefixLength;

    NetworkTreeRoot() {
        super("Netzwerke (2001:db8::/32)");
        globalPrefix = IPv6Address.parseIPv6("2001:db8::");
        globalPrefixLength = 32;
    }

    IPv6Address getGlobalPrefix() {
        return globalPrefix;
    }

    void setGlobalPrefix(IPv6Address globalPrefix, int globalPrefixLength) {
        if(globalPrefixLength > 128 || globalPrefixLength < 0)
            throw new IllegalArgumentException("'globalPrefixLength' is out of range. (0-128)");
        this.globalPrefix = globalPrefix;
        this.globalPrefixLength = globalPrefixLength;
        if(globalPrefix == null) {
            this.globalPrefixLength = 0;
            setUserObject("Netzwerke");
        } else {
            setUserObject("Netzwerke (" + Format.format(globalPrefix,Format.IPv6Format.SHORTHAND) + "/" + globalPrefixLength + ")");
        }
    }

    int getGlobalPrefixLength() {
        return globalPrefixLength;
    }
}