package de.itech.netcalc.gui;

import de.itech.netcalc.Config;
import de.itech.netcalc.net.Format;
import de.itech.netcalc.net.Network;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

class InfoPanel extends JPanel {
    private JTextField networkName;
    private JTextField ipv4NetworkId;
    private JTextArea ipv6NetworkId;
    private JTextField ipv6PrefixLength;
    private JTextField ipv4Broadcast;
    private JTextField ipv4Subnetmask;

    void fill(Network network) {
        setVisible(network != null);
        if(network == null) {
            clear();
        }
        else {
            networkName.setText(network.getName());
            ipv4NetworkId.setText(Format.format(network.getNetworkIdV4(), Config.getIpv4Notation()));
            ipv4Subnetmask.setText(Format.format(network.getNetworkMaskV4(), Config.getIpv4Notation()));
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

    private JTextField getJTextField() {
        JTextField jTextField = new JTextField();
        jTextField.setEditable(false);
        jTextField.setOpaque(false);
        jTextField.setBackground(new Color(0,0,0,0));
        jTextField.setBorder(new EmptyBorder(3,10,3,3));
        return jTextField;
    }

    private JLabel getJLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBorder(new EmptyBorder(3,10,3,3));
        return jLabel;
    }

    private JLabel getHeader(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBorder(new EmptyBorder(10,10,3,3));
        return jLabel;
    }

    private JTextArea getTextArea() {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setBorder(new EmptyBorder(3,10,3,10));
        jTextArea.setOpaque(false);
        jTextArea.setBackground(new Color(0,0,0,0));
        jTextArea.setLineWrap(true);
        return jTextArea;
    }

    private void setupLayout() {
        setVisible(false);
        setLayout(new GridBagLayout());
        networkName = getJTextField();
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(networkName, gbc);
        final JLabel label1 = getJLabel("Name");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        add(label1, gbc);
        final JLabel label2 = getHeader("IPv4");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(label2, gbc);
        final JLabel label3 = getJLabel("Netzwerk ID");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        add(label3, gbc);
        ipv4NetworkId = getJTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4NetworkId, gbc);
        final JLabel label4 = getJLabel("Subnetzmaske");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        add(label4, gbc);
        ipv4Subnetmask = getJTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4Subnetmask, gbc);
        final JLabel label5 = getJLabel("Broadcast Adresse");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        add(label5, gbc);
        ipv4Broadcast = getJTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv4Broadcast, gbc);
        final JLabel label6 = getHeader("IPv6");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.WEST;
        add(label6, gbc);
        final JLabel label7 = getJLabel("Netzwerk ID");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.anchor = GridBagConstraints.WEST;
        add(label7, gbc);
        final JLabel label8 = getJLabel("Präfixlänge");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        add(label8, gbc);
        ipv6PrefixLength = getJTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(ipv6PrefixLength, gbc);
        ipv6NetworkId = getTextArea();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        add(ipv6NetworkId, gbc);
    }
}