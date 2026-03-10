import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class PermutationsTestOne {

    // --- chain explicit tests ---

    @Test public void testChainIdentity() {
        // Identity is the neutral element of chain on both sides.
        int[] id = {0, 1, 2, 3};
        int[] p = {2, 0, 3, 1};
        assertArrayEquals(p, Permutations.chain(id, p));
        assertArrayEquals(p, Permutations.chain(p, id));
    }

    @Test public void testChainNonCommutative() {
        // Chain is generally not commutative.
        // a = 3-cycle (0,1,2), b = 3-cycle (1,2,3): overlapping cycles don't commute.
        int[] a = {1, 2, 0, 3};
        int[] b = {0, 2, 3, 1};
        assertArrayEquals(new int[]{1, 0, 3, 2}, Permutations.chain(a, b));
        assertArrayEquals(new int[]{2, 3, 0, 1}, Permutations.chain(b, a));
    }

    @Test public void testChainAssociative() {
        // Chain must be associative: chain(chain(a,b),c) == chain(a, chain(b,c))
        int[] a = {2, 0, 3, 1};
        int[] b = {1, 3, 0, 2};
        int[] c = {3, 2, 1, 0};
        assertArrayEquals(Permutations.chain(Permutations.chain(a, b), c),
                Permutations.chain(a, Permutations.chain(b, c)));
    }

    @Test public void testChainSizeOne() {
        assertArrayEquals(new int[]{0}, Permutations.chain(new int[]{0}, new int[]{0}));
    }

    @Test public void testChainSizeTwo() {
        int[] swap = {1, 0};
        int[] id = {0, 1};
        // Swap composed with itself is identity
        assertArrayEquals(id, Permutations.chain(swap, swap));
        // Identity composed with swap is swap
        assertArrayEquals(swap, Permutations.chain(id, swap));
    }

    @Test public void testChainSpecSemantics() {
        // From spec: "if p2[3]==4 and p1[4]==9, then p[3]==9"
        // Build size-10 permutations where p2 swaps 3<->4 and p1 swaps 4<->9.
        int[] p1 = {0, 1, 2, 3, 9, 5, 6, 7, 8, 4};
        int[] p2 = {0, 1, 2, 4, 3, 5, 6, 7, 8, 9};
        int[] result = Permutations.chain(p1, p2);
        assertEquals(9, result[3]);
    }

    // --- inverse explicit tests ---

    @Test public void testInverseSpecExample() {
        // From spec: inverse of {2,1,4,0,5,3} is {3,1,0,5,2,4}
        assertArrayEquals(new int[]{3, 1, 0, 5, 2, 4},
                Permutations.inverse(new int[]{2, 1, 4, 0, 5, 3}));
    }

    @Test public void testInverseOfIdentity() {
        int[] id = {0, 1, 2, 3};
        assertArrayEquals(id, Permutations.inverse(id));
    }

    @Test public void testInverseOfInverse() {
        // inverse(inverse(p)) == p
        int[] p = {3, 0, 1, 2};
        assertArrayEquals(p, Permutations.inverse(Permutations.inverse(p)));
    }

    @Test public void testInverseChainProducesIdentity() {
        // chain(p, inverse(p)) == identity, and chain(inverse(p), p) == identity
        int[] p = {3, 0, 1, 2};
        int[] inv = Permutations.inverse(p);
        int[] id = {0, 1, 2, 3};
        assertArrayEquals(id, Permutations.chain(p, inv));
        assertArrayEquals(id, Permutations.chain(inv, p));
    }

    @Test public void testInverseSelfInverse() {
        // An involution (self-inverse permutation): a swap
        int[] swap = {1, 0};
        assertArrayEquals(swap, Permutations.inverse(swap));

        // Larger involution: product of disjoint transpositions
        int[] invol = {1, 0, 3, 2, 4};
        assertArrayEquals(invol, Permutations.inverse(invol));
    }

    @Test public void testInverseSizeOne() {
        assertArrayEquals(new int[]{0}, Permutations.inverse(new int[]{0}));
    }

    // --- square explicit tests ---

    @Test public void testSquareOfIdentity() {
        int[] id = {0, 1, 2, 3};
        assertArrayEquals(id, Permutations.square(id));
    }

    @Test public void testSquareOfInvolution() {
        // Square of any involution (self-inverse) is identity
        int[] swap = {1, 0};
        assertArrayEquals(new int[]{0, 1}, Permutations.square(swap));

        int[] rev = {3, 2, 1, 0};
        assertArrayEquals(new int[]{0, 1, 2, 3}, Permutations.square(rev));
    }

    @Test public void testSquareOfThreeCycle() {
        // (0,1,2): square gives the other 3-cycle direction
        int[] p = {1, 2, 0};
        assertArrayEquals(new int[]{2, 0, 1}, Permutations.square(p));
        // And cubing gives identity
        assertArrayEquals(new int[]{0, 1, 2},
                Permutations.chain(p, Permutations.square(p)));
    }

    @Test public void testSquareOfFourCycle() {
        // (0,1,2,3): square gives two disjoint transpositions
        int[] p = {1, 2, 3, 0};
        assertArrayEquals(new int[]{2, 3, 0, 1}, Permutations.square(p));
        // And square of square is identity (order 4)
        assertArrayEquals(new int[]{0, 1, 2, 3},
                Permutations.square(Permutations.square(p)));
    }

    @Test public void testSquareEqualsChainWithSelf() {
        // square(p) must equal chain(p, p)
        int[] p = {5, 0, 4, 2, 3, 1};
        assertArrayEquals(Permutations.chain(p, p), Permutations.square(p));
    }

    // --- power explicit tests ---

    @Test public void testPowerZero() {
        // p^0 = identity, regardless of p
        int[] p = {2, 0, 1};
        assertArrayEquals(new int[]{0, 1, 2}, Permutations.power(p, 0));

        int[] q = {4, 3, 2, 1, 0};
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, Permutations.power(q, 0));
    }

    @Test public void testPowerOne() {
        int[] p = {1, 2, 0};
        assertArrayEquals(p, Permutations.power(p, 1));
    }

    @Test public void testPowerTwo() {
        int[] p = {1, 2, 0};
        assertArrayEquals(Permutations.square(p), Permutations.power(p, 2));
    }

    @Test public void testPowerMatchesOrder() {
        // 3-cycle has order 3
        int[] p3 = {1, 2, 0};
        assertArrayEquals(new int[]{0, 1, 2}, Permutations.power(p3, 3));
        assertArrayEquals(p3, Permutations.power(p3, 4)); // 4 % 3 = 1

        // 4-cycle has order 4
        int[] p4 = {1, 2, 3, 0};
        assertArrayEquals(new int[]{0, 1, 2, 3}, Permutations.power(p4, 4));
        assertArrayEquals(p4, Permutations.power(p4, 5)); // 5 % 4 = 1

        // 5-cycle has order 5
        int[] p5 = {1, 2, 3, 4, 0};
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, Permutations.power(p5, 5));
    }

    @Test public void testPowerNegativeOne() {
        // p^(-1) == inverse(p)
        int[] p = {1, 2, 0};
        assertArrayEquals(Permutations.inverse(p), Permutations.power(p, -1));

        int[] q = {3, 0, 1, 2};
        assertArrayEquals(Permutations.inverse(q), Permutations.power(q, -1));
    }

    @Test public void testPowerNegative() {
        // p^(-k) == inverse(p)^k
        int[] p = {1, 2, 0}; // 3-cycle
        // p^(-2) should equal p itself (since inv^2 = p for order-3)
        assertArrayEquals(new int[]{1, 2, 0}, Permutations.power(p, -2));
        // p^(-3) = identity
        assertArrayEquals(new int[]{0, 1, 2}, Permutations.power(p, -3));

        // 4-cycle
        int[] p4 = {1, 2, 3, 0};
        // p4^(-1) = [3,0,1,2]
        assertArrayEquals(new int[]{3, 0, 1, 2}, Permutations.power(p4, -1));
        // p4^(-4) = identity
        assertArrayEquals(new int[]{0, 1, 2, 3}, Permutations.power(p4, -4));
        // p4^(-99): 99 % 4 = 3, so inv^3 = p4^1
        assertArrayEquals(Permutations.power(p4, 1), Permutations.power(p4, -99));
    }

    @Test public void testPowerLarge() {
        // 4-cycle, order 4: p^(10^6) where 10^6 % 4 = 0 -> identity
        int[] p4 = {1, 2, 3, 0};
        assertArrayEquals(new int[]{0, 1, 2, 3}, Permutations.power(p4, 1_000_000));
        assertArrayEquals(p4, Permutations.power(p4, 1_000_001));

        // 5-cycle, order 5: p^(10^9 + 5) % 5 = 0 -> identity
        int[] p5 = {1, 2, 3, 4, 0};
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, Permutations.power(p5, 1_000_000_005));
    }

    @Test public void testPowerSizeOne() {
        assertArrayEquals(new int[]{0}, Permutations.power(new int[]{0}, 42));
        assertArrayEquals(new int[]{0}, Permutations.power(new int[]{0}, -17));
    }

    @Test public void testPowerLargerPermutation() {
        // p6 = {5,0,4,2,3,1} has order 3
        int[] p6 = {5, 0, 4, 2, 3, 1};
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5}, Permutations.power(p6, 3));
        assertArrayEquals(new int[]{1, 5, 3, 4, 2, 0}, Permutations.power(p6, 2));
        // p6^(-1) == p6^2 (since order 3)
        assertArrayEquals(Permutations.power(p6, 2), Permutations.power(p6, -1));
    }

    @Test public void testPowerAlgebraicConsistency() {
        // p^a * p^b == p^(a+b) for various a, b
        int[] p = {2, 4, 1, 0, 3}; // some permutation
        for (int a : new int[]{0, 1, 3, 7, -2, -5}) {
            for (int b : new int[]{0, 1, 2, 4, -1, -3}) {
                int[] pa = Permutations.power(p, a);
                int[] pb = Permutations.power(p, b);
                int[] pab = Permutations.power(p, a + b);
                assertArrayEquals("p^" + a + " * p^" + b + " should equal p^" + (a+b),
                        pab, Permutations.chain(pa, pb));
            }
        }
    }

    // --- CRC mass tests ---

    @Test public void testInverseHundred() {
        testMass(100, 2135030874L, 0);
    }

    @Test public void testInverseHundredThousand() {
        testMass(100_000, 1999991L, 0);
    }

    @Test public void testSquareHundred() {
        testMass(100, 2783838539L, 1);
    }

    @Test public void testSquareHundredThousand() {
        testMass(100_000, 2415705031L, 1);
    }

    @Test public void testPowerHundred() {
        testMass(100, 4179039232L, 2);
    }

    @Test public void testPowerTwoThousand() {
        testMass(2000, 778167724L, 2);
    }

    @Test public void testPowerMillion() {
        testMass(1_000_000, 3234036070L, 2);
    }

    private static void testMass(int n, long expected, int mode) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int goal = 2, nn = 2;
        for(int i = 0; i < n; i++) {
            if(i == goal) { nn++; goal = 2 * goal; }
            int[] perm = new int[nn];
            for(int j = 0; j < nn; j++) { perm[j] = j; }
            for(int k = 0; k < nn; k++) {
                int s = rng.nextInt(nn - k) + k;
                int tmp = perm[k]; perm[k] = perm[s]; perm[s] = tmp;
            }
            int[] res = null;
            if(mode == 0) { res = Permutations.inverse(perm); }
            if(mode == 1) { res = Permutations.square(perm); }
            if(mode == 2) {
                res = Permutations.power(perm, i % 2 == 0 ? i : -i);
            }
            assert res != null;
            assertEquals(perm.length, res.length);
            try {
                check.update(Arrays.toString(res).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            if(mode == 0) {
                assertArrayEquals(perm, Permutations.inverse(res));
            }
        }
        assertEquals(expected, check.getValue());
    }
}