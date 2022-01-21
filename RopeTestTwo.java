import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.HashSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RopeTestTwo {

    @Test public void testEqualsExplicit() {
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("world");
        assertEquals(r1, r1);
        assertEquals(r2, r2);
        assertFalse(r1.equals(r2));
        Rope r3 = new StringRope("helloworld");
        Rope r4 = new ConcatRope(r1, r2);
        assertEquals(r3, r4);
        Rope r5 = new SubRope(r3, 3, 8);
        Rope r6 = new StringRope("lowor");
        assertEquals(r5, r6);
        assertFalse(r1.equals(r6));
        assertFalse(r2.equals(r6));
    }

    @Test public void testCompareExplicit() {
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("hella");
        assertTrue(r1.compareTo(r2) > 0);
        assertTrue(r2.compareTo(r1) < 0);
        assertTrue(r1.compareTo(r1) == 0);
        assertTrue(r2.compareTo(r2) == 0);

        Rope r3 = new ConcatRope(r1, r2); // "hellohella"
        Rope r4 = new ConcatRope(r2, r1); // "hellahello"
        assertTrue(r1.compareTo(r3) < 0);
        assertTrue(r2.compareTo(r3) < 0);
        assertTrue(r3.compareTo(r1) > 0);
        assertTrue(r3.compareTo(r2) > 0);
        assertTrue(r3.compareTo(r4) > 0);
        assertTrue(r4.compareTo(r3) < 0);

        Rope r5 = new SubRope(r3, 0, 5); // "hello"
        Rope r6 = new SubRope(r3, 5, 9); // "hell"
        assertTrue(r5.compareTo(r6) > 0);
        assertTrue(r6.compareTo(r5) < 0);
        assertTrue(r1.compareTo(r5) == 0);
        assertTrue(r1.compareTo(r6) > 0);
        assertTrue(r2.compareTo(r6) > 0);
    }

    @Test public void massTestComparisonsHundred() {
        massTestComparisons(100, 1600454116L);
    }

    @Test public void massTestComparisonsTenThousand() {
        massTestComparisons(10_000, 2319695085L);
    }

    @Test public void massTestComparisonsHundredThousand() {
        massTestComparisons(100_000, 2947062926L);
    }

    private void massTestComparisons(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        Rope[] ropes = new Rope[n];
        String[] initial = {
                "aaaaa", "abab", "babb", "bbbaba"
        };
        final int MAXLEN = 1000; // Let's keep these rope lengths a bit shorter for this test.

        HashSet<Rope> ropeHash = new HashSet<>();
        TreeSet<Rope> ropeTree = new TreeSet<>();

        for(int i = 0; i < n; i++) {
            if(i < initial.length) {
                ropes[i] = new StringRope(initial[i]);
            }
            else {
                Rope r1 = ropes[rng.nextInt(i)];
                Rope r2 = ropes[rng.nextInt(i)];
                // Concatenate the ropes 50% of the time, but only if the result length fits in an int.
                if(rng.nextInt(100) < 50 && r1.length() < MAXLEN && r2.length() < MAXLEN) {
                    ropes[i] = new ConcatRope(r1, r2);
                }
                else {
                    int len = r1.length();
                    int start, end;
                    if(len > 0) {
                        start = rng.nextInt(len);
                        end = start + rng.nextInt(len - start);
                    }
                    else {
                        end = start = 0;
                    }
                    ropes[i] = new SubRope(r1, start, end);
                }
            }
            ropeHash.add(ropes[i]);
            ropeTree.add(ropes[i]);
            // The sizes of two sets must be in lockstep at all times.
            assertFalse(ropeHash.size() < ropeTree.size());
            assertFalse(ropeHash.size() > ropeTree.size());
            // Update the checksum based on some order comparisons.
            if(i > 0) {
                check.update(ropes[i].compareTo(ropes[i-1]) < 0 ? 42: 99);
            }
        }
        assertEquals(expected, check.getValue());
    }
}