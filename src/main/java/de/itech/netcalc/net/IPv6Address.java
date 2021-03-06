package de.itech.netcalc.net;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Random;

/**
 * The IPv6Address class represents an IPv6 Address without implementing any network logic.
 */
@XmlRootElement
public class IPv6Address extends IPAddress {
    /**
     * stores the first 64 bits of the address
     */
    @XmlAttribute
    private long networkId;

    /**
     * stores the last 64 bits of the address
     */
    @XmlAttribute
    private long interfaceId;

    /**
     * Creates a new Instance of the IPv6 address with the passed values
     * @param networkId the first 64 bits of the IPv6 address
     * @param interfaceId the last 64 bits of the IPv6 address
     */
    public IPv6Address(long networkId, long interfaceId){
        this.networkId = networkId;
        this.interfaceId = interfaceId;
    }

    private IPv6Address(){}

    /**
     * Compares an object with the IPv4 address
     * @param o the object to compare to
     * @return true if o points to the same object, or the IPv4 addresses have the same value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPv6Address that = (IPv6Address) o;

        return networkId == that.networkId && interfaceId == that.interfaceId;
    }

    /**
     * calculates the hash code of the IPv6 address
     * @return the address hash code
     */
    @Override
    public int hashCode() {
        int result = (int) (networkId ^ (networkId >>> 32));
        result = 31 * result + (int) (interfaceId ^ (interfaceId >>> 32));
        return result;
    }

    /**
     * Gets the first 64 bits of the IPv6 address, which represents the interface part by default.
     * @return the interface part
     */
    public long getInterfaceId() {
        return interfaceId;
    }

    /**
     * Gets the first 64 bits of the IPv6 address, which represents the interface part by default.
     * @return the network part
     */
    public long getNetworkId() {
        return networkId;
    }

    /**
     * Gets the string representation of the IPv6 address in normal notation.
     * @return the IPv6 address string representation
     */
    @Override
    public String toString() {
        return Format.format(this, Format.IPv6Format.NORMAL);
    }

    /**
     * Returns a random interface address with a random mac address.
     * This is used for simulation and demonstration only
     * @return a random interface address
     */
    private static long getRandomInterfaceAddress(){
        //due to EUI Standards the 25th bit from the right always has to be 0
        //https://supportforums.cisco.com/document/100566/understanding-ipv6-eui-64-bit-address
        return (new Random().nextLong() & ~(1 << 24)) | 0xFFFEL << 24;
    }

    /**
     * Generates an IPv6 address based on the passed network ID which is populated with a random interface Id
     * @param networkId the network ID to use
     * @return random IPv6 address
     */
    public static IPv6Address getAddressWithRandomHost(long networkId){
        return new IPv6Address(networkId, getRandomInterfaceAddress());
    }

    /**
     * Generates an IPv6 address based on the passed network ID which is populated with a random interface Id
     * @param address teh network ID to use
     * @return random IPv6 address
     */
    public static IPv6Address getAddressWithRandomHost(IPv6Address address){
        return new IPv6Address(address.networkId, getRandomInterfaceAddress());
    }

    /**
     * Returns a value clone of the IPv6 address
     * @return a value clone
     */
    @Override
    protected IPv6Address clone() {
        return new IPv6Address(networkId, interfaceId);
    }
}