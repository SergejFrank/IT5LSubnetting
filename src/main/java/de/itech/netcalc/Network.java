package de.itech.netcalc;

import com.sun.javaws.exceptions.InvalidArgumentException;

import java.util.ArrayList;

public class Network extends NetworkBase{
    private ArrayList<Subnet> subnets = new ArrayList<>();

    public Network(IpAddress address, IpAddress mask) {
        this.setAddress(address);
        this.setMask(mask);
    }

    public ArrayList<Subnet> getSubnets(){
        return subnets;
    }

    public Subnet addSubnet(Subnet subnet){
        subnets.add(subnet);
        return subnet;
    }

    public void splitEqualy(int size) {
        int length = getLength();
        int realSize = size + 2;
        if(getLength() % realSize != 0)
            throw new IllegalArgumentException("Size " + size + " is not suitable for network length " + length);
        int count = length / size;
        subnets.clear();
        for (int i=0;i < count; i++) {
            IpAddress nAddress = new IpAddress(getAddress().getValue() + i * size);

            //Subnet subnet = new Subnet()
        }
    }
}
