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

    public boolean isColliding(Subnet subnet) {
        return NetUtils.isInSubnet(getAddress(), getMask(), subnet.getAddress())
                || NetUtils.isInSubnet(subnet.getAddress(), subnet.getMask(), getAddress());
    }



    public Host[] getHosts(){
        return hosts;
    }
}
