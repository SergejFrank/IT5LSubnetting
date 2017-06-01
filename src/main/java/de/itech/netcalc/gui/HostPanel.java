package de.itech.netcalc.gui;

import de.itech.netcalc.net.Host;
import de.itech.netcalc.net.IPAddress;
import de.itech.netcalc.net.Subnet;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.*;

class HostPanel extends JPanel implements TableModelListener{
    private HostTableModel model;
    private Subnet subnet;

    HostPanel() {
        super(new GridLayout());
        JTable table = new JTable(model = new HostTableModel());
        model.addColumn("IPv4 Adresse");
        model.addColumn("Name");
        model.addTableModelListener(this);
        this.add(new JScrollPane(table));
    }

    public void setSubnet(Subnet subnet) {
        this.subnet = subnet;
        model.setRowCount(0);
        if(subnet != null)
        {
            for(Host h : subnet.getHosts()) {
                Object[] data = new Object[] {
                        h.getIPv4Address(),
                        h.getName()
                };
                model.addRow(data);
            }
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if(e.getType() != TableModelEvent.UPDATE) return;
        Host host = subnet.getHost(IPAddress.parseIPv4(model.getValueAt(e.getFirstRow(), 0).toString()));
        String newName = model.getValueAt(e.getFirstRow(), 1).toString();
        host.setName(newName);
    }
}