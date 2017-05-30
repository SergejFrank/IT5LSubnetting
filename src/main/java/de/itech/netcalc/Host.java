package de.itech.netcalc;

public class Host {
    private String name;
    private IPv4Address ipv4Address;
    private IPv6Address ipv6Address;
    private Subnet ipv4Subnet;
    private int ipv6Prefix;

    Host(Subnet ipv4Subnet, IPv4Address ipv4Address, String name) throws IllegalArgumentException {
        if(ipv4Subnet == null) throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null) throw new IllegalArgumentException("ipv4Address can not be null");
        this.ipv4Subnet = ipv4Subnet;
        this.ipv4Address = ipv4Address.clone();
        this.name = name;
    }

    Host(Subnet ipv4Subnet, IPv4Address ipv4Address, IPv6Address ipv6Address, int ipv6Prefix, String name) throws IllegalArgumentException {
        if(ipv4Subnet == null) throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null) throw new IllegalArgumentException("ipv4Address can not be null.");
        if(ipv6Address == null) throw new IllegalArgumentException("ipv6Address can not be null.");
        if(ipv6Prefix < 0 || ipv6Prefix > 128) throw new IllegalArgumentException(ipv6Prefix + " is not a valid IPv6 Prefix");
        this.ipv4Address = ipv4Address.clone();
        this.ipv4Subnet = ipv4Subnet;
        this.ipv6Address = ipv6Address.clone();
        this.ipv6Prefix = ipv6Prefix;
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

    public Subnet getIPv4Subnet() {
        return ipv4Subnet;
    }

    public IPv6Address getIPv6Address() {
        return ipv6Address == null ? null : ipv6Address.clone();
    }

    public IPv4Address getIPv4Address() {
        return ipv4Address.clone();
    }

    public int getIPv6Prefix() {
        return ipv6Prefix;
    }
}