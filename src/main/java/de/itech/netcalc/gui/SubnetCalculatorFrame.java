package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.*;
import java.awt.*;

public class SubnetCalculatorFrame extends JFrame {
    public static SubnetCalculatorFrame Instance;

    private JTabbedPane tabbedPane = new JTabbedPane();
    // Panels
    private NetworksPanel netsPan;

    private JPanel subnetsPanel = new SubnetPanel(Network.parse("10.0.0.5/24"));
    private JPanel hostsPanel = new JPanel();
    private JLabel networksLabel = new JLabel("Networks");
    private JLabel subnetsLabel = new JLabel("Subnets");
    private JLabel hostsLabel = new JLabel("Hosts");

    public SubnetCalculatorFrame(String title){
        super(title);
        Instance = this;

        netsPan = new NetworksPanel();

        // Add Panels to the tabs
        tabbedPane.add("Networks", netsPan);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.add("Hosts", hostsPanel);

        add(tabbedPane);

        //setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);

    }

    public void goToSubnets(Network network) {
        tabbedPane.remove(subnetsPanel);
        tabbedPane.remove(hostsPanel);
        subnetsPanel = new SubnetPanel(network);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.add("hosts", hostsPanel);
        tabbedPane.setSelectedIndex(1);
    }
}
