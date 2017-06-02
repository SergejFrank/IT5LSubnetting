package de.itech.netcalc.gui;

import javax.swing.*;
import java.awt.*;

public class SubnetCalculatorFrame extends JFrame {
    static SubnetCalculatorFrame Instance;

    public SubnetCalculatorFrame(String title){
        super(title);

        Instance = this;

        add(new TreePanel());

        setSize(1000, 550);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);
    }
}