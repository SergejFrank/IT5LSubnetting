
import de.itech.netcalc.net.IPAddress;
import de.itech.netcalc.net.IPv6Address;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(Enclosed.class)
public class IPv6Parser {

    @RunWith(Parameterized.class)
    public static class ValidateeTests extends IPv6Parser{
        @Parameter(value = 0)
        public String in;

        @Parameter(value = 1)
        public boolean expected;


        @Parameters(name = "test-{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, false},
                    {"fe80:0:0:0:5b9e:86db:8874:6456", true},
                    {"::255.255.255.255", true},
                    {"2001:db8:3:4::192.0.2.33", true},
                    {"9d38:90d7:10cc:7ab4:5.5.5.5", false},
                    {"::ffff:1.2.3.4", true},
                    {"::ffff:192.168.1.4", true},
                    {"2001:db8::", true},
                    {"::", true}
            });
        }

        @Test
        public void shouldValidate(){
            assertEquals(expected, IPAddress.isValidIPv6(in));
        }
    }

    @RunWith(Parameterized.class)
    public static class ValidatePrefixTests extends IPv6Parser{
        @Parameter(value = 0)
        public String in;

        @Parameter(value = 1)
        public boolean expected;


        @Parameters(name = "test-{0}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {null, false},
                    {"fe80:0:0:0:5b9e:86db:8874:6456/64", true},
                    {"::255.255.255.255/24", true},
                    {"2001:db8:3:4::192.0.2.33/0", true},
                    {"9d38:90d7:10cc:7ab4:5.5.5.5/65", false},
                    {"::ffff:1.2.3.4/5", true},
                    {"::ffff:192.168.1.4/33", true},
                    {"2001:db8::/0", true},
                    {"::/0", true},
                    {"2001:db8:34fa::abcd/934", false}
            });
        }

        @Test
        public void shouldValidate(){
            assertEquals(expected, IPAddress.isValidIPv6WithPrefix(in, 64));
        }
    }

    @RunWith(Parameterized.class)
    public static class ParseTests extends IPv6Parser{
        @Parameter(value = 0)
        public String in;

        @Parameter(value = 1)
        public String expected;


        @Parameters(name = "convert-{index}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"fe80:0:0:0:5b9e:86db:8874:6456", "fe80:0000:0000:0000:5b9e:86db:8874:6456"},
                    {"::1", "0000:0000:0000:0000:0000:0000:0000:0001"},
                    {"2001:0:9d38:90d7:10cc:7ab4:72a4:2d3d", "2001:0000:9d38:90d7:10cc:7ab4:72a4:2d3d"},
                    {"2001:0:9d38:90d7::2d3d", "2001:0000:9d38:90d7:0000:0000:0000:2d3d"},
                    {"ffff::0:1", "ffff:0000:0000:0000:0000:0000:0000:0001"},
                    {"::", "0000:0000:0000:0000:0000:0000:0000:0000"},
                    {"2001:db8::", "2001:0db8:0000:0000:0000:0000:0000:0000"}
            });
        }

        @Test
        public void convert() {
            IPv6Address ip = IPAddress.parseIPv6(in);
            assertEquals(expected, ip.toString());
        }
    }

    @RunWith(Parameterized.class)
    public static class ParsePrefixTests extends IPv6Parser{
        @Parameter(value = 0)
        public String in;

        @Parameter(value = 1)
        public int expected;


        @Parameters(name = "convert-{index}")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {"fe80:0:0:0:5b9e:86db:8874:6456/24", 24},
                    {"::1/0", 0},
                    {"2001:0:9d38:90d7:10cc:7ab4:72a4:2d3d/64", 64},
                    {"ffff::0:1/128", 128}
            });
        }

        @Test
        public void convert() {
            assertEquals(expected, IPAddress.parseIPv6Prefix(in));
        }
    }
}
