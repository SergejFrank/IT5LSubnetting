package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.Subnet;

import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

class NetworkTreeModel extends DefaultTreeModel {
    NetworkTreeModel() {
        super(new DefaultMutableTreeNode("Netzwerke"));
    }

    void addNetwork(Network network) {
        DefaultMutableTreeNode root = getRootNode();
        NetworkTreeNode networkNode = new NetworkTreeNode(network);

        Optional<Network> collidingNetwork = getNetworks().stream().filter(other -> other.isColliding(network)).findFirst();
        if(collidingNetwork.isPresent()){
            throw new UnsupportedOperationException("Network is Colliding with "+collidingNetwork.get());
        }else{
            insertNodeInto(networkNode, root, root.getChildCount());
        }
    }

    ArrayList<Network> getNetworks(){
        ArrayList<Network> networks = new ArrayList<>();
        Collections.list(root.children()).stream().forEach(node -> networks.add(((NetworkTreeNode) node).getNetwork()));
        return networks;
    }

    void addSubnet(Network network, Subnet subnet) {
        NetworkTreeNode networkTreeNode = getNodeForNetwork(network);
        if(network == null) throw new IllegalArgumentException("Network '" + network + "' not found in JTree Nodes");
        SubnetTreeNode subnetNode = new SubnetTreeNode(subnet);
        networkTreeNode.add(subnetNode);
        nodesWereInserted(subnetNode, new int[]{subnetNode.getChildCount() - 1});
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

    void splitBySize(NetworkTreeNode networkNode, int size) {
        Network network = networkNode.getNetwork();
        network.splitBySize(size);
        networkNode.refreshSubnets();
        this.nodeStructureChanged(networkNode);
    }

    void splitByCount(NetworkTreeNode networkNode, int count) {
        if((count & (count - 1)) != 0){
            throw new IllegalArgumentException("subnets can only be split by powers of 2");
        }
        Network network = networkNode.getNetwork();
        network.splitByCount(count);
        networkNode.refreshSubnets();
        this.nodeStructureChanged(networkNode);
    }
}