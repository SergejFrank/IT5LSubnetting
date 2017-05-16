package de.itech.netcalc;

public class Main {
    public static void main(String[] args) throws Exception {

        Network baseNet = new Network(new IpAddress(10,5,1,0), new IpAddress(255,255,255,0));
        baseNet.addSubnet(14);
        baseNet.addSubnet(new Subnet(new IpAddress(10,5,1,48), new IpAddress(255,255,255,240)));
        baseNet.addSubnet(14);
        baseNet.addSubnet(14);

        baseNet.getSubnets().forEach(subnet -> {
            System.out.println("Netzid: " + subnet.getAddress());
            System.out.println("Broadcast: " + subnet.getBroadcastAddress());
        });
    }
}