package de.itech.netcalc.net;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The IPv4Address class represents an IPv4 Address without implementing any network logic
 */
@XmlRootElement
public class IPv4Address extends IPAddress {

    @XmlAttribute
    private int value;

    /**
     * creates an ip address from 4 octets in integer representation
     * @param oct1 the first octet (0-255)
     * @param oct2 the second octet (0-255)
     * @param oct3 the third octet (0-255)
     * @param oct4 the fourth octet (0-255)
     */
    public IPv4Address(int oct1, int oct2, int oct3, int oct4) {
        if(oct1 < 0 || oct1 > 255) throw new IllegalArgumentException("Value '" + oct1 + "' for oct1 is out of range.");
        if(oct2 < 0 || oct2 > 255) throw new IllegalArgumentException("Value '" + oct2 + "' for oct2 is out of range.");
        if(oct3 < 0 || oct3 > 255) throw new IllegalArgumentException("Value '" + oct3 + "' for oct3 is out of range.");
        if(oct4 < 0 || oct4 > 255) throw new IllegalArgumentException("Value '" + oct4 + "' for oct4 is out of range.");
        value =  (oct1 << 24) + (oct2 << 16) + (oct3 << 8) + oct4;
    }

    private IPv4Address() {}

    /**
     * creates an ip address from an entire integer representation
     * @param val the integer representation
     */
    IPv4Address(int val){
        value = val;
    }

    /**
     * Gets the integer representation for the first segment of the ip address.
     * @return The first segment (0-255)
     */
    public int getOct1() {
        return (value & 0xFF000000) >>> 24;
    }

    /**
     * Gets the integer representation for the second segment of the ip address.
     * @return The second segment (0-255)
     */
    public int getOct2() {
        return (value & 0x00FF0000) >> 16;
    }

    /**
     * Gets the integer representation for the first third of the ip address.
     * @return The third segment (0-255)
     */
    public int getOct3() {
        return (value & 0x0000FF00) >> 8;
    }

    /**
     * Gets the integer representation for the fourth segment of the ip address.
     * @return The fourth segment (0-255)
     */
    public int getOct4() {
        return value & 0x000000FF;
    }

    /**
     * Gets the integer value of the ip address. This can be used for bitwise calculation.
     * For greater/lesser than operations use {@link #getLValue()}
     * @return the integer representation of the ip address
     */
    public int getValue() {
        return value;
    }

    /**
     * Gets the long integer value of the ip address.
     * This can be used for bitwise calculation and greater/lesser than operations.
     * @return the long integer representation of the ip address
     */
    public long getLValue() {
        return Integer.toUnsignedLong(value);
    }

    /**
     * Compares two ip address and returns an boolean if the first ip address is greater than the second one
     * @param ipAddress the ip address to compare to
     * @return true if the ip address is greater than the passed one.
     */
    public boolean isGreaterThan(IPv4Address ipAddress) {
        return getLValue() > ipAddress.getLValue();
    }

    /**
     * Gets the string representation of the ip address in default format.
     * Every block is displayed as an integer
     * @return the string representation of the ip address
     */
    @Override
    public String toString(){
        return Format.format(this, Format.IPv4Format.DECIMAL);
    }

    /**
     * clones the ip address
     * @return a clone of the ip address
     */
    @Override
    protected IPv4Address clone() {
        return new IPv4Address(value);
    }

    /**
     * Compares an object with the ip address
     * @param o the object to compare to
     * @return true if o points to the same object, or the ip addresses have the same value
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPv4Address that = (IPv4Address) o;

        return value == that.value;
    }

    /**
     * generates the hash code for this ip address.
     * @return the hash code
     */
    @Override
    public int hashCode() {
        return value;
    }
}