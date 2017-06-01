package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import sun.reflect.generics.tree.Tree;

import javax.swing.table.DefaultTableModel;
import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

class NetworkTreeModel extends DefaultTreeModel {
    NetworkTreeModel() {
        super(new DefaultMutableTreeNode("Netzwerke"));
    }

    void addNetwork(Network network) {
        addNetwork(network, getRootNode());
    }

    void addNetwork(Network network, NetworkTreeNode parent) {
        parent.getNetwork().addSubnet(network);
        addNetwork(network, parent);
    }

    private void addNetwork(Network network, DefaultMutableTreeNode parent) {
        NetworkTreeNode networkNode = new NetworkTreeNode(network);
        Optional<Network> collidingNetwork = getNetworks().stream().filter(other -> other.isColliding(network)).findFirst();
        if(collidingNetwork.isPresent()){
            throw new UnsupportedOperationException("Network is Colliding with "+collidingNetwork.get());
        }else{
            insertNodeInto(networkNode, parent, parent.getChildCount());
        }
    }

    ArrayList<Network> getNetworks(){
        ArrayList<Network> networks = new ArrayList<>();
        Collections.list(root.children()).stream().forEach(node -> networks.add(((NetworkTreeNode) node).getNetwork()));
        return networks;
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
        TreeNode parent = networkNode.getParent();
        if(parent instanceof NetworkTreeNode) {
            Network network = ((NetworkTreeNode)parent).getNetwork();
            network.removeSubnet(networkNode.getNetwork());
        }
        this.removeNodeFromParent(networkNode);
    }

    void clearSubnets(NetworkTreeNode networkNode) {
        networkNode.getNetwork().clearSubnets();
        networkNode.removeAllChildren();
        nodeStructureChanged(networkNode);
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