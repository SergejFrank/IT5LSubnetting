package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.Subnet;
import javax.swing.tree.*;

class NetworkTreeModel extends DefaultTreeModel {
    NetworkTreeModel() {
        super(new DefaultMutableTreeNode("Netzwerke"));
    }

    void addNetwork(Network network) {
        DefaultMutableTreeNode root = getRootNode();
        NetworkTreeNode networkNode = new NetworkTreeNode(network);
        insertNodeInto(networkNode, root, root.getChildCount());
    }

    void addSubnet(Network network, Subnet subnet) {
        NetworkTreeNode networkTreeNode = getNodeForNetwork(network);
        if(network == null) throw new IllegalArgumentException("Network '" + network + "' not found in JTree Nodes");
        SubnetTreeNode subnetNode = new SubnetTreeNode(subnet);
        networkTreeNode.add(subnetNode);
        nodesWereInserted(subnetNode, new int[]{subnetNode.getChildCount()-1});
    }

    DefaultMutableTreeNode getRootNode() {
        return (DefaultMutableTreeNode)getRoot();
    }

    NetworkTreeNode getNodeForNetwork(Network network) {
        for(int i=0; i<getRootNode().getChildCount();i++) {
            NetworkTreeNode node = ((NetworkTreeNode)getRootNode().getChildAt(i));
            if(node.getNetwork().equals(network))
                return node;
        }
        return null;
    }

    void deleteNetwork(NetworkTreeNode networkNode) {
        this.removeNodeFromParent(networkNode);
    }

    void deleteSubnet(SubnetTreeNode subnetNode) {
        Network network = ((NetworkTreeNode)subnetNode.getParent()).getNetwork();
        network.removeSubnet(subnetNode.getSubnet());
        this.removeNodeFromParent(subnetNode);
    }

    void splitEqualyBySize(NetworkTreeNode networkNode, int size) {
        Network network = networkNode.getNetwork();
        network.splitEqualy(size);
        networkNode.refreshSubnets();
        this.nodeStructureChanged(networkNode);
    }
}