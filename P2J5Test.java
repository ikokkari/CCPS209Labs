import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J5Test {

    private static final int SEED = 12345;
    private static final BigInteger TWO = new BigInteger("2");

    // ---------------------------------------------------------------
    // fibonacciSum tests
    // ---------------------------------------------------------------

    @Test public void testFibonacciSumExplicit() {
        // Small values and exact Fibonacci numbers
        assertEquals("[1]", P2J5.fibonacciSum(BigInteger.ONE).toString());
        assertEquals("[2]", P2J5.fibonacciSum(new BigInteger("2")).toString());
        assertEquals("[3]", P2J5.fibonacciSum(new BigInteger("3")).toString());
        assertEquals("[3, 1]", P2J5.fibonacciSum(new BigInteger("4")).toString());
        assertEquals("[5]", P2J5.fibonacciSum(new BigInteger("5")).toString());
        assertEquals("[5, 1]", P2J5.fibonacciSum(new BigInteger("6")).toString());
        assertEquals("[5, 2]", P2J5.fibonacciSum(new BigInteger("7")).toString());
        assertEquals("[8]", P2J5.fibonacciSum(new BigInteger("8")).toString());
        assertEquals("[8, 1]", P2J5.fibonacciSum(new BigInteger("9")).toString());
        assertEquals("[8, 2]", P2J5.fibonacciSum(new BigInteger("10")).toString());

        // From original tests
        assertEquals("[13, 5]", P2J5.fibonacciSum(new BigInteger("18")).toString());
        assertEquals("[21]", P2J5.fibonacciSum(new BigInteger("21")).toString());
        assertEquals("[34, 8, 2]", P2J5.fibonacciSum(new BigInteger("44")).toString());
        assertEquals("[55, 21, 8, 2]", P2J5.fibonacciSum(new BigInteger("86")).toString());
        assertEquals("[89, 8, 3, 1]", P2J5.fibonacciSum(new BigInteger("101")).toString());
        assertEquals("[1597, 377, 34, 8, 3, 1]",
                P2J5.fibonacciSum(new BigInteger("2020")).toString());

        // Spec example
        assertEquals("[832040, 121393, 46368, 144, 55]",
                P2J5.fibonacciSum(new BigInteger("1000000")).toString());
    }

    @Test public void testFibonacciSumZeckendorfProperties() {
        // For a range of n, verify Zeckendorf representation properties:
        // 1) Sum equals n
        // 2) Descending order, all distinct
        // 3) No two consecutive Fibonacci numbers (checked via the property
        //    that consecutive Fibs F(k), F(k-1) satisfy F(k) - F(k-1) < F(k-1),
        //    while non-consecutive Fibs have F(k) - F(j) >= F(j) for j <= k-2)
        for (int val = 1; val <= 300; val++) {
            BigInteger n = BigInteger.valueOf(val);
            List<BigInteger> result = P2J5.fibonacciSum(n);

            // Sum must equal n
            BigInteger sum = BigInteger.ZERO;
            for (BigInteger f : result) { sum = sum.add(f); }
            assertEquals("Sum mismatch for n=" + val, n, sum);

            // Descending order and all distinct
            for (int i = 1; i < result.size(); i++) {
                assertTrue("Not strictly descending at n=" + val,
                        result.get(i).compareTo(result.get(i - 1)) < 0);
            }

            // No two consecutive Fibonacci numbers:
            // For consecutive Fibs F(k) > F(k-1), we have F(k) - F(k-1) = F(k-2) < F(k-1).
            // For non-consecutive F(k) > F(j) where j <= k-2, F(k) - F(j) >= F(k-1) >= F(j).
            for (int i = 0; i < result.size() - 1; i++) {
                BigInteger diff = result.get(i).subtract(result.get(i + 1));
                assertTrue("Consecutive Fibonacci numbers for n=" + val +
                                ": " + result.get(i) + " and " + result.get(i + 1),
                        diff.compareTo(result.get(i + 1)) >= 0);
            }
        }
    }

    @Test public void testFibonacciSumFuzz() {
        CRC32 check = new CRC32();
        Random rng = new Random(SEED);
        BigInteger curr = BigInteger.ONE;
        for (int i = 0; i < 500; i++) {
            List<BigInteger> result = P2J5.fibonacciSum(curr);
            // Verify sum equals input
            BigInteger sum = BigInteger.ZERO;
            for (BigInteger b : result) { sum = sum.add(b); }
            assertEquals("Sum mismatch for " + curr, curr, sum);
            for (BigInteger b : result) {
                try {
                    check.update(b.toString().getBytes("UTF-8"));
                } catch (UnsupportedEncodingException ignored) { }
            }
            curr = curr.add(new BigInteger("" + (rng.nextInt(5) + 1)));
            curr = curr.multiply(TWO);
        }
        assertEquals(3283204958L, check.getValue());
    }

    // ---------------------------------------------------------------
    // sevenZero tests
    // ---------------------------------------------------------------

    @Test public void testSevenZeroExplicit() {
        // Small values
        assertEquals(new BigInteger("7"), P2J5.sevenZero(1));
        assertEquals(new BigInteger("70"), P2J5.sevenZero(2));
        assertEquals(new BigInteger("777"), P2J5.sevenZero(3));
        assertEquals(new BigInteger("700"), P2J5.sevenZero(4));
        assertEquals(new BigInteger("70"), P2J5.sevenZero(5));
        assertEquals(new BigInteger("7770"), P2J5.sevenZero(6));
        assertEquals(new BigInteger("7"), P2J5.sevenZero(7));
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(8));
        assertEquals(new BigInteger("777777777"), P2J5.sevenZero(9));
        assertEquals(new BigInteger("70"), P2J5.sevenZero(10));
        assertEquals(new BigInteger("77"), P2J5.sevenZero(11));

        // Powers of 2: need trailing zeros
        assertEquals(new BigInteger("70000"), P2J5.sevenZero(16));
        assertEquals(new BigInteger("700000"), P2J5.sevenZero(32));

        // Powers of 5: need trailing zeros
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(125));
        assertEquals(new BigInteger("70000"), P2J5.sevenZero(625));

        // Mixed cases
        assertEquals(new BigInteger("7770"), P2J5.sevenZero(42));
        assertEquals(new BigInteger("70"), P2J5.sevenZero(70));
        assertEquals(new BigInteger("77"), P2J5.sevenZero(77));
        assertEquals(new BigInteger("700"), P2J5.sevenZero(100));
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(200));
        assertEquals(new BigInteger("77700"), P2J5.sevenZero(300));
        assertEquals(new BigInteger("70000"), P2J5.sevenZero(400));
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(500));
        assertEquals(new BigInteger("7777777770"), P2J5.sevenZero(666));
        assertEquals(new BigInteger("777700"), P2J5.sevenZero(2020));
        assertEquals(new BigInteger("70000000"), P2J5.sevenZero(625000));

        // Pure sevens (not divisible by 2 or 5)
        assertEquals(new BigInteger("777777777777777777777777777777777777777777777777777777"),
                P2J5.sevenZero(513));
    }

    @Test public void testSevenZeroProperties() {
        // For each result, verify:
        // 1) Positive
        // 2) Divisible by n
        // 3) Matches the seven-zero digit pattern
        for (int n = 1; n <= 200; n++) {
            BigInteger result = P2J5.sevenZero(n);
            BigInteger bn = BigInteger.valueOf(n);

            assertTrue("Result must be positive for n=" + n,
                    result.compareTo(BigInteger.ZERO) > 0);

            assertEquals("Result not divisible by n=" + n,
                    BigInteger.ZERO, result.mod(bn));

            String s = result.toString();
            int firstNonSeven = 0;
            while (firstNonSeven < s.length() && s.charAt(firstNonSeven) == '7') {
                firstNonSeven++;
            }
            assertTrue("Must contain at least one 7 for n=" + n, firstNonSeven > 0);
            for (int j = firstNonSeven; j < s.length(); j++) {
                assertEquals("Non-zero digit after sevens for n=" + n, '0', s.charAt(j));
            }
        }
    }

    @Test public void testSevenZeroMinimality() {
        // For small n, verify no smaller seven-zero number is divisible by n
        for (int n = 1; n <= 50; n++) {
            BigInteger result = P2J5.sevenZero(n);
            BigInteger bn = BigInteger.valueOf(n);
            String rs = result.toString();
            int maxDigits = rs.length();
            for (int d = 1; d <= maxDigits; d++) {
                StringBuilder sevens = new StringBuilder();
                for (int s = 1; s <= d; s++) {
                    sevens.append('7');
                    int zeros = d - s;
                    StringBuilder num = new StringBuilder(sevens);
                    for (int z = 0; z < zeros; z++) num.append('0');
                    BigInteger candidate = new BigInteger(num.toString());
                    if (candidate.compareTo(result) < 0) {
                        assertTrue("Smaller seven-zero " + candidate + " divisible by " + n,
                                !candidate.mod(bn).equals(BigInteger.ZERO));
                    }
                }
            }
        }
    }

    @Test public void testSevenZeroFuzz() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int curr = 2;
        for (int i = 2; i < 400; i++) {
            BigInteger result = P2J5.sevenZero(curr);
            // Verify divisibility on every fuzz result
            assertEquals("Not divisible for n=" + curr,
                    BigInteger.ZERO, result.mod(BigInteger.valueOf(curr)));
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
            curr += rng.nextInt(5) + 1;
        }
        assertEquals(916368163L, check.getValue());
    }
}