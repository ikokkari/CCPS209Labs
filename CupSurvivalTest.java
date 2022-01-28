import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.zip.CRC32;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class CupSurvivalTest {

    private static final Fraction ZERO = new Fraction(0, 1);
    private static final Fraction ONE = new Fraction(1,1);
    private static final Fraction HALF = new Fraction(1, 2);
    private static final Fraction THIRD = new Fraction(1, 3);
    private static final Fraction TWO_THIRDS = new Fraction(2, 3);
    private static final Fraction EIGHTH = new Fraction(1, 8);

    @Test public void testExplicit() {

        Fraction[][] winProb0 = {
                {ONE, HALF, THIRD, HALF},
                {HALF, ONE, TWO_THIRDS, TWO_THIRDS},
                {TWO_THIRDS, THIRD, ONE, TWO_THIRDS},
                {HALF, THIRD, THIRD, ONE }
        };
        Fraction[] expected0 = {
                new Fraction(7, 36), THIRD, THIRD, new Fraction(5, 36)
        };
        Fraction[] result0 = CupSurvival.computeSurvival(winProb0);
        assertArrayEquals(expected0, result0);

        // If everyone has the same chance of winning, survival probabilities should be equal.
        Fraction[][] winProb1 = {
                {ONE, HALF, HALF, HALF, HALF, HALF, HALF, HALF},
                {HALF, ONE, HALF, HALF, HALF, HALF, HALF, HALF},
                {HALF, HALF, ONE, HALF, HALF, HALF, HALF, HALF},
                {HALF, HALF, HALF, ONE, HALF, HALF, HALF, HALF},
                {HALF, HALF, HALF, HALF, ONE, HALF, HALF, HALF},
                {HALF, HALF, HALF, HALF, HALF, ONE, HALF, HALF},
                {HALF, HALF, HALF, HALF, HALF, HALF, ONE, HALF},
                {HALF, HALF, HALF, HALF, HALF, HALF, HALF, ONE},
        };
        Fraction[] expected1 = {
                EIGHTH, EIGHTH, EIGHTH, EIGHTH, EIGHTH, EIGHTH, EIGHTH, EIGHTH
        };
        Fraction[] result1 = CupSurvival.computeSurvival(winProb1);
        assertArrayEquals(expected1, result1);

        // If team 1 always wins, its survival probability should be one, all others zero.
        Fraction[][] winProb2 = {
                {ONE, ZERO, THIRD, HALF},
                {ONE, ONE, ONE, ONE},
                {TWO_THIRDS, ZERO, ONE, THIRD},
                {HALF, ZERO, TWO_THIRDS, ONE}
        };
        Fraction[] expected2 = {ZERO, ONE, ZERO, ZERO};
        Fraction[] result2 = CupSurvival.computeSurvival(winProb2);
        assertArrayEquals(expected2, result2);
    }

    @Test public void testUpToLevelThree() {
        massTest(3, 3981152699L);
    }

    @Test public void testUpToLevelSix() {
        massTest(6, 2912218050L);
    }

    private void massTest(int level, long expected) {
        Random rng = new Random(12345 + level);
        CRC32 check = new CRC32();
        int[] PRIMES = {2, 3, 5, 7, 11, 13, 17};
        for(int k = 2; k <= level; k++) {
            int n = (1 << k);
            int[] strength = new int[n];
            Fraction[][] winProb = new Fraction[n][n];
            for(int cup = 0; cup < 10; cup++) {
                // Give each team a random strength.
                for (int i = 0; i < n; i++) {
                    strength[i] = 1 + rng.nextInt(10);
                }
                // Make up winning probabilities for each pairwise match.
                for (int i = 0; i < n; i++) {
                    winProb[i][i] = ONE;
                    for (int j = i + 1; j < n; j++) {
                        int si = strength[i] + rng.nextInt(3);
                        int sj = strength[j] + rng.nextInt(3);
                        winProb[i][j] = new Fraction(si, si + sj);
                        winProb[j][i] = new Fraction(sj, si + sj);
                    }
                }
                Fraction[] survival = CupSurvival.computeSurvival(winProb);
                // Final survival probabilities have to add up to one.
                Fraction sum = ZERO;
                for (Fraction f : survival) {
                    try {
                        check.update(f.toString().getBytes("UTF-8"));
                    } catch(UnsupportedEncodingException ignored) {}
                    sum = sum.add(f);
                }
                assertEquals(ONE, sum);
            }
        }
        assertEquals(expected, check.getValue());
    }
}