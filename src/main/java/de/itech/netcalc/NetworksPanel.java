package de.itech.netcalc;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by Marcel on 29.05.2017.
 */
public class NetworksPanel extends JPanel{

    private JLabel networksLabel;
    private JLabel ipLabel;
    private JTextField ipText;

    // Sub panels
    private JPanel bottomPanel;
    private JPanel inputPanel;
    private JPanel buttonPanel;

    // JList
    private JList netList;
    private DefaultListModel listModel;

    // Buttons
    private JButton bTest1;
    private JButton bTest2;
    private JButton bTest3;

    // Border

    public NetworksPanel(LayoutManager layoutManager, Network network){
        super(layoutManager);

        networksLabel = new JLabel("Networks");
        ipLabel = new JLabel("IP: ");
        ipText = new JTextField();
        ipText.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        // Buttons
        bTest1 = new JButton("New");
        bTest2 = new JButton("Test2");
        bTest3 = new JButton("Test3");

        // Panels
        bottomPanel = new JPanel(new GridLayout(2,1));
        inputPanel = new JPanel(new BorderLayout(42,5));
        buttonPanel = new JPanel();


        // JList
        listModel = new DefaultListModel();
        netList = new JList(listModel);
        netList.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));


        bTest1.addActionListener(new ButtonListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("TEST");
            }
        });
        bTest2.addActionListener(new ButtonListener());
        bTest3.addActionListener(new ButtonListener());

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


        // Configure Networks Layout
        networksLabel.setVerticalAlignment(JLabel.TOP);
        add(networksLabel, BorderLayout.LINE_START);
        add(netList);
        add(bottomPanel, BorderLayout.PAGE_END);

        bottomPanel.add(inputPanel);
        bottomPanel.add(buttonPanel);

        ipLabel.setVerticalAlignment(JLabel.TOP);
        inputPanel.add(ipLabel, BorderLayout.LINE_START);
        inputPanel.add(ipText, BorderLayout.CENTER);

        buttonPanel.add(bTest1);
        buttonPanel.add(bTest2);
        buttonPanel.add(bTest3);

    }

    public class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            System.out.println("Good job you pressed a button, what a man!");
        }
    }

}
