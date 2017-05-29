package de.itech.netcalc;

public class Host {
    private String name;
    private IPv4Address ipv4Address;
    private IPv6Address ipv6Address;
    private Subnet ipv4Subnet;
    private int ipv6Prefix;

    Host(Subnet ipv4Subnet, IPv4Address ipv4Address, IPv6Address ipv6Address, String name) throws Exception {
        if(ipv4Subnet == null)
            throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null)
            throw new Exception("Host must contain a valid IPv4Address.");
        this.ipv4Subnet = ipv4Subnet;
        this.ipv4Address = ipv4Address.clone();
        this.ipv6Address = ipv6Address.clone();
        this.name = name;
    }

    @Override
    public String toString(){
        String result = "Host: " + name;
        if(ipv4Address == null)
            return result + " IPv6: " + ipv6Address;
        if(ipv6Address == null)
            return result + " IPv4: " + ipv4Address;
        return result + " IPv4: " + ipv4Address + " IPv6: " + ipv6Address;
    }

    public String getName() {
        return name;
    }

    public Subnet getIpv4Subnet() {
        return ipv4Subnet;
    }

    public IPv6Address getIpv6Address() {
        return ipv6Address.clone();
    }

    public IPv4Address getIpv4Address() {
        return ipv4Address.clone();
    }

    public int getIpv6Prefix() {
        return ipv6Prefix;
    }
}