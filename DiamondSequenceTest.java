import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class DiamondSequenceTest {
      
    private void outputSequence() {
        Iterator<Integer> it = new DiamondSequence();
        int[] a = new int[20];
        int[] b = new int[20];
        for(int i = 0; i < 20; i++) {
            a[i] = it.next();
            b[i] = a[i] + (i > 0 ? b[i-1]: 0);
        }
        System.out.printf("k:");
        for(int i = 0; i < 20; i++) {
            System.out.printf("%6d", i+1);
        }
        System.out.printf("\nf:");
        for(int i = 0; i < 20; i++) {
            System.out.printf("%6d", a[i]);
        }
        System.out.printf("\nF:");
        for(int i = 0; i < 20; i++) {
            System.out.printf("%6d", b[i]);
        }
    }
    
    @Test
    public void knownInitialPrefix() {
        Iterator<Integer> it = new DiamondSequence();
        assertEquals(new Integer(1), it.next());
        assertEquals(new Integer(3), it.next());
        assertEquals(new Integer(2), it.next());
        assertEquals(new Integer(6), it.next());
        assertEquals(new Integer(8), it.next());
        assertEquals(new Integer(4), it.next());
        assertEquals(new Integer(11), it.next());
        assertEquals(new Integer(5), it.next());
        assertEquals(new Integer(14), it.next());
        assertEquals(new Integer(16), it.next());
        assertEquals(new Integer(7), it.next());
        assertEquals(new Integer(19), it.next());
        assertEquals(new Integer(21), it.next());
        assertEquals(new Integer(9), it.next());
        assertEquals(new Integer(24), it.next());
    }
    
    @Test
    public void testSelfReferentiality() {
        Map<Integer, Integer> mustBe = new HashMap<>();
        Iterator<Integer> it = new DiamondSequence();
        for(int k = 1; k < 1_000_000; k++) {
            int v = it.next();
            if(mustBe.containsKey(k)) {
                // Cast to long is necessary to disambiguate between assertEquals overloadings.
                assertEquals((long)v, (long)mustBe.get(k));
                mustBe.remove(k);
            }
            if(v > k) {
                mustBe.put(v, k);
            }
        }
    }
    
    @Test
    public void firstHundredMillion() {
        int count = 100_000_000;
        CRC32 check = new CRC32();
        Iterator<Integer> it = new DiamondSequence();
        while(--count > 0) {
            int i = it.next();
            if(count % 1000 == 0) { check.update(i); }
        }
        assertEquals(2546749209L, check.getValue());
    }
}
