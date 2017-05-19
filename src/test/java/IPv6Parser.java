
import de.itech.netcalc.IPv6Address;
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
                    //// TODO: 19.05.17 add more Testcases
            });
        }

        @Test
        public void shouldValidate(){
            assertEquals(expected,IPv6Address.isValid(in));
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
                    {"::1", "0000:0000:0000:0000:0000:0000:0000:0001"},
                    //// TODO: 19.05.17 add more Testcases
            });
        }

        @Test
        public void convert(){
            IPv6Address ip = IPv6Address.parse(in);
            assertEquals(expected, ip.toString());
        }
    }
}
