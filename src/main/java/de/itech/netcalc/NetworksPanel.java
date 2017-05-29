package de.itech.netcalc;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Marcel on 29.05.2017.
 */
public class NetworksPanel extends JPanel {

    private JPanel bottomPanel;
    private JList netList;
    private DefaultListModel listModel;
    private JLabel networksLabel;

    private JButton bTest1;
    private JButton bTest2;
    private JButton bTest3;

    public NetworksPanel(LayoutManager layoutManager, Network network){
        super(layoutManager);

        bottomPanel = new JPanel(new FlowLayout());
        networksLabel = new JLabel("Networks");
        listModel = new DefaultListModel();
        netList = new JList(listModel);

        // Buttons
        bTest1 = new JButton("Test1");
        bTest2 = new JButton("Test2");
        bTest3 = new JButton("Test3");

        // Configure networks list
        listModel.addElement(network);
        listModel.addElement("test");
        listModel.addElement("eins");
        listModel.addElement("zwei");
        netList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        netList.setLayoutOrientation(JList.VERTICAL);
        netList.setVisibleRowCount(-1);
        netList.setSelectedIndex(0);
        netList.setVisibleRowCount(5);
        netList.setPreferredSize(new Dimension(200, 200));
        JScrollPane listScrollPane = new JScrollPane(netList);


        // Configure Networks Tab
        networksLabel.setVerticalAlignment(JLabel.TOP);
        add(networksLabel, BorderLayout.LINE_START);
        add(netList);
        bottomPanel.add(bTest1);
        bottomPanel.add(bTest2);
        bottomPanel.add(bTest3);
        add(bottomPanel, BorderLayout.PAGE_END);    }

}
