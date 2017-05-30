package de.itech.netcalc.gui;

import javax.swing.*;
import java.awt.*;

public class DialogBox {

    public static void error(String msg, Component parentComponent){
        JOptionPane.showMessageDialog(parentComponent,
                msg,
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}