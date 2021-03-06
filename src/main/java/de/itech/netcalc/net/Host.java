package de.itech.netcalc.net;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * The host class can be used to configure a virtual ipv4 and ipv6 end point of a subnet
 */
@XmlRootElement
public class Host {
    /**
     * backing field for the Name property
     */
    @XmlAttribute
    private String hostName;

    /**
     * backing field for the Ipv4Address property
     */
    @XmlElement
    private IPv4Address ipv4Address;

    /**
     * backing field for the Ipv6Address property
     */
    @XmlElement
    private IPv6Address ipv6Address;

    /**
     * backing field for the Subnet property
     */
    private Network subnet;

    /**
     * Creates a host in the given subnet with an IPv4Address and a hostName
     * @param subnet the subnet the host is assigned to
     * @param ipv4Address the IPv4Address of the host
     * @throws IllegalArgumentException if ipv4Address or hostName is null
     */
    public Host(Network subnet, IPv4Address ipv4Address) throws IllegalArgumentException {
        if(subnet == null) throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null) throw new IllegalArgumentException("ipv4Address can not be null");
        this.subnet = subnet;
        this.ipv4Address = ipv4Address.clone();
    }

    private Host(){}

    /**
     * Creates a host in the given subnet with an IPv4Address, an IPv6Address and a hostName
     * @param subnet the subnet the host is assigned to
     * @param ipv4Address the IPv4Address of the host
     * @param ipv6Address the IPv6Address of the host
     * @throws IllegalArgumentException if ipv4Address, ipv6Address or hostName is null
     */
    public Host(Network subnet, IPv4Address ipv4Address, IPv6Address ipv6Address) throws IllegalArgumentException {
        if(subnet == null) throw new IllegalArgumentException("Subnet can not be null.");
        if(ipv4Address == null) throw new IllegalArgumentException("ipv4Address can not be null.");
        if(ipv6Address == null) throw new IllegalArgumentException("ipv6Address can not be null.");
        this.ipv4Address = ipv4Address.clone();
        this.subnet = subnet;
        this.ipv6Address = ipv6Address.clone();
    }

    @Override
    public String toString(){
        String result = "Host: " + hostName;
        if(ipv4Address == null)
            return result + " IPv6: " + ipv6Address;
        if(ipv6Address == null)
            return result + " IPv4: " + ipv4Address;
        return result + " IPv4: " + ipv4Address + " IPv6: " + ipv6Address;
    }

    /**
     * Gets the hostName of the host
     * @return the hostName of the host
     */
    public String getName() {
        return hostName;
    }

    /**
     * Sets the hostName of the host
     * @param name the hostName to be set
     */
    public void setName(String name) {
        this.hostName = name;
    }

    /**
     * Gets the subnet, the host is assigned to
     * @return the assigned subnet
     */
    public Network getSubnet() {
        return subnet;
    }

    /**
     * Gets the IPv6Address of the host
     * @return The IPv6 Address of the host
     */
    public IPv6Address getIPv6Address() {
        return ipv6Address == null ? null : ipv6Address.clone();
    }

    /**
     * Gets the IPv4Address of the host
     * @return The IPv4 Address of the host
     */
    public IPv4Address getIPv4Address() {
        return ipv4Address.clone();
    }

    /**
     * Sets the IPv6Address of the host.
     * @param ipv6Address the IPv6Address to be set
     * @exception UnsupportedOperationException if IPv6 is not configured on the subnet, or the given address is not part of the subnet
     */
    public void setIpv6Address(IPv6Address ipv6Address){
        if(ipv6Address == null)
            this.ipv6Address = null;
        else if(subnet.getNetworkIdV6() == null) {
            throw new UnsupportedOperationException("Configure IPv6 on subnet first.");
        }
        else if(Arrays.stream(subnet.getHosts()).anyMatch(host -> host != null && ipv6Address.equals(host.getIPv6Address()))) {
            throw new UnsupportedOperationException("IPv6 already in use.");
        }
        else {
            if(!NetUtils.isInSubnet(subnet.getNetworkIdV6(), subnet.getPrefixV6(), ipv6Address)) {
                throw new UnsupportedOperationException("IPv6Address '" + ipv6Address + "' not in subnet '"
                        + subnet.getNetworkIdV6() + "' with prefix '" + subnet.getPrefixV6());
            }
            this.ipv6Address = ipv6Address;
        }
    }

    /**
     * Gets if the host is physical reachable, using the ping protocol
     * @return true if the host returns a pong packet
     */
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