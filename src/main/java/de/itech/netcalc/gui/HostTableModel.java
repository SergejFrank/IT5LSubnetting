package de.itech.netcalc.gui;

import javax.swing.table.DefaultTableModel;

public class HostTableModel extends DefaultTableModel {
    @Override
    public boolean isCellEditable(int row, int column) {
        return column != 0;
    }
}
