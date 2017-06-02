package de.itech.netcalc.net;

import java.io.IOException;
import java.net.InetAddress;

public class Host {
    private String name;
    private IPv4Address ipv4Address;
    private IPv6Address ipv6Address;
    private Network subnet;

    Host(Network subnet, IPv4Address ipv4Address, String name) throws IllegalArgumentException {
        if(subnet == null) throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null) throw new IllegalArgumentException("ipv4Address can not be null");
        this.subnet = subnet;
        this.ipv4Address = ipv4Address.clone();
        this.name = name;
    }

    Host(Network subnet, IPv4Address ipv4Address, IPv6Address ipv6Address, String name) throws IllegalArgumentException {
        if(subnet == null) throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null) throw new IllegalArgumentException("ipv4Address can not be null.");
        if(ipv6Address == null) throw new IllegalArgumentException("ipv6Address can not be null.");
        this.ipv4Address = ipv4Address.clone();
        this.subnet = subnet;
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

    public void setName(String name) {
        this.name = name;
    }

    public Network getSubnet() {
        return subnet;
    }

    public IPv6Address getIPv6Address() {
        return ipv6Address == null ? null : ipv6Address.clone();
    }

    public IPv4Address getIPv4Address() {
        return ipv4Address.clone();
    }

    public void setIpv6Address(IPv6Address ipv6Address) {
        if(ipv6Address == null)
            this.ipv6Address = null;
        else if(subnet.getNetworkIdV6() == null) {
            throw new UnsupportedOperationException("Configure IPv6 on subnet first.");
        } else {
            if(!NetUtils.isInSubnet(subnet.getNetworkIdV6(), ipv6Address)) {
                throw new UnsupportedOperationException("IPv6Address '" + ipv6Address + "' not in subnet '"
                        + subnet.getNetworkIdV6() + "' with prefix '" + subnet.getPrefixV6());
            }
            this.ipv6Address = ipv6Address;
        }
    }

    public int getIPv6Prefix() {
        return subnet.getPrefixV6();
    }

    public boolean isReachable(){
        int timeout = 100;
        try {
            return InetAddress.getByName(this.getIPv4Address().toString()).isReachable(timeout);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}