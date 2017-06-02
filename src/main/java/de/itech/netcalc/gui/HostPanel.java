package de.itech.netcalc.gui;

import de.itech.netcalc.net.Host;
import de.itech.netcalc.net.IPAddress;
import de.itech.netcalc.net.IPv6Address;
import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

class HostPanel extends JPanel implements TableModelListener{
    private HostTableModel model;
    private Network network;
    private Boolean updatedProgrammatically = false;

    HostPanel() {
        super(new GridLayout());
        JTable table = new JTable(model = new HostTableModel());
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
                        h.getIPv6Address(),
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
            if(input == null || input.equals("")) {
                host.setIpv6Address(null);
            } else {
                System.out.println(input);
                IPv6Address address = IPAddress.parseIPv6(input);
                System.out.println(address);
                host.setIpv6Address(address);
                updatedProgrammatically = true;
                model.setValueAt(address, e.getFirstRow(), 1);
                updatedProgrammatically = false;
            }
        }
    }
}