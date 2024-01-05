import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EgyptianFractionsTest {
    
    private static boolean addsUp(List<BigInteger> egyptian, Fraction f) {
        Fraction sum = new Fraction(BigInteger.ZERO);
        for(BigInteger n: egyptian) {
            sum = sum.add(new Fraction(BigInteger.ONE, n));
        }
        return sum.equals(f);
    }
    
    @Test public void testGreedy() {
        /* Explicit test cases */
        assertEquals("[4, 28]", EgyptianFractions.greedy(new Fraction(2, 7)).toString());
        assertEquals("[2, 3, 10, 240]", EgyptianFractions.greedy(new Fraction(15, 16)).toString());
        assertEquals("[2, 3, 14, 231]", EgyptianFractions.greedy(new Fraction(10, 11)).toString());
        assertEquals("[2, 3, 21, 714]", EgyptianFractions.greedy(new Fraction(30, 34)).toString());
        assertEquals("[3, 263, 138075]", EgyptianFractions.greedy(new Fraction(59, 175)).toString());
        assertEquals("[3, 8, 264]", EgyptianFractions.greedy(new Fraction(61, 132)).toString());
        assertEquals("[2, 3, 13, 168, 46350, 3222437400]",
            EgyptianFractions.greedy(new Fraction(175, 191)).toString());
        assertEquals("[4, 23, 1564]", EgyptianFractions.greedy(new Fraction(5, 17)).toString());
        assertEquals("[2, 25, 674, 964663, 1861148442475]",
            EgyptianFractions.greedy(new Fraction(124, 229)).toString());
        assertEquals("[2, 13, 199, 52603, 4150560811, 34454310087467394631]",
            EgyptianFractions.greedy(new Fraction(71, 122)).toString());
        assertEquals("[19, 433, 249553, 93414800161, 17452649778145716451681]",
                EgyptianFractions.greedy(new Fraction(5, 91)).toString());
        
        /* Pseudorandom fuzz tester */
        CRC32 check = new CRC32();
        Random rng = new Random(444);
        for(int i = 0; i < 400; i++) {
            int a, b;
            do {
                b = rng.nextInt(10 * i + 1) + 6;
                a = rng.nextInt(b - 1) + 1;
            } while(a % 2 == 0 && b % 2 == 0);
            Fraction apb = new Fraction(a, b);
            // Verify that the result unit fractions add up to the original.
            List<BigInteger> gRes = EgyptianFractions.greedy(apb);
            Fraction result = new Fraction(0);
            for(BigInteger n: gRes) {
                result = result.add(new Fraction(BigInteger.ONE, n));
            }
            assertEquals(apb, result);
            try {
                check.update(gRes.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            assertTrue(addsUp(gRes, apb));
        }
        assertEquals(93321355L, check.getValue());
    }
    
    @Test public void testPairing() {
        /* Explicit test cases */
        assertEquals("[2, 4, 8, 16]", EgyptianFractions.pairing(new Fraction(15, 16)).toString());
        assertEquals("[2, 7, 14]", EgyptianFractions.pairing(new Fraction(20, 28)).toString());
        assertEquals("[4, 28]", EgyptianFractions.pairing(new Fraction(2, 7)).toString());
        assertEquals("[3, 5, 27, 45]", EgyptianFractions.pairing(new Fraction(16, 27)).toString());
        assertEquals("[3, 11, 33, 132]", EgyptianFractions.pairing(new Fraction(61, 132)).toString());
        assertEquals("[5, 17, 45, 77, 11781]", EgyptianFractions.pairing(new Fraction(5, 17)).toString());
        assertEquals("[5, 15]", EgyptianFractions.pairing(new Fraction(4, 15)).toString());
        assertEquals("[6, 21, 66, 116, 216, 492, 861, 26796, 93096, 185546, 68854450686]",
            EgyptianFractions.pairing(new Fraction(121, 492)).toString());
        assertEquals("[3, 33]", EgyptianFractions.pairing(new Fraction(132, 363)).toString());
        assertEquals("[7, 33, 91, 2145]", EgyptianFractions.pairing(new Fraction(12, 65)).toString());
        assertEquals("[5, 17, 45, 77, 11781]", EgyptianFractions.pairing(new Fraction(5, 17)).toString());
        assertEquals("[2, 6, 14, 378]", EgyptianFractions.pairing(new Fraction(20, 27)).toString());
        assertEquals("[23, 91, 2093]", EgyptianFractions.pairing(new Fraction(5, 91)).toString());
        
        /* Pseudorandom fuzz tester */
        CRC32 check = new CRC32();
        Random rng = new Random(444);
        for(int i = 0; i < 400; i++) {
            int a, b;
            do {
                b = rng.nextInt(10 * i + 1) + 6;
                a = rng.nextInt(b - 1) + 1;
            } while(a % 2 == 0 && b % 2 == 0);
            Fraction apb = new Fraction(a, b);
            // Verify that the result unit fractions add up to the original.
            List<BigInteger> pRes = EgyptianFractions.pairing(apb);
            Fraction result = new Fraction(0);
            for(BigInteger n: pRes) {
                result = result.add(new Fraction(BigInteger.ONE, n));
            }
            assertEquals(apb, result);
            try {
                check.update(pRes.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            assertTrue(addsUp(pRes, apb));
        }
        assertEquals(2989506769L, check.getValue());
    }
}