package de.itech.netcalc;

public class Subnet extends NetworkBase {
    private Host[] hosts;

    public Subnet(IpAddress address, IpAddress mask){
        this.setAddress(new IpAddress(address.getValue() & mask.getValue()));
        this.setMask(mask);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(new IpAddress(getAddress().getValue() + i), null);
            hosts[i-1] = host;
        }
    }

    public Host[] getHosts(){
        return hosts;
    }
}
