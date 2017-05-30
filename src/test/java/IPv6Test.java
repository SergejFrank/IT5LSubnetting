import de.itech.netcalc.net.IPv6Address;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IPv6Test {

    @Test
    public void shouldCreateIPv6AddressWithOnlyNetworkId(){
        long networkId = 255;
        IPv6Address address = new IPv6Address(networkId);

        assertThat(address.getNetworkId(), is(255));
    }

    @Test
    public void shouldCreateIPv6AddressWithBothIds(){
        long networkId = 255, interfaceId = 256;
        IPv6Address address = new IPv6Address(networkId, interfaceId);

        assertThat(address.getNetworkId(), is(255));
        assertThat(address.getInterfaceId(), is(256));
    }

    @Test
    public void returnToStringForIPv6Address() {
        IPv6Address address = new IPv6Address(0, 0);
        IPv6Address address1 = new IPv6Address(255, 255);
        IPv6Address address2 = new IPv6Address(Long.MAX_VALUE, Long.MAX_VALUE);
        IPv6Address address3 = new IPv6Address(-1 , -1);

        assertThat("0:0:0:0:0:0:0:0", is(address.toString()));
        assertThat("0:0:0:ff:0:0:0:ff", is(address1.toString()));
        assertThat("7fff:ffff:ffff:ffff:7fff:ffff:ffff:ffff", is(address2.toString()));
        assertThat("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", is(address3.toString()));
    }

    @Test
    public void shouldCorrectlyShorthandAddress(){
        IPv6Address address = new IPv6Address(0, 0);
        IPv6Address address1 = new IPv6Address(255, 255);
        IPv6Address address2 = new IPv6Address(Long.MAX_VALUE, Long.MAX_VALUE);

        assertThat(address.toString(true), is("::"));
        assertThat(address1.toString(true), is("0:0:0:ff::ff"));
        assertThat(address2.toString(true), is("7fff:ffff:ffff:ffff:7fff:ffff:ffff:ffff"));
    }

}
