package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class TreeTabPanel extends JPanel implements TreeSelectionListener {
    private static TreeTabPanel Instance;
    private NetworkTreeModel networkTreeModel;
    private JSplitPane infoPane;
    private JTree networkTree;
    private HostPanel hostPanel;

    TreeTabPanel() {
        super(new GridLayout(1,0));
        Instance = this;
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPane.setDividerSize(3);
        infoPane.setDividerSize(3);
        mainPane.setLeftComponent(infoPane);
        mainPane.setRightComponent(hostPanel = new HostPanel());
        mainPane.setResizeWeight(0.3);
        infoPane.setResizeWeight(0.5);

        networkTree = new JTree(networkTreeModel = new NetworkTreeModel());
        networkTree.addTreeSelectionListener(this);
        networkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTreeRightClick(e);
            }
        });
        infoPane.setTopComponent(new JScrollPane(networkTree));

        add(mainPane);

        Network testNetwork1 = Network.parse("192.168.254.0/24");
        Network testNetwork2 = Network.parse("10.0.5.0/24");
        Network testNetwork3 = Network.parse("178.34.0.0/16");
        Network testNetwork4 = Network.parse("1.2.3.4/24");
        testNetwork1.splitBySize(126);
        testNetwork2.splitBySize(30);
        testNetwork3.splitBySize(14);
        testNetwork3.addSubnet(24);
        networkTreeModel.addNetwork(testNetwork1);
        networkTreeModel.addNetwork(testNetwork2);
        networkTreeModel.addNetwork(testNetwork3);
        networkTreeModel.addNetwork(testNetwork4);

    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object node = networkTree.getLastSelectedPathComponent();
        if(node instanceof NetworkTreeNode)
        {
            Network network = ((NetworkTreeNode)node).getNetwork();
            fillInfoPanel(network);
            fillHostPanel(network);
        }
        else
        {
            infoPane.setBottomComponent(null);
        }
    }

    private void fillInfoPanel(Network networkBase) {
        JPanel infoPanel = new JPanel(new GridLayout(networkBase.getNetworkIdV6() == null ? 5 : 8,2));
        infoPanel.add(new JLabel("Netzwerk"));
        infoPanel.add(new JLabel(networkBase.getName()));
        infoPanel.add(new JLabel("IPv4"));
        infoPanel.add(new JLabel());
        infoPanel.add(new JLabel("Netzwerk Id:"));
        infoPanel.add(new JLabel(networkBase.getNetworkIdV4().toString()));
        infoPanel.add(new JLabel("Subnetzmaske:"));
        infoPanel.add(new JLabel(networkBase.getNetworkIdV4().toString()));
        infoPanel.add(new JLabel("Broadcastaddresse:"));
        infoPanel.add(new JLabel(networkBase.getBroadcastAddress().toString()));
        if(networkBase.getNetworkIdV6() != null)
        {
            infoPanel.add(new JLabel("IPv6"));
            infoPanel.add(new JLabel());
            infoPanel.add(new JLabel("Netzwerk Id:"));
            infoPanel.add(new JLabel(networkBase.getNetworkIdV6().toString()));
            infoPanel.add(new JLabel("Prefix:"));
            infoPanel.add(new JLabel(String.valueOf(networkBase.getPrefixV6())));
        }
        infoPane.setBottomComponent(infoPanel);
    }

    private void fillHostPanel(Network network) {
        hostPanel.setNetwork(network);
    }

    private void handleTreeRightClick(MouseEvent e){
        if (SwingUtilities.isRightMouseButton(e)) {
            int row = networkTree.getClosestRowForLocation(e.getX(), e.getY());
            networkTree.setSelectionRow(row);
            JPopupMenu menu = new JPopupMenu();
            Object element = networkTree.getPathForRow(row).getLastPathComponent();
            if(element == networkTreeModel.getRoot())
            {
                menu.add(new AbstractAction("Neues Netzwerk") {
                    public void actionPerformed (ActionEvent e) {
                        TreeTabPanel.Instance.handleCreateNetwork();
                    }
                });
            }
            else if(element instanceof NetworkTreeNode)
            {
                NetworkTreeNode networkNode = (NetworkTreeNode)element;
                menu.add(new AbstractAction("Neues Subnetz") {
                    public void actionPerformed (ActionEvent e) {
                        TreeTabPanel.Instance.handleCreateNetwork(networkNode);
                    }
                });
                menu.add(new AbstractAction("Gleichmäßig nach Größe") {
                    public void actionPerformed (ActionEvent e) {
                        TreeTabPanel.Instance.handleSplitBySize(networkNode);
                    }
                });
                menu.add(new AbstractAction("Gleichmäßig nach Anzahl") {
                    public void actionPerformed (ActionEvent e) {
                        TreeTabPanel.Instance.handleSplitByCount(networkNode);
                    }
                });
                menu.addSeparator();
                menu.add(new AbstractAction("Netzwerk/Subnetz löschen") {
                    public void actionPerformed (ActionEvent e) {
                        networkTreeModel.deleteNetwork(networkNode);
                    }
                });
            }
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void handleCreateNetwork() {
        String input = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Netzwerk Id und Prefix:",
                "Netzwerk hinzufügen",
                JOptionPane.PLAIN_MESSAGE);
        if(input == null) return;
        try{
            Network network = Network.parse(input);
            networkTreeModel.addNetwork(network);
        }catch (UnsupportedOperationException e){
            DialogBox.error(e.getMessage(),this);
        }
    }

    private void handleCreateNetwork(NetworkTreeNode parent) {
        String input = (String)JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Netzwerk Id und Prefix:",
                "Subnetz hinzufügen",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                GuiUtils.getInitialSubnetString(parent.getNetwork()));
        if(input == null) return;
        try{
            Network network = Network.parse(input);
            networkTreeModel.addNetwork(network, parent);
        }catch (UnsupportedOperationException e){
            DialogBox.error(e.getMessage(),this);
        }
    }

    private void handleSplitBySize(NetworkTreeNode networkTreeNode) {
        ArrayList<Integer> deviders = networkTreeNode.getNetwork().possibleDividers();

        Object input = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Größe der Subnetze angeben",
                "Netzwerk teilen",
                JOptionPane.QUESTION_MESSAGE, null,
                deviders.toArray(), // Array of choices
                deviders.get(0)); // Initial choice

        if(input == null){
            return;
        }

        networkTreeModel.splitBySize(networkTreeNode, (int)input);
    }

    private void handleSplitByCount(NetworkTreeNode networkTreeNode) {
        ArrayList<Integer> sizes = new ArrayList<>();

        networkTreeNode.getNetwork().possibleDividers()
                .forEach(x -> sizes.add((networkTreeNode.getNetwork().getMaxHosts()+2) / (x+2)));
        sizes.add(1);

        Object input = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Anzahl der Subnetze angeben",
                "Netzwerk teilen",
                JOptionPane.QUESTION_MESSAGE, null,
                sizes.toArray(), // Array of choices
                sizes.get(0)); // Initial choice

        if(input == null){
            return;
        }

        networkTreeModel.splitByCount(networkTreeNode, (int)input);
    }
}