package de.itech.netcalc.gui;

import de.itech.netcalc.net.Network;

import javax.swing.table.DefaultTableModel;

class HostTableModel extends DefaultTableModel {
    private Network network;

    public void setNetwork(Network network) {
        this.network = network;
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0 && (column != 1 || network.isIPv6Enabled());
    }
}