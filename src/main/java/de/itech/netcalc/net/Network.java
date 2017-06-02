package de.itech.netcalc.net;

import java.util.*;

public class Network {
    public enum SubnetStatus{
        UNSPECIFIED, HAS_HOSTS, HAS_SUBNETS;
    }

    private ArrayList<Network> subnets = new ArrayList<>();

    int hostCount;

    private Host[] hosts;

    private SubnetStatus status;

    private String name;
    private IPv4Address networkIdV4;
    private IPv4Address networkMaskV4;
    private IPv6Address networkIdV6;
    private int prefixV6;

    public Network(IPv4Address networkIdV4, IPv4Address networkMaskV4) {
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
        status = SubnetStatus.UNSPECIFIED;
        hostCount = 0;
        hosts = new Host[getMaxHosts()];
        checkIsZeroNetmask(networkMaskV4);
    }

    public Network(IPv4Address networkIdV4, IPv4Address networkMaskV4, IPv6Address networkIdV6, int prefixV6) {
        if(prefixV6 < 0 || prefixV6 > 128) throw new IllegalArgumentException(prefixV6 + " is not a valid IPv6 Prefix");
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & networkMaskV4.getValue()));
        this.setNetworkMaskV4(networkMaskV4);
        this.setNetworkIdV6(networkIdV6);
        this.setPrefixV6(prefixV6);
        status = SubnetStatus.UNSPECIFIED;
        hostCount = 0;
        hosts = new Host[getMaxHosts()];
        checkIsZeroNetmask(networkMaskV4);
    }

    public void addAllHosts(){
        if(status == SubnetStatus.HAS_SUBNETS){
            throw new UnsupportedOperationException("can't add host to subnetted network");
        }
        status = SubnetStatus.HAS_HOSTS;
        hostCount = getMaxHosts();
        for(int i = 0; i < getMaxHosts(); i++) {
            if(hosts[i] == null){
                IPv4Address hostV4 = new IPv4Address(getNetworkIdV4().getValue() + i + 1);
                Host host = new Host(this, hostV4, null);
                hosts[i] = host;
            }
        }
    }

    public void removeHost(IPv4Address ip){
        for (Host host:hosts) {
            if(host != null && host.getIPv4Address().equals(ip)){
                host = null;
                hostCount--;
            }
        }
    }

    public void removeHost(Host host){
        for (Host other:hosts) {
            if(other != null && other.equals(host)){
                other = null;
                hostCount--;
            }
        }
    }

    public void addHost(){
        switch (status){
            case HAS_SUBNETS:
                throw new UnsupportedOperationException("can't add host to subnetted network");
            case UNSPECIFIED:
                hosts[0] = new Host(this, new IPv4Address(getNetworkIdV4().getValue() + 1), null);
                status = SubnetStatus.HAS_HOSTS;
                hostCount = 1;
                break;
            case HAS_HOSTS:
                if(hostCount >= getMaxHosts()){
                    throw new UnsupportedOperationException("network already has maximum amount of hosts");
                }
                int indexOfAdress = 0;
                while(hosts[indexOfAdress] != null){
                    indexOfAdress++;
                }

                int addressValue = networkIdV4.getValue() | (indexOfAdress + 1);

                hosts[indexOfAdress] = new Host(this, new IPv4Address(addressValue), null);
                hostCount++;
        }
    }

    public void addHost(IPv4Address address){

        if(!NetUtils.isInSubnet(networkIdV4, networkMaskV4, address)){
            throw new IllegalArgumentException("IP adress must be in the specified network");
        }

        switch (status){
            case HAS_SUBNETS:
                throw new UnsupportedOperationException("can't add host to subnetted network");
            case UNSPECIFIED:
            case HAS_HOSTS:
                if(hostCount >= getMaxHosts()){
                    throw new UnsupportedOperationException("network already has maximum amount of hosts");
                }
                if(Arrays.stream(hosts).anyMatch(h -> h != null && h.getIPv4Address().equals(address))){
                    throw new UnsupportedOperationException("IPv4 Address is already given in this subnet");
                }
                int indexOfAddress = (address.getValue() & ~networkMaskV4.getValue());
                hosts[indexOfAddress - 1] = new Host(this, address, null);
                hostCount++;
        }
    }

    public void clearHosts() {
        hosts = new Host[getMaxHosts()];
        hostCount = 0;
        if(getStatus() == SubnetStatus.HAS_HOSTS)
            status = SubnetStatus.UNSPECIFIED;
    }

    public Network addSubnet(Network subnet) {
        if(status == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("can't add subnets to network with hosts");
        if(subnets.stream().anyMatch(sub -> sub.getNetworkIdV4().equals(subnet.getNetworkIdV4()) || sub.isColliding(subnet)))
            throw new UnsupportedOperationException("Subnet already exists.");
        if(NetUtils.isInSubnet(this.getNetworkIdV4(), this.getNetworkMaskV4(), subnet.getNetworkIdV4()))
            throw new UnsupportedOperationException("Subnet is not in range of the parent network.");
        status = SubnetStatus.HAS_SUBNETS;

        subnets.add(subnet);
        sortSubnets();
        return subnet;
    }

    public Network addSubnet(int size) {
        if(status == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("can't add subnets to network with hosts");
        int maskLength = (int)Math.ceil (Math.log( size + 2 ) / Math.log( 2.0 ));
        int realSize = (int)Math.pow(2, maskLength);
        IPv4Address subnetMask = NetUtils.prefixToMask(32 - maskLength);

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
        if(subnets.size() == 0){
            status = SubnetStatus.UNSPECIFIED;
        }
    }

    public void clearSubnets() {
        subnets.clear();
        if(getStatus() == SubnetStatus.HAS_SUBNETS)
            status = SubnetStatus.UNSPECIFIED;
    }

    private void sortSubnets(){
        subnets.sort(Comparator.comparing(o -> o.getNetworkIdV4().getLValue()));
    }

    public ArrayList<Integer> possibleDividers(){
        long num = getAmountIpAddresses();
        ArrayList<Integer> dividers = new ArrayList<>();
        for (int i = 2; i <= Math.sqrt(num); i++) {
            if (num % i == 0) {
                if (i - 2 > 0) {
                    dividers.add(i - 2);
                }
                if (i != num/i) {
                    dividers.add((int) (num / i) - 2);
                }
            }
        }

        Collections.sort(dividers);
        return dividers;
    }

    public void splitBySize(int size) {
        if(getStatus() == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("can't add subnets to network with hosts");
        long length = getAmountIpAddresses();
        int realSize = size + 2;
        if(length % realSize != 0) {
            throw new IllegalArgumentException("Size " + size + " is not suitable for network length " + length+ "\npossible sizes: "+ possibleDividers());
        }
        long count = length / realSize;
        split(realSize,count);
    }

    public void splitByCount(long count){
        if(getStatus() == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("can't add subnets to network with hosts");
        int realSize = (int) (getAmountIpAddresses() / count);
        split(realSize,count);
    }

    private void split(int realSize, long count){
        subnets.clear();
        status = SubnetStatus.HAS_SUBNETS;
        for (int i=0;i < count; i++) {
            IPv4Address nAddress = new IPv4Address(getNetworkIdV4().getValue() + i * realSize);
            int prefixLength = (int)(Math.log( count ) / Math.log( 2.0 ));
            IPv4Address mask = NetUtils.addPrefixToMask(getNetworkMaskV4(), prefixLength);
            Network subnet = new Network(nAddress,mask);
            subnets.add(subnet);
        }
    }

    public boolean isColliding(Network network) {
        return NetUtils.isInSubnet(getNetworkIdV4(), getNetworkMaskV4(), network.getNetworkIdV4())
                || NetUtils.isInSubnet(network.getNetworkIdV4(), network.getNetworkMaskV4(), getNetworkIdV4());
    }

    public static Network parse(String value) {
        if(value == null) throw new IllegalArgumentException("'value' can not be null.");
        String[] splitted = value.split("/");
        if(splitted.length != 2 || !IPAddress.isValidIPv4(splitted[0])){
            throw new UnsupportedOperationException("'value' is not a valid network identifier.");
        }

        IPv4Address netmask = null;

        if(splitted[1].chars().allMatch( Character::isDigit )){
            netmask = NetUtils.prefixToMask(Integer.valueOf(splitted[1]));
        }else if(IPAddress.isValidIPv4(splitted[1])){
            netmask = IPAddress.parseIPv4(splitted[1]);
        }else{
            throw new UnsupportedOperationException("'value' is not a valid network identifier.");
        }

        IPv4Address networkID = IPAddress.parseIPv4(splitted[0]);
        return new Network(networkID, netmask);
    }

    public Host getHost(Object iPv4Address) {
        Optional<Host> host = Arrays.stream(getHosts()).filter(h -> h != null && h.getIPv4Address().equals(iPv4Address)).findFirst();
        return host.orElse(null);
    }

    private void checkIsZeroNetmask(IPv4Address mask){
        if(mask.getValue() == 0){
            status = SubnetStatus.HAS_SUBNETS;
        }
    }

    //getter and setter
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getAmountIpAddresses() { return (~getNetworkMaskV4().getLValue()) + 1; }

    public IPv4Address getNetworkIdV4() {
        return networkIdV4.clone();
    }

    private void setNetworkIdV4(IPv4Address networkIdV4) {
        this.networkIdV4 = networkIdV4;
    }

    public IPv4Address getNetworkMaskV4() {
        return networkMaskV4.clone();
    }

    public int getMaxHosts(){
        return Math.max((int) (getAmountIpAddresses() - 2), 0);
    }

    public IPv4Address getBroadcastAddress(){
        return new IPv4Address((int) (getNetworkIdV4().getValue() + getMaxHosts() + 1));
    }

    private void setNetworkMaskV4(IPv4Address networkMaskV4) {
        this.networkMaskV4 = networkMaskV4;
    }

    public IPv6Address getNetworkIdV6() {
        return networkIdV6;
    }

    private void setNetworkIdV6(IPv6Address networkIdV6) {
        this.networkIdV6 = networkIdV6;
    }

    public int getPrefixV6() {
        return prefixV6;
    }

    private void setPrefixV6(int prefixV6) {
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

    public SubnetStatus getStatus(){
        return status;
    }


    //overwridden methods
    @Override
    public String toString(){
        return this.getNetworkIdV4().toString()+"/"+NetUtils.maskToPrefix(this.getNetworkMaskV4()) + (name == null ? "" : " (" + name + ")");
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