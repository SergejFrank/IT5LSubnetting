import de.itech.netcalc.IPAddress;
import de.itech.netcalc.IPv4Address;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class IPv4Tests {

    @Test
    public void ipsShouldBeComparable(){

        //given
        IPv4Address ip1 = IPAddress.parseIPv4("0.0.0.0");
        IPv4Address ip2 = IPAddress.parseIPv4("255.255.255.255");

        IPv4Address ip3 = IPAddress.parseIPv4("192.168.0.1");
        IPv4Address ip4 = IPAddress.parseIPv4("192.168.0.0");

        //when
        boolean ip1GTip2 = ip1.isGreaterThan(ip2);
        boolean ip3GTip4 = ip3.isGreaterThan(ip4);

        //then
        assertThat(ip1GTip2, is(false));
        assertThat(ip3GTip4, is(true));

    }
}
