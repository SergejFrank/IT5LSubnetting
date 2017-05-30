package de.itech.netcalc.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Network extends NetworkBase{
    private ArrayList<Subnet> subnets = new ArrayList<>();

    public Network(IPv4Address networkIdV4, IPv4Address networkMaskV4) {
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
    }

    public Network(IPv4Address networkIdV4, IPv4Address networkMaskV4, IPv6Address networkIdV6, int prefixV6) {
        if(prefixV6 < 0 || prefixV6 > 128)
            throw new IllegalArgumentException(prefixV6 + " is not a valid IPv6 Prefix");
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4.clone());
        this.setNetworkIdV6(networkIdV6.clone());
        this.setPrefixV6(prefixV6);
    }

    public ArrayList<Subnet> getSubnets(){
        return subnets;
    }

    public Subnet addSubnet(Subnet subnet) throws IllegalArgumentException {
        if(subnets.stream().anyMatch(sub -> sub.getNetworkIdV4().equals(subnet.getNetworkIdV4()) || sub.isColliding(subnet)))
            throw new IllegalArgumentException("Subnet already exists.");

        subnets.add(subnet);
        sortSubnets();
        return subnet;
    }

    public Subnet addSubnet(int size) throws Exception {
        int maskLength = (int)Math.ceil (Math.log( size + 2 ) / Math.log( 2.0 ));
        int realSize = (int)Math.pow(2, maskLength);
        IPv4Address subnetMask = NetUtils.getMaskFromPrefix(32 - maskLength);

        if(subnets.isEmpty() || NetUtils.getLengthBetweenIpAddresses(this.getNetworkIdV4(), subnets.get(0).getNetworkIdV4()) >= size) {
            return addSubnet(new Subnet(getNetworkIdV4(), subnetMask));
        }

        for(int i=0;i<subnets.size()-1;i++) {
            Subnet curr = subnets.get(i);
            Subnet next = subnets.get(i + 1);
            if (NetUtils.getLengthBetweenNetworks(curr, next) >= size)
                return addSubnet(new Subnet(new IPv4Address(getOffset(curr.getBroadcastAddress().getValue() + 1, realSize)), subnetMask));
        }

        Subnet last = subnets.get(subnets.size()-1);
        if(NetUtils.getLengthBetweenIpAddresses(last.getBroadcastAddress(), this.getBroadcastAddress()) >= size) {
            return addSubnet(new Subnet(new IPv4Address(getOffset(last.getBroadcastAddress().getValue() + 1,realSize)), subnetMask));
        }

        return null;
    }

    private int getOffset(int position, int count) {
        return (int)Math.ceil((double)position / (double)count) * count;
    }

    private void sortSubnets(){
        Collections.sort(subnets, Comparator.comparing(o -> o.getNetworkIdV4().getLValue()));
    }

    private ArrayList<Integer> deviders(){
        int num = getLength();
        ArrayList<Integer> deviders = new ArrayList<>();
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                if (i - 2 > 0) {
                    deviders.add(i - 2);
                }
                if (i != num/i) {
                    deviders.add((num/i)-2);
                }
            }
        }

        Collections.sort(deviders);
        return deviders;
    }

    public void splitEqualy(int size) {
        int length = getLength();
        int realSize = size + 2;
        if(length % realSize != 0) {
            throw new IllegalArgumentException("Size " + size + " is not suitable for network length " + length+ "\npossible sizes: "+deviders());
        }
        int count = length / realSize;
        subnets.clear();
        for (int i=0;i < count; i++) {
            IPv4Address nAddress = new IPv4Address(getNetworkIdV4().getValue() + i * realSize);
            int prefixLength = (int)(Math.log( count ) / Math.log( 2.0 ));
            IPv4Address mask = NetUtils.addPrefixToMask(getNetworkMaskV4(), prefixLength);

            Subnet subnet = new Subnet(nAddress,mask);
            subnets.add(subnet);
        }
    }

    public static Network parse(String value) {
        if(value == null) throw new IllegalArgumentException("'value' can not be null.");
        final String v4Regex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/([0-9]|[1-2][0-9]|3[0-2]))$";
        if(!value.matches(v4Regex)) throw new UnsupportedOperationException("'value' is not a valid network identifier.");
        String[] splitted = value.split("/");
        IPv4Address netmask = NetUtils.getMaskFromPrefix(Integer.valueOf(splitted[1]));
        IPv4Address networkID = IPAddress.parseIPv4(splitted[0]);
        return new Network(networkID, netmask);
    }
}