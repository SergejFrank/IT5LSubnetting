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
        IPv6Address iPv6Address3b = IPAddress.parseIPv6("2001:db8:b45::77ac");
        assertThat(NetUtils.isInSubnet(iPv6Address1a, 32, iPv6Address1b), is(true));
        assertThat(NetUtils.isInSubnet(iPv6Address2a, 64, iPv6Address2b), is(true));
        assertThat(NetUtils.isInSubnet(iPv6Address3a, 32, iPv6Address3b), is(false));
    }
}