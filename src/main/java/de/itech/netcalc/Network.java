package de.itech.netcalc;

import com.sun.javaws.exceptions.InvalidArgumentException;

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
        if(getLength() % realSize != 0) {
            throw new IllegalArgumentException("Size " + size + " is not suitable for network length " + length+ "\npossible sizes: "+deviders());
        }
        int count = length / size;
        subnets.clear();
        for (int i=0;i < count; i++) {
            IpAddress nAddress = new IpAddress(getAddress().getValue() + i * size);

            
            // TODO: 15.05.17
            Subnet subnet = new Subnet(nAddress,getMask());
            subnets.add(subnet);
        }
    }
}
