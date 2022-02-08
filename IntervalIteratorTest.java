import org.junit.Test;
import java.util.Iterator;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

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

    @Test public void testIterationExplicit() {
        IntervalSet intervals = new IntervalSet();
        assertEquals("[]", extinguish(intervals.iterator()));

        intervals.add(10, 15);
        intervals.add(17);
        intervals.add(20, 24);
        assertEquals("[10, 11, 12, 13, 14, 15, 17, 20, 21, 22, 23, 24]", extinguish(intervals.iterator()));

        intervals.remove(0, 100);
        intervals.add(5, 8);
        intervals.add(10, 14);
        intervals.add(3);
        intervals.add(42);
        assertEquals("[3, 5, 6, 7, 8, 10, 11, 12, 13, 14, 42]", extinguish(intervals.iterator()));

        // Last test using two iterators simultanously to the same IntervalSet.
        Iterator<Integer> iterator0 = intervals.iterator();
        Iterator<Integer> iterator1 = intervals.iterator();
        // Consume the first two elements from the first iterator.
        iterator0.next();
        iterator0.next();
        assertEquals("[3, 5, 6, 7, 8, 10, 11, 12, 13, 14, 42]", extinguish(iterator1));
        // First two elements should not be there in the result.
        assertEquals("[6, 7, 8, 10, 11, 12, 13, 14, 42]", extinguish(iterator0));
    }

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