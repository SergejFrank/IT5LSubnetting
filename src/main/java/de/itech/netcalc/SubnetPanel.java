package de.itech.netcalc;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import javax.xml.soap.Text;
import java.awt.*;

/**
 * Created by it5-senkjo on 29.05.2017.
 */
public class SubnetPanel extends JPanel {


    GridBagConstraints c = new GridBagConstraints();

    JProgressBar progressBar = new JProgressBar();
    JLabel NetworkLabel = new JLabel();
    JTextField NetworkField = new JTextField();
    JLabel NetclassLabel = new JLabel();
    JTextField NetclassField = new JTextField();
    JLabel SubnetLabel = new JLabel();
    JTextArea SubnetTextArea = new JTextArea();
    public SubnetPanel()
    {
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

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 1;

        this.add(NetclassLabel, c);

        NetworkLabel.setText("Network");

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 2;
        this.add(NetworkLabel, c);





        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth=2;
        this.add(NetworkField, c);


        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 1;

        this.add(NetclassField, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 1;
        c.gridy = 2;

        this.add(SubnetTextArea, c);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth=3;
        this.add(progressBar, c);


    }
}
