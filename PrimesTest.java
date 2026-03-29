import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PrimesTest {

    @Test public void testIsPrimeEdgeCases() {
        assertFalse("negative", Primes.isPrime(-7));
        assertFalse("negative", Primes.isPrime(-1));
        assertFalse("zero", Primes.isPrime(0));
        assertFalse("one", Primes.isPrime(1));
        assertTrue("two", Primes.isPrime(2));
        assertTrue("three", Primes.isPrime(3));
        assertFalse("four", Primes.isPrime(4));
        assertTrue("five", Primes.isPrime(5));
    }

    @Test public void testIsPrimeExplicit() {
        assertTrue(Primes.isPrime(7));
        assertTrue(Primes.isPrime(47));
        assertTrue(Primes.isPrime(101));
        assertTrue(Primes.isPrime(593));
        assertTrue(Primes.isPrime(1069));
        assertTrue(Primes.isPrime(1847));
        assertTrue(Primes.isPrime(21023));
        assertTrue(Primes.isPrime(40927));
        assertTrue(Primes.isPrime(76157));
        assertTrue(Primes.isPrime(100393));
        assertTrue(Primes.isPrime(131627));
        assertTrue(Primes.isPrime(341777));

        assertFalse(Primes.isPrime(15));
        assertFalse(Primes.isPrime(69));
        assertFalse(Primes.isPrime(121));
        assertFalse(Primes.isPrime(867));
        assertFalse(Primes.isPrime(1491));
        assertFalse(Primes.isPrime(1833));
        assertFalse(Primes.isPrime(21393));
        assertFalse(Primes.isPrime(36041));
        assertFalse(Primes.isPrime(68361));
        assertFalse(Primes.isPrime(95147));
    }

    @Test(timeout = 10000)
    public void testIsPrimeMass() {
        CRC32 check = new CRC32();
        for (int k = 0; k < 10_000_000; k++) {
            if (Primes.isPrime(k)) { check.update(k); }
        }
        assertEquals(783904569L, check.getValue());
    }

    @Test public void testKthPrimeExplicit() {
        assertEquals(2, Primes.kthPrime(0));
        assertEquals(3, Primes.kthPrime(1));
        assertEquals(5, Primes.kthPrime(2));
        assertEquals(13, Primes.kthPrime(5));
        assertEquals(37, Primes.kthPrime(11));
        assertEquals(73, Primes.kthPrime(20));
        assertEquals(191, Primes.kthPrime(42));
        assertEquals(547, Primes.kthPrime(100));
        assertEquals(3121, Primes.kthPrime(444));
        assertEquals(10067, Primes.kthPrime(1234));
        assertEquals(22343, Primes.kthPrime(2500));
        assertEquals(104729, Primes.kthPrime(9999));
        assertEquals(132247, Primes.kthPrime(12345));
        assertEquals(350381, Primes.kthPrime(30000));
        assertEquals(853819, Primes.kthPrime(67890));
        assertEquals(1632913, Primes.kthPrime(123456));
    }

    @Test public void testKthPrimeMass() {
        CRC32 check = new CRC32();
        for (int k = 0; k < 30_000; k++) {
            check.update(Primes.kthPrime(k));
        }
        assertEquals(3080752681L, check.getValue());
    }

    @Test public void testFactorizeExplicit() {
        assertEquals(Arrays.asList(2), Primes.factorize(2));
        assertEquals(Arrays.asList(3), Primes.factorize(3));
        assertEquals(Arrays.asList(2, 2), Primes.factorize(4));
        assertEquals(Arrays.asList(2, 2, 5, 11), Primes.factorize(220));
        assertEquals(Arrays.asList(2, 2, 2, 2, 2, 2, 5, 5, 5, 5, 5, 5),
                Primes.factorize(1000000));
        assertEquals(Arrays.asList(999961), Primes.factorize(999961));
        assertEquals(Arrays.asList(13, 13, 97, 131), Primes.factorize(2147483));
    }

    @Test(timeout = 10000)
    public void testFactorizeMass() {
        CRC32 check = new CRC32();
        for (int k = 2; k < 500_000; k++) {
            List<Integer> factors = Primes.factorize(k);
            int prod = 1;
            int prev = 0;
            for (int f : factors) {
                assertTrue("factors of " + k + " not sorted", f >= prev);
                prev = f;
                prod = prod * f;
                check.update(f);
            }
            assertEquals("product of factors of " + k, k, prod);
        }
        assertEquals(2607517043L, check.getValue());
    }
}