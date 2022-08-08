import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.Arrays;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RepeatingDecimalsTest {

    @Test public void testExplicit() {
        String[] r1 = RepeatingDecimals.decimals(1, 6);
        String[] e1 = {"0.1", "6"};
        assertArrayEquals(e1, r1);

        String[] r2 = RepeatingDecimals.decimals(3, 7);
        String[] e2 = {"0.", "428571"};
        assertArrayEquals(e2, r2);

        String[] r3 = RepeatingDecimals.decimals(1, 1001);
        String[] e3 = {"0.", "000999"};
        assertArrayEquals(e3, r3);

        String[] r4 = RepeatingDecimals.decimals(1, 81);
        String[] e4 = {"0.", "012345679"};
        assertArrayEquals(e4, r4);

        String[] r5 = RepeatingDecimals.decimals(1, 23);
        String[] e5 = {"0.", "0434782608695652173913"};
        assertArrayEquals(e5, r5);

        String[] r6 = RepeatingDecimals.decimals(3, 19);
        String[] e6 = {"0.", "157894736842105263"};
        assertArrayEquals(e6, r6);

        String[] r7 = RepeatingDecimals.decimals(1, 44);
        String[] e7 = {"0.02", "27"};
        assertArrayEquals(e6, r6);

        String[] r8 = RepeatingDecimals.decimals(1, 97);
        String[] e8 = {"0.", "010309278350515463917525773195876288659793814432989690721649484536082474226804123711340206185567"};
        assertArrayEquals(e8, r8);

        String[] r9 = RepeatingDecimals.decimals(1, 100);
        String[] e9 = {"0.01", "0"};
        assertArrayEquals(e9, r9);

        String[] r10 = RepeatingDecimals.decimals(1, 256);
        String[] e10 = {"0.00390625", "0"};
        assertArrayEquals(e10, r10);
    }

    @Test public void massTestHundred() {
        massTest(100, 2300676283L);
    }

    @Test public void massTestTenThousand() {
        massTest(10000, 2465431047L);
    }

    private void massTest(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        int b = 3;
        while(b < n) {
            int a = 1;
            while(a < b) {
                String[] result = RepeatingDecimals.decimals(a, b);
                for(int i = 0; i < result[0].length(); i++) {
                    check.update(result[0].charAt(i));
                }
                for(int i = 0; i < result[1].length(); i++) {
                    check.update(result[1].charAt(i));
                }
                a += rng.nextInt(b - a + 2) + 1;
            }
            b++;
        }
        assertEquals(expected, check.getValue());
    }
}
