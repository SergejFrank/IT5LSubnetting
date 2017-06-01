package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.Subnet;

import javax.swing.*;
import java.awt.*;

public class SubnetCalculatorFrame extends JFrame {
    public static SubnetCalculatorFrame Instance;

    private JTabbedPane tabbedPane = new JTabbedPane();
    // Panels
    private NetworksPanel netsPan;

    private JPanel subnetsPanel;
    private JPanel hostsPanel;
    private JPanel treePanel = new TreeTabPanel();

    public SubnetCalculatorFrame(String title){
        super(title);

        Instance = this;

        netsPan = new NetworksPanel();

        // Add Panels to the tabs
        tabbedPane.add("Network-Tree", treePanel);
        tabbedPane.add("Networks", netsPan);

        add(tabbedPane);

        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);
    }

    public void goToSubnets(Network network) {
        tabbedPane.remove(subnetsPanel);
        tabbedPane.remove(hostsPanel);
        subnetsPanel = new SubnetPanel(network);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.setSelectedIndex(2);
    }

    public void goToHosts(Subnet subnet) {
        tabbedPane.remove(hostsPanel);
        hostsPanel = new JPanel();
        tabbedPane.add("Hosts", hostsPanel);
        tabbedPane.setSelectedIndex(3);
    }
}