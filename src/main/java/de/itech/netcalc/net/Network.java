package de.itech.netcalc.net;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.File;
import java.util.*;

/**
 * The Network class represents a logical network that can hold sub-networks and hosts.
 */
@XmlRootElement
public class Network {
    /**
     * Specifies, if a Network has Hosts or sub-networks
     */
    public enum SubnetStatus{
        UNSPECIFIED, HAS_HOSTS, HAS_SUBNETS
    }

    /**
     * Backing field for the Parent property
     */
    private Network parent;

    /**
     * Backing field for all sub-networks hold by the network
     */
    @XmlElement(name="subnet")
    private final ArrayList<Network> subnets = new ArrayList<>();

    /**
     * Backing field for all hosts hold by the network
     */
    @XmlElement(name="host")
    private Host[] hosts;

    /**
     * Backing field for the Name property
     */
    @XmlAttribute
    private String networkName;

    /**
     * Backing field for the IPv4 network ID
     */
    @XmlElement
    private IPv4Address networkIdV4;

    /**
     * Backing field for the IPv4 subnet mask
     */
    @XmlElement
    private IPv4Address subnetMaskV4;

    /**
     * Backing field for the IPv6 network ID
     */
    @XmlElement
    private IPv6Address networkIdV6;

    /**
     * Backing field for the IPv6 network prefix
     */
    @XmlElement
    private int prefixV6;

    /**
     * Creates a new Network with the passed IPv4 details
     * @param parent the parent network, can be null
     * @param networkIdV4 the IPv4 network ID
     * @param subnetMaskV4 the IPv4 network mask
     */
    public Network(Network parent, IPv4Address networkIdV4, IPv4Address subnetMaskV4) {
        this.parent = parent;
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & subnetMaskV4.getValue()));
        this.setSubnetMaskV4(subnetMaskV4);
        hosts = new Host[getMaxHosts()];
    }

    /**
     * Creates an empty Network instance.
     */
    private Network(){}

    /**
     *
     * Creates a new Network with the passed IPv4 and IPv6 details
     * @param parent the parent network, can be null
     * @param networkIdV4 the IPv4 network ID
     * @param subnetMaskV4 the IPv4 network mask
     * @param networkIdV6 the IPv6 network ID
     * @param prefixV6 the IPv6 network prefix
     */
    public Network(Network parent, IPv4Address networkIdV4, IPv4Address subnetMaskV4, IPv6Address networkIdV6, int prefixV6) {
        this.parent = parent;
        if(prefixV6 < 0 || prefixV6 > 128) throw new IllegalArgumentException(prefixV6 + " is not a valid IPv6 Prefix");
        this.setNetworkIdV4(new IPv4Address(networkIdV4.getValue() & subnetMaskV4.getValue()));
        this.setSubnetMaskV4(subnetMaskV4);
        this.setNetworkIdV6(networkIdV6);
        this.setPrefixV6(prefixV6);
        hosts = new Host[getMaxHosts()];
    }

    /**
     * Fills all unused IPv4 addresses with empty hosts
     */
    public void addAllHosts(boolean assignIPv6){
        if(getStatus() == SubnetStatus.HAS_SUBNETS){
            throw new UnsupportedOperationException("can't add host to subnetted network");
        }
        for(int i = 0; i < getMaxHosts(); i++) {
            if(hosts[i] == null){
                IPv4Address hostV4 = new IPv4Address(getNetworkIdV4().getValue() + i + 1);
                Host host;
                if(isIPv6Enabled() && assignIPv6) {
                    IPv6Address random = IPv6Address.getAddressWithRandomHost(networkIdV6.getNetworkId());
                    host = new Host(this, hostV4, random);
                }
                else {
                    host = new Host(this, hostV4);
                }
                hosts[i] = host;
            }
        }
    }

    /**
     * Removes a host from the network.
     * @param ip the IPv4 address of host to remove
     */
    public void removeHost(IPv4Address ip){
        for (int i = 0; i < hosts.length; i++) {
            if(hosts[i] != null && hosts[i].getIPv4Address().equals(ip))
            {
                hosts[i] = null;
                return;
            }
        }
    }

    /**
     * Removes a host from the network.
     * @param host the host to remove
     */
    public void removeHost(Host host){
        for (int i = 0; i < hosts.length; i++) {
            if(hosts[i] != null && hosts[i].equals(host))
            {
                hosts[i] = null;
                return;
            }
        }
    }

    /**
     * Fills the next unused IPv4 address with an empty host.
     * @return the created host
     */
    public Host addHost(){
        Host newHost;
        switch (getStatus()){
            case HAS_SUBNETS:
                throw new UnsupportedOperationException("can't add host to subnetted network");
            case UNSPECIFIED:
                if(hosts == null) hosts = new Host[getMaxHosts()];
                if(isIPv6Enabled()){
                    IPv6Address random = IPv6Address.getAddressWithRandomHost(networkIdV6.getNetworkId());
                    newHost = new Host(this, new IPv4Address(getNetworkIdV4().getValue() + 1), random);
                } else {
                    newHost = new Host(this, new IPv4Address(getNetworkIdV4().getValue() + 1));
                }

                hosts[0] = newHost;
                break;
            case HAS_HOSTS:
                if(hosts == null) hosts = new Host[getMaxHosts()];
                if(getHostCount() >= getMaxHosts()){
                    throw new UnsupportedOperationException("network already has maximum amount of hosts");
                }
                int indexOfAdress = 0;
                while(hosts[indexOfAdress] != null){
                    indexOfAdress++;
                }

                int addressValue = networkIdV4.getValue() | (indexOfAdress + 1);

                if(isIPv6Enabled()){
                    IPv6Address random = IPv6Address.getAddressWithRandomHost(networkIdV6.getNetworkId());
                    newHost = new Host(this, new IPv4Address(addressValue), random);
                } else {
                    newHost = new Host(this, new IPv4Address(addressValue));
                }

                hosts[indexOfAdress] = newHost;
                return newHost;
        }
        return null;
    }

    /**
     * Fills the passed IPv4 address with an empty host.
     * @param address the IPv4 address to fill
     * @return the created host
     */
    public Host addHost(IPv4Address address){

        if(!NetUtils.isInSubnet(networkIdV4, subnetMaskV4, address)){
            throw new IllegalArgumentException("IP adress must be in the specified network");
        }

        switch (getStatus()){
            case HAS_SUBNETS:
                throw new UnsupportedOperationException("can't add host to subnetted network");
            case UNSPECIFIED:
            case HAS_HOSTS:
                if(hosts == null) hosts = new Host[getMaxHosts()];
                if(getHostCount() >= getMaxHosts()){
                    throw new UnsupportedOperationException("network already has maximum amount of hosts");
                }
                if(Arrays.stream(hosts).anyMatch(h -> h != null && h.getIPv4Address().equals(address))){
                    throw new UnsupportedOperationException("IPv4 Address is already given in this subnet");
                }
                int indexOfAddress = (address.getValue() & ~subnetMaskV4.getValue());

                Host newHost;
                if(isIPv6Enabled()){
                    IPv6Address random = IPv6Address.getAddressWithRandomHost(networkIdV6.getNetworkId());
                    newHost = new Host(this, address, random);
                } else {
                    newHost = new Host(this, address);
                }
                hosts[indexOfAddress - 1] = newHost;
                return newHost;
        }
        return null;
    }

    /**
     * Removes all hosts from the network.
     */
    public void clearHosts() {
        hosts = new Host[getMaxHosts()];
    }

    /**
     * Adds the passed subnet to the network.
     * @param subnet the subnet to add
     * @return the added subnet
     */
    public Network addSubnet(Network subnet) {
        if(getStatus() == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("can't add subnet to network with hosts");
        if(subnets.stream().anyMatch(sub -> sub.getNetworkIdV4().equals(subnet.getNetworkIdV4()) || sub.isColliding(subnet)))
            throw new UnsupportedOperationException("Subnet already exists.");
        if(!NetUtils.isInSubnet(this.getNetworkIdV4(), this.getSubnetMaskV4(), subnet.getNetworkIdV4()))
            throw new UnsupportedOperationException("Subnet is not in range of the parent network.");
        Optional<Network> collidingNetwork = getSubnets().stream().filter(other -> other.isColliding(subnet)).findFirst();
        if(collidingNetwork.isPresent())
            throw new UnsupportedOperationException("Subnet is Colliding with "+collidingNetwork.get());

        subnets.add(subnet);
        sortSubnets();
        return subnet;
    }

    /**
     * Adds a new subnet to the network with the given size.
     * Calculates the sub-network size and locates the first free space.
     * @param size the minimum size of the subnet
     * @return the created subnet
     */
    public Network addSubnet(int size) {
        if(getStatus() == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("can't add subnets to network with hosts");
        int maskLength = (int)Math.ceil (Math.log( size + 2 ) / Math.log( 2.0 ));
        int realSize = (int)Math.pow(2, maskLength);
        IPv4Address subnetMask = NetUtils.prefixToMask(32 - maskLength);

        if(subnets.isEmpty() || NetUtils.getLengthBetweenIpAddresses(this.getNetworkIdV4(), subnets.get(0).getNetworkIdV4()) >= size) {
            return addSubnet(new Network(this, getNetworkIdV4(), subnetMask));
        }

        for(int i=0;i<subnets.size()-1;i++) {
            Network curr = subnets.get(i);
            Network next = subnets.get(i + 1);
            if (NetUtils.getLengthBetweenNetworks(curr, next) >= size)
                return addSubnet(new Network(this, new IPv4Address(getOffset(curr.getBroadcastAddress().getValue() + 1, realSize)), subnetMask));
        }

        Network last = subnets.get(subnets.size()-1);
        if(NetUtils.getLengthBetweenIpAddresses(last.getBroadcastAddress(), this.getBroadcastAddress()) >= size) {
            return addSubnet(new Network(this, new IPv4Address(getOffset(last.getBroadcastAddress().getValue() + 1,realSize)), subnetMask));
        }

        return null;
    }

    /**
     * Removed a sub-network from the network.
     * @param subnet the sub-network to remove
     */
    public void removeSubnet(Network subnet) {
        subnets.remove(subnet);
    }

    /**
     * Removes all sub-networks from the network.
     */
    public void clearSubnets() {
        subnets.clear();
    }

    /**
     * Sorts the subnet by their network ID
     */
    private void sortSubnets(){
        subnets.sort(Comparator.comparing(o -> o.getNetworkIdV4().getLValue()));
    }

    /**
     * Calculates all possible subnet sizes of the network
     * @return a list of all sizes
     */
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

    /**
     * Assigns random IPv6 Address to all hosts of the network
     */
    public void assignIPv6ToAllHosts() {
        if(!isIPv6Enabled())
            throw new UnsupportedOperationException("Configure IPv6 first.");
        for (Host h : getHosts()) {
            if(h.getIPv6Address() != null) continue;
            h.setIpv6Address(IPv6Address.getAddressWithRandomHost(this.getNetworkIdV6()));
        }
    }

    /**
     * Splits the network in sub-networks with the specified size.
     * @param size the size of the sub-networks
     */
    public void splitBySize(int size) {
        if(getStatus() == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("Can not add subnet to network with hosts");
        long length = getAmountIpAddresses();
        int realSize = size + 2;
        if(length % realSize != 0) {
            throw new IllegalArgumentException("Size " + size + " is not suitable for network length " + length+ "\npossible sizes: "+ possibleDividers());
        }
        long count = length / realSize;
        split(realSize, count);
    }

    /**
     * Splits the network in sub-networks with the specified host count.
     * @param count the host count of the sub-networks
     */
    public void splitByCount(long count){
        if(getStatus() == SubnetStatus.HAS_HOSTS) throw new UnsupportedOperationException("Can not add subnet to network with hosts");
        int realSize = (int) (getAmountIpAddresses() / count);
        split(realSize, count);
    }

    private void split(int realSize, long count){
        subnets.clear();
        for (int i=0;i < count; i++) {
            IPv4Address nAddress = new IPv4Address(getNetworkIdV4().getValue() + i * realSize);
            int prefixLength = (int)(Math.log( count ) / Math.log( 2.0 ));
            IPv4Address mask = NetUtils.addPrefixToMask(getSubnetMaskV4(), prefixLength);
            Network subnet = new Network(this, nAddress,mask);
            subnets.add(subnet);
        }
    }

    /**
     * Calculates, if two networks have colliding network ranges.
     * @param network the network to compare to
     * @return true, if the networks collide
     */
    public boolean isColliding(Network network) {
        return NetUtils.isInSubnet(getNetworkIdV4(), getSubnetMaskV4(), network.getNetworkIdV4())
                || NetUtils.isInSubnet(network.getNetworkIdV4(), network.getSubnetMaskV4(), getNetworkIdV4());
    }

    /**
     * Parse a network string representation to a network.
     * @param value the string value
     * @param parent the parent network
     * @return the parsed network
     */
    public static Network parse(String value, Network parent) {
        if(value == null) throw new IllegalArgumentException("'value' can not be null.");
        String[] splitted = value.split("/");
        if(splitted.length != 2 || !IPAddress.isValidIPv4(splitted[0])){
            throw new UnsupportedOperationException("'value' is not a valid network identifier.");
        }

        IPv4Address netmask;

        if(splitted[1].chars().allMatch( Character::isDigit )){
            netmask = NetUtils.prefixToMask(Integer.valueOf(splitted[1]));
        }else if(IPAddress.isValidIPv4(splitted[1])){
            netmask = IPAddress.parseIPv4(splitted[1]);
        }else{
            throw new UnsupportedOperationException("'value' is not a valid network identifier.");
        }

        IPv4Address networkID = IPAddress.parseIPv4(splitted[0]);
        return new Network(parent, networkID, netmask);
    }

    public Host getHost(Object iPv4Address) {
        Optional<Host> host = Arrays.stream(getHosts()).filter(h -> h != null && h.getIPv4Address().equals(iPv4Address)).findFirst();
        return host.orElse(null);
    }

    /**
     * Gets the Property indicating whether IPv6 is configured for this network.
     * @return true, if IPv6 is configured
     */
    public boolean isIPv6Enabled(){
        return networkIdV6 != null;
    }

    /**
     * Gets the networkName of the network.
     * @return the networkName
     */
    public String getName() {
        return networkName;
    }

    /**
     * Sets the networkName of the network.
     * @param name the networkName to set
     */
    public void setName(String name) {
        this.networkName = name;
    }

    /**
     * Gets the possible amount of IPv4 addresses in the network.
     * Includes the network ID and broadcast address.
     * @return the amount of IPv4 addresses
     */
    public long getAmountIpAddresses() {
        return ((~getSubnetMaskV4().getLValue()) & Integer.toUnsignedLong(-1)) + 1;
    }

    /**
     * Gets the IPv4 network ID of the network
     * @return the network ID
     */
    public IPv4Address getNetworkIdV4() {
        return networkIdV4.clone();
    }

    /**
     * Sets the IPv4 network ID of the network.
     * @param networkIdV4 the IPv4 network ID to be set
     */
    private void setNetworkIdV4(IPv4Address networkIdV4) {
        this.networkIdV4 = networkIdV4;
    }

    /**
     * Gets the IPv4 subnet mask of the network
     * @return the IPv4 subnet mask
     */
    public IPv4Address getSubnetMaskV4() {
        return subnetMaskV4.clone();
    }

    /**
     * Gets the maximum amount of IPv4 host addresses for the network.
     * Excludes the network ID and broadcast address.
     * @return the maximum host amount
     */
    public int getMaxHosts(){
        return Math.max((int) (getAmountIpAddresses() - 2), 0);
    }

    /**
     * Gets the amount of initialized hosts of the network.
     * @return the host count
     */
    public int getHostCount() {
        return (int)Arrays.stream(getHosts()).filter(Objects::nonNull).count();
    }

    /**
     * Gets the IPv4 broadcast address of the network.
     * @return the IPv4 broadcast address
     */
    public IPv4Address getBroadcastAddress(){
        return new IPv4Address(getNetworkIdV4().getValue() + getMaxHosts() + 1);
    }

    /**
     * Sets the IPv4 network mask of the network.
     * @param subnetMaskV4 the IPv4 network mask to set
     */
    private void setSubnetMaskV4(IPv4Address subnetMaskV4) {
        this.subnetMaskV4 = subnetMaskV4;
    }

    /**
     * Gets the IPv6 network ID of the network
     * @return the IPv6 network ID
     */
    public IPv6Address getNetworkIdV6() {
        return networkIdV6;
    }

    /**
     * Sets the IPv6 network ID of the network
     * @param networkIdV6 the IPv6 address to set
     */
    private void setNetworkIdV6(IPv6Address networkIdV6) {
        this.networkIdV6 = networkIdV6;
    }

    /**
     * Gets the IPv6 network prefix of the network
     * @return the IPv6 network prefix
     */
    public int getPrefixV6() {
        return prefixV6;
    }

    /**
     * Sets the IPv6 network prefix of the network.
     * @param prefixV6 the IPv6 network prefix to set
     */
    private void setPrefixV6(int prefixV6) {
        this.prefixV6 = prefixV6;
    }

    /**
     * Configures IPv6 for the network.
     * @param networkId the IPv6 network ID
     * @param prefix the IPv6 network prefix
     */
    public void setIPv6(IPv6Address networkId, int prefix) {
        if(networkId != null) {
            long nId = prefix > 64 ? networkId.getNetworkId()
                    : networkId.getNetworkId() & NetUtils.ipv6PrefixLengthToValue(prefix);
            long iId = prefix <= 64 ? networkId.getInterfaceId()
                    : networkId.getInterfaceId() & NetUtils.ipv6PrefixLengthToValue(prefix - 64);
            networkId = new IPv6Address(nId, iId);
        }
        setNetworkIdV6(networkId);
        setPrefixV6(networkId == null ? 0 : prefix);
    }

    /**
     * Gets a list of the sub-networks of the network.
     * @return the sub-network
     */
    public ArrayList<Network> getSubnets(){
        return subnets;
    }

    private int getOffset(int position, int count) {
        return (int)Math.ceil((double)position / (double)count) * count;
    }

    /**
     * Gets an Array of all host slots of the network. Each host is positioned depending on its IPv4 address.
     * The array is always of size getMaxHosts(). This can lead to empty slots.
     * @return the hosts of the network
     */
    public Host[] getHosts(){
        return hosts;
    }

    /**
     * Gets the network status, describing if the network contains sub-networks of hosts.
     * @return the network status
     */
    public SubnetStatus getStatus(){
        if(subnets.size() > 0) return SubnetStatus.HAS_SUBNETS;
        if(hosts != null && Arrays.stream(hosts).anyMatch(Objects::nonNull)) return SubnetStatus.HAS_HOSTS;
        return SubnetStatus.UNSPECIFIED;
    }

    /**
     * Gets the parent network of the network. Returns null, if this is a root network.
     * @return the parent network
     */
    public Network getParent() {
        return parent;
    }

    /**
     * Parse a Network from an XML file.
     * @param file file containing the XML
     * @return the parsed network
     */
    public static Network fromXML(File file){

        //todo: set parent of host and network;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Network.class);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Network network = (Network) jaxbUnmarshaller.unmarshal(file);
            System.out.println(network);

            return network;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Saves a Network to an XML file.
     * @param file the file to save to
     */
    public void save(File file){
        try {

            JAXBContext jaxbContext = JAXBContext.newInstance(Network.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

            // output pretty printed
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            jaxbMarshaller.marshal(this, file);
            jaxbMarshaller.marshal(this, System.out);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Gets the string representation of the network either in IPv4 or IPv6 notation.
     * @param ipv6IfEnabled use IPv6 notation
     * @return the network's string representation
     */
    public String toString(boolean ipv6IfEnabled){
        if(!ipv6IfEnabled || !isIPv6Enabled()){
            return toString();
        }
        return Format.format(getNetworkIdV6(), Format.IPv6Format.SHORTHAND) + "/" + getPrefixV6() + (networkName == null ? "" : " (" + networkName + ")");
    }

    /**
     * Gets the string representation of the network in IPv4 notation.
     * @return the network's string representation
     */
    @Override
    public String toString(){
        return this.getNetworkIdV4().toString()+"/"+NetUtils.maskToPrefix(this.getSubnetMaskV4()) + (networkName == null ? "" : " (" + networkName + ")");
    }

    /**
     * Compares the network to an object by reference and value.
     * @param o the object to compare to
     * @return true, if both references point to the same object, or both networks have the same values.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Network that = (Network) o;

        return prefixV6 == that.prefixV6
                && networkIdV4.equals(that.networkIdV4)
                && subnetMaskV4.equals(that.subnetMaskV4)
                && (networkIdV6 != null ? networkIdV6.equals(that.networkIdV6) : that.networkIdV6 == null);
    }

    /**
     * Calculates a hash code for the network.
     * @return the hash code of the the network.
     */
    @Override
    public int hashCode() {
        int result = networkIdV4.hashCode();
        result = 31 * result + subnetMaskV4.hashCode();
        result = 31 * result + (networkIdV6 != null ? networkIdV6.hashCode() : 0);
        result = 31 * result + prefixV6;
        return result;
    }
}