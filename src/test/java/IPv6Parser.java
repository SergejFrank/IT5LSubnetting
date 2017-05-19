import de.itech.netcalc.IPv6Address;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertEquals;

@RunWith(value = Parameterized.class)
public class IPv6Parser {

    @Parameter(value = 0)
    public String in;

    @Parameter(value = 1)
    public String expected;


    @Parameters(name = "convert-{index}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"fe80:0:0:0:5b9e:86db:8874:6456", "fe80:0000:0000:0000:5b9e:86db:8874:6456"},
                {"::1", "0000:0000:0000:0000:0000:0000:0000:0001"},
        });
    }

    @Test
    public void convert(){
        IPv6Address ip = IPv6Address.parse(in);
        assertEquals(expected, ip.toString());
    }

}
