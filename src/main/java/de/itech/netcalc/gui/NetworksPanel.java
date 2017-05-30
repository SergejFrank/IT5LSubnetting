package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class NetworksPanel extends JPanel{

    GridBagConstraints c = new GridBagConstraints();

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

    public NetworksPanel(Network network){
        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Labels, textfields
        networksLabel = new JLabel("Networks");
        ipLabel = new JLabel("IP: ");
        ipText = new JTextField();
        ipText.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        // Buttons
        bTest1 = new JButton("New");
        bTest2 = new JButton("Test2");
        bTest3 = new JButton("Test3");

        // Panels
        /*bottomPanel = new JPanel(new GridLayout(2,1));
        inputPanel = new JPanel(new BorderLayout(42,5));
        buttonPanel = new JPanel();*/


        // JList
        listModel = new DefaultListModel();
        netList = new JList(listModel);
        netList.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        // Buttonlisteners
        bTest1.addActionListener(new ButtonListener());
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

        //networksLabel.setVerticalAlignment(JLabel.CENTER);
        //ipLabel.setVerticalAlignment(JLabel.CENTER);

        /*
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
        buttonPanel.add(bTest3);*/


        c.weightx =1;
        c.weighty =1;

        // Add labels
        networksLabel.setVerticalAlignment(SwingConstants.TOP);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth=1;
        add(networksLabel, c);

        c.gridx = 0;
        c.gridy = 1;
        add(ipLabel, c);

        // Add listscrollpane
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth=2;
        add(listScrollPane, c);

        c.gridx = 1;
        c.gridy = 1;
        add(ipText, c);

        // Add buttons
        c.insets = new Insets(3,3,3,3);
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.weightx = 0.1;
        c.insets = new Insets(3,3,3,3);
        add(bTest1, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.1;
        add(bTest2, c);
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.1;
        add(bTest3, c);

    }

    public class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == bTest1) {
                System.out.println("1");
            }else if(e.getSource() == bTest2){
                System.out.println("2");
            }else if(e.getSource() == bTest3){
                System.out.println("3");
            }

        }
    }

}
