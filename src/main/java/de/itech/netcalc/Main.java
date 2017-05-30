package de.itech.netcalc;

import de.itech.netcalc.gui.SubnetCalculatorFrame;
import de.itech.netcalc.net.IPv4Address;
import de.itech.netcalc.net.Network;

public class Main {
    public static void main(String[] args) throws Exception {
        Network baseNet = new Network(new IPv4Address(10,5,1,0), new IPv4Address(255,255,255,0));
        baseNet.addSubnet(40);
        baseNet.addSubnet(14);
        baseNet.addSubnet(14);
        baseNet.addSubnet(14);
        baseNet.addSubnet(40);
        baseNet.addSubnet(3);
        baseNet.addSubnet(3);
        baseNet.addSubnet(40);

        // GUI
        SubnetCalculatorFrame calcFrame = new SubnetCalculatorFrame("SubnetCalculator", baseNet);
    }
}