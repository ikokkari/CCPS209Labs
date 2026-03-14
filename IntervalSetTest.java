import org.junit.Test;
import java.util.zip.CRC32;
import java.util.Random;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IntervalSetTest {

    // --- add explicit tests ---

    @Test public void testAddExplicit() {
        IntervalSet is0 = new IntervalSet();
        assertEquals("[]", is0.toString());
        is0.add(10, 20);
        assertEquals("[10-20]", is0.toString());
        is0.add(40, 60);
        assertEquals("[10-20, 40-60]", is0.toString());
        is0.add(5, 25);
        assertEquals("[5-25, 40-60]", is0.toString());
        is0.add(20, 45);
        assertEquals("[5-60]", is0.toString());
        is0.add(70, 80);
        assertEquals("[5-60, 70-80]", is0.toString());
        is0.add(61, 69);
        assertEquals("[5-80]", is0.toString());
        is0.add(1, 3);
        assertEquals("[1-3, 5-80]", is0.toString());
        is0.add(90, 100);
        assertEquals("[1-3, 5-80, 90-100]", is0.toString());
        is0.add(2, 95);
        assertEquals("[1-100]", is0.toString());

        IntervalSet is1 = new IntervalSet();
        is1.add(42);
        assertEquals("[42]", is1.toString());
        is1.add(17);
        assertEquals("[17, 42]", is1.toString());
        is1.add(99);
        assertEquals("[17, 42, 99]", is1.toString());
        is1.add(41, 98);
        assertEquals("[17, 41-99]", is1.toString());
        is1.add(100, 110);
        assertEquals("[17, 41-110]", is1.toString());
        is1.add(0, 15);
        assertEquals("[0-15, 17, 41-110]", is1.toString());
        is1.add(16);
        assertEquals("[0-17, 41-110]", is1.toString());
        is1.add(17, 41);
        assertEquals("[0-110]", is1.toString());
    }

    @Test public void testAddSubsetNoChange() {
        // Adding an interval already fully contained should not change the set.
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        is.add(12, 18);
        assertEquals("[10-20]", is.toString());
        is.add(10, 20);
        assertEquals("[10-20]", is.toString());
        is.add(15);
        assertEquals("[10-20]", is.toString());
    }

    @Test public void testAddAdjacentMerge() {
        // Adjacent intervals (end+1 == start of next) should merge.
        IntervalSet is = new IntervalSet();
        is.add(1, 3);
        assertEquals("[1-3]", is.toString());
        is.add(4, 6);
        assertEquals("[1-6]", is.toString()); // 3+1 == 4, so they merge
        is.add(8, 10);
        assertEquals("[1-6, 8-10]", is.toString());
        is.add(7); // bridges the gap
        assertEquals("[1-10]", is.toString());
    }

    @Test public void testAddAbsorbsMultiple() {
        // One add that absorbs three separate intervals into one.
        IntervalSet is = new IntervalSet();
        is.add(1, 3);
        is.add(7, 9);
        is.add(13, 15);
        assertEquals("[1-3, 7-9, 13-15]", is.toString());
        is.add(4, 12); // absorbs all three
        assertEquals("[1-15]", is.toString());
    }

    @Test public void testAddSingleton() {
        IntervalSet is = new IntervalSet();
        is.add(5);
        assertEquals("[5]", is.toString());
        is.add(3);
        assertEquals("[3, 5]", is.toString());
        is.add(4); // merges 3, 4, 5
        assertEquals("[3-5]", is.toString());
    }

    // --- contains explicit tests ---

    @Test public void testContainsExplicit() {
        IntervalSet is = new IntervalSet();
        assertFalse(is.contains(1_000_000_000));
        assertFalse(is.contains(42, 99));
        is.add(42, 99);
        assertEquals("[42-99]", is.toString());
        assertTrue(is.contains(42, 99));
        assertTrue(is.contains(50, 72));
        assertFalse(is.contains(42, 100));
        assertFalse(is.contains(35, 70));
        is.add(10, 25);
        assertEquals("[10-25, 42-99]", is.toString());
        assertTrue(is.contains(42, 99));
        assertTrue(is.contains(86));
        assertTrue(is.contains(20));
        assertTrue(is.contains(24, 25));
        assertFalse(is.contains(25, 42));
        is.add(35, 60);
        assertEquals("[10-25, 35-99]", is.toString());
        assertTrue(is.contains(11, 14));
        assertTrue(is.contains(35, 99));
        assertTrue(is.contains(40, 98));
    }

    @Test public void testContainsEmptySet() {
        IntervalSet is = new IntervalSet();
        assertFalse(is.contains(0));
        assertFalse(is.contains(100));
        assertFalse(is.contains(0, 100));
    }

    @Test public void testContainsBoundaries() {
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        // Inclusive boundaries
        assertTrue(is.contains(10));
        assertTrue(is.contains(20));
        assertTrue(is.contains(10, 20));
        // Just outside
        assertFalse(is.contains(9));
        assertFalse(is.contains(21));
        assertFalse(is.contains(9, 20));
        assertFalse(is.contains(10, 21));
    }

    @Test public void testContainsSpanningGap() {
        // Interval that spans a gap between two stored intervals
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        is.add(30, 40);
        assertFalse(is.contains(10, 40)); // gap at 21-29
        assertFalse(is.contains(20, 30)); // gap at 21-29
        assertTrue(is.contains(10, 20));
        assertTrue(is.contains(30, 40));
    }

    @Test public void testContainsSingleton() {
        IntervalSet is = new IntervalSet();
        is.add(42);
        assertTrue(is.contains(42));
        assertFalse(is.contains(41));
        assertFalse(is.contains(43));
        assertFalse(is.contains(41, 43));
    }

    // --- remove explicit tests ---

    @Test public void testRemoveExplicit() {
        IntervalSet is = new IntervalSet();
        is.remove(42, 100); // nothing should happen, not even a crash
        assertEquals("[]", is.toString());
        is.add(10, 50);
        is.remove(5, 20);
        assertEquals("[21-50]", is.toString());
        is.remove(30, 33);
        assertEquals("[21-29, 34-50]", is.toString());
        is.remove(34, 37);
        assertEquals("[21-29, 38-50]", is.toString());
        is.remove(21, 29);
        assertEquals("[38-50]", is.toString());
        is.remove(45, 100);
        assertEquals("[38-44]", is.toString());
        is.remove(39, 42);
        assertEquals("[38, 43-44]", is.toString());
        is.remove(0, 42);
        assertEquals("[43-44]", is.toString());
        is.remove(44, 100);
        assertEquals("[43]", is.toString());
        is.remove(43);
        assertEquals("[]", is.toString());

        is.add(30, 66);
        is.remove(50);
        assertEquals("[30-49, 51-66]", is.toString());
        is.remove(40, 50);
        assertEquals("[30-39, 51-66]", is.toString());
        is.remove(55, 60);
        assertEquals("[30-39, 51-54, 61-66]", is.toString());
        is.remove(52, 64);
        assertEquals("[30-39, 51, 65-66]", is.toString());
        is.remove(35, 65);
        assertEquals("[30-34, 66]", is.toString());
        is.remove(20, 100);
        assertEquals("[]", is.toString());
    }

    @Test public void testRemoveFromEmpty() {
        IntervalSet is = new IntervalSet();
        is.remove(1, 100);
        assertEquals("[]", is.toString());
        is.remove(42);
        assertEquals("[]", is.toString());
    }

    @Test public void testRemoveNonOverlapping() {
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        is.remove(5, 8); // entirely before
        assertEquals("[10-20]", is.toString());
        is.remove(25, 30); // entirely after
        assertEquals("[10-20]", is.toString());
    }

    @Test public void testRemoveExactInterval() {
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        is.remove(10, 20);
        assertEquals("[]", is.toString());
    }

    @Test public void testRemoveSplitsInterval() {
        // Removing the middle of an interval creates two intervals.
        IntervalSet is = new IntervalSet();
        is.add(1, 100);
        is.remove(50);
        assertEquals("[1-49, 51-100]", is.toString());
    }

    @Test public void testRemoveSingleton() {
        IntervalSet is = new IntervalSet();
        is.add(5);
        is.remove(5);
        assertEquals("[]", is.toString());
    }

    @Test public void testRemoveLeftOverlap() {
        // Remove overlapping the left end of an interval
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        is.remove(5, 15);
        assertEquals("[16-20]", is.toString());
    }

    @Test public void testRemoveRightOverlap() {
        // Remove overlapping the right end
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        is.remove(15, 25);
        assertEquals("[10-14]", is.toString());
    }

    @Test public void testRemoveMultipleIntervals() {
        // Remove spanning across multiple intervals
        IntervalSet is = new IntervalSet();
        is.add(1, 10);
        is.add(20, 30);
        is.add(40, 50);
        is.remove(5, 45);
        assertEquals("[1-4, 46-50]", is.toString());
    }

    // --- Combined add/remove/contains sequences ---

    @Test public void testAddRemoveAddSequence() {
        IntervalSet is = new IntervalSet();
        is.add(10, 20);
        assertTrue(is.contains(15));
        is.remove(15);
        assertFalse(is.contains(15));
        assertEquals("[10-14, 16-20]", is.toString());
        is.add(15);
        assertTrue(is.contains(15));
        assertEquals("[10-20]", is.toString()); // re-merged
    }

    @Test public void testAddThenRemoveSameEqualsEmpty() {
        IntervalSet is = new IntervalSet();
        is.add(5, 15);
        is.remove(5, 15);
        assertEquals("[]", is.toString());
        assertFalse(is.contains(5));
        assertFalse(is.contains(10));
        assertFalse(is.contains(15));
    }

    @Test public void testLargeValues() {
        IntervalSet is = new IntervalSet();
        is.add(1_000_000_000);
        assertEquals("[1000000000]", is.toString());
        assertTrue(is.contains(1_000_000_000));
        assertFalse(is.contains(999_999_999));
    }

    // --- CRC mass tests ---

    @Test public void massTestOneHundred() {
        massTest(100, 1471632031L);
    }

    @Test public void massTestTenThousand() {
        massTest(10000, 1796090957L);
    }

    @Test public void massTestOneMillion() {
        massTest(1_000_000, 1101228305L);
    }

    private void massTest(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        IntervalSet is = new IntervalSet();
        int[] starts = new int[n], ends = new int[n];
        for(int i = 0; i < n; i++) {
            int start, end;
            if(i < 5 || rng.nextBoolean()) {
                start = rng.nextInt(20 * i + 10);
                end = start + rng.nextInt(5 * i + 10);
            }
            else {
                int js = rng.nextInt(i);
                int je = rng.nextInt(i);
                start = Math.max(0, starts[js] + rng.nextInt(3) - 1);
                end = Math.max(0, ends[je] + rng.nextInt(3) - 1);
                if(start > end) {
                    int tmp = start; start = end; end = tmp;
                }
            }
            starts[i] = start; ends[i] = end;
            check.update(is.contains(start, end) ? 42: 99);
            check.update(is.contains(start-3, start+3) ? 42: 99);
            check.update(is.contains(end-10, end+10) ? 42: 99);
            if(rng.nextBoolean()) {
                is.add(start, end);
            }
            else {
                is.remove(start, end);
            }
            try {
                check.update(is.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(expected, check.getValue());
    }
}