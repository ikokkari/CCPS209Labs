import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J5Test {

    private static final int SEED = 12345;
    private static final BigInteger TWO = new BigInteger("2");
    
    @Test public void testFibonacciSum() {
        // Explicit test cases
        String b1 = "[13, 5]";
        assertEquals(b1, P2J5.fibonacciSum(new BigInteger("18")).toString());
        
        String b2 = "[21]";
        assertEquals(b2, P2J5.fibonacciSum(new BigInteger("21")).toString());
        
        String b3 = "[34, 8, 2]";
        assertEquals(b3, P2J5.fibonacciSum(new BigInteger("44")).toString());
        
        String b4 = "[55, 21, 8, 2]";
        assertEquals(b4, P2J5.fibonacciSum(new BigInteger("86")).toString());
        
        String b5 = "[89, 8, 3, 1]";
        assertEquals(b5, P2J5.fibonacciSum(new BigInteger("101")).toString());
        
        String b6 = "[1597, 377, 34, 8, 3, 1]";
        assertEquals(b6, P2J5.fibonacciSum(new BigInteger("2020")).toString());
        
        // Pseudorandom fuzz tester 
        CRC32 check = new CRC32();
        Random rng = new Random(SEED);
        BigInteger curr = BigInteger.ONE;
        for(int i = 0; i < 500; i++) {
            List<BigInteger> result = P2J5.fibonacciSum(curr);
            for(BigInteger b: result) {
                try {
                    check.update(b.toString().getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
            }
            curr = curr.add(new BigInteger("" + (rng.nextInt(5) + 1)));
            curr = curr.multiply(TWO);
        }
        assertEquals(3283204958L, check.getValue());
    }
    
    @Test public void testSevenZero() {
        // Explicit test cases
        assertEquals(new BigInteger("7"), P2J5.sevenZero(1));
        assertEquals(new BigInteger("7"), P2J5.sevenZero(7));
        assertEquals(new BigInteger("7770"), P2J5.sevenZero(42));
        assertEquals(new BigInteger("70"), P2J5.sevenZero(70));
        assertEquals(new BigInteger("77"), P2J5.sevenZero(77));
        assertEquals(new BigInteger("70000"), P2J5.sevenZero(16));
        assertEquals(new BigInteger("7770"), P2J5.sevenZero(42));
        assertEquals(new BigInteger("700"), P2J5.sevenZero(100));
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(125));
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(200));
        assertEquals(new BigInteger("77700"), P2J5.sevenZero(300));
        assertEquals(new BigInteger("70000"), P2J5.sevenZero(400));
        assertEquals(new BigInteger("7000"), P2J5.sevenZero(500));
        assertEquals(new BigInteger("70000"), P2J5.sevenZero(625));
        assertEquals(
          new BigInteger("777777777777777777777777777777777777777777777777777777"),
          P2J5.sevenZero(513)
        );
        assertEquals(new BigInteger("7777777770"), P2J5.sevenZero(666));
        assertEquals(new BigInteger("777700"), P2J5.sevenZero(2020));
        assertEquals(new BigInteger("70000000"), P2J5.sevenZero(625000));
        
        // Systematic checksum tester 
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int curr = 2;
        for(int i = 2; i < 400; i++) {
            BigInteger result = P2J5.sevenZero(curr);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            curr += rng.nextInt(5) + 1;
        }
        assertEquals(916368163L, check.getValue());
    }
}