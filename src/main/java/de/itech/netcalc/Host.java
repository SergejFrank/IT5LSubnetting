package de.itech.netcalc;

public class Host {
    private String name;
    private IPv4Address ip;

    Host(IPv4Address address, String name) {
        this.ip = address;
        this.name = name;
    }

    @Override
    public String toString(){
        return "Host: "+name+" Ip: "+ip;
    }
}