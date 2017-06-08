package de.itech.netcalc.gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SubnetCalculatorFrame extends JFrame {
    public SubnetCalculatorFrame(String title){
        super(title);

        initialiteMenu();

        add(new TreePanel());

        setSize(1000, 550);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);
    }

    private void initialiteMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Datei");
        fileMenu.add(new AbstractAction("Neu") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        fileMenu.addSeparator();
        fileMenu.add(new AbstractAction("Öffnen") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        fileMenu.add(new AbstractAction("Speichern unter...") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        fileMenu.addSeparator();
        fileMenu.add(new AbstractAction("Beenden") {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        JMenu viewMenu = new JMenu("Ansicht");
        JMenu notationMenu =  new JMenu("Schreibweise");
        notationMenu.add(new JRadioButtonMenuItem("Dezimal"));
        notationMenu.add(new JRadioButtonMenuItem("Binär"));
        viewMenu.add(notationMenu);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }
}