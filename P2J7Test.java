import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J7Test {

    private static final int SEED = 76543;

    // ---------------------------------------------------------------
    // josephus tests
    // ---------------------------------------------------------------

    @Test public void testJosephusExplicit() {
        // Singleton: always returns the single element regardless of k
        assertEquals("[123.456]",
                P2J7.josephus(Collections.singletonList(123.456), 99).toString());
        assertEquals("[42]",
                P2J7.josephus(Collections.singletonList(42), 1).toString());

        // k=1: sequential elimination in original order
        assertEquals("[1, 2, 3, 4, 5]",
                P2J7.josephus(Arrays.asList(1, 2, 3, 4, 5), 1).toString());

        // k=2: classic Josephus
        assertEquals("[moe, rob, joe, tom, bob]",
                P2J7.josephus(Arrays.asList("joe", "moe", "bob", "rob", "tom"), 2).toString());

        // k=3
        assertEquals("[17, 99, 5, 42]",
                P2J7.josephus(Arrays.asList(42, 99, 17, 5), 3).toString());

        // Spec example: k=9 with 6 men
        assertEquals("[ringo, ross, ted, rachel, alice, bob]",
                P2J7.josephus(Arrays.asList("ross", "ted", "ringo", "alice", "bob", "rachel"), 9).toString());

        // k equals list size
        assertEquals("[D, A, C, B]",
                P2J7.josephus(Arrays.asList('A', 'B', 'C', 'D'), 4).toString());

        // k larger than list size
        assertEquals("[C, D, A, B]",
                P2J7.josephus(Arrays.asList('A', 'B', 'C', 'D'), 7).toString());

        // Unicode characters from original
        List<Character> a4 = Arrays.asList(
                '\u047C', '\u042b', '\u0413', '\u042f', '\u04cb', '\u0410', '\u0415'
        );
        assertEquals("[\u04cb, \u0413, \u042b, \u042f, \u0415, \u047C, \u0410]",
                P2J7.josephus(a4, 5).toString());

        // Two elements
        assertEquals("[B, A]", P2J7.josephus(Arrays.asList('A', 'B'), 2).toString());
        assertEquals("[A, B]", P2J7.josephus(Arrays.asList('A', 'B'), 1).toString());
    }

    @Test public void testJosephusProperties() {
        // Property: result is a permutation of the input (same elements, same count)
        // Property: result has the same size as input
        // Property: original list is not modified
        Random rng = new Random(42);
        for (int trial = 0; trial < 300; trial++) {
            int n = rng.nextInt(20) + 1;
            int k = rng.nextInt(3 * n) + 1;
            List<Integer> items = new ArrayList<>();
            for (int j = 0; j < n; j++) items.add(j);
            List<Integer> original = new ArrayList<>(items);
            List<Integer> result = P2J7.josephus(items, k);

            // Original not modified
            assertEquals("Original modified", original, items);
            // Same size
            assertEquals(n, result.size());
            // Same elements (permutation)
            List<Integer> sortedResult = new ArrayList<>(result);
            Collections.sort(sortedResult);
            List<Integer> sortedOriginal = new ArrayList<>(items);
            Collections.sort(sortedOriginal);
            assertEquals("Not a permutation", sortedOriginal, sortedResult);
        }
    }

    @Test public void testJosephusFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        List<String> people = Arrays.asList(
                "bob", "ted", "alice", "ringo",
                "ross", "rachel", "joey",
                "phoebe", "chandler", "monica",
                "charlie", "alan", "walden"
        );
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String next = people.get(i % people.size());
            if (i >= people.size()) { next += i; }
            items.add(next);
            int k = rng.nextInt(2 * i + 2) + 1;
            items = P2J7.josephus(items, k);
            try {
                check.update(items.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
        }
        assertEquals(3746131365L, check.getValue());
    }

    // ---------------------------------------------------------------
    // huntingtonHill tests
    // ---------------------------------------------------------------

    @Test public void testHuntingtonHillExplicit() {
        // Single state: gets all seats
        assertArrayEquals(new int[]{5}, P2J7.huntingtonHill(new int[]{42}, 5));
        assertArrayEquals(new int[]{1}, P2J7.huntingtonHill(new int[]{100}, 1));

        // Two states, close populations
        assertArrayEquals(new int[]{2, 2}, P2J7.huntingtonHill(new int[]{3, 4}, 4));
        assertArrayEquals(new int[]{4, 3}, P2J7.huntingtonHill(new int[]{18, 17}, 7));

        // Multiple states
        assertArrayEquals(new int[]{2, 1, 1, 1, 1, 2},
                P2J7.huntingtonHill(new int[]{17, 3, 4, 10, 11, 14}, 8));

        assertArrayEquals(new int[]{1, 1, 2, 3, 3, 4, 5, 6},
                P2J7.huntingtonHill(new int[]{13, 15, 20, 33, 45, 55, 60, 82}, 25));

        // Proportional: 1+2+...+10 = 55 seats, pops 1..10
        assertArrayEquals(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10},
                P2J7.huntingtonHill(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}, 55));

        // Equal populations: tiebreak by index (earlier state gets seat first)
        assertArrayEquals(new int[]{2, 1, 1},
                P2J7.huntingtonHill(new int[]{100, 100, 100}, 4));
        assertArrayEquals(new int[]{2, 2, 2},
                P2J7.huntingtonHill(new int[]{100, 100, 100}, 6));

        // Minimum seats = number of states (each gets 1)
        assertArrayEquals(new int[]{1, 1, 1},
                P2J7.huntingtonHill(new int[]{50, 100, 200}, 3));
    }

    @Test public void testHuntingtonHillOverflow() {
        // Populations near int limits: must use Fraction/BigInteger, not int arithmetic
        assertArrayEquals(new int[]{34, 33, 33},
                P2J7.huntingtonHill(new int[]{1000000, 999999, 999998}, 100));

        assertArrayEquals(new int[]{334, 333, 333},
                P2J7.huntingtonHill(new int[]{1000000000, 999999999, 999999998}, 1000));

        // Two states with populations differing by 1, both large
        int[] bigPops = {999999999, 1000000000};
        int[] result = P2J7.huntingtonHill(bigPops, 10);
        assertEquals("Seats must sum to total", 10, result[0] + result[1]);
        // The larger state should get at least as many seats
        assertTrue("Larger state should get >= seats", result[1] >= result[0]);
    }

    @Test public void testHuntingtonHillProperties() {
        // For random inputs, verify:
        // 1) Seats sum to total
        // 2) Every state gets at least 1 seat
        // 3) Result array has correct length
        Random rng = new Random(42);
        for (int trial = 0; trial < 200; trial++) {
            int n = rng.nextInt(10) + 1;
            int[] pops = new int[n];
            for (int j = 0; j < n; j++) {
                pops[j] = rng.nextInt(10000) + 1;
            }
            int seats = n + rng.nextInt(5 * n + 1);
            int[] result = P2J7.huntingtonHill(pops, seats);

            assertEquals("Wrong result length", n, result.length);
            int sum = 0;
            for (int j = 0; j < n; j++) {
                assertTrue("State " + j + " has no seat", result[j] >= 1);
                sum += result[j];
            }
            assertEquals("Seats don't sum to total", seats, sum);
        }
    }

    @Test public void testHuntingtonHillFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        HashSet<Integer> seen = new HashSet<>();
        int scale = 1;
        for (int i = 1; i < 400; i++) {
            if (i % 50 == 0) { scale *= 10; }
            int[] pops = new int[i + 1];
            seen.clear();
            for (int j = 0; j < pops.length; j++) {
                int p;
                do {
                    if (j > 0 && rng.nextInt(100) < 20) {
                        p = pops[j - 1] + 1;
                    } else {
                        p = (rng.nextInt(50) + 1) * scale;
                        p += rng.nextInt(p);
                    }
                } while (seen.contains(p));
                assert p > 0;
                seen.add(p);
                pops[j] = p;
            }
            int seats = 2 * i + rng.nextInt(10 * i + 2);
            int[] result = P2J7.huntingtonHill(pops, seats);
            // Verify seats sum
            int sum = 0;
            for (int s : result) sum += s;
            assertEquals("Seats sum mismatch at i=" + i, seats, sum);
            try {
                check.update(Arrays.toString(result).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
        }
        assertEquals(325067613L, check.getValue());
    }
}