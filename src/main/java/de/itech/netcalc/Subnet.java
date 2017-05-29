package de.itech.netcalc;

public class Subnet extends NetworkBase{
    private IPv6Address networkIdV6;
    private int prefixV6;
    private Host[] hosts;

    public Subnet(IPv4Address netwotkIdV4, IPv4Address networkMaskV4) throws Exception {
        this.setAddress(new IPv4Address(netwotkIdV4.getValue() & networkMaskV4.getValue()));
        this.setMask(networkMaskV4);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(this, new IPv4Address(getAddress().getValue() + i), null, null);
            hosts[i-1] = host;
        }
    }

    public Subnet(IPv4Address networkIdV4, IPv4Address networkMaskV4, IPv6Address networkIdV6, int prefixV6) throws Exception {
        if(prefixV6 < 0 || prefixV6 > 128) throw new IllegalArgumentException(prefixV6 + " is not a valid IPv6 Prefix");
        this.setAddress(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setMask(networkMaskV4);
        this.networkIdV6 = networkIdV6;
        this.prefixV6 = prefixV6;
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(this, new IPv4Address(getAddress().getValue() + i), null, null);
            hosts[i-1] = host;
        }
    }

    public Host[] getHosts(){
        return hosts;
    }

    public IPv6Address getNetworkIdV6() {
        return networkIdV6;
    }

    public int getPrefixV6() {
        return prefixV6;
    }
}