package de.itech.netcalc;

public class Host {
    private String name;
    private Ipv4Address ip;

    Host(Ipv4Address address, String name) {
        this.ip = address;
        this.name = name;
    }

    @Override
    public String toString(){
        return "Host: "+name+" Ip: "+ip;
    }
}