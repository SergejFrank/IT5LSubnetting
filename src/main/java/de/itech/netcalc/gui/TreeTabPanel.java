package de.itech.netcalc.gui;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;

public class TreeTabPanel extends JPanel {

    public TreeTabPanel() {
        super(new GridLayout(1,0));
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        JSplitPane treePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPane.setLeftComponent(treePane);
        mainPane.setRightComponent(new JPanel());

        DefaultMutableTreeNode node = new DefaultMutableTreeNode("asasd");
        DefaultTreeModel t = new DefaultTreeModel(node);
        JTree networkTree = new JTree(t);
        treePane.setTopComponent(networkTree);
        treePane.setBottomComponent(new JPanel());

        node.add(new DefaultMutableTreeNode());
        node.add(new DefaultMutableTreeNode());
        node.add(new DefaultMutableTreeNode());
        node.add(new DefaultMutableTreeNode());
        node.add(new DefaultMutableTreeNode());
        node.add(new DefaultMutableTreeNode());

        add(mainPane);
    }
}