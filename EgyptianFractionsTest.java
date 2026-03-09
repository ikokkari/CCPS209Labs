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

public class EgyptianFractionsTest {

    private static Fraction sumOfUnitFractions(List<BigInteger> egyptian) {
        Fraction sum = new Fraction(BigInteger.ZERO);
        for(BigInteger n: egyptian) {
            sum = sum.add(new Fraction(BigInteger.ONE, n));
        }
        return sum;
    }

    private static void verifyEgyptian(List<BigInteger> egyptian, Fraction f) {
        // Must sum to the original fraction.
        assertEquals(f, sumOfUnitFractions(egyptian));
        // All denominators must be positive.
        for(BigInteger n: egyptian) {
            assertTrue("Denominator must be positive", n.signum() > 0);
        }
        // All denominators must be distinct.
        Set<BigInteger> seen = new HashSet<>(egyptian);
        assertEquals("All denominators must be distinct", egyptian.size(), seen.size());
        // Must be in ascending sorted order.
        for(int i = 1; i < egyptian.size(); i++) {
            assertTrue("Denominators must be in ascending order",
                    egyptian.get(i).compareTo(egyptian.get(i - 1)) > 0);
        }
    }

    // --- Greedy: explicit tests ---

    @Test public void testGreedyUnitFraction() {
        // A unit fraction 1/n should return just [n].
        assertEquals("[5]", EgyptianFractions.greedy(new Fraction(1, 5)).toString());
        assertEquals("[2]", EgyptianFractions.greedy(new Fraction(1, 2)).toString());
        assertEquals("[100]", EgyptianFractions.greedy(new Fraction(1, 100)).toString());
    }

    @Test public void testGreedySimpleFractions() {
        assertEquals("[2, 6]", EgyptianFractions.greedy(new Fraction(2, 3)).toString());
        assertEquals("[4, 28]", EgyptianFractions.greedy(new Fraction(2, 7)).toString());
        assertEquals("[4, 23, 1564]", EgyptianFractions.greedy(new Fraction(5, 17)).toString());
    }

    @Test public void testGreedyNearOne() {
        // Fractions close to 1 exercise deeper recursion.
        assertEquals("[2, 3, 10, 240]",
                EgyptianFractions.greedy(new Fraction(15, 16)).toString());
        assertEquals("[2, 3, 14, 231]",
                EgyptianFractions.greedy(new Fraction(10, 11)).toString());
    }

    @Test public void testGreedyReducibleFraction() {
        // 30/34 should reduce to 15/17 internally.
        assertEquals("[2, 3, 21, 714]",
                EgyptianFractions.greedy(new Fraction(30, 34)).toString());
    }

    @Test public void testGreedyLargerDenominators() {
        assertEquals("[3, 263, 138075]",
                EgyptianFractions.greedy(new Fraction(59, 175)).toString());
        assertEquals("[3, 8, 264]",
                EgyptianFractions.greedy(new Fraction(61, 132)).toString());
        assertEquals("[2, 25, 674, 964663, 1861148442475]",
                EgyptianFractions.greedy(new Fraction(124, 229)).toString());
    }

    @Test public void testGreedyExplosiveGrowth() {
        // Greedy can produce enormous denominators.
        assertEquals("[2, 3, 13, 168, 46350, 3222437400]",
                EgyptianFractions.greedy(new Fraction(175, 191)).toString());
        assertEquals("[2, 13, 199, 52603, 4150560811, 34454310087467394631]",
                EgyptianFractions.greedy(new Fraction(71, 122)).toString());
        assertEquals("[19, 433, 249553, 93414800161, 17452649778145716451681]",
                EgyptianFractions.greedy(new Fraction(5, 91)).toString());
    }

    @Test public void testGreedyVerifyStructure() {
        // Verify distinctness, order, and sum for a few cases.
        Fraction f = new Fraction(175, 191);
        verifyEgyptian(EgyptianFractions.greedy(f), f);
        f = new Fraction(71, 122);
        verifyEgyptian(EgyptianFractions.greedy(f), f);
        f = new Fraction(5, 91);
        verifyEgyptian(EgyptianFractions.greedy(f), f);
    }

    // --- Pairing: explicit tests ---

    @Test public void testPairingUnitFraction() {
        assertEquals("[5]", EgyptianFractions.pairing(new Fraction(1, 5)).toString());
        assertEquals("[2]", EgyptianFractions.pairing(new Fraction(1, 2)).toString());
    }

    @Test public void testPairingSimpleFractions() {
        assertEquals("[4, 28]", EgyptianFractions.pairing(new Fraction(2, 7)).toString());
        assertEquals("[5, 15]", EgyptianFractions.pairing(new Fraction(4, 15)).toString());
        assertEquals("[3, 33]", EgyptianFractions.pairing(new Fraction(132, 363)).toString());
    }

    @Test public void testPairingPowerOfTwoDenominator() {
        // 15/16 = 1/2 + 1/4 + 1/8 + 1/16: pairing produces clean binary split.
        assertEquals("[2, 4, 8, 16]",
                EgyptianFractions.pairing(new Fraction(15, 16)).toString());
    }

    @Test public void testPairingReducibleFraction() {
        // 20/28 reduces to 5/7.
        assertEquals("[2, 7, 14]",
                EgyptianFractions.pairing(new Fraction(20, 28)).toString());
    }

    @Test public void testPairingOddConflictResolution() {
        // Cases that exercise the odd-denominator conflict path.
        assertEquals("[3, 5, 27, 45]",
                EgyptianFractions.pairing(new Fraction(16, 27)).toString());
        assertEquals("[5, 17, 45, 77, 11781]",
                EgyptianFractions.pairing(new Fraction(5, 17)).toString());
        assertEquals("[7, 33, 91, 2145]",
                EgyptianFractions.pairing(new Fraction(12, 65)).toString());
    }

    @Test public void testPairingLarger() {
        assertEquals("[3, 11, 33, 132]",
                EgyptianFractions.pairing(new Fraction(61, 132)).toString());
        assertEquals("[2, 6, 14, 378]",
                EgyptianFractions.pairing(new Fraction(20, 27)).toString());
        assertEquals("[6, 21, 66, 116, 216, 492, 861, 26796, 93096, 185546, 68854450686]",
                EgyptianFractions.pairing(new Fraction(121, 492)).toString());
    }

    @Test public void testPairingVsGreedySpecExample() {
        // Spec highlights that 5/91 has very different results between algorithms.
        Fraction f = new Fraction(5, 91);
        List<BigInteger> g = EgyptianFractions.greedy(f);
        List<BigInteger> p = EgyptianFractions.pairing(f);
        assertEquals("[19, 433, 249553, 93414800161, 17452649778145716451681]", g.toString());
        assertEquals("[23, 91, 2093]", p.toString());
        // Both must sum to the same fraction.
        verifyEgyptian(g, f);
        verifyEgyptian(p, f);
        // Pairing produces fewer terms here.
        assertTrue(p.size() < g.size());
    }

    @Test public void testPairingVerifyStructure() {
        Fraction f = new Fraction(121, 492);
        verifyEgyptian(EgyptianFractions.pairing(f), f);
        f = new Fraction(16, 27);
        verifyEgyptian(EgyptianFractions.pairing(f), f);
        f = new Fraction(5, 17);
        verifyEgyptian(EgyptianFractions.pairing(f), f);
    }

    // --- Mass / CRC tests ---

    @Test public void testGreedyMassSmall() {
        greedyMass(200, 260335817L);
    }

    @Test public void testGreedyMass() {
        greedyMass(400, 93321355L);
    }

    @Test public void testPairingMassSmall() {
        pairingMass(200, 225772926L);
    }

    @Test public void testPairingMass() {
        pairingMass(400, 2989506769L);
    }

    private void greedyMass(int trials, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(444);
        for(int i = 0; i < trials; i++) {
            int a, b;
            do {
                b = rng.nextInt(10 * i + 1) + 6;
                a = rng.nextInt(b - 1) + 1;
            } while(a % 2 == 0 && b % 2 == 0);
            Fraction apb = new Fraction(a, b);
            List<BigInteger> gRes = EgyptianFractions.greedy(apb);
            verifyEgyptian(gRes, apb);
            try {
                check.update(gRes.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(expected, check.getValue());
    }

    private void pairingMass(int trials, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(444);
        for(int i = 0; i < trials; i++) {
            int a, b;
            do {
                b = rng.nextInt(10 * i + 1) + 6;
                a = rng.nextInt(b - 1) + 1;
            } while(a % 2 == 0 && b % 2 == 0);
            Fraction apb = new Fraction(a, b);
            List<BigInteger> pRes = EgyptianFractions.pairing(apb);
            verifyEgyptian(pRes, apb);
            try {
                check.update(pRes.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(expected, check.getValue());
    }
}