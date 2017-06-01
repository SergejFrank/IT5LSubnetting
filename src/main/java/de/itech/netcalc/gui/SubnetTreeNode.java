package de.itech.netcalc.gui;

import de.itech.netcalc.net.Subnet;
import javax.swing.tree.DefaultMutableTreeNode;

class SubnetTreeNode extends DefaultMutableTreeNode {
    private final Subnet subnet;

    SubnetTreeNode(Subnet subnet) {
        super(subnet, false);
        this.subnet = subnet;
    }

    Subnet getSubnet() {
        return subnet;
    }
}