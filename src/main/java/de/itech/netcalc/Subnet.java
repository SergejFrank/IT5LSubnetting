package de.itech.netcalc;

public class Subnet extends NetworkBase {
    private Host[] hosts;

    public Subnet(IPv4Address address, IPv4Address mask){
        this.setAddress(new IPv4Address(address.getValue() & mask.getValue()));
        this.setMask(mask);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(new IPv4Address(getAddress().getValue() + i), null);
            hosts[i-1] = host;
        }
    }

    public Host[] getHosts(){
        return hosts;
    }
}