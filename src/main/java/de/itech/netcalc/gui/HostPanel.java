package de.itech.netcalc.gui;

import de.itech.netcalc.net.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;
import java.awt.dnd.InvalidDnDOperationException;
import java.util.InvalidPropertiesFormatException;

class HostPanel extends JPanel implements TableModelListener{
    private HostTableModel model;
    private Network network;
    private boolean updatedProgrammatically;

    HostPanel() {
        super(new GridLayout());
        JTable table = new JTable(model = new HostTableModel());
        table.setFont(new Font("monospaced", Font.PLAIN, 12));
        model.addColumn("IPv4 Adresse");
        model.addColumn("IPv6 Adresse");
        model.addColumn("Name");
        model.addTableModelListener(this);
        this.add(new JScrollPane(table));
    }

    public void setNetwork(Network network) {
        this.network = network;
        reloadHosts();
    }

    void reloadHosts() {
        model.setRowCount(0);
        if(network != null && network.getHosts() != null)
        {
            for(Host h : network.getHosts()) {
                if(h == null) continue;
                Object[] data = new Object[] {
                        h.getIPv4Address(),
                        h.getIPv6Address() == null ? null : h.getIPv6Address().toString(true),
                        h.getName()
                };
                model.addRow(data);
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if(e.getType() != TableModelEvent.UPDATE || updatedProgrammatically) return;
        if(e.getColumn() == 2) {
            Host host = network.getHost(IPAddress.parseIPv4(model.getValueAt(e.getFirstRow(), 0).toString()));
            String newName = model.getValueAt(e.getFirstRow(), 2).toString();
            host.setName(newName);
        }
        else if(e.getColumn() == 1) {
            String input = model.getValueAt(e.getFirstRow(), 1).toString();
            Host host = network.getHost(IPAddress.parseIPv4(model.getValueAt(e.getFirstRow(), 0).toString()));
            if (input == null || input.equals("")) {
                host.setIpv6Address(null);
            } else {
                if(network.getNetworkIdV6() == null) {
                    DialogBox.error("Biite zuerst IPv6 im Netzwerk konfigurieren.", null);
                    throw new UnsupportedOperationException("Configure IPv6 on Network first.");
                }
                if (!IPAddress.isValidIPv6(input)) {
                    DialogBox.error("Die eingegebene Adresse ist keine gültige IPv6 Adresse.", null);
                    throw new UnsupportedOperationException("Invalid IPv6 address.");
                }
                IPv6Address address = IPAddress.parseIPv6(input);
                if(!NetUtils.isInSubnet(network.getNetworkIdV6(), address)) {
                    DialogBox.error("Die eingegebene Adresse liegt nicht im IPv6 Subnetz.", null);
                    throw new UnsupportedOperationException("Not in Network.");
                }
                try {
                    host.setIpv6Address(address);
                    //model.setValueAt will fire tableChanged event -> prevent event handler from executing
                    updatedProgrammatically = true;
                    model.setValueAt(address, e.getFirstRow(), 1);
                    updatedProgrammatically = false;
                } catch(Exception ex) {
                    updatedProgrammatically = false;
                    DialogBox.error("Ein Fehler ist aufgetreten\n" + ex.getMessage(), null);
                    throw new UnsupportedOperationException("Unexpected Exception", ex);
                }
            }
        }
    }
}