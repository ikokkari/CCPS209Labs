import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class PrimesTest {
    
    @Test public void isPrimeTest() {
        CRC32 check = new CRC32();
        for(int k = 0; k < 10_000_000; k++) {
            if(Primes.isPrime(k)) { check.update(k); }            
        }
        assertEquals(783904569L, check.getValue());
    }
    
    @Test public void kthPrimeTest() {
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