package de.itech.netcalc.net;

import java.util.Random;

public class IPv6Address extends IPAddress {
    private long networkId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IPv6Address that = (IPv6Address) o;

        if (networkId != that.networkId) return false;
        return interfaceId == that.interfaceId;

    }

    @Override
    public int hashCode() {
        int result = (int) (networkId ^ (networkId >>> 32));
        result = 31 * result + (int) (interfaceId ^ (interfaceId >>> 32));
        return result;
    }

    private long interfaceId;

    public IPv6Address(long networkId, long interfaceId){
        this.networkId = networkId;
        this.interfaceId = interfaceId;
    }

    public long getInterfaceId() {
        return interfaceId;
    }

    public long getNetworkId() {
        return networkId;
    }

    @Override
    public String toString() {
        return toString(false);
    }

    public String toString(Boolean shorthand) {
        Short[] segments = new Short[8];


        for(int i = 0; i < 4; i++){
            segments[i] = (short) (networkId >> (3-i)*16);
            segments[i + 4] = (short) (interfaceId >> (3-i)*16);
        }

        String address = shorthand
                ? Long.toString(Short.toUnsignedLong(segments[0]), 16)
                : String.format("%04X", Short.toUnsignedLong(segments[0]));

        for(int i = 1; i < segments.length; i++){
            address += shorthand
                    ? ":" + Long.toString(Short.toUnsignedLong(segments[i]), 16)
                    : ":" + String.format("%04X", Short.toUnsignedLong(segments[i]));
        }

        if(shorthand){
            address = address.replaceAll("((?::0\\b){2,}):?(?!\\S*\\b\\1:0\\b)(\\S*)", "::$2");
            if(address.equals("0::")) address = "::";
        }

        return address.toLowerCase();
    }

    private static long getRandomInterfaceAddress(){
        //due to EUI Standards the 25th bit from the right always has to be 0
        //https://supportforums.cisco.com/document/100566/understanding-ipv6-eui-64-bit-address
        return (new Random().nextLong() & ~(1 << 24)) | 0xFFFEL << 24;
    }

    public static IPv6Address getAddressWithRandomHost(long networkId){
        //for explanation see other method implementation of getAddressWithRandomHost
        return new IPv6Address(networkId, getRandomInterfaceAddress());
    }

    public static IPv6Address getAddressWithRandomHost(IPv6Address address){
        //for explanation see other method implementation of getAddressWithRandomHost
        return new IPv6Address(address.networkId, getRandomInterfaceAddress());
    }

    @Override
    protected IPv6Address clone() {
        return new IPv6Address(networkId, interfaceId);
    }
}