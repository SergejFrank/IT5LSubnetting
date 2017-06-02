package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.Optional;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class NetworksPanel extends JPanel{

    GridBagConstraints c = new GridBagConstraints();

    private JLabel networksLabel;
    private JLabel ipLabel;
    private JTextField ipText;

    // JList
    private JList netList;
    private DefaultListModel<Network> listModel;

    // Buttons
    private JButton bNewNetwork;
    private JButton bDeleteNetwork;
    private JButton bSubnets;

    // Border

    NetworksPanel(){
        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Labels, textfield
        networksLabel = new JLabel("Networks");
        ipLabel = new JLabel("IP: ");
        ipText = new JTextField();
        ipText.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        // Buttons
        bNewNetwork = new JButton("New Network");
        bDeleteNetwork = new JButton("Delete Network");
        bSubnets = new JButton("Subnets");

        // JList
        listModel = new DefaultListModel();
        netList = new JList(listModel);
        netList.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        // Buttonlistener
        ButtonListener bListener = new ButtonListener();
        bNewNetwork.addActionListener(bListener);
        bDeleteNetwork.addActionListener(bListener);
        bSubnets.addActionListener(bListener);

        // Configure networks list
        Network testNetwork1 = Network.parse("192.168.254.0/24");
        Network testNetwork2 = Network.parse("10.0.5.0/24");
        Network testNetwork3 = Network.parse("178.34.0.0/16");
        testNetwork1.splitBySize(126);
        testNetwork2.splitBySize(30);
        testNetwork3.splitBySize(14);
        listModel.addElement(testNetwork1);
        listModel.addElement(testNetwork2);
        listModel.addElement(testNetwork3);
        netList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        netList.setLayoutOrientation(JList.VERTICAL);
        netList.setVisibleRowCount(-1);
        netList.setSelectedIndex(0);
        netList.setVisibleRowCount(5);
        netList.setPreferredSize(new Dimension(200, 200));
        netList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    Network selectedNetwork = listModel.elementAt(index);
                    SubnetCalculatorFrame.Instance.goToSubnets(selectedNetwork);
                }
            }
        });

        JScrollPane listScrollPane = new JScrollPane(netList);


        // Configure Networks Layout

        c.weightx = 1;
        c.weighty = 1;

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

        // Add listcrollpane
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
        add(bNewNetwork, c);
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0.1;
        add(bDeleteNetwork, c);
        c.gridx = 2;
        c.gridy = 2;
        c.weightx = 0.1;
        add(bSubnets, c);

    }

    public class ButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(e.getSource() == bNewNetwork) {
                addNewNetwork();
            }else if(e.getSource() == bDeleteNetwork){
                System.out.println("2");
            }else if(e.getSource() == bSubnets){
                System.out.println("3");
            }

        }
    }

    private void addNewNetwork(){
        String input = ipText.getText();
        try{
            Network network = Network.parse(input);
            Optional<Network> collidingNetwork = Collections.list(listModel.elements()).stream().filter(other -> other.isColliding(network)).findFirst();
            if(collidingNetwork.isPresent()){
                throw new UnsupportedOperationException("Network is Colliding with "+collidingNetwork.get());
            }else{
                listModel.addElement(network);
            }
        }catch (UnsupportedOperationException e){
            DialogBox.error(e.getMessage(),this);
        }
    }
}