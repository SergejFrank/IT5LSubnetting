package de.itech.netcalc;

import java.util.Arrays;

public class Subnet extends NetworkBase{
    private Host[] hosts;

    public Subnet(IPv4Address networkIdV4, IPv4Address networkMaskV4) throws Exception {
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            Host host = new Host(this, new IPv4Address(getNetworkIdV4().getValue() + i), null);
            hosts[i-1] = host;
        }
    }

    public Subnet(IPv4Address networkIdV4, IPv4Address networkMaskV4, IPv6Address networkIdV6, int prefixV6) throws Exception {
        if(prefixV6 < 0 || prefixV6 > 128) throw new IllegalArgumentException(prefixV6 + " is not a valid IPv6 Prefix");
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
        this.setNetworkIdV6(networkIdV6);
        this.setPrefixV6(prefixV6);
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            IPv4Address hostV4 = new IPv4Address(getNetworkIdV4().getValue() + i);
            Host host = new Host(this, hostV4, null);
            hosts[i-1] = host;
        }
    }

    public void assignRandomIPv6Address(Host host) {
        if(host == null) throw new IllegalArgumentException("'Host' can not be null.");
    }

    public Host[] getHosts(){
        return hosts;
    }
}