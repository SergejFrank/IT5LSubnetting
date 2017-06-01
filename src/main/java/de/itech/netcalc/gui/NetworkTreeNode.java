package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.Subnet;

import javax.swing.tree.DefaultMutableTreeNode;

class NetworkTreeNode extends DefaultMutableTreeNode {
    private final Network network;

    NetworkTreeNode(Network network) {
        super(network, true);
        this.network = network;
        for(Subnet s : network.getSubnets())
            add(new SubnetTreeNode(s));
    }

    Network getNetwork() {
        return network;
    }

    void splitEqualyBySize(int size) {
    }
}