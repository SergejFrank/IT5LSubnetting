package de.itech.netcalc.gui;

import de.itech.netcalc.net.IPv6Address;
import de.itech.netcalc.net.Network;

import javax.swing.tree.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

class NetworkTreeModel extends DefaultTreeModel {
    NetworkTreeModel() {
        super(new NetworkTreeRoot());
    }

    void addNetwork(Network network) {
        Optional<Network> collidingNetwork = getNetworks().stream().filter(other -> other.isColliding(network)).findFirst();
        collidingNetwork.ifPresent(n -> {
            throw new UnsupportedOperationException("Network is Colliding with "+n);
        });
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

    void addNetworkRange(List<Network> networks) {
        if(networks == null) throw new IllegalArgumentException("networks can not be null.");
        for (Network network:networks) {
            Optional<Network> collidingNetwork = getNetworks().stream().filter(other -> other.isColliding(network)).findFirst();
            collidingNetwork.ifPresent(n -> {
                throw new UnsupportedOperationException("Network" + network + " is Colliding with "+collidingNetwork.get());
            });
            insertNetwork(network, getRootNode());
        }
    }

    private void insertNetwork(Network network, DefaultMutableTreeNode parent) {
        NetworkTreeNode networkNode = new NetworkTreeNode(network);
        insertNodeInto(networkNode, parent, parent.getChildCount());
    }

    ArrayList<Network> getNetworks(){
        ArrayList<Network> networks = new ArrayList<>();
        Collections.list(root.children()).forEach(node -> networks.add(((NetworkTreeNode) node).getNetwork()));
        return networks;
    }

    private NetworkTreeRoot getRootNode() {
        return (NetworkTreeRoot) getRoot();
    }

    int getRootIPv6PrefixLength() {
        return getRootNode().getGlobalPrefix() == null ? 0 : getRootNode().getGlobalPrefixLength();
    }

    IPv6Address getRootIPv6Prefix() {
        return getRootNode().getGlobalPrefix();
    }

    void setRootIPv6Prefix(IPv6Address iPv6Address, int prefixLength) {
        getRootNode().setGlobalPrefix(iPv6Address, prefixLength);
        nodeChanged(getRootNode());
    }

    void removeNetwork(NetworkTreeNode networkNode) {
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

    void clear() {
        setRoot(new NetworkTreeRoot());
    }
}