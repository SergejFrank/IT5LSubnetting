package de.itech.netcalc;

public class Main {
    public static void main(String[] args) {
        /*Network baseNet = new Network(new IpAddress(10,5,1,0), new IpAddress(255,255,255,0));
        baseNet.addSubnet(new Subnet(new IpAddress(10,0,1,0),new IpAddress(255,255,255,224)));
        baseNet.addSubnet(new Subnet(new IpAddress(10,0,1,32),new IpAddress(255,255,255,224)));
        baseNet.addSubnet(new Subnet(new IpAddress(10,0,1,64),new IpAddress(255,255,255,224)));
        baseNet.addSubnet(new Subnet(new IpAddress(10,0,1,96),new IpAddress(255,255,255,224)));
        baseNet.addSubnet(new Subnet(new IpAddress(10,0,1,96),new IpAddress(255,255,255,224)));*/

        Network basenet = new Network(new IpAddress(10,5,1,11), new IpAddress(255,255,255,224));
        basenet.splitEqualy(14);


        basenet.getSubnets().forEach(subnet -> {
            System.out.println("Netzid: " + subnet.getAddress());
            for(Host host : subnet.getHosts()){
                //System.out.println(host);
            }
            System.out.println("Broadcast: " + subnet.getBroadcastAddress());
        });



    }
}
