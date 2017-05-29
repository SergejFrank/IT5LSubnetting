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

    private JPanel networksPanel = new JPanel(new BorderLayout(5, 5));
    private JPanel networksBottomPanel = new JPanel();
    private JPanel subnetsPanel = new JPanel();
    private JPanel hostsPanel = new JPanel();
    private JLabel networksLabel = new JLabel("Networks");
    private JLabel subnetsLabel = new JLabel("Subnets");
    private JLabel hostsLabel = new JLabel("Hosts");
    private JTabbedPane tabbedPane = new JTabbedPane();
    // Network List
    private JList list;
    private DefaultListModel listModel;

    // Buttons
    JButton test1 = new JButton("Test1");
    JButton test2 = new JButton("Test2");
    JButton test3 = new JButton("Test3");

    public SubnetCalculatorFrame(String title, Network network){
        super(title);
        this.network = network;

        // Add Panels to the tabs
        tabbedPane.add("Networks", netsPan);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.add("Hosts", hostsPanel);

        add(tabbedPane);

        //this.setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);

    }

}
