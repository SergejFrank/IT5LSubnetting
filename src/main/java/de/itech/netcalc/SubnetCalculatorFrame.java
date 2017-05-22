package de.itech.netcalc;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Marcel on 22.05.2017.
 */
public class SubnetCalculatorFrame extends JFrame {

    private Network network;

    private JPanel networksPanel = new JPanel(new BorderLayout());
    private JPanel subnetsPanel = new JPanel();
    private JPanel hostsPanel = new JPanel();

    private JLabel networksLabel = new JLabel("Networks");
    private JLabel subnetsLabel = new JLabel("Subnets");
    private JLabel hostsLabel = new JLabel("Hosts");

    private JTabbedPane tabbedPane = new JTabbedPane();

    private JList list;
    private DefaultListModel listModel;

    public SubnetCalculatorFrame(String title, Network network){
        super(title);
        this.network = network;

        listModel = new DefaultListModel();
        listModel.addElement(network.getAddress().toString());
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
        JScrollPane listScrollPane = new JScrollPane(list);


        networksPanel.add(networksLabel);
        networksPanel.add(list);
        subnetsPanel.add(subnetsLabel);
        hostsPanel.add(hostsLabel);

        tabbedPane.add("Networks", networksPanel);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.add("Hosts", hostsPanel);

        this.add(tabbedPane);

        this.setSize(800, 600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);

        //this.pack();
        this.setVisible(true);

    }

}
