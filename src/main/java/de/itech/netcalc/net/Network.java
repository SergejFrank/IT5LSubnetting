package de.itech.netcalc.net;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Network {
    private ArrayList<Network> subnets = new ArrayList<>();

    private Host[] hosts;

    private boolean isSubnetted;

    private String name;
    private IPv4Address networkIdV4;
    private IPv4Address networkMaskV4;
    private IPv6Address networkIdV6;
    private int prefixV6;


    public Network(IPv4Address networkIdV4, IPv4Address networkMaskV4) {
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
    }

    public Network(IPv4Address networkIdV4, IPv4Address networkMaskV4, IPv6Address networkIdV6, int prefixV6) {
        if(prefixV6 < 0 || prefixV6 > 128) throw new IllegalArgumentException(prefixV6 + " is not a valid IPv6 Prefix");
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
        this.setNetworkIdV6(networkIdV6);
        this.setPrefixV6(prefixV6);
    }

    public void addAllHosts(){
        hosts = new Host[getMaxHosts()];
        for(int i = 1; i <= getMaxHosts(); i++) {
            IPv4Address hostV4 = new IPv4Address(getNetworkIdV4().getValue() + i);
            Host host = new Host(this, hostV4, null);
            hosts[i-1] = host;
        }
    }



    public boolean isColliding(Network network) {
        return NetUtils.isInSubnet(getNetworkIdV4(), getNetworkMaskV4(), network.getNetworkIdV4())
                || NetUtils.isInSubnet(network.getNetworkIdV4(), network.getNetworkMaskV4(), getNetworkIdV4());
    }



    public Network addSubnet(Network subnet) throws IllegalArgumentException {
        if(subnets.stream().anyMatch(sub -> sub.getNetworkIdV4().equals(subnet.getNetworkIdV4()) || sub.isColliding(subnet)))
            throw new IllegalArgumentException("Subnet already exists.");

        subnets.add(subnet);
        sortSubnets();
        return subnet;
    }

    public Network addSubnet(int size) {
        int maskLength = (int)Math.ceil (Math.log( size + 2 ) / Math.log( 2.0 ));
        int realSize = (int)Math.pow(2, maskLength);
        IPv4Address subnetMask = NetUtils.getMaskFromPrefix(32 - maskLength);

        if(subnets.isEmpty() || NetUtils.getLengthBetweenIpAddresses(this.getNetworkIdV4(), subnets.get(0).getNetworkIdV4()) >= size) {
            return addSubnet(new Network(getNetworkIdV4(), subnetMask));
        }

        for(int i=0;i<subnets.size()-1;i++) {
            Network curr = subnets.get(i);
            Network next = subnets.get(i + 1);
            if (NetUtils.getLengthBetweenNetworks(curr, next) >= size)
                return addSubnet(new Network(new IPv4Address(getOffset(curr.getBroadcastAddress().getValue() + 1, realSize)), subnetMask));
        }

        Network last = subnets.get(subnets.size()-1);
        if(NetUtils.getLengthBetweenIpAddresses(last.getBroadcastAddress(), this.getBroadcastAddress()) >= size) {
            return addSubnet(new Network(new IPv4Address(getOffset(last.getBroadcastAddress().getValue() + 1,realSize)), subnetMask));
        }

        return null;
    }

    public void removeSubnet(Network subnet) {
        subnets.remove(subnet);
    }

    private void sortSubnets(){
        Collections.sort(subnets, Comparator.comparing(o -> o.getNetworkIdV4().getLValue()));
    }

    public ArrayList<Integer> possibleDividers(){
        int num = getLength();
        ArrayList<Integer> deviders = new ArrayList<>();
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                if (i - 2 > 0) {
                    deviders.add(i - 2);
                }
                if (i != num/i) {
                    deviders.add((num/i)-2);
                }
            }
        }

        Collections.sort(deviders);
        return deviders;
    }

    public void splitBySize(int size) {
        int length = getLength();
        int realSize = size + 2;
        if(length % realSize != 0) {
            throw new IllegalArgumentException("Size " + size + " is not suitable for network length " + length+ "\npossible sizes: "+ possibleDividers());
        }
        int count = length / realSize;
        subnets.clear();
        for (int i=0;i < count; i++) {
            IPv4Address nAddress = new IPv4Address(getNetworkIdV4().getValue() + i * realSize);
            int prefixLength = (int)(Math.log( count ) / Math.log( 2.0 ));
            IPv4Address mask = NetUtils.addPrefixToMask(getNetworkMaskV4(), prefixLength);

            Network subnet = new Network(nAddress,mask);
            subnets.add(subnet);
        }
    }

    public void splitByCount(int count){
        int realSize = getLength() / count;
        subnets.clear();
        for (int i=0;i < count; i++) {
            IPv4Address nAddress = new IPv4Address(getNetworkIdV4().getValue() + i * realSize);
            int prefixLength = (int)(Math.log( count ) / Math.log( 2.0 ));
            IPv4Address mask = NetUtils.addPrefixToMask(getNetworkMaskV4(), prefixLength);

            Network subnet = new Network(nAddress,mask);
            subnets.add(subnet);
        }
    }

    public static Network parse(String value) {
        if(value == null) throw new IllegalArgumentException("'value' can not be null.");
        final String v4Regex = "^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])(\\/([0-9]|[1-2][0-9]|3[0-2]))$";
        if(!value.matches(v4Regex)) throw new UnsupportedOperationException("'value' is not a valid network identifier.");
        String[] splitted = value.split("/");
        IPv4Address netmask = NetUtils.getMaskFromPrefix(Integer.valueOf(splitted[1]));
        IPv4Address networkID = IPAddress.parseIPv4(splitted[0]);
        return new Network(networkID, netmask);
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

    public ArrayList<Network> getSubnets(){
        return subnets;
    }

    private int getOffset(int position, int count) {
        return (int)Math.ceil((double)position / (double)count) * count;
    }

    public Host[] getHosts(){
        return hosts;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Network that = (Network) o;

        if (prefixV6 != that.prefixV6) return false;
        if (!networkIdV4.equals(that.networkIdV4)) return false;
        if (!networkMaskV4.equals(that.networkMaskV4)) return false;
        return networkIdV6 != null ? networkIdV6.equals(that.networkIdV6) : that.networkIdV6 == null;
    }

    @Override
    public int hashCode() {
        int result = networkIdV4.hashCode();
        result = 31 * result + networkMaskV4.hashCode();
        result = 31 * result + (networkIdV6 != null ? networkIdV6.hashCode() : 0);
        result = 31 * result + prefixV6;
        return result;
    }
}