package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.tree.DefaultMutableTreeNode;

class SubnetTreeNode extends DefaultMutableTreeNode {
    private final Network subnet;

    SubnetTreeNode(Network subnet) {
        super(subnet, false);
        this.subnet = subnet;
    }

    Network getSubnet() {
        return subnet;
    }
}