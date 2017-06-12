package de.itech.netcalc.gui;

import de.itech.netcalc.Config;
import de.itech.netcalc.net.Format;
import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class InfoPanel extends JPanel {
    private final JTextArea networkName = GuiUtils.getInfoTextArea();
    private final JTextArea ipv4NetworkId = GuiUtils.getInfoTextArea();
    private final JTextArea ipv6NetworkId = GuiUtils.getInfoTextArea();
    private final JTextArea ipv6PrefixLength = GuiUtils.getInfoTextArea();
    private final JTextArea ipv4Broadcast = GuiUtils.getInfoTextArea();
    private final JTextArea ipv4Subnetmask = GuiUtils.getInfoTextArea();

    void fill(Network network) {
        setVisible(network != null);
        if(network == null) {
            clear();
        }
        else {
            networkName.setText(network.getName());
            ipv4NetworkId.setText(Format.format(network.getNetworkIdV4(), Config.getIpv4Notation()));
            ipv4Subnetmask.setText(Format.format(network.getSubnetMaskV4(), Config.getIpv4Notation()));
            ipv4Broadcast.setText(Format.format(network.getBroadcastAddress(), Config.getIpv4Notation()));
            ipv6NetworkId.setText(network.isIPv6Enabled()
                ? Format.format(network.getNetworkIdV6(), Config.getIpv6Notation())
                : null);
            ipv6PrefixLength.setText(network.isIPv6Enabled() ? String.valueOf(network.getPrefixV6()) : null);
        }
    }

    private void clear() {
        networkName.setText(null);
        ipv4NetworkId.setText(null);
        ipv4Subnetmask.setText(null);
        ipv4Broadcast.setText(null);
        ipv6NetworkId.setText(null);
        ipv6PrefixLength.setText(null);
    }

    {
        setupLayout();
    }

    private JLabel getHeader(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBorder(new EmptyBorder(10,10,3,3));
        return jLabel;
    }

    private void setupLayout() {
        setVisible(false);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc;

        //Name Label
        final JLabel label1 = GuiUtils.getInfoLabel("Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(label1, gbc);

        //Name
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(networkName, gbc);

        //IPv4 Header
        final JLabel label2 = getHeader("IPv4");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(label2, gbc);

        //IPv4 Network ID Label
        final JLabel label3 = GuiUtils.getInfoLabel("Netzwerk ID");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(label3, gbc);

        //IPv4 Network ID
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4NetworkId, gbc);

        //IPv4 Subnetmask Label
        final JLabel label4 = GuiUtils.getInfoLabel("Subnetzmaske");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(label4, gbc);

        //IPv4 Subnetmask
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4Subnetmask, gbc);

        //IPv4 Broadcastaddress Label
        final JLabel label5 = GuiUtils.getInfoLabel("Broadcast Adresse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(label5, gbc);

        //IPv4 Broadcastaddress
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4Broadcast, gbc);

        //IPv6 Header
        final JLabel label6 = getHeader("IPv6");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        add(label6, gbc);

        //IPv6 Network ID Label
        final JLabel label7 = GuiUtils.getInfoLabel("Netzwerk ID");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        add(label7, gbc);

        // IPv6 Network ID
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(ipv6NetworkId, gbc);

        //IPv6 Prefix Length Label
        final JLabel label8 = GuiUtils.getInfoLabel("Präfixlänge");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        add(label8, gbc);

        //IPv6 Prefix Length
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv6PrefixLength, gbc);
    }
}