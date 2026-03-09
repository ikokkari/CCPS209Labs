import org.junit.Test;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZeckendorfTest {

    // --- Explicit tests ---

    @Test public void testEncodeSingleOne() {
        // 1 is the first Fibonacci number; encoding is "1" + separator "1" = "11".
        List<BigInteger> items = Arrays.asList(BigInteger.ONE);
        String enc = Zeckendorf.encode(items);
        assertEquals("11", enc);
        assertEquals(items, Zeckendorf.decode(enc));
    }

    @Test public void testEncodeSingleSmallValues() {
        // 2 = fib(2): "01" + "1" = "011"
        List<BigInteger> a2 = Arrays.asList(BigInteger.TWO);
        assertEquals("011", Zeckendorf.encode(a2));
        assertEquals(a2, Zeckendorf.decode("011"));

        // 3 = fib(3): "001" + "1" = "0011"
        List<BigInteger> a3 = Arrays.asList(new BigInteger("3"));
        assertEquals("0011", Zeckendorf.encode(a3));
        assertEquals(a3, Zeckendorf.decode("0011"));

        // 4 = 3+1 = fib(3)+fib(1): "101" + "1" = "1011"
        List<BigInteger> a4 = Arrays.asList(new BigInteger("4"));
        assertEquals("1011", Zeckendorf.encode(a4));
        assertEquals(a4, Zeckendorf.decode("1011"));
    }

    @Test public void testEncodeSpecExample() {
        // Example from the spec: [4, 36, 127]
        List<BigInteger> items = Arrays.asList(
                new BigInteger("4"), new BigInteger("36"), new BigInteger("127")
        );
        assertEquals("101101000001110100001011", Zeckendorf.encode(items));
        assertEquals(items, Zeckendorf.decode("101101000001110100001011"));
    }

    @Test public void testEncodeExplicitFromOriginal() {
        List<BigInteger> a1 = Arrays.asList(
                new BigInteger("1"), new BigInteger("82")
        );
        String s1 = Zeckendorf.encode(a1);
        assertEquals("111001001011", s1);
        assertEquals(a1, Zeckendorf.decode(s1));

        List<BigInteger> a2 = Arrays.asList(
                new BigInteger("5"), new BigInteger("85"),
                new BigInteger("172"), new BigInteger("345")
        );
        String s2 = Zeckendorf.encode(a2);
        assertEquals("0001110001010110101001000110100001001011", s2);
        assertEquals(a2, Zeckendorf.decode(s2));

        List<BigInteger> a3 = Arrays.asList(
                new BigInteger("4"), new BigInteger("30"),
                new BigInteger("139"), new BigInteger("353")
        );
        String s3 = Zeckendorf.encode(a3);
        assertEquals("101110001011001001010110100101001011", s3);
        assertEquals(a3, Zeckendorf.decode(s3));
    }

    @Test public void testEncodeReversedOrder() {
        // Same numbers in reverse order produce different encoding.
        List<BigInteger> a4 = Arrays.asList(
                new BigInteger("353"), new BigInteger("139"),
                new BigInteger("30"), new BigInteger("4")
        );
        String s4 = Zeckendorf.encode(a4);
        assertEquals("010010100101100100101011100010111011", s4);
        assertEquals(a4, Zeckendorf.decode(s4));
    }

    @Test public void testEncodeConsecutiveSmallIntegers() {
        // [1, 2, 3]: "11" + "011" + "0011" = "110110011"
        List<BigInteger> items = Arrays.asList(
                BigInteger.ONE, BigInteger.TWO, new BigInteger("3")
        );
        assertEquals("110110011", Zeckendorf.encode(items));
        assertEquals(items, Zeckendorf.decode("110110011"));
    }

    @Test public void testEncodeRepeatedOnes() {
        // [1, 1, 1]: "11" + "11" + "11" = "111111"
        List<BigInteger> items = Arrays.asList(
                BigInteger.ONE, BigInteger.ONE, BigInteger.ONE
        );
        assertEquals("111111", Zeckendorf.encode(items));
        assertEquals(items, Zeckendorf.decode("111111"));
    }

    @Test public void testEncodeHundred() {
        // 100 = 89 + 8 + 3: positions 2,4,9 -> "00101000011"
        List<BigInteger> items = Arrays.asList(new BigInteger("100"));
        String enc = Zeckendorf.encode(items);
        assertEquals("00101000011", enc);
        assertEquals(items, Zeckendorf.decode(enc));
    }

    @Test public void testEncodingContainsNoInternalConsecutiveOnes() {
        // Within each number's encoding (before separator), "11" should not appear.
        // The only "11" patterns should be at separators.
        List<BigInteger> items = Arrays.asList(
                new BigInteger("42"), new BigInteger("100"), new BigInteger("255")
        );
        String enc = Zeckendorf.encode(items);
        List<BigInteger> back = Zeckendorf.decode(enc);
        assertEquals(items, back);
        // Verify all characters are '0' or '1'.
        for(int i = 0; i < enc.length(); i++) {
            assertTrue(enc.charAt(i) == '0' || enc.charAt(i) == '1');
        }
    }

    @Test public void testEncodeLargeValue() {
        // A large value to exercise BigInteger arithmetic.
        // 10^18 should work fine.
        BigInteger big = new BigInteger("1000000000000000000");
        List<BigInteger> items = Arrays.asList(big);
        String enc = Zeckendorf.encode(items);
        List<BigInteger> back = Zeckendorf.decode(enc);
        assertEquals(items, back);
    }

    @Test public void testRoundtripLargerList() {
        // A longer list of varied sizes.
        List<BigInteger> items = Arrays.asList(
                new BigInteger("1"), new BigInteger("2"), new BigInteger("3"),
                new BigInteger("5"), new BigInteger("8"), new BigInteger("13"),
                new BigInteger("21"), new BigInteger("34"), new BigInteger("55"),
                new BigInteger("89")
        );
        String enc = Zeckendorf.encode(items);
        assertEquals(items, Zeckendorf.decode(enc));
    }

    @Test public void testRoundtripFibonacciNumbers() {
        // Fibonacci numbers themselves should each encode to a single '1' at
        // the right position, plus the separator.
        List<BigInteger> fibs = new ArrayList<>();
        BigInteger a = BigInteger.ONE, b = BigInteger.TWO;
        for(int i = 0; i < 15; i++) {
            fibs.add(a);
            BigInteger temp = b;
            b = a.add(b);
            a = temp;
        }
        String enc = Zeckendorf.encode(fibs);
        assertEquals(fibs, Zeckendorf.decode(enc));
    }

    // --- Mass / CRC tests ---

    @Test public void massTestOneThousand() {
        massTest(1000, 2438377908L);
    }

    @Test public void massTestThreeThousand() {
        massTest(3000, 61550952L);
    }

    private void massTest(int trials, long expected) {
        Random rng = new Random(12345);
        BigInteger two = new BigInteger("2");
        CRC32 check = new CRC32();
        int count = 0, goal = 5, n = 2;
        for(int i = 1; i <= trials; i++) {
            List<BigInteger> orig = new ArrayList<>();
            BigInteger b = new BigInteger("" + (1 + rng.nextInt(i * i)));
            orig.add(b);
            for(int j = 0; j < n; j++) {
                b = b.multiply(two);
                b = b.add(new BigInteger("" + rng.nextInt(100)));
                orig.add(b);
            }
            Collections.shuffle(orig, rng);
            String zits = Zeckendorf.encode(orig);
            check.update(zits.length());
            for(int k = 0; k < zits.length(); k++) {
                assertTrue(zits.charAt(k) == '0' || zits.charAt(k) == '1');
            }
            List<BigInteger> back = Zeckendorf.decode(zits);
            assertEquals(orig, back);
            if(++count == goal) {
                count = 0;
                goal = goal + 3;
                n += 1;
            }
        }
        assertEquals(expected, check.getValue());
    }
}