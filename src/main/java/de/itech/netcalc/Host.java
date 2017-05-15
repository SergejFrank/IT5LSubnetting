package de.itech.netcalc;

public class Host {
    private String name;
    private IpAddress ip;

    public Host(IpAddress address, String name) {
        this.ip = address;
        this.name = name;
    }

    @Override
    public String toString(){
        return "Host: "+name+" Ip: "+ip;
    }
}
