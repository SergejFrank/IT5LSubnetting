package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.tree.DefaultMutableTreeNode;

class NetworkTreeNode extends DefaultMutableTreeNode {
    private final Network network;

    NetworkTreeNode(Network network) {
        super(network, true);
        this.network = network;
        for(Network s : network.getSubnets())
            add(new SubnetTreeNode(s));
    }

    Network getNetwork() {
        return network;
    }

    void refreshSubnets() {
        this.removeAllChildren();
        for(Network s : network.getSubnets())
            add(new SubnetTreeNode(s));
    }
}