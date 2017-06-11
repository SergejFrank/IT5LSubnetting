package de.itech.netcalc.gui;

import de.itech.netcalc.Config;
import de.itech.netcalc.net.*;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.stream.IntStream;

class HostPanel extends JPanel implements TableModelListener{
    private final HostTableModel model;
    private final JTable hostTable;
    private final HostInfoPanel hostInfoPanel;
    private Network network;
    private boolean updatedProgrammatically;

    HostPanel() {
        super(new BorderLayout());
        setMinimumSize(new Dimension(400, 500));
        hostTable = new JTable(model = new HostTableModel(null));
        hostTable.setFont(new Font("monospaced", Font.PLAIN, 12));
        model.addColumn("IPv4 Adresse");
        model.addColumn("IPv6 Adresse");
        model.addColumn("Name");
        model.addTableModelListener(this);
        this.add(new JScrollPane(hostTable), BorderLayout.CENTER);
        this.add(hostInfoPanel = new HostInfoPanel(), BorderLayout.PAGE_END);

        hostTable.getSelectionModel().addListSelectionListener(e -> {
            if(hostTable.getSelectedRows().length != 1) {
                hostInfoPanel.fill(null);
            } else {
                String value = model.getValueAt(hostTable.getSelectedRow(),0).toString();
                Host host = network.getHost(IPAddress.parseIPv4(value));
                hostInfoPanel.fill(host);
            }
        });
    }

    public void setNetwork(Network network) {
        this.network = network;
        model.setNetwork(network);
        reload();
    }

    void reload() {
        setupContextMenu();
        model.setRowCount(0);
        if(network != null && network.getHosts() != null)
        {
            for(Host h : network.getHosts()) {
                if(h == null) continue;
                model.addRow(getData(h));
            }
        }
    }

    private Object[] getData(Host host) {
        return new Object[] {
                host.getIPv4Address(),
                host.getIPv6Address() == null ? null : Format.format(host.getIPv6Address(), Format.IPv6Format.SHORTHAND),
                host.getName()
        };
    }

    private void setupContextMenu() {
        if(network == null) return;
        final JPopupMenu popupMenu = new JPopupMenu();
        if(network.isIPv6Enabled()) {
            popupMenu.add(new AbstractAction("Zufällige IPv6 Adresse zuweisen") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int[] rows = hostTable.getSelectedRows();
                    Arrays.sort(rows);
                    for (int i=rows.length-1;i>=0;i--) {
                        int row = rows[i];
                        IPv4Address address = IPAddress.parseIPv4(model.getValueAt(row, 0).toString());
                        Host host = network.getHost(address);
                        host.setIpv6Address(IPv6Address.getAddressWithRandomHost(network.getNetworkIdV6()));
                        model.setValueAt(Format.format(host.getIPv6Address(), Config.getIpv6Notation()), row, 1);
                    }
                }
            });
        }
        popupMenu.add(new AbstractAction("Host entfernen") {
            @Override
            public void actionPerformed(ActionEvent e) {
                int[] rows = hostTable.getSelectedRows();
                Arrays.sort(rows);
                for (int i=rows.length-1;i>=0;i--) {
                    int row = rows[i];
                    IPv4Address address = IPAddress.parseIPv4(model.getValueAt(row, 0).toString());
                    network.removeHost(address);
                    model.removeRow(row);
                }
            }
        });
        popupMenu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(() -> {
                    int rowAtPoint = hostTable.rowAtPoint(SwingUtilities.convertPoint(popupMenu, new Point(0, 0), hostTable));
                    if (rowAtPoint > -1 && IntStream.of(hostTable.getSelectedRows()).noneMatch(x -> x == rowAtPoint)) {
                        hostTable.setRowSelectionInterval(rowAtPoint, rowAtPoint);
                    }
                });
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });

        hostTable.setComponentPopupMenu(popupMenu);
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if(e.getType() != TableModelEvent.UPDATE || updatedProgrammatically) return;
        if(e.getColumn() == 1) {
            String input = model.getValueAt(e.getFirstRow(), 1).toString();
            Host host = network.getHost(IPAddress.parseIPv4(model.getValueAt(e.getFirstRow(), 0).toString()));
            if (input == null || input.equals("")) {
                host.setIpv6Address(null);
            } else {
                if(network.getNetworkIdV6() == null) {
                    GuiUtils.error("Bitte zuerst IPv6 im Netzwerk konfigurieren.");
                    throw new UnsupportedOperationException("Configure IPv6 on Network first.");
                }
                if (!IPAddress.isValidIPv6(input)) {
                    GuiUtils.error("Die eingegebene Adresse ist keine gültige IPv6 Adresse.");
                    throw new UnsupportedOperationException("Invalid IPv6 address.");
                }
                IPv6Address address = IPAddress.parseIPv6(input);
                if(!NetUtils.isInSubnet(network.getNetworkIdV6(), network.getPrefixV6(), address)) {
                    GuiUtils.error("Die eingegebene Adresse liegt nicht im IPv6 Subnetz.");
                    throw new UnsupportedOperationException("Not in Network.");
                }
                if(address.equals(host.getIPv6Address())){
                    return;
                }
                try {
                    host.setIpv6Address(address);
                    //model.setValueAt will fire tableChanged event -> prevent event handler from executing
                    updatedProgrammatically = true;
                    model.setValueAt(address, e.getFirstRow(), 1);
                    updatedProgrammatically = false;
                } catch(Exception ex) {
                    updatedProgrammatically = false;
                    GuiUtils.error("Ein Fehler ist aufgetreten\n" + ex.getMessage());
                    throw new UnsupportedOperationException("Unexpected Exception", ex);
                }
            }
        } else if(e.getColumn() == 2) {
            Host host = network.getHost(IPAddress.parseIPv4(model.getValueAt(e.getFirstRow(), 0).toString()));
            String newName = model.getValueAt(e.getFirstRow(), 2).toString();
            host.setName(newName);
        }
    }
}