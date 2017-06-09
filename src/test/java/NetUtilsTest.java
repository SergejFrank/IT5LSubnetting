import de.itech.netcalc.net.*;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class NetUtilsTest {
    @Test
    public void isInSubnetIPv6Test() {
        IPv6Address iPv6Address1a = IPAddress.parseIPv6("2001:db8::");
        IPv6Address iPv6Address1b = IPAddress.parseIPv6("2001:db8::c6:12:1");
        IPv6Address iPv6Address2a = IPAddress.parseIPv6("2001:db8:bb89:36aa::");
        IPv6Address iPv6Address2b = IPAddress.parseIPv6("2001:db8:bb89:36aa::34");
        IPv6Address iPv6Address3a = IPAddress.parseIPv6("2001:db8::");
        IPv6Address iPv6Address3b = IPAddress.parseIPv6("2001:db7:b45::77ac");
        IPv6Address iPv6Address4a = IPAddress.parseIPv6("2001:db8::");
        IPv6Address iPv6Address4b = IPAddress.parseIPv6("2001:db8:5::");
        assertThat(NetUtils.isInSubnet(iPv6Address1a, 32, iPv6Address1b), is(true));
        assertThat(NetUtils.isInSubnet(iPv6Address2a, 64, iPv6Address2b), is(true));
        assertThat(NetUtils.isInSubnet(iPv6Address3a, 32, iPv6Address3b), is(false));
        assertThat(NetUtils.isInSubnet(iPv6Address4a, 32, iPv6Address4b), is(true));
    }

    @Test
    public void maskToPrefixTest(){
        IPv4Address mask1 = new IPv4Address(255, 255, 255, 0);
        IPv4Address mask2 = new IPv4Address(255, 128, 0, 0);
        IPv4Address mask3 = new IPv4Address(255, 255, 255, 255);
        IPv4Address mask4 = new IPv4Address(0, 0, 0, 0);

        assertThat(NetUtils.maskToPrefix(mask1), is(24));
        assertThat(NetUtils.maskToPrefix(mask2), is(9));
        assertThat(NetUtils.maskToPrefix(mask3), is(32));
        assertThat(NetUtils.maskToPrefix(mask4), is(0));
    }
}