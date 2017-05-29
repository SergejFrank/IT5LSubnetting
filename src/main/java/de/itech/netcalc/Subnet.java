package de.itech.netcalc;

public class Subnet extends NetworkBase {
    private Host[] hosts;

    public Subnet(Ipv4Address address, Ipv4Address mask){
        this.setAddress(new Ipv4Address(address.getValue() & mask.getValue()));
        this.setMask(mask);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(new Ipv4Address(getAddress().getValue() + i), null);
            hosts[i-1] = host;
        }
    }

    public Host[] getHosts(){
        return hosts;
    }
}