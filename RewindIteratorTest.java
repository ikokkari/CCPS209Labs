import org.junit.Test;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RewindIteratorTest {

    // Helper: create an infinite iterator of natural numbers
    private static Iterator<Integer> naturals() {
        return new Iterator<>() {
            int v = 0;
            public boolean hasNext() { return true; }
            public Integer next() { return v++; }
        };
    }

    // Helper: collect the next n elements from the iterator
    private static List<Integer> collect(RewindIterator<Integer> ri, int n) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < n; i++) { result.add(ri.next()); }
        return result;
    }

    // --- Basic forward iteration (no marks) ---

    @Test public void testForwardOnly() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        assertEquals(List.of(0, 1, 2, 3, 4), collect(ri, 5));
        assertEquals(Integer.valueOf(5), ri.next());
    }

    // --- Simple mark and rewind ---

    @Test public void testSimpleMarkRewind() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        assertEquals(Integer.valueOf(0), ri.next());
        assertEquals(Integer.valueOf(1), ri.next());
        ri.mark(); // mark after position 1
        assertEquals(Integer.valueOf(2), ri.next());
        assertEquals(Integer.valueOf(3), ri.next());
        ri.rewind(); // rewind to after position 1
        // Replay 2, 3, then continue with 4
        assertEquals(List.of(2, 3, 4), collect(ri, 3));
    }

    @Test public void testMarkAtStart() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        ri.mark(); // mark before anything consumed
        assertEquals(Integer.valueOf(0), ri.next());
        assertEquals(Integer.valueOf(1), ri.next());
        ri.rewind();
        // Replay from the very beginning
        assertEquals(List.of(0, 1, 2), collect(ri, 3));
    }

    @Test public void testMarkRewindImmediately() {
        // Mark then immediately rewind with no elements between
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        assertEquals(Integer.valueOf(0), ri.next());
        ri.mark();
        ri.rewind(); // nothing to replay
        assertEquals(Integer.valueOf(1), ri.next()); // continues normally
    }

    // --- Nested marks ---

    @Test public void testNestedMarks() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        assertEquals(Integer.valueOf(0), ri.next());
        ri.mark(); // outer mark
        assertEquals(Integer.valueOf(1), ri.next());
        assertEquals(Integer.valueOf(2), ri.next());
        ri.mark(); // inner mark
        assertEquals(Integer.valueOf(3), ri.next());
        assertEquals(Integer.valueOf(4), ri.next());
        ri.rewind(); // rewind to inner mark (after 2)
        assertEquals(List.of(3, 4, 5), collect(ri, 3));
        ri.rewind(); // rewind to outer mark (after 0)
        assertEquals(List.of(1, 2, 3), collect(ri, 3));
    }

    @Test public void testDoubleMarkSamePosition() {
        // Two marks at the same position
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        ri.mark();
        ri.mark();
        assertEquals(Integer.valueOf(0), ri.next());
        assertEquals(Integer.valueOf(1), ri.next());
        ri.rewind(); // inner mark
        assertEquals(List.of(0, 1), collect(ri, 2));
        ri.rewind(); // outer mark
        assertEquals(List.of(0, 1, 2), collect(ri, 3));
    }

    // --- Spec trace verification ---

    @Test public void testSpecTraceLine1() {
        // From spec: "0 1 2 M R 3 4 5 6 M 7 R 7"
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        assertEquals(Integer.valueOf(0), ri.next());
        assertEquals(Integer.valueOf(1), ri.next());
        assertEquals(Integer.valueOf(2), ri.next());
        ri.mark();
        ri.rewind(); // immediate rewind, nothing between
        assertEquals(Integer.valueOf(3), ri.next());
        assertEquals(Integer.valueOf(4), ri.next());
        assertEquals(Integer.valueOf(5), ri.next());
        assertEquals(Integer.valueOf(6), ri.next());
        ri.mark();
        assertEquals(Integer.valueOf(7), ri.next());
        ri.rewind();
        assertEquals(Integer.valueOf(7), ri.next()); // replayed
    }

    // --- hasNext behavior ---

    @Test public void testHasNextWithBuffer() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        ri.mark();
        ri.next(); // 0
        ri.next(); // 1
        ri.rewind();
        assertTrue(ri.hasNext()); // buffer has elements
        assertEquals(Integer.valueOf(0), ri.next());
    }

    @Test public void testHasNextFiniteIterator() {
        // Finite iterator: hasNext becomes true again after rewind
        Iterator<Integer> finite = Arrays.asList(10, 20, 30).iterator();
        RewindIterator<Integer> ri = new RewindIterator<>(finite);
        ri.mark();
        assertEquals(List.of(10, 20, 30), collect(ri, 3));
        assertFalse(ri.hasNext()); // underlying exhausted
        ri.rewind();
        assertTrue(ri.hasNext()); // buffer has elements!
        assertEquals(List.of(10, 20, 30), collect(ri, 3));
        assertFalse(ri.hasNext()); // truly done now
    }

    // --- Rewind without mark throws ---

    @Test public void testRewindWithoutMarkThrows() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        try {
            ri.rewind();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException ignored) {}
    }

    @Test public void testRewindAfterAllMarksConsumedThrows() {
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        ri.mark();
        ri.next();
        ri.rewind();
        // Now no marks left
        try {
            ri.rewind();
            fail("Should throw IllegalStateException");
        } catch (IllegalStateException ignored) {}
    }

    // --- Elements continue correctly after rewind ---

    @Test public void testContinuationAfterReplay() {
        // After replay elements are exhausted, new elements come from underlying
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        ri.mark();
        assertEquals(List.of(0, 1, 2), collect(ri, 3));
        ri.rewind();
        // Replay 0,1,2, then new elements 3,4
        assertEquals(List.of(0, 1, 2, 3, 4), collect(ri, 5));
    }

    @Test public void testMultipleRewindsToSameMark() {
        // Mark once, rewind multiple times (with re-marking)
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        ri.mark();
        assertEquals(List.of(0, 1, 2), collect(ri, 3));
        ri.rewind();
        ri.mark(); // re-mark at same position
        assertEquals(List.of(0, 1, 2), collect(ri, 3));
        ri.rewind();
        assertEquals(List.of(0, 1, 2, 3), collect(ri, 4));
    }

    // --- Memory: emitted list cleared when no marks ---

    @Test public void testNoMarksNoAccumulation() {
        // Without marks, the emitted list should not grow
        // (We can't test internal state directly, but we can verify behavior)
        RewindIterator<Integer> ri = new RewindIterator<>(naturals());
        // Consume many elements without marking
        for (int i = 0; i < 1000; i++) {
            assertEquals(Integer.valueOf(i), ri.next());
        }
        // Now mark and rewind should only affect future elements
        ri.mark();
        assertEquals(Integer.valueOf(1000), ri.next());
        assertEquals(Integer.valueOf(1001), ri.next());
        ri.rewind();
        assertEquals(Integer.valueOf(1000), ri.next());
    }

    // --- CRC mass tests ---

    @Test public void testFirst1000() {
        massTest(1000, 876040768L);
    }

    @Test public void testFirstMillion() {
        massTest(1_000_000, 1839975941L);
    }

    @Test public void testFirstHundredMillion() {
        massTest(100_000_000, 2819947101L);
    }

    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(4444);
        Iterator<Integer> ints = new Iterator<>() {
            int v = 0;
            public boolean hasNext() { return true; }
            public Integer next() { return v++; }
        };
        RewindIterator<Integer> rwi = new RewindIterator<>(ints);
        int marks = 0, count = 0, prev = -1;
        for(int i = 0; i < n; i++) {
            int v = rwi.next();
            if(prev != -1) {
                assertEquals(prev + 1, v);
            }
            prev = v;
            count++;
            check.update(v);
            if(rng.nextInt(100 + i) < 20 || (marks == 0 && count > 10 + i / 10)) {
                rwi.mark();
                marks++;
                count = 0;
            }
            if(marks > 0 && rng.nextInt(100 + i) < 30) {
                rwi.rewind();
                marks--;
                prev = -1;
            }
        }
        assertEquals(expected, check.getValue());
        while(marks-- > 0) { rwi.rewind(); }
        try { rwi.rewind(); } catch(IllegalStateException e) { return; }
        fail();
    }
}