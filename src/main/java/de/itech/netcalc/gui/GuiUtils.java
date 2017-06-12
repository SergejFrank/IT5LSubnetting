package de.itech.netcalc.gui;

import de.itech.netcalc.net.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

class GuiUtils {
    static void error(String msg){
        JOptionPane.showMessageDialog(null, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }

    static String getInitialSubnetString(Network network) {
        IPv4Address networkId = network.getNetworkIdV4();
        Integer prefix = NetUtils.maskToPrefix(network.getSubnetMaskV4());
        if(prefix >= 24)
            return networkId.getOct1() + "." + networkId.getOct2() + "." + networkId.getOct3() + ".";
        if(prefix >= 16)
            return networkId.getOct1() + "." + networkId.getOct2() + ".";
        if(prefix >= 8)
            return networkId.getOct1() + ".";
        return "";
    }

    static String getInitialSubnetV6String(IPv6Address networkId, int networkPrefix) {
        String[] values = Format.format(networkId, Format.IPv6Format.HIDELEADINGZEROS).split(":");
        int segments = (int)Math.floor((double)networkPrefix / 16);
        System.out.println(segments);
        values = Arrays.stream(values).limit(segments).collect(Collectors.toList()).toArray(new String[segments]);
        return String.join(":", values) + ":";
    }

    static boolean confirmation(String title, String message) {
        int input = JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.OK_CANCEL_OPTION);
        return input == JOptionPane.OK_OPTION;
    }

    static boolean confirmationYesNo(String title, String message) {
        int input = JOptionPane.showConfirmDialog(null, message, title,
                JOptionPane.YES_NO_OPTION);
        return input == JOptionPane.YES_OPTION;
    }

    static IPv6Address iPv6AddressDialog(String title, String message, String initialValue) {
        Object input = JOptionPane.showInputDialog(null, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
        if(input == null) return null;
        String inputString = input.toString();
        if(!IPAddress.isValidIPv6(inputString)) {
            error("Die angegebene IPv6 Adresse ist ungültig.");
            return iPv6AddressDialog(title, message, inputString);
        } else {
            try {
                return IPAddress.parseIPv6(inputString);
            } catch(Exception e) {
                error("Fehler beim Umwandeln der IPv6 Adresse.");
                return iPv6AddressDialog(title, message, inputString);
            }
        }
    }

    static IPv4Address iPv4AddressDialog(String title, String message, String initialValue) {
        Object input = JOptionPane.showInputDialog(null, message, title,
                JOptionPane.PLAIN_MESSAGE, null, null, initialValue);
        if(input == null) return null;
        String inputString = input.toString();
        if(!IPAddress.isValidIPv4(inputString)) {
            error("Die angegebene IPv4 Adresse ist ungültig.");
            return iPv4AddressDialog(title, message, inputString);
        } else {
            try {
                return IPAddress.parseIPv4(inputString);
            } catch(Exception e) {
                error("Fehler beim Umwandeln der IPv4 Adresse.");
                return iPv4AddressDialog(title, message, inputString);
            }
        }
    }

    static File getFileOpen(String title) {
        FileDialog dialog = new java.awt.FileDialog((Frame) null, title, FileDialog.LOAD);
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        if(fileName == null) return null;
        File file = new File(dialog.getDirectory(), fileName);
        if(!file.exists() || file.isDirectory()) {
            GuiUtils.error("Die angegebene Datei konnte nicht gefunden werden.");
            return getFileOpen(title);
        }
        return file;
    }

    static File getSaveFile(String title, String initialFileName) {
        FileDialog dialog = new FileDialog((Frame)null, title, FileDialog.SAVE);
        dialog.setFile(initialFileName);
        dialog.setVisible(true);
        String fileName = dialog.getFile();
        if(fileName == null) return null;
        Path filePath = Paths.get(dialog.getDirectory(), fileName);
        return filePath.toFile();
    }

    static JTextArea getInfoTextArea() {
        JTextArea jTextArea = new JTextArea();
        jTextArea.setBorder(new EmptyBorder(3,10,3,10));
        jTextArea.setOpaque(false);
        jTextArea.setBackground(new Color(0,0,0,0));
        jTextArea.setLineWrap(true);
        return jTextArea;
    }

    static JLabel getInfoLabel(String text) {
        JLabel jLabel = new JLabel(text);
        jLabel.setBorder(new EmptyBorder(3,10,3,3));
        return jLabel;
    }
}