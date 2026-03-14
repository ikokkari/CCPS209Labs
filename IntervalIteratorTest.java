import org.junit.Test;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.ArrayList;

public class IntervalIteratorTest {

    private String extinguish(Iterator<Integer> iterator) {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        result.append("[");
        while(iterator.hasNext()) {
            if(!first) { result.append(", "); }
            result.append(iterator.next());
            first = false;
        }
        result.append("]");
        return result.toString();
    }

    // --- Iteration explicit tests ---

    @Test public void testIterationExplicit() {
        IntervalSet intervals = new IntervalSet();
        assertEquals("[]", extinguish(intervals.iterator()));

        intervals.add(17);
        assertEquals("[17]", extinguish(intervals.iterator()));

        intervals.add(10, 15);
        intervals.add(20, 24);
        assertEquals("[10, 11, 12, 13, 14, 15, 17, 20, 21, 22, 23, 24]", extinguish(intervals.iterator()));

        intervals.remove(0, 100);
        intervals.add(5, 8);
        intervals.add(10, 14);
        intervals.add(3);
        intervals.add(42);
        assertEquals("[3, 5, 6, 7, 8, 10, 11, 12, 13, 14, 42]", extinguish(intervals.iterator()));

        // Last test using two simultaneous iterators to the previous IntervalSet.
        Iterator<Integer> iterator0 = intervals.iterator();
        Iterator<Integer> iterator1 = intervals.iterator();
        // Consume the first two elements from the first iterator.
        iterator0.next();
        iterator0.next();
        assertEquals("[3, 5, 6, 7, 8, 10, 11, 12, 13, 14, 42]", extinguish(iterator1));
        // First two elements of the IntervalSet should not be in the result.
        assertEquals("[6, 7, 8, 10, 11, 12, 13, 14, 42]", extinguish(iterator0));

        // Let's celebrate the fact that the IntervalSet is now an Iterable.
        ArrayList<Integer> list = new ArrayList<>();
        for(Integer e: intervals) { // Foreach-loop through the IntervalSet!
            list.add(e);
        }
        assertEquals("[3, 5, 6, 7, 8, 10, 11, 12, 13, 14, 42]", list.toString());
    }

    @Test public void testIterateEmptySet() {
        IntervalSet intervals = new IntervalSet();
        Iterator<Integer> it = intervals.iterator();
        assertFalse(it.hasNext());
        assertEquals("[]", extinguish(intervals.iterator()));
    }

    @Test public void testIterateSingleElement() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(42);
        Iterator<Integer> it = intervals.iterator();
        assertTrue(it.hasNext());
        assertEquals(42, (int) it.next());
        assertFalse(it.hasNext());
    }

    @Test public void testIterateSingleRange() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(3, 7);
        assertEquals("[3, 4, 5, 6, 7]", extinguish(intervals.iterator()));
    }

    @Test public void testIterateMultipleIntervals() {
        // Mix of singletons and ranges
        IntervalSet intervals = new IntervalSet();
        intervals.add(1);
        intervals.add(5, 8);
        intervals.add(15);
        intervals.add(20, 22);
        assertEquals("[1, 5, 6, 7, 8, 15, 20, 21, 22]", extinguish(intervals.iterator()));
    }

    @Test public void testIterateSkipsGaps() {
        // Elements between intervals should not appear
        IntervalSet intervals = new IntervalSet();
        intervals.add(10, 12);
        intervals.add(20, 22);
        ArrayList<Integer> elems = new ArrayList<>();
        for (int e : intervals) { elems.add(e); }
        assertEquals("[10, 11, 12, 20, 21, 22]", elems.toString());
        // 13-19 should not appear
        assertFalse(elems.contains(13));
        assertFalse(elems.contains(19));
    }

    @Test public void testIterateAfterAddAndRemove() {
        // Build set, modify it, then iterate the final state
        IntervalSet intervals = new IntervalSet();
        intervals.add(1, 10);
        intervals.remove(5);
        // Set is now [1-4, 6-10]
        assertEquals("[1, 2, 3, 4, 6, 7, 8, 9, 10]", extinguish(intervals.iterator()));
    }

    // --- Two simultaneous iterators ---

    @Test public void testTwoIteratorsIndependent() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(1, 5);
        Iterator<Integer> it1 = intervals.iterator();
        Iterator<Integer> it2 = intervals.iterator();

        // Advance it1 by 3 steps
        assertEquals(1, (int) it1.next());
        assertEquals(2, (int) it1.next());
        assertEquals(3, (int) it1.next());

        // it2 is still at the beginning
        assertEquals(1, (int) it2.next());

        // it1 continues from 4
        assertEquals(4, (int) it1.next());
        assertEquals(5, (int) it1.next());
        assertFalse(it1.hasNext());

        // it2 continues independently
        assertEquals("[2, 3, 4, 5]", extinguish(it2));
    }

    // --- For-each loop ---

    @Test public void testForEachLoop() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(10, 12);
        intervals.add(20);
        int sum = 0;
        int count = 0;
        for (int e : intervals) {
            sum += e;
            count++;
        }
        assertEquals(4, count); // 10, 11, 12, 20
        assertEquals(10 + 11 + 12 + 20, sum);
    }

    // --- hasNext idempotent ---

    @Test public void testHasNextIdempotent() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(5, 7);
        Iterator<Integer> it = intervals.iterator();
        // Calling hasNext multiple times should not advance the iterator
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertTrue(it.hasNext());
        assertEquals(5, (int) it.next()); // still returns first element
    }

    @Test public void testHasNextFalseAfterExhaustion() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(42);
        Iterator<Integer> it = intervals.iterator();
        it.next();
        assertFalse(it.hasNext());
        assertFalse(it.hasNext()); // still false, no crash
    }

    // --- ConcurrentModificationException ---

    @Test public void testConcurrentModification() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(42, 99);
        Iterator<Integer> iterator = intervals.iterator();
        iterator.next();
        iterator.next();
        intervals.add(10, 20);
        try {
            iterator.next();
            fail(); // This line must be unreachable due to exception being thrown.
        }
        catch(ConcurrentModificationException ignored) {}
        iterator = intervals.iterator();
        iterator.next();
        intervals.remove(11, 70);
        try {
            iterator.next();
            fail(); // This line must be unreachable due to exception being thrown.
        }
        catch(ConcurrentModificationException ignored) {}
    }

    @Test public void testConcurrentModificationOnHasNext() {
        // hasNext should also detect modification
        IntervalSet intervals = new IntervalSet();
        intervals.add(10, 20);
        Iterator<Integer> it = intervals.iterator();
        it.next();
        intervals.add(30, 40);
        try {
            it.hasNext();
            fail();
        }
        catch(ConcurrentModificationException ignored) {}
    }

    @Test public void testConcurrentModificationAfterRemove() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(1, 100);
        Iterator<Integer> it = intervals.iterator();
        intervals.remove(50);
        try {
            it.next();
            fail();
        }
        catch(ConcurrentModificationException ignored) {}
    }

    @Test public void testNoModificationNoException() {
        // Creating a new iterator after modification should work fine
        IntervalSet intervals = new IntervalSet();
        intervals.add(5, 10);
        intervals.add(20, 25); // modification before iterator creation
        Iterator<Integer> it = intervals.iterator();
        // This should not throw
        assertTrue(it.hasNext());
        assertEquals(5, (int) it.next());
    }

    // --- Element count consistency ---

    @Test public void testIterationCountMatchesExpected() {
        IntervalSet intervals = new IntervalSet();
        intervals.add(1, 10);   // 10 elements
        intervals.add(20, 25);  // 6 elements
        intervals.add(100);     // 1 element
        int count = 0;
        for (int e : intervals) { count++; }
        assertEquals(17, count);
    }

    // --- CRC mass tests ---

    @Test public void testIteratorHundred() {
        massTest(100, 395166661L);
    }

    @Test public void testIteratorTenThousand() {
        massTest(10000, 431583957L);
    }

    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345 + n);
        int step = 5, count = 0, goal = 5;
        for(int i = 0; i < n; i++) {
            IntervalSet intervals = new IntervalSet();
            int start = rng.nextInt(1 + i);
            int total = 0;
            for(int j = 0; j * j < i + 5; j++) {
                int end = start + rng.nextInt(step);
                intervals.add(start, end);
                total += (end - start) + 1;
                start = end + 1 + rng.nextInt(step);
            }
            total *= 2;
            // Just to make that your iterators are independent of each other.
            Iterator<Integer> tortoise = intervals.iterator();
            Iterator<Integer> hare = intervals.iterator();
            while(tortoise.hasNext() || hare.hasNext()) {
                total--;
                Iterator<Integer> toUse = null;
                if(!tortoise.hasNext()) { toUse = hare; }
                else if(!hare.hasNext()) { toUse = tortoise; }
                else { toUse = rng.nextInt(100) < 30 ? tortoise: hare; }
                int e = toUse.next();
                check.update(e);
            }
            assertEquals(0, total);
            if(++count == goal) {
                step++; count = 0; goal++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}