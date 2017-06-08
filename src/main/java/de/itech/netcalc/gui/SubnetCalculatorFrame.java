package de.itech.netcalc.gui;

import de.itech.netcalc.Config;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SubnetCalculatorFrame extends JFrame {
    private TreePanel treePanel = new TreePanel();

    public SubnetCalculatorFrame(String title){
        super(title);

        initialiteMenu();

        add(treePanel);

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
                System.exit(0);
            }
        });

        JMenu viewMenu = new JMenu("Ansicht");
        JMenu notationV4Menu =  new JMenu("Schreibweise IPv4");
        JRadioButtonMenuItem notV4Dec = new JRadioButtonMenuItem("Dezimal", true);
        JRadioButtonMenuItem notV4Bin = new JRadioButtonMenuItem("Binär");
        notV4Dec.addActionListener(e -> {
            Config.setIpv4Notation(notV4Dec.isSelected() ? Config.IPNotation.DECIMAL : Config.IPNotation.BINARY);
            notV4Bin.setSelected(!notV4Dec.isSelected());
            treePanel.refresh();
        });
        notV4Bin.addActionListener(e -> {
            Config.setIpv4Notation(notV4Bin.isSelected() ? Config.IPNotation.BINARY : Config.IPNotation.DECIMAL);
            notV4Dec.setSelected(!notV4Bin.isSelected());
            treePanel.refresh();
        });
        notationV4Menu.add(notV4Dec);
        notationV4Menu.add(notV4Bin);
        JMenu notationV6Menu =  new JMenu("Schreibweise IPv6");
        JRadioButtonMenuItem notV6Hex = new JRadioButtonMenuItem("Hexadezimal", true);
        JRadioButtonMenuItem notV6Bin = new JRadioButtonMenuItem("Binär");
        notV6Hex.addActionListener(e -> {
            Config.setIpv4Notation(notV6Hex.isSelected() ? Config.IPNotation.HEXADECEMAL : Config.IPNotation.BINARY);
            notV6Bin.setSelected(!notV6Hex.isSelected());
            treePanel.refresh();
        });
        notV6Bin.addActionListener(e -> {
            Config.setIpv4Notation(notV6Bin.isSelected() ? Config.IPNotation.BINARY : Config.IPNotation.HEXADECEMAL);
            notV6Hex.setSelected(!notV6Bin.isSelected());
            treePanel.refresh();
        });
        notationV6Menu.add(notV6Hex);
        notationV6Menu.add(notV6Bin);

        viewMenu.add(notationV4Menu);
        viewMenu.add(notationV6Menu);

        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);
    }
}