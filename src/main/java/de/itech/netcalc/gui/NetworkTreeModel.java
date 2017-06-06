package de.itech.netcalc.gui;

import de.itech.netcalc.net.IPv6Address;
import de.itech.netcalc.net.Network;

import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

class NetworkTreeModel extends DefaultTreeModel {
    NetworkTreeModel() {
        super(new NetworkTreeRoot());
    }

    void addNetwork(Network network) {
        insertNetwork(network, getRootNode());
    }

    void addNetwork(Network network, NetworkTreeNode parent) {
        parent.getNetwork().addSubnet(network);
        insertNetwork(network, parent);
    }

    void addNetwork(NetworkTreeNode parent, int size) {
        parent.getNetwork().addSubnet(size);
        parent.refreshSubnets();
        this.nodeStructureChanged(parent);
    }

    private void insertNetwork(Network network, DefaultMutableTreeNode parent) {
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

    private NetworkTreeRoot getRootNode() {
        return (NetworkTreeRoot) getRoot();
    }

    int getRootIPv6PrefixLength() {
        return getRootNode().getGlobalPrefix() == null ? 128 : getRootNode().getGlobalPrefixLength();
    }

    IPv6Address getRootIPv6Prefix() {
        return getRootNode().getGlobalPrefix();
    }

    void setRootIPv6Prefix(IPv6Address iPv6Address, int prefixLength) {
        getRootNode().setGlobalPrefix(iPv6Address, prefixLength);
        nodeChanged(getRootNode());
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