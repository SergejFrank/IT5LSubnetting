package de.itech.netcalc;

public abstract class NetworkBase {

    private String name;
    private IPv4Address address;
    private IPv4Address mask;

    public boolean isColliding(NetworkBase network) {
        return NetUtils.isInSubnet(getAddress(), getMask(), network.getAddress())
                || NetUtils.isInSubnet(network.getAddress(), network.getMask(), getAddress());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() { return ~getMask().getValue() + 1; }

    public IPv4Address getAddress() {
        return address.clone();
    }

    protected void setAddress(IPv4Address address) {
        this.address = address;
    }

    public IPv4Address getMask() {
        return mask.clone();
    }

    public int getMaxHosts(){ return getLength() - 2; }

    public IPv4Address getBroadcastAddress(){
        return new IPv4Address(getAddress().getValue() + getMaxHosts() + 1);
    }

    protected void setMask(IPv4Address mask) {
        this.mask = mask;
    }

    @Override
    public String toString(){
        return this.getAddress().toString()+"/"+NetUtils.getPrefixFromMask(this.getMask());
    }
}
