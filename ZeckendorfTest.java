import org.junit.Test;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZeckendorfTest {
    
    @Test
    public void massTest() {
        // Explicit test cases
        List<BigInteger> a1 = Arrays.asList(
            new BigInteger("1"), new BigInteger("82")
        );
        String s1 = Zeckendorf.encode(a1);
        assertEquals("111001001011", s1);
        assertEquals(a1, Zeckendorf.decode(s1));
        
        List<BigInteger> a2 = Arrays.asList(
            new BigInteger("5"), new BigInteger("85"), new BigInteger("172"), new BigInteger("345")
        );
        String s2 = Zeckendorf.encode(a2);
        assertEquals("0001110001010110101001000110100001001011", s2);
        assertEquals(a2, Zeckendorf.decode(s2));
        
        List<BigInteger> a3 = Arrays.asList(
            new BigInteger("4"), new BigInteger("30"), new BigInteger("139"), new BigInteger("353")
        );
        String s3 = Zeckendorf.encode(a3);
        assertEquals("101110001011001001010110100101001011", s3);
        assertEquals(a3, Zeckendorf.decode(s3));
        
        List<BigInteger> a4 = Arrays.asList(
            new BigInteger("353"), new BigInteger("139"), new BigInteger("30"), new BigInteger("4")
        );
        String s4 = Zeckendorf.encode(a4);
        assertEquals("010010100101100100101011100010111011", s4);
        assertEquals(a4, Zeckendorf.decode(s4));
        
        // Pseudorandom fuzz tester
        Random rng = new Random(12345);
        BigInteger two = new BigInteger("2");
        CRC32 check = new CRC32();
        int count = 0, goal = 5, n = 2;
        for(int i = 1; i <= 3000; i++) {
            List<BigInteger> orig = new ArrayList<>();
            BigInteger b = new BigInteger("" + (1 + rng.nextInt(i*i)));
            orig.add(b);
            for(int j = 0; j < n; j++) {
                b = b.multiply(two);
                b = b.add(new BigInteger("" + rng.nextInt(100)));
                orig.add(b);
            }
            Collections.shuffle(orig, rng);
            String zits = Zeckendorf.encode(orig);
            check.update(zits.length());
            for(int k = 0; k < zits.length(); k++) {
                assertTrue(zits.charAt(k) == '0' || zits.charAt(k) == '1');
            }
            List<BigInteger> back = Zeckendorf.decode(zits);
            assertEquals(orig, back);
            if(++count == goal) {
                count = 0;
                goal = goal + 3;
                n += 1;
            }
        }
        assertEquals(61550952L, check.getValue());
    }
}