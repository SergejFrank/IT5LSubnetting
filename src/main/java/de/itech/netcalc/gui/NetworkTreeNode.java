package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.tree.DefaultMutableTreeNode;

class NetworkTreeNode extends DefaultMutableTreeNode {
    private final Network network;

    NetworkTreeNode(Network network) {
        super(network, true);
        this.network = network;
        refreshSubnets();
    }

    Network getNetwork() {
        return network;
    }

    void refreshSubnets() {
        if(network.getStatus() == Network.SubnetStatus.HAS_SUBNETS){
            setAllowsChildren(true);
            for(Network s : network.getSubnets())
                add(new NetworkTreeNode(s));
        } else {
            setAllowsChildren(false);
        }
    }

    @Override
    public String toString() {
        return ((Network)userObject).toString();
    }
}