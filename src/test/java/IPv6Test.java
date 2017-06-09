import de.itech.netcalc.net.Format;
import de.itech.netcalc.net.IPv6Address;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class IPv6Test {

    @Test
    public void shouldCreateIPv6AddressWithOnlyNetworkId(){
        long networkId = 255;
        IPv6Address address = IPv6Address.getAddressWithRandomHost(networkId);

        assertThat(address.getNetworkId(), is(255L));
    }

    @Test
    public void shouldCreateIPv6AddressWithBothIds(){
        long networkId = 255, interfaceId = 256;
        IPv6Address address = new IPv6Address(networkId, interfaceId);

        assertThat(address.getNetworkId(), is(255L));
        assertThat(address.getInterfaceId(), is(256L));
    }

    @Test
    public void returnToStringForIPv6Address() {
        IPv6Address address = new IPv6Address(0, 0);
        IPv6Address address1 = new IPv6Address(255, 255);
        IPv6Address address2 = new IPv6Address(Long.MAX_VALUE, Long.MAX_VALUE);
        IPv6Address address3 = new IPv6Address(-1 , -1);

        assertThat("0000:0000:0000:0000:0000:0000:0000:0000", is(address.toString()));
        assertThat("0000:0000:0000:00ff:0000:0000:0000:00ff", is(address1.toString()));
        assertThat("7fff:ffff:ffff:ffff:7fff:ffff:ffff:ffff", is(address2.toString()));
        assertThat("ffff:ffff:ffff:ffff:ffff:ffff:ffff:ffff", is(address3.toString()));
    }

    @Test
    public void shouldCorrectlyShorthandAddress(){
        IPv6Address address = new IPv6Address(0, 0);
        IPv6Address address1 = new IPv6Address(255, 255);
        IPv6Address address2 = new IPv6Address(Long.MAX_VALUE, Long.MAX_VALUE);
        IPv6Address address3 = new IPv6Address(0xFFFF_FFFF_0000_0000L, 0x0000_0000_FFFF_FFFFL);
        IPv6Address address4 = new IPv6Address(0xFFFF_0000_FFFF_0000L, 0x0000_0000_FFFF_FFFFL);

        assertThat(Format.format(address,Format.IPv6Format.SHORTHAND), is("::"));
        assertThat(Format.format(address1, Format.IPv6Format.SHORTHAND), is("0:0:0:ff::ff"));
        assertThat(Format.format(address2, Format.IPv6Format.SHORTHAND), is("7fff:ffff:ffff:ffff:7fff:ffff:ffff:ffff"));
        assertThat(Format.format(address3, Format.IPv6Format.SHORTHAND), is("ffff:ffff::ffff:ffff"));
        assertThat(Format.format(address4, Format.IPv6Format.SHORTHAND), is("ffff:0:ffff::ffff:ffff"));
    }
}