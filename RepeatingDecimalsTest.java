import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RepeatingDecimalsTest {

    // --- Terminating decimals ---

    @Test public void testTerminating() {
        // Denominators whose only prime factors are 2 and 5
        assertArrayEquals(new String[]{"0.5", "0"}, RepeatingDecimals.decimals(1, 2));
        assertArrayEquals(new String[]{"0.25", "0"}, RepeatingDecimals.decimals(1, 4));
        assertArrayEquals(new String[]{"0.2", "0"}, RepeatingDecimals.decimals(1, 5));
        assertArrayEquals(new String[]{"0.125", "0"}, RepeatingDecimals.decimals(1, 8));
        assertArrayEquals(new String[]{"0.1", "0"}, RepeatingDecimals.decimals(1, 10));
        assertArrayEquals(new String[]{"0.0625", "0"}, RepeatingDecimals.decimals(1, 16));
        assertArrayEquals(new String[]{"0.04", "0"}, RepeatingDecimals.decimals(1, 25));
        assertArrayEquals(new String[]{"0.375", "0"}, RepeatingDecimals.decimals(3, 8));
        assertArrayEquals(new String[]{"0.99", "0"}, RepeatingDecimals.decimals(99, 100));
    }

    @Test public void testTerminatingLongInitial() {
        // 1/256 = 1/2^8: long terminating expansion
        assertArrayEquals(new String[]{"0.00390625", "0"}, RepeatingDecimals.decimals(1, 256));
        // 1/32 = 1/2^5
        assertArrayEquals(new String[]{"0.03125", "0"}, RepeatingDecimals.decimals(1, 32));
        // 1/80000 = 1/(2^7 * 5^4): seven digits after decimal
        assertArrayEquals(new String[]{"0.0000125", "0"}, RepeatingDecimals.decimals(1, 80000));
    }

    // --- Pure repeating (no initial part beyond "0.") ---

    @Test public void testPureRepeating() {
        // Denominator coprime to 10: repeat starts right after "0."
        assertArrayEquals(new String[]{"0.", "3"}, RepeatingDecimals.decimals(1, 3));
        assertArrayEquals(new String[]{"0.", "6"}, RepeatingDecimals.decimals(2, 3));
        assertArrayEquals(new String[]{"0.", "142857"}, RepeatingDecimals.decimals(1, 7));
        assertArrayEquals(new String[]{"0.", "857142"}, RepeatingDecimals.decimals(6, 7));
        assertArrayEquals(new String[]{"0.", "1"}, RepeatingDecimals.decimals(1, 9));
        assertArrayEquals(new String[]{"0.", "09"}, RepeatingDecimals.decimals(1, 11));
        assertArrayEquals(new String[]{"0.", "076923"}, RepeatingDecimals.decimals(1, 13));
        assertArrayEquals(new String[]{"0.", "428571"}, RepeatingDecimals.decimals(3, 7));
    }

    // --- Mixed: initial part + repeating block ---

    @Test public void testMixedInitialAndRepeating() {
        assertArrayEquals(new String[]{"0.1", "6"}, RepeatingDecimals.decimals(1, 6));
        assertArrayEquals(new String[]{"0.0", "675"}, RepeatingDecimals.decimals(5, 74));
        assertArrayEquals(new String[]{"0.97", "2"}, RepeatingDecimals.decimals(70, 72));
        assertArrayEquals(new String[]{"0.3", "789"}, RepeatingDecimals.decimals(631, 1665));
        assertArrayEquals(new String[]{"0.8", "144"}, RepeatingDecimals.decimals(452, 555));
        assertArrayEquals(new String[]{"0.123", "579"}, RepeatingDecimals.decimals(123456, 999000));
        assertArrayEquals(new String[]{"0.", "740"}, RepeatingDecimals.decimals(20, 27));
        assertArrayEquals(new String[]{"0.", "90"}, RepeatingDecimals.decimals(10, 11));
    }

    // --- Long initial part before repeating block ---

    @Test public void testLongInitialPart() {
        // 1/112 = 1/(2^4 * 7): 4 initial digits, then 6-digit repeat
        assertArrayEquals(new String[]{"0.0089", "285714"},
                RepeatingDecimals.decimals(1, 112));

        // 1/6000 = 1/(2^4 * 3 * 5^3): 4 initial digits, repeat "6"
        assertArrayEquals(new String[]{"0.0001", "6"},
                RepeatingDecimals.decimals(1, 6000));

        // 1/7000 = 1/(2^3 * 5^3 * 7): 3 initial digits, repeat "142857"
        assertArrayEquals(new String[]{"0.000", "142857"},
                RepeatingDecimals.decimals(1, 7000));

        // 1/13000 = 1/(2^3 * 5^3 * 13): 3 initial digits, repeat "076923"
        assertArrayEquals(new String[]{"0.000", "076923"},
                RepeatingDecimals.decimals(1, 13000));

        // 1/700000 = 1/(2^5 * 5^5 * 7): 5 initial digits, repeat "142857"
        assertArrayEquals(new String[]{"0.00000", "142857"},
                RepeatingDecimals.decimals(1, 700000));

        // 1/1875 = 1/(3 * 5^4): 4 initial digits, repeat "3"
        assertArrayEquals(new String[]{"0.0005", "3"},
                RepeatingDecimals.decimals(1, 1875));
    }

    // --- Long repeating blocks ---

    @Test public void testLongRepeatingBlock() {
        // 1/29: block of 28 digits (mentioned in the spec)
        String[] r29 = RepeatingDecimals.decimals(1, 29);
        assertEquals("0.", r29[0]);
        assertEquals(28, r29[1].length());
        assertEquals("0344827586206896551724137931", r29[1]);

        // 1/47: block of 46 digits
        String[] r47 = RepeatingDecimals.decimals(1, 47);
        assertEquals("0.", r47[0]);
        assertEquals(46, r47[1].length());
        assertEquals("0212765957446808510638297872340425531914893617", r47[1]);

        // 1/97: block of 96 digits
        String[] r97 = RepeatingDecimals.decimals(1, 97);
        assertEquals("0.", r97[0]);
        assertEquals(96, r97[1].length());
    }

    @Test public void testLongInitialAndLongRepeat() {
        // 1/1552 = 1/(2^4 * 97): 4-digit initial, 96-digit repeat
        String[] r = RepeatingDecimals.decimals(1, 1552);
        assertEquals("0.0006", r[0]);
        assertEquals(96, r[1].length());

        // 41/(5000*7*101) = 41/3535000: long initial + repeat
        String[] r2 = RepeatingDecimals.decimals(41, 5000 * 7 * 101);
        assertEquals("0.0000", r2[0]);
        assertEquals("115983026874", r2[1]);
    }

    // --- Minimal period (no doubled periods) ---

    @Test public void testMinimalPeriod() {
        // 1/9901: repeat is "000100999899" (12 digits), NOT a doubled 6-digit period
        String[] r = RepeatingDecimals.decimals(1, 9901);
        assertEquals("0.", r[0]);
        assertEquals("000100999899", r[1]);
    }

    // --- Famous fraction ---

    @Test public void testFamous998001() {
        // 1/998001 produces all three-digit numbers except 998 in sequence
        String[] r = RepeatingDecimals.decimals(1, 998001);
        assertEquals("0.", r[0]);
        // Repeat block should be 2997 digits (999 three-digit groups, minus "998")
        assertEquals(2997, r[1].length());
        // Starts with "000001002003..."
        assertEquals("000", r[1].substring(0, 3));
        assertEquals("001", r[1].substring(3, 6));
        assertEquals("002", r[1].substring(6, 9));
        // Ends with "999"
        assertEquals("999", r[1].substring(2994, 2997));
        // "998" should NOT appear at position 998*3
        assertArrayEquals(new String[]{"997"}, new String[]{r[1].substring(2991, 2994)});
    }

    // --- Existing explicit tests ---

    @Test public void testExplicit() {
        assertArrayEquals(new String[]{"0.0", "675"}, RepeatingDecimals.decimals(5, 74));
        assertArrayEquals(new String[]{"0.1", "6"}, RepeatingDecimals.decimals(1, 6));
        assertArrayEquals(new String[]{"0.", "428571"}, RepeatingDecimals.decimals(3, 7));
        assertArrayEquals(new String[]{"0.", "000999"}, RepeatingDecimals.decimals(1, 1001));
        assertArrayEquals(new String[]{"0.", "012345679"}, RepeatingDecimals.decimals(1, 81));
        assertArrayEquals(new String[]{"0.00", "012"}, RepeatingDecimals.decimals(1, 8325));
        assertArrayEquals(new String[]{"0.", "157894736842105263"}, RepeatingDecimals.decimals(3, 19));

        String[] r7 = RepeatingDecimals.decimals(1, 61);
        assertEquals("0.", r7[0]);
        assertEquals("016393442622950819672131147540983606557377049180327868852459", r7[1]);

        assertArrayEquals(new String[]{"0.01", "0"}, RepeatingDecimals.decimals(1, 100));
        assertArrayEquals(new String[]{"0.00390625", "0"}, RepeatingDecimals.decimals(1, 256));
        assertArrayEquals(new String[]{"0.00561", "3"}, RepeatingDecimals.decimals(421, 3*2*2*2*5*5*5*5*5));
        assertArrayEquals(new String[]{"0.35970", "5"}, RepeatingDecimals.decimals(64747, 180000));
    }

    // --- CRC mass tests ---

    @Test public void massTestHundred() {
        massTest(100, 435856192L);
    }

    @Test public void massTestTenThousand() {
        massTest(10000, 2945858635L);
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
            b += rng.nextInt(4) + 1;
        }
        assertEquals(expected, check.getValue());
    }
}