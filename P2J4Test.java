import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J4Test {

    private static final int SEED = 12345;

    // ---------------------------------------------------------------
    // firstMissingPositive tests
    // ---------------------------------------------------------------

    @Test public void testFirstMissingPositiveExplicit() {
        // Empty list
        assertEquals(1, P2J4.firstMissingPositive(Arrays.asList()));

        // No 1 present at all
        assertEquals(1, P2J4.firstMissingPositive(Arrays.asList(42, 99, 66)));
        assertEquals(1, P2J4.firstMissingPositive(Arrays.asList(6, 2, 12345678)));
        assertEquals(1, P2J4.firstMissingPositive(Arrays.asList(42)));

        // Singleton with 1
        assertEquals(2, P2J4.firstMissingPositive(Arrays.asList(1)));

        // Gap in middle
        assertEquals(3, P2J4.firstMissingPositive(Arrays.asList(6, 1, 7, 4, 5, 2)));
        assertEquals(2, P2J4.firstMissingPositive(Arrays.asList(9, 3, 8, 1, 4, 7, 7)));

        // Complete 1..n: answer is n+1
        assertEquals(11, P2J4.firstMissingPositive(Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1)));
        assertEquals(4, P2J4.firstMissingPositive(Arrays.asList(1, 2, 3)));
        assertEquals(6, P2J4.firstMissingPositive(Arrays.asList(5, 4, 3, 2, 1)));

        // Duplicates present
        assertEquals(3, P2J4.firstMissingPositive(Arrays.asList(1, 2, 2, 4)));
        assertEquals(4, P2J4.firstMissingPositive(Arrays.asList(1, 2, 2, 3)));
        assertEquals(2, P2J4.firstMissingPositive(Arrays.asList(1, 1, 1, 1)));

        // Spec example
        assertEquals(8, P2J4.firstMissingPositive(
                Arrays.asList(7, 5, 2, 3, 10, 2, 9999999, 4, 6, 3, 1, 9, 2)));

        // Verify equals vs == for Integer boxing (values > 127 are not cached)
        List<Integer> boxed = new ArrayList<>();
        for (int i = 1; i < 200; i++) {
            for (int j = 1; j < 3; j++) {
                boxed.add(i);
            }
        }
        assertEquals(200, P2J4.firstMissingPositive(boxed));
    }

    @Test public void testFirstMissingPositiveFuzz() {
        Random rng = new Random(SEED + 1);
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.clear();
            int miss = 1 + rng.nextInt(1000);
            for (int j = 1; j < miss; j++) {
                int rep = 1 + rng.nextInt(10);
                for (int k = 0; k < rep; k++) { items.add(j); }
            }
            int more = rng.nextInt(i + 2);
            for (int j = 0; j < more; j++) {
                items.add(rng.nextInt(100000000));
            }
            Collections.sort(items);
            int ans = P2J4.firstMissingPositive(items);
            assertEquals(miss, ans);
        }
    }

    @Test public void testFirstMissingPositiveShuffled() {
        // Same idea as fuzz but with shuffled lists to test order-independence
        Random rng = new Random(77777);
        for (int i = 0; i < 200; i++) {
            int miss = 1 + rng.nextInt(500);
            List<Integer> items = new ArrayList<>();
            for (int j = 1; j < miss; j++) { items.add(j); }
            // Add some large values beyond the gap
            for (int j = 0; j < 10; j++) {
                items.add(miss + 1 + rng.nextInt(10000));
            }
            Collections.shuffle(items, rng);
            assertEquals("miss=" + miss, miss, P2J4.firstMissingPositive(items));
        }
    }

    // ---------------------------------------------------------------
    // runningMedianOfThree tests
    // ---------------------------------------------------------------

    @Test public void testRunningMedianOfThreeExplicit() {
        // Empty list
        assertEquals(new ArrayList<>(), P2J4.runningMedianOfThree(new ArrayList<>()));

        // Singleton
        assertEquals(Collections.singletonList(13),
                P2J4.runningMedianOfThree(Collections.singletonList(13)));

        // Two elements: returned as-is
        assertEquals(Arrays.asList(13, 98),
                P2J4.runningMedianOfThree(Arrays.asList(13, 98)));

        // Three elements: median is the middle value when sorted
        assertEquals(Arrays.asList(17, 99, 42),
                P2J4.runningMedianOfThree(Arrays.asList(17, 99, 42)));

        // Spec example
        assertEquals(Arrays.asList(5, 2, 5, 2, 7, 4, 6, 4, 6),
                P2J4.runningMedianOfThree(Arrays.asList(5, 2, 9, 1, 7, 4, 6, 3, 8)));

        // Descending sequence: medians smooth toward middle
        assertEquals(Arrays.asList(777, 666, 666, 555, 444, 333, 222),
                P2J4.runningMedianOfThree(Arrays.asList(777, 666, 555, 444, 333, 222, 111)));

        // Ascending sequence
        assertEquals(Arrays.asList(1, 2, 2, 3, 4, 5),
                P2J4.runningMedianOfThree(Arrays.asList(1, 2, 3, 4, 5, 6)));

        // All same elements
        assertEquals(Arrays.asList(5, 5, 5, 5, 5),
                P2J4.runningMedianOfThree(Arrays.asList(5, 5, 5, 5, 5)));

        // Negative values
        assertEquals(Arrays.asList(99, -10, 0, -5, -5, -5),
                P2J4.runningMedianOfThree(Arrays.asList(99, -10, 0, -5, -8, 999)));

        // Two equal out of three
        assertEquals(Arrays.asList(3, 3, 3),
                P2J4.runningMedianOfThree(Arrays.asList(3, 3, 7)));
        assertEquals(Arrays.asList(3, 7, 7),
                P2J4.runningMedianOfThree(Arrays.asList(3, 7, 7)));
    }

    @Test public void testRunningMedianOfThreeProperties() {
        // Property: result has same size as input
        // Property: first two elements are copied verbatim
        // Property: each element from index 2 onward is the median of a window of 3
        Random rng = new Random(42);
        for (int trial = 0; trial < 500; trial++) {
            int len = rng.nextInt(50);
            List<Integer> items = new ArrayList<>();
            for (int j = 0; j < len; j++) { items.add(rng.nextInt(1000) - 500); }
            List<Integer> result = P2J4.runningMedianOfThree(items);
            assertEquals(items.size(), result.size());
            for (int j = 0; j < Math.min(2, len); j++) {
                assertEquals(items.get(j), result.get(j));
            }
            for (int j = 2; j < len; j++) {
                int[] triple = {items.get(j - 2), items.get(j - 1), items.get(j)};
                Arrays.sort(triple);
                assertEquals("index " + j, (Integer) triple[1], result.get(j));
            }
        }
    }

    @Test public void testRunningMedianOfThreeFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < 4000; i++) {
            items.clear();
            for (int j = 0; j < i; j++) { items.add(rng.nextInt(100000)); }
            List<Integer> ans = P2J4.runningMedianOfThree(items);
            check.update(ans.size());
            for (int e : ans) { check.update(e); }
        }
        assertEquals(4053632720L, check.getValue());
    }

    // ---------------------------------------------------------------
    // sortByElementFrequency tests
    // ---------------------------------------------------------------

    @Test public void testSortByElementFrequencyExplicit() {
        // Empty list
        List<Integer> a0 = new ArrayList<>();
        P2J4.sortByElementFrequency(a0);
        assertEquals(new ArrayList<>(), a0);

        // Singleton
        List<Integer> a0b = new ArrayList<>(Arrays.asList(42));
        P2J4.sortByElementFrequency(a0b);
        assertEquals(Arrays.asList(42), a0b);

        // All same frequency => ascending order
        List<Integer> a3 = new ArrayList<>(Arrays.asList(42, 17, 99, -10, 5));
        P2J4.sortByElementFrequency(a3);
        assertEquals(Arrays.asList(-10, 5, 17, 42, 99), a3);

        // Frequency dominates over value order
        List<Integer> a1 = new ArrayList<>(Arrays.asList(42, 42, 17, 42, 42, 17, 5, 5));
        P2J4.sortByElementFrequency(a1);
        assertEquals(Arrays.asList(42, 42, 42, 42, 5, 5, 17, 17), a1);

        // Tie in frequency: smaller value first
        List<Integer> a2 = new ArrayList<>(Arrays.asList(6, 3, 6, 3, 6, 3, 6, 3, 6));
        P2J4.sortByElementFrequency(a2);
        assertEquals(Arrays.asList(6, 6, 6, 6, 6, 3, 3, 3, 3), a2);

        // All identical elements
        List<Integer> a4 = new ArrayList<>(Arrays.asList(101, 101, 101, 101, 101, 101, 101, 101, 101));
        P2J4.sortByElementFrequency(a4);
        assertEquals(Arrays.asList(101, 101, 101, 101, 101, 101, 101, 101, 101), a4);

        // Spec example
        List<Integer> a5 = new ArrayList<>(Arrays.asList(4, 99999, 2, 2, 99999, 4, 4, 4));
        P2J4.sortByElementFrequency(a5);
        assertEquals(Arrays.asList(4, 4, 4, 4, 2, 2, 99999, 99999), a5);

        // Three-way frequency tie
        List<Integer> a5b = new ArrayList<>(Arrays.asList(67, 4, 101, 67, 67, 67, 4, 4, 4, 4, 101, 4));
        P2J4.sortByElementFrequency(a5b);
        assertEquals(Arrays.asList(4, 4, 4, 4, 4, 4, 67, 67, 67, 67, 101, 101), a5b);

        // Integer overflow trap: comparator must not subtract Integer.MIN_VALUE from MAX_VALUE
        int v1 = Integer.MAX_VALUE;
        int v2 = Integer.MIN_VALUE;
        int v3 = Integer.MAX_VALUE - 1;
        int v4 = Integer.MIN_VALUE + 1;
        List<Integer> a6 = new ArrayList<>(Arrays.asList(v1, v2, v3, v4, v4, v3, v2, v1));
        P2J4.sortByElementFrequency(a6);
        assertEquals(Arrays.asList(v2, v2, v4, v4, v3, v3, v1, v1), a6);

        // Negative values with different frequencies
        List<Integer> a7 = new ArrayList<>(Arrays.asList(-5, -5, -5, 3, 3, -1));
        P2J4.sortByElementFrequency(a7);
        assertEquals(Arrays.asList(-5, -5, -5, 3, 3, -1), a7);
    }

    @Test public void testSortByElementFrequencyProperties() {
        // Property: sorted result is a permutation of the input
        // Property: descending frequency, with ascending value as tiebreaker
        Random rng = new Random(42);
        for (int trial = 0; trial < 300; trial++) {
            int distinct = rng.nextInt(10) + 1;
            List<Integer> items = new ArrayList<>();
            for (int d = 0; d < distinct; d++) {
                int val = rng.nextInt(2000) - 1000;
                int rep = rng.nextInt(20) + 1;
                for (int r = 0; r < rep; r++) { items.add(val); }
            }
            Collections.shuffle(items, rng);
            List<Integer> copy = new ArrayList<>(items);
            Collections.sort(copy);

            P2J4.sortByElementFrequency(items);

            // Same multiset
            List<Integer> sorted = new ArrayList<>(items);
            Collections.sort(sorted);
            assertEquals("Permutation check", copy, sorted);

            // Verify ordering: frequency descending, value ascending as tiebreaker
            Map<Integer, Integer> freq = new HashMap<>();
            for (int e : items) { freq.put(e, freq.getOrDefault(e, 0) + 1); }
            for (int i = 1; i < items.size(); i++) {
                int a = items.get(i - 1), b = items.get(i);
                int fa = freq.get(a), fb = freq.get(b);
                assertTrue("Ordering violation at index " + i +
                                ": " + a + "(freq=" + fa + ") before " + b + "(freq=" + fb + ")",
                        fa > fb || (fa == fb && a <= b));
            }
        }
    }

    @Test public void testSortByElementFrequencyFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        List<Integer> items = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            items.clear();
            for (int j = 0; j < i; j++) {
                int rep = rng.nextInt(100);
                int e = rng.nextInt(2_000_000_000) - 1_000_000_000;
                for (int k = 0; k < rep; k++) { items.add(e); }
            }
            Collections.shuffle(items, rng);
            P2J4.sortByElementFrequency(items);
            for (int e : items) { check.update(e); }
        }
        assertEquals(981235996L, check.getValue());
    }

    // ---------------------------------------------------------------
    // factorFactorial tests
    // ---------------------------------------------------------------

    @Test public void testFactorFactorialExplicit() {
        // Expected answers for factorials from 0 to 9
        List<List<Integer>> expected = Arrays.asList(
                Collections.emptyList(), // 0!
                Collections.emptyList(), // 1!
                Collections.singletonList(2), // 2!
                Arrays.asList(2, 3), // 3!
                Arrays.asList(2, 2, 2, 3), // 4!
                Arrays.asList(2, 2, 2, 3, 5), // 5!
                Arrays.asList(2, 2, 2, 2, 3, 3, 5), // 6!
                Arrays.asList(2, 2, 2, 2, 3, 3, 5, 7), // 7!
                Arrays.asList(2, 2, 2, 2, 2, 2, 2, 3, 3, 5, 7), // 8!
                Arrays.asList(2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 5, 7) // 9!
        );
        for (int n = 0; n < expected.size(); n++) {
            assertEquals("factorFactorial(" + n + ")", expected.get(n), P2J4.factorFactorial(n));
        }

        // 10! = 3628800
        assertEquals(Arrays.asList(2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 5, 5, 7),
                P2J4.factorFactorial(10));
    }

    @Test public void testFactorFactorialProductCheck() {
        // For small n, verify the product of factors equals n!
        long factorial = 1;
        for (int n = 0; n <= 20; n++) {
            List<Integer> factors = P2J4.factorFactorial(n);
            // Verify sorted
            for (int i = 1; i < factors.size(); i++) {
                assertTrue("Factors not sorted at n=" + n,
                        factors.get(i) >= factors.get(i - 1));
            }
            // Verify all factors are >= 2
            for (int f : factors) {
                assertTrue("Factor must be >= 2", f >= 2);
            }
            // Verify product equals n!
            if (n >= 2) { factorial *= n; }
            long product = 1;
            for (int f : factors) { product *= f; }
            long expectedFactorial = (n <= 1) ? 1 : factorial;
            assertEquals("Product of factors of " + n + "!",
                    expectedFactorial, product);
        }
    }

    @Test public void testFactorFactorialPrimality() {
        // For a range of n, verify every factor in the list is actually prime
        for (int n = 0; n <= 100; n++) {
            List<Integer> factors = P2J4.factorFactorial(n);
            for (int f : factors) {
                assertTrue("Non-prime factor " + f + " in factorFactorial(" + n + ")",
                        isPrime(f));
            }
        }
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n < 4) return true;
        if (n % 2 == 0 || n % 3 == 0) return false;
        for (int i = 5; (long) i * i <= n; i += 6) {
            if (n % i == 0 || n % (i + 2) == 0) return false;
        }
        return true;
    }

    @Test public void testFactorFactorialFuzz() {
        CRC32 check = new CRC32();
        for (int n = 0; n < 1000; n++) {
            List<Integer> ans = P2J4.factorFactorial(n);
            check.update(ans.size());
            for (int e : ans) { check.update(e); }
        }
        assertEquals(775274151L, check.getValue());
    }
}