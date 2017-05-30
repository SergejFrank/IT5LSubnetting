package de.itech.netcalc;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marcel on 22.05.2017.
 */
public class SubnetCalculatorFrame extends JFrame {

    private Network network;

    private JTabbedPane tabbedPane = new JTabbedPane();

    // Panels
    private NetworksPanel netsPan;

    private JPanel subnetsPanel = new SubnetPanel();
    private JPanel hostsPanel = new JPanel();
    private JLabel networksLabel = new JLabel("Networks");
    private JLabel subnetsLabel = new JLabel("Subnets");
    private JLabel hostsLabel = new JLabel("Hosts");

    public SubnetCalculatorFrame(String title, Network network){
        super(title);
        this.network = network;

        netsPan = new NetworksPanel(network);

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

}
