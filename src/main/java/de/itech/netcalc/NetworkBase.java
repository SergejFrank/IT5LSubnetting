package de.itech.netcalc;

public abstract class NetworkBase {

    private String name;
    private IpAddress address;
    private IpAddress mask;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() { return ~getMask().getValue() + 1; }

    public IpAddress getAddress() {
        return new IpAddress(address.getValue());
    }

    protected void setAddress(IpAddress address) {
        this.address = address;
    }

    public IpAddress getMask() {
        return new IpAddress(mask.getValue());
    }

    public int getMaxHosts(){ return getLength() - 2; }

    public IpAddress getBroadcastAddress(){
        return new IpAddress(getAddress().getValue() + getMaxHosts() + 1);
    }

    protected void setMask(IpAddress mask) {
        this.mask = mask;
    }

    @Override
    public String toString(){
        return this.getAddress().toString()+"/"+NetUtils.getSuffixFromMask(this.getMask());
    }
}
