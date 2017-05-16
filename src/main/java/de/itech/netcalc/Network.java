package de.itech.netcalc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Network extends NetworkBase{
    private ArrayList<Subnet> subnets = new ArrayList<>();

    public Network(IpAddress address, IpAddress mask) {
        this.setAddress(new IpAddress(address.getValue() & mask.getValue()));
        this.setMask(mask);
    }

    public ArrayList<Subnet> getSubnets(){
        return subnets;
    }

    public Subnet addSubnet(Subnet subnet) throws IllegalArgumentException {
        if(subnets.stream().anyMatch(sub -> sub.getAddress().equals(subnet.getAddress())))
            throw new IllegalArgumentException("Subnet already exists.");

        subnets.add(subnet);
        sortSubnets();
        return subnet;
    }

    public Subnet addSubnet(int size) throws Exception {
        IpAddress smask = NetUtils.addSuffixToMask (getMask(),(int)(Math.ceil (Math.log( size + 2 ) / Math.log( 2.0 ))));

        if(subnets.isEmpty() || NetUtils.getLengthBetweenIpAddresses(this.getAddress(), subnets.get(0).getAddress()) >= size) {
            return addSubnet(new Subnet(getAddress(), smask));
        }

        for(int i=0;i<subnets.size()-1;i++) {
            Subnet curr = subnets.get(i);
            Subnet next = subnets.get(i + 1);
            if (NetUtils.getLengthBetweenNetworks(curr, next) >= size)
                return addSubnet(new Subnet(new IpAddress(curr.getBroadcastAddress().getValue() + 1), smask));
        }

        Subnet last = subnets.get(subnets.size()-1);
        if(NetUtils.getLengthBetweenIpAddresses(last.getBroadcastAddress(), this.getBroadcastAddress()) >= size) {
            return addSubnet(new Subnet(new IpAddress(last.getBroadcastAddress().getValue() + 1), smask));
        }

        return null;
    }

    public boolean isColliding(NetworkBase network) {
        return NetUtils.isInSubnet(getAddress(), getMask(), network.getAddress())
                || NetUtils.isInSubnet(network.getAddress(), network.getMask(), getAddress());
    }

    private void sortSubnets(){
        Collections.sort(subnets, Comparator.comparing(o -> o.getAddress().toString()));
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
            IpAddress nAddress = new IpAddress(getAddress().getValue() + i * realSize);
            int suffixLength = (int)(Math.log( count ) / Math.log( 2.0 ));
            IpAddress mask = NetUtils.addSuffixToMask(getMask(), suffixLength);

            Subnet subnet = new Subnet(nAddress,mask);
            subnets.add(subnet);
        }
    }
}