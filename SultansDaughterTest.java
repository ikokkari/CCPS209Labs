import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SultansDaughterTest {

    @Test public void testSultansDaughter() {
        assertEquals("555666777", SultansDaughter.sultansDaughter("15556667772"));
        assertEquals("555666777", SultansDaughter.sultansDaughter("15556667772"));
        assertEquals("987987", SultansDaughter.sultansDaughter("319872"));
        assertEquals("456456456456", SultansDaughter.sultansDaughter("3314562"));
        assertEquals("8282", SultansDaughter.sultansDaughter("44431282"));
        assertEquals("738", SultansDaughter.sultansDaughter("5147382"));
        assertEquals("1991912", SultansDaughter.sultansDaughter("619919122"));
        assertEquals("298210", SultansDaughter.sultansDaughter("71982102"));

        // Some undefined expressions for Sultan's daughter.
        assertNull(SultansDaughter.sultansDaughter("55428686861"));
        assertNull(SultansDaughter.sultansDaughter("55429001"));
        assertNull(SultansDaughter.sultansDaughter("3971478514722"));
        assertNull(SultansDaughter.sultansDaughter("81932"));
        assertNull(SultansDaughter.sultansDaughter("12345"));
        assertNull(SultansDaughter.sultansDaughter("1"));
        assertNull(SultansDaughter.sultansDaughter("12")); // Quoted string must be nonempty
        assertNull(SultansDaughter.sultansDaughter("2"));
        assertNull(SultansDaughter.sultansDaughter("3"));
        assertNull(SultansDaughter.sultansDaughter("4"));
        assertNull(SultansDaughter.sultansDaughter("5"));
        assertNull(SultansDaughter.sultansDaughter("6"));
        assertNull(SultansDaughter.sultansDaughter("7"));

        // Two given known fixed points for Sultan's daughter.
        assertEquals("47536414753642", SultansDaughter.sultansDaughter("47536414753642"));
        assertEquals("47431474312", SultansDaughter.sultansDaughter("47431474312"));
    }

    private static String createSultanString(int depth, Random rng, boolean allowEightNine) {
        if(depth < 1) {
            char d = "0123456789".charAt(rng.nextInt(10));
            return d + (rng.nextBoolean() ? "": createSultanString(depth-1, rng, allowEightNine));
        }
        String y = createSultanString(depth-1, rng, allowEightNine);
        int die = rng.nextInt(100);
        if(die < 30) { return "1" + y + "2"; }
        if(die < 45) { return "3" + y; }
        if(die < 60) { return "4" + y; }
        if(die < 70) { return "5" + y; }
        if(die < 80) { return "6" + y; }
        if(die < 90) { return "7" + y; }
        if(allowEightNine) {
            return (rng.nextBoolean() ? "8" : "9") + y;
        }
        else {
            return "1234567".charAt(rng.nextInt(7)) + y;
        }
    }

    @Test public void massTestSultansDaughterHundred() {
        massTestSultansDaughter(100, 3262647218L);
    }

    @Test public void massTestSultansDaughterThousand() {
        massTestSultansDaughter(1000, 2884125309L);
    }

    @Test public void massTestSultansDaughterTenThousand() {
        massTestSultansDaughter(10000, 4187249190L);
    }

    private void massTestSultansDaughter(int rounds, long expected) {
        Random rng = new Random(12345 + rounds);
        CRC32 check = new CRC32();
        for(int r = 0; r < rounds; r++) {
            int depth = rng.nextInt(3 + r/10) + 1;
            String expr = createSultanString(depth, rng, true);
            String result = SultansDaughter.sultansDaughter(expr);
            if(result != null) {
                check.update(result.length());
                for (int i = 0; i < result.length(); i++) {
                    char c = result.charAt(i);
                    assertTrue("0123456789".indexOf(c) > -1);
                    check.update(c - '0');
                }
            }
            else { check.update(1234567); }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testIterateSultansDaughter() {
        assertEquals(1, SultansDaughter.iterateSultansDaughter("663653"));
        assertEquals(2, SultansDaughter.iterateSultansDaughter("140952"));
        assertEquals(3, SultansDaughter.iterateSultansDaughter("111122"));
        assertEquals(4, SultansDaughter.iterateSultansDaughter("1171112222"));
        assertEquals(5, SultansDaughter.iterateSultansDaughter("61111679612222"));
        assertEquals(6, SultansDaughter.iterateSultansDaughter("1113151722222"));
        assertEquals(7, SultansDaughter.iterateSultansDaughter("161316111267647653667222222"));
        assertEquals(8, SultansDaughter.iterateSultansDaughter("13115616633131152222222"));

        // Playing around with the fixed points of Sultan's daughter.
        assertEquals(-1, SultansDaughter.iterateSultansDaughter("47536414753642"));
        assertEquals(-1, SultansDaughter.iterateSultansDaughter("47431474312"));
        assertEquals(-1, SultansDaughter.iterateSultansDaughter("4447431474312"));
        assertEquals(-1, SultansDaughter.iterateSultansDaughter("5647431474312"));
    }

    @Test public void massTestIterateSultansDaughterHundred() {
        massTestIterateSultansDaughter(100, 740331748L);
    }

    @Test public void massTestIterateSultansDaughterThousand() {
        massTestIterateSultansDaughter(1000, 1501637869L);
    }

    private void massTestIterateSultansDaughter(int rounds, long expected) {
        Random rng = new Random(12345 + rounds);
        CRC32 check = new CRC32();
        for(int r = 0; r < rounds; r++) {
            int depth = rng.nextInt(3 + r / 20) + 1;
            String expr = createSultanString(depth, rng, false);
            int result = SultansDaughter.iterateSultansDaughter(expr);
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }
}