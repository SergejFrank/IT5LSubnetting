import de.itech.netcalc.*;
import org.junit.Test;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertEquals;

public class NetworkTest {

    @Test(expected=IllegalArgumentException.class)
    public void shouldThrowErrorOnCollidingSubnets() throws Exception {

        //given
        IPv4Address networkNetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address networkMask = IPAddress.parseIPv4("255.255.255.0");
        Network network = new Network(networkNetId,networkMask);

        IPv4Address subnet1NetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address subnet1kMask = IPAddress.parseIPv4("255.255.255.128");
        Subnet subnet1 = new Subnet(subnet1NetId,subnet1kMask);

        IPv4Address subnet2NetId = IPAddress.parseIPv4("192.168.178.64");
        IPv4Address subnet2kMask = IPAddress.parseIPv4("255.255.255.192");
        Subnet subnet2 = new Subnet(subnet2NetId,subnet2kMask);

        //when
        network.addSubnet(subnet1);
        network.addSubnet(subnet2);


        //then
        //throw error
    }

    @Test
    public void shouldCreateFourSubnets() throws Exception {
        //given
        IPv4Address networkNetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address networkMask = IPAddress.parseIPv4("255.255.255.0");
        Network network = new Network(networkNetId,networkMask);

        //when
        network.splitEqualy(62);

        //then
        assertEquals(4, network.getSubnets().size());
    }

    @Test
    public void shouldCreateSubnetInsideFirstGap() throws Exception {
        //given
        IPv4Address networkNetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address networkMask = IPAddress.parseIPv4("255.255.255.0");
        Network network = new Network(networkNetId,networkMask);

        IPv4Address subnet1NetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address subnet1kMask = IPAddress.parseIPv4("255.255.255.192");
        Subnet subnet1 = new Subnet(subnet1NetId,subnet1kMask);

        IPv4Address subnet2NetId = IPAddress.parseIPv4("192.168.178.64");
        IPv4Address subnet2kMask = IPAddress.parseIPv4("255.255.255.192");
        Subnet subnet2 = new Subnet(subnet2NetId,subnet2kMask);

        IPv4Address subnet3NetId = IPAddress.parseIPv4("192.168.178.128");
        IPv4Address subnet3kMask = IPAddress.parseIPv4("255.255.255.192");
        Subnet subnet3 = new Subnet(subnet3NetId,subnet3kMask);

        network.addSubnet(subnet1);
        network.addSubnet(subnet3);

        //when
        network.addSubnet(62);

        //then
        assertThat(network.getSubnets(), hasItem(subnet2));

    }


}
