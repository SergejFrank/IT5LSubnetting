package de.itech.netcalc;

public abstract class NetworkBase {

    private String name;
    private Ipv4Address address;
    private Ipv4Address mask;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() { return ~getMask().getValue() + 1; }

    public Ipv4Address getAddress() {
        return new Ipv4Address(address.getValue());
    }

    protected void setAddress(Ipv4Address address) {
        this.address = address;
    }

    public Ipv4Address getMask() {
        return new Ipv4Address(mask.getValue());
    }

    public int getMaxHosts(){ return getLength() - 2; }

    public Ipv4Address getBroadcastAddress(){
        return new Ipv4Address(getAddress().getValue() + getMaxHosts() + 1);
    }

    protected void setMask(Ipv4Address mask) {
        this.mask = mask;
    }

    @Override
    public String toString(){
        return this.getAddress().toString()+"/"+NetUtils.getSuffixFromMask(this.getMask());
    }
}
