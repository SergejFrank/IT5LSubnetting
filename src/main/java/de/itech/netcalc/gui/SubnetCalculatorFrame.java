package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.*;
import java.awt.*;

public class SubnetCalculatorFrame extends JFrame {
    static SubnetCalculatorFrame Instance;

    private JTabbedPane tabbedPane = new JTabbedPane();

    private JPanel subnetsPanel;
    private JPanel hostsPanel;

    public SubnetCalculatorFrame(String title){
        super(title);

        Instance = this;

        NetworksPanel netsPan = new NetworksPanel();

        // Add Panels to the tabs
        JPanel treePanel = new TreeTabPanel();
        tabbedPane.add("Network-Tree", treePanel);
        tabbedPane.add("Networks", netsPan);

        add(tabbedPane);

        setSize(600, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);

    }

    void goToSubnets(Network network) {
        tabbedPane.remove(subnetsPanel);
        tabbedPane.remove(hostsPanel);
        subnetsPanel = new SubnetPanel(network);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.setSelectedIndex(2);
    }

    void goToHosts(Network subnet) {
        tabbedPane.remove(hostsPanel);
        hostsPanel = new JPanel();
        tabbedPane.add("Hosts", hostsPanel);
        tabbedPane.setSelectedIndex(3);
    }
}
