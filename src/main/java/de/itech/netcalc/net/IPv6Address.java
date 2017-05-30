package de.itech.netcalc.net;

import java.util.Random;

public class IPv6Address extends IPAddress {
    private long networkId;

    private long interfaceId;

    public IPv6Address(long networkId){
        this.networkId = networkId;
        this.interfaceId = getRandomInterfaceAddress();
    }

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

    public static long getRandomInterfaceAddress(){
        //due to EUI Standards the 25th bit from the right always has to be 0
        //https://supportforums.cisco.com/document/100566/understanding-ipv6-eui-64-bit-address
        return (new Random().nextLong() & ~(1 << 24)) | 0xFFFE << 24;
    }

    public static IPv6Address getRandomInterfaceAddress(long networkId){
        //for explanation see other method implementation of getRandomInterfaceAddress
        return new IPv6Address(networkId, (new Random().nextLong() & ~(1 << 24)) | 0xFFFE << 24);
    }

    public static IPv6Address getRandomInterfaceAddress(IPv6Address address){
        //for explanation see other method implementation of getRandomInterfaceAddress
        return new IPv6Address(address.networkId, (new Random().nextLong() & ~(1 << 24)) | 0xFFFE << 24);
    }

    @Override
    protected IPv6Address clone() {
        return new IPv6Address(networkId, interfaceId);
    }
}