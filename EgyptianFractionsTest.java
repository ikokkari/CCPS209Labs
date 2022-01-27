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
            List<BigInteger> gRes = EgyptianFractions.greedy(apb);
            try {
                check.update(gRes.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            assertTrue(addsUp(gRes, apb));
        }
        assertEquals(93321355L, check.getValue());
    }
    
    @Test public void testSplitting() {
        /* Explicit test cases */
        assertEquals("[2, 4, 8, 16]", EgyptianFractions.splitting(new Fraction(15, 16)).toString());
        assertEquals("[2, 7, 14]", EgyptianFractions.splitting(new Fraction(20, 28)).toString());
        assertEquals("[7, 8, 56]", EgyptianFractions.splitting(new Fraction(2, 7)).toString());
        assertEquals("[3, 9, 10, 27, 90]", EgyptianFractions.splitting(new Fraction(16, 27)).toString());
        assertEquals("[3, 11, 33, 132]", EgyptianFractions.splitting(new Fraction(61, 132)).toString());
        assertEquals("[9, 10, 17, 90, 153, 154, 23562]", EgyptianFractions.splitting(new Fraction(5, 17)).toString());
        assertEquals("[5, 15]", EgyptianFractions.splitting(new Fraction(4, 15)).toString());
        assertEquals("[11, 21, 22, 41, 42, 231, 431, 462, 492, 861, 862, 1722, 371091, 742182]",
            EgyptianFractions.splitting(new Fraction(121, 492)).toString());
        assertEquals("[6, 11, 12, 66, 132]", EgyptianFractions.splitting(new Fraction(132, 363)).toString());
        assertEquals("[13, 14, 65, 66, 182, 4290]", EgyptianFractions.splitting(new Fraction(12, 65)).toString());
        assertEquals("[9, 10, 17, 90, 153, 154, 23562]", EgyptianFractions.splitting(new Fraction(5, 17)).toString());
        assertEquals("[3, 4, 12, 27, 28, 756]", EgyptianFractions.splitting(new Fraction(20, 27)).toString());
        
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
            List<BigInteger> gRes = EgyptianFractions.splitting(apb);
            try {
                check.update(gRes.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            assertTrue(addsUp(gRes, apb));
        }
        assertEquals(2886553470L, check.getValue());
    }
}