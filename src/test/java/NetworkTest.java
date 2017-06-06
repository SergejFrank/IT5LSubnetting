import de.itech.netcalc.net.IPAddress;
import de.itech.netcalc.net.IPv4Address;
import de.itech.netcalc.net.NetUtils;
import de.itech.netcalc.net.Network;
import org.junit.Test;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

public class NetworkTest {

    @Test(expected=UnsupportedOperationException.class)
    public void shouldThrowErrorOnCollidingSubnets() throws Exception {

        //given
        IPv4Address networkNetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address networkMask = IPAddress.parseIPv4("255.255.255.0");
        Network network = new Network(null, networkNetId,networkMask);

        IPv4Address subnet1NetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address subnet1kMask = IPAddress.parseIPv4("255.255.255.128");
        Network subnet1 = new Network(null, subnet1NetId,subnet1kMask);

        IPv4Address subnet2NetId = IPAddress.parseIPv4("192.168.178.64");
        IPv4Address subnet2kMask = IPAddress.parseIPv4("255.255.255.192");
        Network subnet2 = new Network(null, subnet2NetId,subnet2kMask);

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
        Network network = new Network(null, networkNetId,networkMask);

        //when
        network.splitBySize(62);

        //then
        assertThat(network.getSubnets().size(), is(4));
    }

    @Test
    public void shouldCreateSubnetInsideFirstGap() throws Exception {
        //given
        IPv4Address networkNetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address networkMask = IPAddress.parseIPv4("255.255.255.0");
        Network network = new Network(null, networkNetId,networkMask);

        IPv4Address subnet1NetId = IPAddress.parseIPv4("192.168.178.0");
        IPv4Address subnet1kMask = IPAddress.parseIPv4("255.255.255.192");
        Network subnet1 = new Network(null, subnet1NetId,subnet1kMask);

        IPv4Address subnet2NetId = IPAddress.parseIPv4("192.168.178.64");
        IPv4Address subnet2kMask = IPAddress.parseIPv4("255.255.255.192");
        Network subnet2 = new Network(null, subnet2NetId,subnet2kMask);

        IPv4Address subnet3NetId = IPAddress.parseIPv4("192.168.178.128");
        IPv4Address subnet3kMask = IPAddress.parseIPv4("255.255.255.192");
        Network subnet3 = new Network(null, subnet3NetId,subnet3kMask);

        network.addSubnet(subnet1);
        network.addSubnet(subnet3);

        //when
        network.addSubnet(62);

        //then
        assertThat(network.getSubnets(), hasItem(subnet2));
    }

    @Test
    public void shouldReturnValidNetwork() {
        //given
        String net1input = "0.0.0.0/0";
        String net2input = "10.0.0.0/24";
        String net3input = "255.255.255.255/32";
        String net4input = "192.168.165.4/27";
        String net5input = "127.0.0.1/8";

        //when
        Network net1Result =  Network.parse(net1input, null);
        Network net2Result =  Network.parse(net2input, null);
        Network net3Result =  Network.parse(net3input, null);
        Network net4Result =  Network.parse(net4input, null);
        Network net5Result =  Network.parse(net5input, null);

        //then
        assertThat(net1Result.toString(), is("0.0.0.0/0"));
        assertThat(net2Result.toString(), is("10.0.0.0/24"));
        assertThat(net3Result.toString(), is("255.255.255.255/32"));
        assertThat(net4Result.toString(), is("192.168.165.0/27"));
        assertThat(net5Result.toString(), is("127.0.0.0/8"));
    }

    @Test
    public void shouldCalculateTheLength(){
        IPv4Address netId = IPAddress.parseIPv4("0.0.0.0");

        for (int prefix = 32 ; prefix>= 1; prefix--){
            try{
                Network network = new Network(null, netId,NetUtils.prefixToMask(prefix));
                assertThat(network.getAmountIpAddresses(), is((long)(Math.pow(2,32-prefix))));
            }catch (OutOfMemoryError e){
                System.out.println("OutOfMemoryError for prefix:"+prefix);
                return;
            }


        }

    }
}
