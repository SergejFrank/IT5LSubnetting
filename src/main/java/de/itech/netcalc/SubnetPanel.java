package de.itech.netcalc;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import sun.nio.ch.Net;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.xml.soap.Text;
import java.awt.*;

/**
 * Created by it5-senkjo on 29.05.2017.
 */
public class SubnetPanel extends JPanel {


    GridBagConstraints c = new GridBagConstraints();

    JProgressBar progressBar = new JProgressBar();
    JProgressBar progressBar2 = new JProgressBar();
    JLabel NetworkLabel = new JLabel("Network",SwingConstants.LEFT);
    JTextField NetworkField = new JTextField();
    JLabel NetclassLabel = new JLabel();
    JTextField NetclassField = new JTextField();
    JLabel SubnetLabel = new JLabel();
    JTextArea SubnetTextArea = new JTextArea();

    private JList list;
    private DefaultListModel listModel;

    public SubnetPanel()
    {


        this.setBorder(new EmptyBorder(10, 10, 10, 10));

        listModel = new DefaultListModel();
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
