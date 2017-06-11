package de.itech.netcalc.gui;

import de.itech.netcalc.Config;
import de.itech.netcalc.net.Format;
import de.itech.netcalc.net.Network;
import de.itech.netcalc.net.NetworkCollection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class SubnetCalculatorFrame extends JFrame {
    private final TreePanel treePanel = new TreePanel();

    public SubnetCalculatorFrame(){
        super("Netzwerkplaner - BS14 IT5l");

        initializeMenu();

        add(treePanel);

        setSize(1000, 550);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2); // screen center

        setVisible(true);
    }

    private void initializeMenu() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("Datei");
        fileMenu.add(new AbstractAction("Neu") {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(!GuiUtils.confirmation("Neue Netzwerkplanung", "Soll eine neue Netzwerkplanung erstellt werden?")) return;
                treePanel.clear();
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(new AbstractAction("Öffnen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = GuiUtils.getFileOpen("Planung öffnen");
                    if(file == null) return;
                    NetworkCollection networkCollection =  NetworkCollection.fromXML(file);
                    treePanel.getNetworkTreeModel().clear();
                    treePanel.getNetworkTreeModel().setRootIPv6Prefix(networkCollection.getGlobalIPv6Prefix(), networkCollection.getGlobalIPv6PrefixLength());
                    treePanel.getNetworkTreeModel().addNetworkRange(networkCollection.getNetworks());
                } catch(Exception ex) {
                    GuiUtils.error("Beim Öffnen der Planung ist ein Fehler aufgetreten.");
                }
            }
        });
        fileMenu.add(new AbstractAction("Speichern unter...") {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    File file = GuiUtils.getSaveFile("Planung speichern untert...", "Planung.xml");
                    if(file == null) return;
                    NetworkCollection networkCollection = new NetworkCollection();
                    ArrayList<Network> networks = treePanel.getNetworkTreeModel().getNetworks();
                    networkCollection.setNetworks(networks);
                    networkCollection.setGlobalIPv6Prefix(treePanel.getNetworkTreeModel().getRootIPv6Prefix());
                    networkCollection.setGlobalIPv6PrefixLength(treePanel.getNetworkTreeModel().getRootIPv6PrefixLength());
                    networkCollection.save(file);
                } catch (Exception ex) {
                    GuiUtils.error("Beim Speichern der Planung ist ein Fehler aufgetreten.");
                }
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
            Config.setIpv4Notation(notV4Dec.isSelected() ? Format.IPv4Format.DECIMAL : Format.IPv4Format.BINARY);
            notV4Bin.setSelected(!notV4Dec.isSelected());
            treePanel.refresh();
        });
        notV4Bin.addActionListener(e -> {
            Config.setIpv4Notation(notV4Bin.isSelected() ? Format.IPv4Format.BINARY : Format.IPv4Format.DECIMAL);
            notV4Dec.setSelected(!notV4Bin.isSelected());
            treePanel.refresh();
        });
        notationV4Menu.add(notV4Dec);
        notationV4Menu.add(notV4Bin);
        JMenu notationV6Menu =  new JMenu("Schreibweise IPv6");
        JRadioButtonMenuItem notV6Hex = new JRadioButtonMenuItem("Hexadezimal", true);
        JRadioButtonMenuItem notV6Bin = new JRadioButtonMenuItem("Binär");
        notV6Hex.addActionListener(e -> {
            Config.setIpv6Notation(notV6Hex.isSelected() ? Format.IPv6Format.NORMAL : Format.IPv6Format.BINARY);
            notV6Bin.setSelected(!notV6Hex.isSelected());
            treePanel.refresh();
        });
        notV6Bin.addActionListener(e -> {
            Config.setIpv6Notation(notV6Bin.isSelected() ? Format.IPv6Format.BINARY : Format.IPv6Format.NORMAL);
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