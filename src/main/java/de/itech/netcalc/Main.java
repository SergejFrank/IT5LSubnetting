package de.itech.netcalc;

import de.itech.netcalc.gui.SubnetCalculatorFrame;

import javax.swing.*;

/**
 * The Main class provides the main() method as the entry point.
 */
class Main {
    /**
     * Creates a new SubnetCalculatorFrame and shows it.
     * @param args ignored
     */
    public static void main(String[] args) throws ClassNotFoundException, UnsupportedLookAndFeelException, InstantiationException, IllegalAccessException {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        new SubnetCalculatorFrame("SubnetCalculator");
    }
}