package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.Subnet;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import java.util.ArrayList;
import java.util.List;

public class NetworkTreeModel implements TreeModel {
    private TreeNode root = new DefaultMutableTreeNode("Netzwerke");
    private List<TreeModelListener> listeners = new ArrayList<>();
    private List<Network> networks = new ArrayList<>();

    @Override
    public Object getRoot() {
        return root;
    }

    @Override
    public Object getChild(Object parent, int index) {
        if(parent == root) {
            return networks.get(index);
        }
        if(parent instanceof Network)
        {
            Network network = networks.get(networks.indexOf(parent));
            return network.getSubnets().get(index);
        }
        throw new UnsupportedOperationException("parent is invalid.");
    }

    @Override
    public int getChildCount(Object parent) {
        if(parent == root) {
            return networks.size();
        }
        if(parent instanceof Network)
        {
            Network network = networks.get(networks.indexOf(parent));
            return network.getSubnets().size();
        }
        throw new UnsupportedOperationException("parent is invalid.");
    }

    @Override
    public boolean isLeaf(Object node) {
        return node instanceof Subnet ||(node instanceof Network && ((Network)node).getSubnets().size() == 0);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {

    }

    public void addNetwork(Network network) {
        networks.add(network);
        fireTreeStructureChanged();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        if(parent == root)
            return networks.indexOf(child);
        if(parent instanceof Subnet)
            return networks.get(networks.indexOf(child)).getSubnets().size();
        return 0;
    }

    @Override
    public void addTreeModelListener(TreeModelListener l)
    {
        listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l)
    {
        listeners.remove(l);
    }

    private void fireTreeStructureChanged() {
        Object[] o = {root};
        TreeModelEvent e = new TreeModelEvent(this, o);
        for(TreeModelListener l : listeners)
            l.treeStructureChanged(e);
    }
}