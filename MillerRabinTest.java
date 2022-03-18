import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class MillerRabinTest {

    @Test public void testPowerMod() {
        // Explicit test cases
        assertEquals(10, MillerRabin.powerMod(11, 10, 13));
        assertEquals(1, MillerRabin.powerMod(15, 12, 7));
        assertEquals(0, MillerRabin.powerMod(2, 29, 8));
        assertEquals(2, MillerRabin.powerMod(33, 541, 31));
        assertEquals(2, MillerRabin.powerMod(33, 541, 31));
        assertEquals(2, MillerRabin.powerMod(13, 347, 5));
        assertEquals(57, MillerRabin.powerMod(45, 2326, 64));
        assertEquals(33, MillerRabin.powerMod(44, 5958, 97));
        assertEquals(1, MillerRabin.powerMod(83, 3960, 106));

        // Pseudorandom fuzz tests
        CRC32 check = new CRC32();
        Random rng = new Random(1234567);
        for(int i = 0; i < 2000; i++) {
            int a = 2 + rng.nextInt(10 + i);
            int b = 3 + rng.nextInt(10 + i * i);
            int m = 2 + rng.nextInt(10 + i);
            long result = MillerRabin.powerMod(a, b, m);
            check.update((int) result);
            check.update((int) (result >> 32));
        }
        assertEquals(1476090350L, check.getValue());
    }

    @Test public void testIsMillerRabinWitnessThousand() {
        testIsMillerRabinWitness(1000, 4271652548L);
    }

    @Test public void testIsMillerRabinWitnessMillion() {
        testIsMillerRabinWitness(1_000_000, 742328848L);
    }

    private static final int[] PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37};

    private void testIsMillerRabinWitness(int rounds, long expected) {
        Random rng = new Random(rounds + 12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < rounds; i++) {
            int n = rng.nextInt(1_000_000_000) + 2;
            outer:
            while(true) {
                for(int p: PRIMES) {
                    if(n % p == 0) { n++; continue outer; }
                }
                break;
            }
            int a = rng.nextInt(100_000) + 2;
            boolean result = MillerRabin.isMillerRabinWitness(n, a);
            check.update(result ? 42 : 99);
        }
        assertEquals(expected, check.getValue());
    }

    private static final long[] SOME_PRIMES = {
            47, 61, 67, 79, 89, 97,
            179, 223, 317, 467, 569, 601, 727, 757, 787, 919, 929, 967,
            1289, 1451, 1721, 2887, 3001, 3697, 4339, 4517, 6353, 6719, 9511,
            18913, 20233, 31891, 34939, 43613, 65423, 73999, 78853, 78977, 80071, 89069,
            100517, 100703, 124769, 312383, 344249, 601319, 655211, 889951,
            1433477, 1984007, 2570651, 4322909, 4324601, 4325813, 5873227, 6000061, 6540407, 8214091, 9874693,
            10001713, 12914771, 14001989, 42324409, 54003253, 51001889, 72173327, 78298873, 88824301, 92323657,
            123126557, 133849141, 231232117, 236476577, 312136789, 374614003, 378298727, 432232223,
            588323209, 636474869, 636478343, 718398173, 812134459, 931233647, 932498947, 943235653,
            1234568837, 1534569727, 1999970333, 2071119119
    };

    private static final long[] SOME_COMPOSITES = {
            3613 * 4057, 65633 * 2713, 84223 * 569, 1013 * 6353, 655507 * 101, 3307 * 3307,
            67481 * 23, 4321787 * 53, 32969 * 62987, 1451 * 499 * 359, 44483 * 119, 971 * 191,
            1249 * 50951, 6907 * 6907, 30859 * 31883, 37649 * 7879, 56009 * 13, 147607 * 137,
            148301 * 593, 1171 * 1867, 7001 * 967, 544897 * 201, 1451 * 787, 727 * 757 * 787,
            565559 * 137, 239 * 613 * 349, 1069 * 157 * 71, 1985713 * 47, 276047 * 97, 45347263,
            45347769
    };

    @Test public void testIsPrime() {
        for(long n: SOME_PRIMES) {
            assertTrue(MillerRabin.isPrime(n));
        }
        for(long n: SOME_COMPOSITES) {
            assertFalse(MillerRabin.isPrime(n));
        }
    }
}