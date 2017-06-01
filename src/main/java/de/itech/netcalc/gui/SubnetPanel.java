package de.itech.netcalc.gui;
import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SubnetPanel extends JPanel {
    private Network network;

    GridBagConstraints c = new GridBagConstraints();

    private JProgressBar progressBar = new JProgressBar();
    private JProgressBar progressBar2 = new JProgressBar();
    private JLabel NetworkLabel = new JLabel("Network",SwingConstants.LEFT);
    private JTextField NetworkField = new JTextField();
    private JLabel NetclassLabel = new JLabel();
    private JTextField NetclassField = new JTextField();
    private JLabel SubnetLabel = new JLabel();
    JTextArea SubnetTextArea = new JTextArea();

    private JList subnetList;
    private DefaultListModel<Network> subnetListModel;

    public SubnetPanel(Network network)
    {
        this.network = network;

        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        subnetListModel = new DefaultListModel();
        network.getSubnets().forEach(subnet -> subnetListModel.addElement(subnet));

        // Create the subnetList and put it in a scroll pane
        subnetList = new JList(subnetListModel);
        subnetList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        subnetList.setLayoutOrientation(JList.VERTICAL);
        subnetList.setVisibleRowCount(-1);
        subnetList.setSelectedIndex(0);
        subnetList.setVisibleRowCount(5);
        subnetList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list = (JList)evt.getSource();
                if (evt.getClickCount() == 2) {
                    int index = list.locationToIndex(evt.getPoint());
                    Network selectedSubnet = subnetListModel.elementAt(index);
                    SubnetCalculatorFrame.Instance.goToHosts(selectedSubnet);
                }
            }
        });
        JScrollPane listScrollPane = new JScrollPane(subnetList);

        c.weightx =1;
        c.weighty =1;
        this.setLayout(new GridBagLayout());
        SubnetLabel.setText("Subnet");
        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth=1;
        this.add(SubnetLabel, c);

        NetclassLabel.setText("Netclass");

        c.gridx = 0;
        c.gridy = 1;

        this.add(NetclassLabel, c);


        NetworkLabel.setVerticalAlignment(SwingConstants.TOP);
        c.fill =1;
        c.gridx = 0;
        c.gridy = 2;
        this.add(NetworkLabel, c);


        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth=2;

        this.add(listScrollPane, c);

        c.fill =2;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth=2;
        c.weightx =8;
        this.add(NetworkField, c);


        c.gridx = 1;
        c.gridy = 1;

        this.add(NetclassField, c);


        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth=3;
        c.weighty =0.5;
        this.add(progressBar, c);

        JButton newSubnetButton = new JButton();
        newSubnetButton.setText("New Subnet");
        JButton deleteSubnetButton = new JButton();
        deleteSubnetButton.setText("Delete Subnet");
        JButton HostsButton = new JButton();
        HostsButton.setText("Hosts");


        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth=1;
        c.weighty =0.1;
        c.insets = new Insets(3,3,3,3);
        this.add(newSubnetButton, c);


        c.gridx = 1;
        c.gridy = 4;
        c.weighty =0.1;
        this.add(deleteSubnetButton, c);

        c.gridx = 2;
        c.gridy = 4;
        c.weighty =0.1;
        this.add(HostsButton, c);



        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth=3;
        c.weighty =0.1;
        this.add(progressBar2, c);



    }
}
