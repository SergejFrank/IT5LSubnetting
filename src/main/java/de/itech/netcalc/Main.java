package de.itech.netcalc;


import javax.swing.*;

public class Main {
    public static void main(String[] args) throws Exception {

        Network baseNet = new Network(new IpAddress(10,5,1,0), new IpAddress(255,255,255,0));
        baseNet.addSubnet(40);
        baseNet.addSubnet(14);
        baseNet.addSubnet(14);
        baseNet.addSubnet(14);
        baseNet.addSubnet(40);
        baseNet.addSubnet(3);
        baseNet.addSubnet(3);
        baseNet.addSubnet(40);


        baseNet.getSubnets().forEach(subnet -> {
            System.out.println("Netzid: " + subnet.getAddress());
            System.out.println("Broadcast: " + subnet.getBroadcastAddress());
        });



        // GUI

        JFrame calcFrame = new JFrame("SubnetCalculator");

        JPanel networksPanel = new JPanel();
        JPanel subnetsPanel = new JPanel();
        JPanel hostsPanel = new JPanel();

        JLabel networksLabel = new JLabel("Networks");
        JLabel subnetsLabel = new JLabel("Subnets");
        JLabel hostsLabel = new JLabel("Hosts");

        networksPanel.add(networksLabel);
        subnetsPanel.add(subnetsLabel);
        hostsPanel.add(hostsLabel);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Networks", networksPanel);
        tabbedPane.add("Subnets", subnetsPanel);
        tabbedPane.add("Hosts", hostsPanel);

        calcFrame.add(tabbedPane);
        calcFrame.setSize(600, 400);
        calcFrame.setVisible(true);


    }
}