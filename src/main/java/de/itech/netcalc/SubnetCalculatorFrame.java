package de.itech.netcalc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marcel on 22.05.2017.
 */
public class SubnetCalculatorFrame extends JFrame {

    private Network network;

    // Panels
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

        listModel = new DefaultListModel();
        listModel.addElement(network);
        listModel.addElement("test");
        listModel.addElement("eins");
        listModel.addElement("zwei");

        // Create the list and put it in a scroll pane
        list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        list.setSelectedIndex(0);
        list.setVisibleRowCount(5);
        list.setPreferredSize(new Dimension(200, 200));
        JScrollPane listScrollPane = new JScrollPane(list);

        networksPanel.add(networksLabel, BorderLayout.LINE_START);
        networksPanel.add(list);
        networksBottomPanel.add(test1);
        networksBottomPanel.add(test2);
        networksBottomPanel.add(test3);
        networksPanel.add(networksBottomPanel, BorderLayout.PAGE_END);
        subnetsPanel.add(subnetsLabel);
        hostsPanel.add(hostsLabel);

        tabbedPane.add("Networks", networksPanel);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.add("Hosts", hostsPanel);

        add(tabbedPane);

        //this.setSize(800, 600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);

    }

}
