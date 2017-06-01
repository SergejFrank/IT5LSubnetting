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
        //TODO: machs richtig
        if(network.getSubnets().size() != 0){
            setAllowsChildren(true);
            for(Network s : network.getSubnets())
                add(new NetworkTreeNode(s));
        } else {
            setAllowsChildren(false);
        }
    }
}