import org.junit.Test;
import java.util.List;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class PrimesTest {
    
    @Test public void isPrimeTest() {
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

        CRC32 check = new CRC32();
        for(int k = 0; k < 10_000_000; k++) {
            if(Primes.isPrime(k)) { check.update(k); }            
        }
        assertEquals(783904569L, check.getValue());
    }
    
    @Test public void kthPrimeTest() {
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

        CRC32 check = new CRC32();
        for(int k = 0; k < 30_000; k++) {
            check.update(Primes.kthPrime(k));
        }
        assertEquals(3080752681L, check.getValue());
    }
    
    @Test public void factorizeTest() {
        CRC32 check = new CRC32();
        for(int k = 2; k < 500_000; k++) {
            List<Integer> factors = Primes.factorize(k);
            int prod = 1;
            int prev = 0;
            for(int f: factors) {
                assertTrue(f >= prev);
                prev = f;
                prod = prod * f;
                check.update(f);
            }            
            assertEquals(k, prod);
        }
        assertEquals(2607517043L, check.getValue());
        
    }
}