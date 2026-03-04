import org.junit.Test;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BeklemishevWormTest {

    // ===== repeat tests =====

    @Test public void testRepeatBaseCases() {
        Rope r1 = new StringRope("hello");
        Rope r2 = BeklemishevWorm.repeat(r1, 0);
        assertEquals(0, r2.length());
        Rope r3 = BeklemishevWorm.repeat(r1, 1);
        assertEquals(r1, r3);
        Rope r4 = BeklemishevWorm.repeat(r1, 2);
        assertEquals(new StringRope("hellohello"), r4);
    }

    @Test public void testRepeatLarge() {
        Rope r1 = new StringRope("hello");
        Rope r5 = BeklemishevWorm.repeat(r1, 200_000_000);
        assertEquals(1_000_000_000, r5.length());
        Random rng = new Random(12345);
        for(int i = 0; i < 100; i++) {
            int idx = rng.nextInt(r5.length());
            assertEquals("hello".charAt(idx % 5), r5.charAt(idx));
        }
    }

    @Test public void testRepeatEmptyRope() {
        Rope r6 = new StringRope("");
        Rope r7 = BeklemishevWorm.repeat(r6, 100_000_000);
        assertEquals(0, r7.length());
    }

    @Test public void testRepeatOddCount() {
        // Odd n exercises the "concatenate once more with original" branch
        Rope xy = new StringRope("xy");
        Rope r = BeklemishevWorm.repeat(xy, 7);
        assertEquals(14, r.length());
        assertEquals(new StringRope("xyxyxyxyxyxyxy"), r);
    }

    @Test public void testRepeatConcatRopeInput() {
        // Ensure repeat works when input is itself a ConcatRope
        Rope c = new ConcatRope(new StringRope("ab"), new StringRope("cd"));
        Rope r = BeklemishevWorm.repeat(c, 3);
        assertEquals(12, r.length());
        assertEquals(new StringRope("abcdabcdabcd"), r);
    }

    @Test public void testRepeatSingleChar() {
        Rope one = new StringRope("x");
        Rope r = BeklemishevWorm.repeat(one, 1_000_000);
        assertEquals(1_000_000, r.length());
        assertEquals('x', r.charAt(0));
        assertEquals('x', r.charAt(999_999));
    }

    // ===== wormStep tests =====

    @Test public void testWormStep() {
        Rope r1 = new StringRope("123");
        assertEquals("12222", BeklemishevWorm.wormStep(r1, 2).toString());
        Rope r2 = new StringRope("20202");
        assertEquals("2020111111", BeklemishevWorm.wormStep(r2, 5).toString());
        Rope r3 = new StringRope("321");
        assertEquals("320320320320320320320", BeklemishevWorm.wormStep(r3, 6).toString());
        Rope r4 = new StringRope("8");
        assertEquals("7777", BeklemishevWorm.wormStep(r4, 3).toString());
        Rope r5 = new StringRope("3042");
        assertEquals("30414141", BeklemishevWorm.wormStep(r5, 2).toString());
        Rope r6 = new StringRope("4242");
        assertEquals("424142414241", BeklemishevWorm.wormStep(r6, 2).toString());
        Rope r7 = new StringRope("1551");
        assertEquals("155015501550155015501550", BeklemishevWorm.wormStep(r7, 5).toString());
        Rope r8 = new StringRope("747");
        assertEquals("746666666", BeklemishevWorm.wormStep(r8, 6).toString());
    }

    @Test public void testWormStepHeadZero() {
        // Head is 0: just chop it off
        assertEquals("", BeklemishevWorm.wormStep(new StringRope("0"), 5).toString());
        assertEquals("505", BeklemishevWorm.wormStep(new StringRope("5050"), 2).toString());
        assertEquals("1234", BeklemishevWorm.wormStep(new StringRope("12340"), 99).toString());
    }

    @Test public void testWormStepSingleDigit() {
        // Single nonzero digit: k=-1, base is (head-1), tail is entire base
        assertEquals("00", BeklemishevWorm.wormStep(new StringRope("1"), 1).toString());
        assertEquals("00000", BeklemishevWorm.wormStep(new StringRope("1"), 4).toString());
        assertEquals("88", BeklemishevWorm.wormStep(new StringRope("9"), 1).toString());
        assertEquals("8888", BeklemishevWorm.wormStep(new StringRope("9"), 3).toString());
    }

    @Test public void testWormStepUniformDigits() {
        // All digits equal: k=-1, entire base is the repeated tail
        assertEquals("332332332", BeklemishevWorm.wormStep(new StringRope("333"), 2).toString());
        assertEquals("554554554554554", BeklemishevWorm.wormStep(new StringRope("555"), 4).toString());
    }

    @Test public void testWormStepDescending() {
        // Strictly descending: every digit >= head, so k=-1
        assertEquals("986986986", BeklemishevWorm.wormStep(new StringRope("987"), 2).toString());
        assertEquals("530530530530", BeklemishevWorm.wormStep(new StringRope("531"), 3).toString());
    }

    @Test public void testWormStepAscending() {
        // Strictly ascending: k is always second-to-last position
        assertEquals("13444", BeklemishevWorm.wormStep(new StringRope("135"), 2).toString());
        assertEquals("0122222", BeklemishevWorm.wormStep(new StringRope("0123"), 3).toString());
    }

    // ===== iterateWorm tests =====

    @Test public void testIterateWorm() {
        assertEquals(24, BeklemishevWorm.iterateWorm("2031", 4));
        assertEquals(31, BeklemishevWorm.iterateWorm("313", 4));
        assertEquals(1, BeklemishevWorm.iterateWorm("2", 50));
        assertEquals(0, BeklemishevWorm.iterateWorm("2", 51));
        assertEquals(40470, BeklemishevWorm.iterateWorm("007", 10));
        assertEquals(21264, BeklemishevWorm.iterateWorm("123", 50));
        assertEquals(72, BeklemishevWorm.iterateWorm("202020", 100));
        assertEquals(5315864, BeklemishevWorm.iterateWorm("12321", 200));
        assertEquals(362889, BeklemishevWorm.iterateWorm("8", 10));
        assertEquals(1150, BeklemishevWorm.iterateWorm("4321", 10));
        assertEquals(7307240, BeklemishevWorm.iterateWorm("420", 42));
        assertEquals(750375878, BeklemishevWorm.iterateWorm("7", 30));
    }

    @Test public void testIterateWormZeroRounds() {
        // Zero rounds: worm is untouched, return its original length
        assertEquals(5, BeklemishevWorm.iterateWorm("12345", 0));
        assertEquals(1, BeklemishevWorm.iterateWorm("0", 0));
    }

    @Test public void testIterateWormAllZeros() {
        // All-zero worms: each step chops one zero, terminates in exactly n steps
        assertEquals(0, BeklemishevWorm.iterateWorm("0", 1));
        assertEquals(0, BeklemishevWorm.iterateWorm("0", 100));
        assertEquals(1, BeklemishevWorm.iterateWorm("000", 2));
        assertEquals(0, BeklemishevWorm.iterateWorm("000", 3));
        assertEquals(1, BeklemishevWorm.iterateWorm("00000", 4));
        assertEquals(0, BeklemishevWorm.iterateWorm("00000", 5));
        assertEquals(0, BeklemishevWorm.iterateWorm("00000", 10));
    }

    @Test public void testIterateWormSingleDigits() {
        // Single digit "1": w0="1" -> w1="00" -> w2="0" -> w3="" (terminates in 3 steps)
        assertEquals(0, BeklemishevWorm.iterateWorm("1", 3));
        assertEquals(0, BeklemishevWorm.iterateWorm("1", 10));
        assertEquals(28, BeklemishevWorm.iterateWorm("3", 5));
        assertEquals(720, BeklemishevWorm.iterateWorm("9", 5));
    }

    @Test public void testIterateWormLeadingZeros() {
        // Leading zeros: they just get stripped as the worm shrinks from the right
        assertEquals(1, BeklemishevWorm.iterateWorm("0001", 5));
        assertEquals(0, BeklemishevWorm.iterateWorm("010", 10));
    }

    @Test public void testIterateWormPalindromic() {
        assertEquals(93, BeklemishevWorm.iterateWorm("121", 10));
        assertEquals(792, BeklemishevWorm.iterateWorm("12321", 10));
    }

    @Test public void testIterateWormTerminationBoundary() {
        // Worm "2" terminates at step 52: verify approach to termination
        assertEquals(3, BeklemishevWorm.iterateWorm("2", 48));
        assertEquals(2, BeklemishevWorm.iterateWorm("2", 49));
        assertEquals(0, BeklemishevWorm.iterateWorm("2", 52));
    }

    @Test public void testIterateWormLargerRuns() {
        // Longer iterations that stress the rope representation
        assertEquals(521661, BeklemishevWorm.iterateWorm("2031", 150));
        assertEquals(54174, BeklemishevWorm.iterateWorm("321", 30));
        assertEquals(4420080, BeklemishevWorm.iterateWorm("9", 15));
        assertEquals(2218173, BeklemishevWorm.iterateWorm("54321", 20));
    }
}