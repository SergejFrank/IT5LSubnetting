package de.itech.netcalc.gui;

import de.itech.netcalc.Config;
import de.itech.netcalc.net.Format;
import de.itech.netcalc.net.Host;

import javax.swing.*;
import java.awt.*;

class HostInfoPanel extends JPanel {
    private JTextArea hostName;
    private JTextField ipv4Address;
    private JTextArea ipv6Address;

    void fill(Host host) {
        setVisible(host != null);
        hostName.setText(host == null ? null : host.getName());
        ipv4Address.setText(host == null ? null : Format.format(host.getIPv4Address(), Config.getIpv4Notation()));
        ipv6Address.setText(host == null || host.getIPv6Address() == null
                ? null : Format.format(host.getIPv6Address(), Config.getIpv6Notation()));
    }

    {
        setupLayout();
    }

    private void setupLayout() {
        setVisible(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        final JLabel nameLabel = GuiUtils.getInfoLabel("Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(nameLabel, gbc);
        hostName = GuiUtils.getInfoTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(hostName, gbc);
        final JLabel ipv4Label = GuiUtils.getInfoLabel("IPv4 Adresse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        add(ipv4Label, gbc);
        ipv4Address = GuiUtils.getInfoTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4Address, gbc);
        final JLabel ipv6Label = GuiUtils.getInfoLabel("IPv6 Adresse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(ipv6Label, gbc);
        ipv6Address = GuiUtils.getInfoTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv6Address, gbc);
    }
}
