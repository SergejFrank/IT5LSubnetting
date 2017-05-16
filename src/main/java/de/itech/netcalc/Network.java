package de.itech.netcalc;


import sun.security.krb5.internal.crypto.NullEType;

import java.util.ArrayList;
import java.util.Collections;

public class Network extends NetworkBase{
    private ArrayList<Subnet> subnets = new ArrayList<>();

    public Network(IpAddress address, IpAddress mask) {
        this.setAddress(new IpAddress(address.getValue() & mask.getValue()));
        this.setMask(mask);
    }

    public ArrayList<Subnet> getSubnets(){
        return subnets;
    }

    public Subnet addSubnet(Subnet subnet){
        subnets.add(subnet);
        return subnet;
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