import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class RepeatingDecimalsTest {

    @Test public void testExplicit() {

        String[] r0 = RepeatingDecimals.decimals(5, 74);
        String[] e0 = {"0.0", "675"};
        assertArrayEquals(e0, r0);

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

        String[] r5 = RepeatingDecimals.decimals(1, 8325);
        String[] e5 = {"0.00", "012"};
        assertArrayEquals(e5, r5);

        String[] r6 = RepeatingDecimals.decimals(3, 19);
        String[] e6 = {"0.", "157894736842105263"};
        assertArrayEquals(e6, r6);

        String[] r7 = RepeatingDecimals.decimals(1, 61);
        String[] e7 = {"0.", "016393442622950819672131147540983606557377049180327868852459"};
        assertArrayEquals(e7, r7);

        String[] r8 = RepeatingDecimals.decimals(1, 97);
        String[] e8 = {"0.", "010309278350515463917525773195876288659793814432989690721649484536082474226804123711340206185567"};
        assertArrayEquals(e8, r8);

        String[] r9 = RepeatingDecimals.decimals(1, 100);
        String[] e9 = {"0.01", "0"};
        assertArrayEquals(e9, r9);

        String[] r10 = RepeatingDecimals.decimals(1, 256);
        String[] e10 = {"0.00390625", "0"};
        assertArrayEquals(e10, r10);

        String[] r11 = RepeatingDecimals.decimals(70, 72);
        String[] e11 = {"0.97", "2"};
        assertArrayEquals(e11, r11);

        // A cute one from https://www.youtube.com/watch?v=daro6K6mym8
        String[] r12 = RepeatingDecimals.decimals(1, 998001);
        StringBuilder allBut998 = new StringBuilder();
        for(int i = 0; i < 1000; i++) {
            if(i != 998) {
                if(i < 10) { allBut998.append("00"); allBut998.append(i); }
                else if(i < 100) { allBut998.append("0"); allBut998.append(i); }
                else { allBut998.append(i); }
            }
        }
        String[] e12 = {"0.", allBut998.toString()};
        assertArrayEquals(e12, r12);

        String[] r13 = RepeatingDecimals.decimals(20, 27);
        String[] e13 = {"0.", "740"};
        assertArrayEquals(e13, r13);

        String[] r14 = RepeatingDecimals.decimals(5, 997);
        String[] e14 = {"0.", "0050150451354062186559679037111334002006018054162487462387161484453360080240722166499498495486459378134403209628886659979939819458375125376128385155466399197592778335"};
        assertArrayEquals(e14, r14);

        String[] r15 = RepeatingDecimals.decimals(452, 555);
        String[] e15 = {"0.8", "144"};
        assertArrayEquals(e15, r15);

        String[] r16 = RepeatingDecimals.decimals(631, 1665);
        String[] e16 = {"0.3", "789"};
        assertArrayEquals(e16, r16);

        String[] r17 = RepeatingDecimals.decimals(123456, 999000);
        String[] e17 = {"0.123", "579"};
        assertArrayEquals(e17, r17);

        String[] r18 = RepeatingDecimals.decimals(421, 3*2*2*2*5*5*5*5*5);
        String[] e18 = {"0.00561", "3"};
        assertArrayEquals(e18, r18);

    }

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
