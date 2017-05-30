package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.NetworkBase;
import de.itech.netcalc.net.Subnet;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TreeTabPanel extends JPanel implements TreeSelectionListener {
    private static TreeTabPanel Instance;
    private NetworkTreeModel networkTreeModel;
    private JSplitPane infoPane;
    private JTree networkTree;

    public TreeTabPanel() {
        super(new GridLayout(1,0));
        Instance = this;
        JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        infoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainPane.setDividerSize(3);
        infoPane.setDividerSize(3);
        mainPane.setLeftComponent(infoPane);
        mainPane.setRightComponent(new JPanel());
        mainPane.setResizeWeight(0.3);
        infoPane.setResizeWeight(0.5);

        networkTreeModel = new NetworkTreeModel();
        networkTree = new JTree(networkTreeModel);
        networkTree.addTreeSelectionListener(this);
        networkTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleTreeRightClick(e);
            }
        });
        JScrollPane treeScrollPane = new JScrollPane(networkTree);
        infoPane.setTopComponent(treeScrollPane);

        Network testNetwork1 = Network.parse("192.168.254.0/24");
        Network testNetwork2 = Network.parse("10.0.5.0/24");
        Network testNetwork3 = Network.parse("178.34.0.0/16");
        Network testNetwork4 = Network.parse("1.2.3.4/24");
        testNetwork1.splitEqualy(126);
        testNetwork2.splitEqualy(30);
        testNetwork3.splitEqualy(14);
        testNetwork3.addSubnet(24);
        networkTreeModel.addNetwork(testNetwork1);
        networkTreeModel.addNetwork(testNetwork2);
        networkTreeModel.addNetwork(testNetwork3);
        networkTreeModel.addNetwork(testNetwork4);

        add(mainPane);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Object node = networkTree.getLastSelectedPathComponent();
        if(node instanceof Network)
        {
            fillInfoPanel((Network)node, "Netzwerk");
        }
        else if(node instanceof Subnet)
        {
            fillInfoPanel((Subnet)node, "Subnetz");
        }
        else
        {
            infoPane.setBottomComponent(null);
        }
    }

    private void fillInfoPanel(NetworkBase networkBase, String header) {
        JPanel infoPanel = new JPanel(new GridLayout(networkBase.getNetworkIdV6() == null ? 5 : 8,2));
        infoPanel.add(new JLabel(header));
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
            else if(element instanceof Network)
            {
                menu.add(new JMenuItem("Neues Subnetz"));
                menu.add(new JMenuItem("Gleichmäßig nach Größe"));
                menu.add(new JMenuItem("Gleichmäßig nach Anzahl"));
                menu.addSeparator();
                menu.add(new JMenuItem("Netzwerk löschen"));
            }
            else if(element instanceof Subnet)
            {
                menu.add(new JMenuItem("Subnetz löschen"));
            }
            menu.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void handleCreateNetwork() {
        String input = JOptionPane.showInputDialog(
                SubnetCalculatorFrame.Instance,
                "Netzwerk Id und Prefix::",
                "Netzwerk hinzufügen",
                JOptionPane.PLAIN_MESSAGE);
        networkTreeModel.addNetwork(Network.parse(input));
    }
}