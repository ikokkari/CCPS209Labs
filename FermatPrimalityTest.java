import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.zip.CRC32;

public class FermatPrimalityTest {

    @Test public void testPowerMod() {
        // Explicit test cases
        assertEquals(10, FermatPrimality.powerMod(11, 10, 13));
        assertEquals(1, FermatPrimality.powerMod(15, 12, 7));
        assertEquals(0, FermatPrimality.powerMod(2, 29, 8));
        assertEquals(2, FermatPrimality.powerMod(33, 541, 31));
        assertEquals(2, FermatPrimality.powerMod(33, 541, 31));
        assertEquals(2, FermatPrimality.powerMod(13, 347, 5));
        assertEquals(57, FermatPrimality.powerMod(45, 2326, 64));
        assertEquals(33, FermatPrimality.powerMod(44, 5958, 97));
        assertEquals(1, FermatPrimality.powerMod(83, 3960, 106));
        
        // Pseudorandom fuzz tests
        CRC32 check = new CRC32();
        Random rng = new Random(1234567);
        for(int i = 0; i < 2000; i++) {
            int a = 2 + rng.nextInt(10 + i);
            int b = 3 + rng.nextInt(10 + i * i);
            int m = 2 + rng.nextInt(10 + i);
            long result = FermatPrimality.powerMod(a, b, m);
            //System.out.println(a + "^" + b + " mod " + m + " equals " + result);
            check.update((int) result);
        }
        assertEquals(1139828259L, check.getValue());
    }
    
    @Test public void testIsNegativeFermatWitness() {
        CRC32 check = new CRC32();
        Random rng = new Random(1234567);
        long p = 5;
        for(int i = 0; i < 70000; i++) {
            int a = rng.nextInt((int)Math.min(Integer.MAX_VALUE, p - 3)) + 2;
            boolean result = FermatPrimality.isFermatNegativeWitness(p, 2);
            if(!result) { 
                //System.out.println(p + " " + a + " " + result); 
            }
            check.update(result ? i : -i);
            int mul = 2 << (rng.nextInt(10));
            p += (long)mul * (long)(1 + rng.nextInt(3 + i * (i / 4)));
            if(p % 10 == 5) { p += 2; }
            assert p > 0;
        }
        assertEquals(2241861074L, check.getValue());
    }
}