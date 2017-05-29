package de.itech.netcalc;

public abstract class NetworkBase {

    private String name;
    private IPv4Address networkIdV4;
    private IPv4Address networkMaskV4;
    private IPv6Address networkIdV6;
    private int prefixV6;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworkBase that = (NetworkBase) o;

        if (!address.equals(that.address)) return false;
        return mask.equals(that.mask);

    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + mask.hashCode();
        return result;
    }

    public boolean isColliding(NetworkBase network) {
        return NetUtils.isInSubnet(getNetworkIdV4(), getNetworkMaskV4(), network.getNetworkIdV4())
                || NetUtils.isInSubnet(network.getNetworkIdV4(), network.getNetworkMaskV4(), getNetworkIdV4());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLength() { return ~getNetworkMaskV4().getValue() + 1; }

    public IPv4Address getNetworkIdV4() {
        return networkIdV4.clone();
    }

    protected void setNetworkIdV4(IPv4Address networkIdV4) {
        this.networkIdV4 = networkIdV4;
    }

    public IPv4Address getNetworkMaskV4() {
        return networkMaskV4.clone();
    }

    public int getMaxHosts(){ return getLength() - 2; }

    public IPv4Address getBroadcastAddress(){
        return new IPv4Address(getNetworkIdV4().getValue() + getMaxHosts() + 1);
    }

    protected void setNetworkMaskV4(IPv4Address networkMaskV4) {
        this.networkMaskV4 = networkMaskV4;
    }

    @Override
    public String toString(){
        return this.getNetworkIdV4().toString()+"/"+NetUtils.getPrefixFromMask(this.getNetworkMaskV4());
    }

    public IPv6Address getNetworkIdV6() {
        return networkIdV6;
    }

    protected void setNetworkIdV6(IPv6Address networkIdV6) {
        this.networkIdV6 = networkIdV6;
    }

    public int getPrefixV6() {
        return prefixV6;
    }

    protected void setPrefixV6(int prefixV6) {
        this.prefixV6 = prefixV6;
    }
}
