package de.itech.netcalc;

public class Subnet extends NetworkBase {
    private Host[] hosts;

    public Subnet(IpAddress address, IpAddress mask){
        this.setAddress(address);
        this.setMask(mask);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(new IpAddress(getAddress().getValue() + i), null);
            hosts[i-1] = host;
        }
    }

    public int getMaxHosts(){
        return getLength() - 2;
    }

    public IpAddress getBroadcastAddress(){
        return new IpAddress(getAddress().getValue() + getMaxHosts() + 1);
    }

    public Host[] getHosts(){
        return hosts;
    }
}
