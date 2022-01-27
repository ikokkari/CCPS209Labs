import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class P2J8Test {
    
    @Test public void testHittingIntegerPowers() {
        // Explicit test cases
        int[] out = new int[2]; // Array that method writes the result into
        
        int[] ex1 = {84, 53};
        P2J8.hittingIntegerPowers(2, 3, 100, out);
        assertArrayEquals(ex1, out);
        
        int[] ex2 = {2, 1};
        P2J8.hittingIntegerPowers(2, 4, 100, out);
        assertArrayEquals(ex2, out);
        
        int[] ex3 = {2, 1};
        P2J8.hittingIntegerPowers(6, 36, 1000000, out);
        assertArrayEquals(ex3, out);
        
        int[] ex4 = {285, 256};
        P2J8.hittingIntegerPowers(5, 6, 1000, out);
        assertArrayEquals(ex4, out);
        
        int[] ex5 = {93, 28};
        P2J8.hittingIntegerPowers(2, 10, 100, out);
        assertArrayEquals(ex5, out);
        
        int[] ex6 = {10, 3}; // 2**10 \approx 10**3
        P2J8.hittingIntegerPowers(2, 10, 30, out);
        assertArrayEquals(ex6, out);
        
        // Pseudorandom fuzz tester
        CRC32 check = new CRC32();
        int[] tens = {1, 10, 100, 10000, 10000, 100000};
        for(int b = 3; b < 20; b++) {
            for(int a = 2; a < b; a++) {
                int t = 2 + (a + b) % 3;
                P2J8.hittingIntegerPowers(a, b, tens[t], out);
                check.update(out[0]);
                check.update(out[1]);
            }
        }
        assertEquals(3805180419L, check.getValue());
    }
    
    @Test public void testNearestPolygonalNumber() {
        // Explicit test cases
        BigInteger a1 = new BigInteger("42");
        BigInteger b1 = new BigInteger("35");
        assertEquals(b1, P2J8.nearestPolygonalNumber(a1, 5));
        
        BigInteger a2 = new BigInteger("45");
        BigInteger b2 = new BigInteger("45");
        assertEquals(b2, P2J8.nearestPolygonalNumber(a2, 3));
        
        BigInteger a3 = new BigInteger("98");
        BigInteger b3 = new BigInteger("91");
        assertEquals(b3, P2J8.nearestPolygonalNumber(a3, 3));
        
        BigInteger a4 = new BigInteger("3999");
        BigInteger b4 = new BigInteger("4030");
        assertEquals(b4, P2J8.nearestPolygonalNumber(a4, 5));
        
        // Pseudorandom fuzz tester
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        BigInteger curr = new BigInteger("1");
        final BigInteger TWO = new BigInteger("2");
        int[] tens = {1, 10, 100, 1000, 10000};
        for(int i = 0; i < 1000; i++) {
            if(i % 5 == 0) { curr = curr.multiply(TWO); }
            int s = rng.nextInt(100) + 2;
            BigInteger result = P2J8.nearestPolygonalNumber(curr, s);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            curr = curr.add(new BigInteger("" + rng.nextInt(100) * tens[(i/5) % tens.length]));
        }
        assertEquals(3138704967L, check.getValue());
    }
}