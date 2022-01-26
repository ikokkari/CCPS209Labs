import org.junit.Test;
import java.util.zip.CRC32;
import java.util.Random;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class CupSurvivalTest {

    private static final Fraction ZERO = new Fraction(0, 1);
    private static final Fraction ONE = new Fraction(1,1);

    @Test public void testExplicit() {
        Fraction HALF = new Fraction(1, 2);
        Fraction THIRD = new Fraction(1, 3);
        Fraction TWO_THIRDS = new Fraction(2, 3);
        Fraction EIGHTH = new Fraction(1, 8);
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

        // If team 1 always wins, its survival probability should be one.
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
        massTest(3, 3034186112L);
    }

    @Test public void testUpToLevelEight() {
        massTest(8, 2311879630L);
    }

    private void massTest(int level, long expected) {
        Random rng = new Random(12345 + level);
        CRC32 check = new CRC32();
        int[] PRIMES = {2, 3, 5, 7, 11, 13, 17};
        for(int k = 2; k <= level; k++) {
            int n = (1 << k);
            Fraction[][] winProb = new Fraction[n][n];
            for(int i = 0; i < n; i++) {
                winProb[i][i] = ONE;
                for (int j = i + 1; j < n; j++) {
                    int den = PRIMES[rng.nextInt(PRIMES.length)];
                    int num = rng.nextInt(den);
                    winProb[i][j] = new Fraction(num, den);
                    winProb[j][i] = ONE.subtract(winProb[i][j]);
                }
            }
            Fraction[] survival = CupSurvival.computeSurvival(winProb);
            for(Fraction f: survival) {
                check.update(f.toString().getBytes());
            }
        }
        assertEquals(expected, check.getValue());
    }

}
