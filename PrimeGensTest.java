import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class PrimeGensTest {

    @Test public void testTwinPrimes() {
        CRC32 check = new CRC32();
        Iterator<Integer> it = new PrimeGens.TwinPrimes();
        for(int i = 0; i < 3500; i++) {
            check.update(it.next());            
        }
        assertEquals(2941193748L, check.getValue());
    }
    
    @Test public void testSafePrimes() {
        CRC32 check = new CRC32();
        Iterator<Integer> it = new PrimeGens.SafePrimes();
        for(int i = 0; i < 3000; i++) {
            check.update(it.next());            
        }
        assertEquals(3874618335L, check.getValue());
    }
    
    @Test public void testStrongPrimes() {
        CRC32 check = new CRC32();
        Iterator<Integer> it = new PrimeGens.StrongPrimes();
        for(int i = 0; i < 15_000; i++) {
            check.update(it.next());            
        }
        assertEquals(494629196L, check.getValue());
    }
    
}
