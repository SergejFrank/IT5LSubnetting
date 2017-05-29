package de.itech.netcalc;

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


        baseNet.getSubnets().forEach(subnet -> {
            System.out.println("Netzid: " + subnet.getNetworkIdV4());
            System.out.println("Broadcast: " + subnet.getBroadcastAddress());
            System.out.println("Mask: " + subnet.getNetworkMaskV4()+"\n");
        });

        // GUI
        SubnetCalculatorFrame calcFrame = new SubnetCalculatorFrame("SubnetCalculator", baseNet);

    }
}