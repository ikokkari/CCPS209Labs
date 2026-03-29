import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinMaxArrayListTest {

    // --- Tests for add(E) ---

    @Test
    public void testAddSingle() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(42);
        assertEquals(Integer.valueOf(42), list.getMin());
        assertEquals(Integer.valueOf(42), list.getMax());
    }

    @Test
    public void testAddUpdatesMin() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(10);
        list.add(5);
        list.add(20);
        assertEquals(Integer.valueOf(5), list.getMin());
        assertEquals(Integer.valueOf(20), list.getMax());
    }

    @Test
    public void testAddDuplicateExtremes() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(3);
        list.add(3);
        list.add(3);
        assertEquals(Integer.valueOf(3), list.getMin());
        assertEquals(Integer.valueOf(3), list.getMax());
    }

    // --- Tests for add(int, E) ---

    @Test
    public void testAddAtIndex() {
        MinMaxArrayList<String> list = new MinMaxArrayList<>();
        list.add("banana");
        list.add("cherry");
        list.add(0, "apple");
        assertEquals("apple", list.getMin());
        assertEquals("cherry", list.getMax());
    }

    @Test
    public void testAddAtIndexNewExtreme() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(10);
        list.add(20);
        list.add(1, 1);
        assertEquals(Integer.valueOf(1), list.getMin());
        assertEquals(Integer.valueOf(20), list.getMax());
    }

    // --- Tests for addAll ---

    @Test
    public void testAddAll() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(50);
        list.addAll(Arrays.asList(10, 20, 30, 90));
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(90), list.getMax());
    }

    @Test
    public void testAddAllAtIndex() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(50);
        list.add(60);
        list.addAll(1, Arrays.asList(1, 100));
        assertEquals(Integer.valueOf(1), list.getMin());
        assertEquals(Integer.valueOf(100), list.getMax());
    }

    // --- Tests for set ---

    @Test
    public void testSetReplacesMax() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.set(2, 15);
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(20), list.getMax());
    }

    @Test
    public void testSetReplacesMin() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.set(0, 25);
        assertEquals(Integer.valueOf(20), list.getMin());
        assertEquals(Integer.valueOf(30), list.getMax());
    }

    @Test
    public void testSetIntroducesNewExtreme() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(10);
        list.add(20);
        list.set(0, 99);
        assertEquals(Integer.valueOf(20), list.getMin());
        assertEquals(Integer.valueOf(99), list.getMax());
    }

    // --- Tests for remove(int) ---

    @Test
    public void testRemoveByIndexMiddle() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(10);
        list.add(20);
        list.add(30);
        list.remove(1); // remove 20
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(30), list.getMax());
    }

    @Test
    public void testRemoveByIndexMin() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(5);
        list.add(10);
        list.add(20);
        list.remove(0); // remove min
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(20), list.getMax());
    }

    @Test
    public void testRemoveByIndexMax() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(5);
        list.add(10);
        list.add(20);
        list.remove(2); // remove max
        assertEquals(Integer.valueOf(5), list.getMin());
        assertEquals(Integer.valueOf(10), list.getMax());
    }

    // --- Tests for remove(Object) ---

    @Test
    public void testRemoveObjectMiddle() {
        MinMaxArrayList<String> list = new MinMaxArrayList<>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");
        list.remove("banana");
        assertEquals("apple", list.getMin());
        assertEquals("cherry", list.getMax());
    }

    @Test
    public void testRemoveObjectMin() {
        MinMaxArrayList<String> list = new MinMaxArrayList<>();
        list.add("apple");
        list.add("banana");
        list.add("cherry");
        list.remove("apple");
        assertEquals("banana", list.getMin());
        assertEquals("cherry", list.getMax());
    }

    // --- Tests for removeAll ---

    @Test
    public void testRemoveAll() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        list.removeAll(Arrays.asList(1, 5));
        assertEquals(Integer.valueOf(2), list.getMin());
        assertEquals(Integer.valueOf(4), list.getMax());
    }

    // --- Tests for retainAll ---

    @Test
    public void testRetainAll() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        list.retainAll(Arrays.asList(2, 3, 4));
        assertEquals(Integer.valueOf(2), list.getMin());
        assertEquals(Integer.valueOf(4), list.getMax());
    }

    // --- Tests for removeIf ---

    @Test
    public void testRemoveIf() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5, 6));
        list.removeIf(e -> e % 2 == 0); // keep 1, 3, 5
        assertEquals(Integer.valueOf(1), list.getMin());
        assertEquals(Integer.valueOf(5), list.getMax());
    }

    @Test
    public void testRemoveIfRemovesExtremes() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3, 4, 5));
        list.removeIf(e -> e == 1 || e == 5);
        assertEquals(Integer.valueOf(2), list.getMin());
        assertEquals(Integer.valueOf(4), list.getMax());
    }

    // --- Tests for replaceAll ---

    @Test
    public void testReplaceAll() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3));
        list.replaceAll(e -> e * 10);
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(30), list.getMax());
    }

    @Test
    public void testReplaceAllReversesOrder() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3));
        list.replaceAll(e -> -e);
        assertEquals(Integer.valueOf(-3), list.getMin());
        assertEquals(Integer.valueOf(-1), list.getMax());
    }

    // --- Tests for sort ---

    @Test
    public void testSort() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(30, 10, 20));
        list.sort(Comparator.naturalOrder());
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(30), list.getMax());
    }

    @Test
    public void testSortReverseComparator() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(30, 10, 20));
        list.sort(Comparator.reverseOrder());
        // Comparable min/max unchanged regardless of storage order
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(30), list.getMax());
    }

    // --- Tests for clear ---

    @Test
    public void testClear() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3));
        list.clear();
        assertTrue(list.isEmpty());
        try {
            list.getMin();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    @Test
    public void testClearThenAdd() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(1, 2, 3));
        list.clear();
        list.add(99);
        assertEquals(Integer.valueOf(99), list.getMin());
        assertEquals(Integer.valueOf(99), list.getMax());
    }

    // --- Tests for getMin/getMax edge cases ---

    @Test(expected = NoSuchElementException.class)
    public void testGetMinEmpty() {
        new MinMaxArrayList<Integer>().getMin();
    }

    @Test(expected = NoSuchElementException.class)
    public void testGetMaxEmpty() {
        new MinMaxArrayList<Integer>().getMax();
    }

    @Test
    public void testSingleElement() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(42);
        assertEquals(Integer.valueOf(42), list.getMin());
        assertEquals(Integer.valueOf(42), list.getMax());
        list.remove(0);
        try {
            list.getMin();
            fail("Expected NoSuchElementException");
        } catch (NoSuchElementException e) {
            // expected
        }
    }

    // --- Tests for Collection constructor ---

    @Test
    public void testConstructorFromCollection() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>(Arrays.asList(30, 10, 50, 20, 40));
        assertEquals(Integer.valueOf(10), list.getMin());
        assertEquals(Integer.valueOf(50), list.getMax());
    }

    // --- Negative values and mixed signs ---

    @Test
    public void testNegativeValues() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.addAll(Arrays.asList(-5, -1, -10, -3));
        assertEquals(Integer.valueOf(-10), list.getMin());
        assertEquals(Integer.valueOf(-1), list.getMax());
    }

    // --- Interleaved operations ---

    @Test
    public void testInterleavedOperations() {
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        list.add(50);
        assertEquals(Integer.valueOf(50), list.getMin());
        list.add(10);
        assertEquals(Integer.valueOf(10), list.getMin());
        list.add(90);
        assertEquals(Integer.valueOf(90), list.getMax());
        list.set(0, 5);
        assertEquals(Integer.valueOf(5), list.getMin());
        list.remove(Integer.valueOf(90));
        assertEquals(Integer.valueOf(10), list.getMax());
        list.addAll(Arrays.asList(1, 100));
        assertEquals(Integer.valueOf(1), list.getMin());
        assertEquals(Integer.valueOf(100), list.getMax());
        list.removeIf(e -> e > 50);
        assertEquals(Integer.valueOf(10), list.getMax());
        list.replaceAll(e -> e * -1);
        assertEquals(Integer.valueOf(-10), list.getMin());
        assertEquals(Integer.valueOf(-1), list.getMax());
    }

    // --- Fuzz test for caching performance ---

    @Test(timeout = 10000)
    public void testCachingPerformanceFuzzTwentyThousand() {
        testCachingPerformanceFuzz(20000);
    }

    private void testCachingPerformanceFuzz(final int N) {
        Random rng = new Random(12345);

        // Build the list with N elements in range [0, N)
        MinMaxArrayList<Integer> list = new MinMaxArrayList<>();
        for (int i = 0; i < N; i++) {
            list.add(rng.nextInt(N));
        }

        // Also maintain a TreeMap<Integer, Integer> (value -> count) as oracle
        TreeMap<Integer, Integer> oracle = new TreeMap<>();
        for (int i = 0; i < list.size(); i++) {
            oracle.merge(list.get(i), 1, Integer::sum);
        }

        for (int i = 0; i < N; i++) {
            // Verify min/max against oracle
            assertEquals(oracle.firstKey(), list.getMin());
            assertEquals(oracle.lastKey(), list.getMax());

            // Decide what to remove
            int removeIndex;
            int coin = rng.nextInt(10);
            if (coin == 0) {
                // Remove the minimum element
                int minVal = list.getMin();
                removeIndex = list.indexOf(minVal);
            } else if (coin == 1) {
                // Remove the maximum element
                int maxVal = list.getMax();
                removeIndex = list.indexOf(maxVal);
            } else {
                // Remove a random element
                removeIndex = rng.nextInt(list.size());
            }

            // Remove from list and update oracle
            int removed = list.remove(removeIndex);
            int count = oracle.get(removed);
            if (count == 1) {
                oracle.remove(removed);
            } else {
                oracle.put(removed, count - 1);
            }

            // Add a fresh random element to keep the list from draining
            int fresh = rng.nextInt(N);
            list.add(fresh);
            oracle.merge(fresh, 1, Integer::sum);
        }

        // Final check
        assertEquals(oracle.firstKey(), list.getMin());
        assertEquals(oracle.lastKey(), list.getMax());
    }
}