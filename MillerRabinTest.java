import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class MillerRabinTest {

    // --- powerMod explicit tests ---

    @Test public void testPowerMod() {
        assertEquals(10, MillerRabin.powerMod(11, 10, 13));
        assertEquals(1, MillerRabin.powerMod(15, 12, 7));
        assertEquals(0, MillerRabin.powerMod(2, 29, 8));
        assertEquals(2, MillerRabin.powerMod(33, 541, 31));
        assertEquals(2, MillerRabin.powerMod(13, 347, 5));
        assertEquals(57, MillerRabin.powerMod(45, 2326, 64));
        assertEquals(33, MillerRabin.powerMod(44, 5958, 97));
        assertEquals(1, MillerRabin.powerMod(83, 3960, 106));
    }

    @Test public void testPowerModEdgeCases() {
        // Exponent 0: a^0 mod m = 1
        assertEquals(1, MillerRabin.powerMod(5, 0, 3));
        assertEquals(1, MillerRabin.powerMod(999, 0, 7));
        // Exponent 1: a^1 mod m = a mod m
        assertEquals(3, MillerRabin.powerMod(3, 1, 7));
        assertEquals(2, MillerRabin.powerMod(9, 1, 7));
        // a divisible by m: result is 0
        assertEquals(0, MillerRabin.powerMod(6, 5, 3));
        assertEquals(0, MillerRabin.powerMod(10, 100, 5));
        // Power of 2
        assertEquals(24, MillerRabin.powerMod(2, 10, 1000));
        assertEquals(0, MillerRabin.powerMod(2, 10, 1024));
    }

    @Test public void testPowerModFermatLittleTheorem() {
        // a^(p-1) mod p = 1 for prime p, a not divisible by p
        assertEquals(1, MillerRabin.powerMod(2, 12, 13));
        assertEquals(1, MillerRabin.powerMod(3, 96, 97));
        assertEquals(1, MillerRabin.powerMod(7, 100, 101));
        assertEquals(1, MillerRabin.powerMod(5, 1008, 1009));
    }

    @Test public void testPowerModLargeValues() {
        assertEquals(147483634L, MillerRabin.powerMod(2, 31, 1000000007L));
    }

    @Test public void testPowerModFuzz() {
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

    // --- isMillerRabinWitness explicit tests ---

    @Test public void testWitnessPrimesPassAll() {
        int[] primes = {7, 13, 97, 101, 1009};
        int[] witnesses = {2, 3, 5, 7};
        for (int p : primes) {
            for (int a : witnesses) {
                if (a < p) {
                    assertTrue("Prime " + p + " should pass witness " + a,
                            MillerRabin.isMillerRabinWitness(p, a));
                }
            }
        }
    }

    @Test public void testWitnessCompositesDetected() {
        assertFalse(MillerRabin.isMillerRabinWitness(15, 2));   // 3*5
        assertFalse(MillerRabin.isMillerRabinWitness(21, 2));   // 3*7
        assertFalse(MillerRabin.isMillerRabinWitness(561, 2));  // Carmichael
        assertFalse(MillerRabin.isMillerRabinWitness(341, 2));  // Fermat pseudoprime base 2
    }

    @Test public void testWitnessPseudoprimeBase2CaughtByBase3() {
        // 2047 = 23*89 passes base 2 but fails base 3
        assertTrue(MillerRabin.isMillerRabinWitness(2047, 2));
        assertFalse(MillerRabin.isMillerRabinWitness(2047, 3));
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

    // --- isPrime explicit tests ---

    @Test public void testIsPrimeSmallPrimes() {
        int[] smallPrimes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 97, 101};
        for (int p : smallPrimes) {
            assertTrue("Should be prime: " + p, MillerRabin.isPrime(p));
        }
    }

    @Test public void testIsPrimeSmallNonPrimes() {
        long[] nonPrimes = {0, 1, 4, 6, 8, 9, 10, 15, 25, 49};
        for (long n : nonPrimes) {
            assertFalse("Should not be prime: " + n, MillerRabin.isPrime(n));
        }
    }

    @Test public void testIsPrimePerfectSquaresOfPrimes() {
        // Not caught by trial division with small primes when p > 37
        assertFalse(MillerRabin.isPrime(121));    // 11^2
        assertFalse(MillerRabin.isPrime(169));    // 13^2
        assertFalse(MillerRabin.isPrime(289));    // 17^2
        assertFalse(MillerRabin.isPrime(961));    // 31^2
        assertFalse(MillerRabin.isPrime(1681));   // 41^2
        assertFalse(MillerRabin.isPrime(10201));  // 101^2
    }

    @Test public void testIsPrimeCarmichaelNumbers() {
        // Fool Fermat's test but not Miller-Rabin with 12 witnesses
        assertFalse(MillerRabin.isPrime(561));    // 3*11*17
        assertFalse(MillerRabin.isPrime(1105));   // 5*13*17
        assertFalse(MillerRabin.isPrime(1729));   // 7*13*19 (Hardy-Ramanujan)
    }

    @Test public void testIsPrimeMersennePrimes() {
        assertTrue(MillerRabin.isPrime(3));            // 2^2 - 1
        assertTrue(MillerRabin.isPrime(7));            // 2^3 - 1
        assertTrue(MillerRabin.isPrime(31));           // 2^5 - 1
        assertTrue(MillerRabin.isPrime(127));          // 2^7 - 1
        assertTrue(MillerRabin.isPrime(8191));         // 2^13 - 1
        assertTrue(MillerRabin.isPrime(131071));       // 2^17 - 1
        assertTrue(MillerRabin.isPrime(524287));       // 2^19 - 1
        assertTrue(MillerRabin.isPrime(2147483647L));  // 2^31 - 1
    }

    @Test public void testIsPrimeLargerValues() {
        assertTrue(MillerRabin.isPrime(1009));
        assertTrue(MillerRabin.isPrime(10007));
        assertTrue(MillerRabin.isPrime(100003));
        assertTrue(MillerRabin.isPrime(1000003));
        assertTrue(MillerRabin.isPrime(1234568837L));
        assertTrue(MillerRabin.isPrime(2071119119L));
    }

    // --- Comprehensive isPrime with OEIS pseudoprime lists ---

    private static final long[] SOME_PRIMES = {
            47, 61, 67, 79, 89, 97,
            179, 223, 317, 467, 569, 601, 727, 757, 787, 919, 929, 967,
            1289, 1451, 1721, 2887, 3001, 3697, 4339, 4517, 6353, 6719, 9511,
            18913, 20233, 31891, 34939, 43613, 51539, 65423, 73999, 78853, 78977, 80071, 89069,
            100517, 100703, 124769, 312383, 344249, 502703, 601319, 655211, 889951,
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
            45347769,
            2047, 1373653, 25326001,
            341, 561, 645, 1105, 1387, 1729, 1905, 2047, 2465, 2701, 2821, 3277, 4033, 4369, 4371,
            4681, 5461, 6601, 7957, 8321, 8481, 8911, 10261, 10585, 11305, 12801, 13741, 13747,
            13981, 14491, 15709, 15841, 16705, 18705, 18721, 19951, 23001, 23377, 25761, 29341,
            2615977, 26634301, 69741001, 1693101241,
            399, 935, 2015, 2915, 4991, 5719, 7055, 8855, 12719, 18095, 20705, 20999, 22847, 29315,
            31535, 46079, 51359, 60059, 63503, 67199, 73535, 76751, 80189, 81719, 88559, 90287,
            104663, 117215, 120581, 147455, 152279, 155819, 162687, 191807, 194327, 196559, 214199,
            9868715, 11521439, 43383167, 126806399, 632309759, 1454412959,
            77, 143, 165, 187, 209, 221, 231, 247, 273, 299, 323, 357, 391, 399, 437, 493, 527,
            561, 589, 598, 713, 715, 899, 935, 943, 989, 1015, 1073, 1105, 1147, 1189, 1247, 1271,
            1295, 1333, 1517, 1537, 1547, 1591, 1595, 1705, 1729, 1739, 1763, 1829, 1885, 1886, 1927,
            63169, 198547, 500039, 3534541, 5971357, 9445027, 13989667,
            2047, 3277, 4033, 4681, 8321, 15841, 29341, 42799, 49141, 52633, 65281, 74665, 80581,
            85489, 88357, 90751, 104653, 130561, 196093, 220729, 233017, 252601, 253241, 256999,
            271951, 280601, 314821, 357761, 390937, 458989, 476971, 486737, 3605429, 21417991,
            77812153, 82870517, 180497633, 327398009, 705351583, 1027744453,
            121, 703, 1891, 3281, 8401, 8911, 10585, 12403, 16531, 18721, 19345, 23521, 31621, 44287,
            47197, 55969, 63139, 74593, 79003, 82513, 87913, 88573, 97567, 105163, 111361, 112141,
            148417, 152551, 182527, 188191, 211411, 218791, 221761, 226801, 2226043, 35728129,
            69444841, 117987841, 220534651, 378682537, 487890703, 1095485821,
            781, 1541, 5461, 5611, 7813, 13021, 14981, 15751, 24211, 25351, 29539, 38081, 40501,
            44801, 53971, 79381, 100651, 102311, 104721, 112141, 121463, 133141, 141361, 146611,
            195313, 211951, 216457, 222301, 251521, 289081, 290629, 298271, 315121, 1197761,
            5481451, 27722857, 45006391, 75663451, 105957601, 528968917, 643767931, 1063398043
    };

    @Test public void testIsPrime() {
        for(long n: SOME_PRIMES) {
            assertTrue(MillerRabin.isPrime(n));
        }
        for(long n: SOME_COMPOSITES) {
            assertFalse(MillerRabin.isPrime(n));
        }
        Random rng = new Random(12345);
        for(int bl = 10; bl < 32; bl++) {
            for(int i = 0; i < 50; i++) {
                BigInteger prime = BigInteger.probablePrime(bl, rng);
                assertTrue(MillerRabin.isPrime(prime.longValue()));
                int bits = rng.nextInt(8) + 8;
                BigInteger p1 = BigInteger.probablePrime(bits, rng);
                BigInteger p2 = BigInteger.probablePrime(31 - bits, rng);
                assertFalse(MillerRabin.isPrime(p1.multiply(p2).longValue()));
            }
        }
    }
}