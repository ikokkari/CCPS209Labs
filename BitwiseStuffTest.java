import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class BitwiseStuffTest {

    @Test public void testCountClusters() {
        Random rng = new Random(12345);
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            int prev = 0, curr, count = 0;
            for(int j = 0; j < 64; j++) {
                curr = (rng.nextInt(100) < 25) ? 1 - prev : prev;
                if(curr == 1 && prev == 0) { count++; }
                n = (n << 1) + curr;
                prev = curr;
            }
            int result = BitwiseStuff.countClusters(n);
            //System.out.println(n + " " + Long.toBinaryString(n) + " " + result + " " + count);
            assertEquals(count, result);
        }
    }   
    
    @Test public void testBestRotateFill() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            for(int j = 0; j < 64; j++) {
                n = (n << 1) + ((rng.nextInt(100) < 75) ? 0 : 1);
            }
            int result = BitwiseStuff.bestRotateFill(n);
            check.update(result);
        }
        assertEquals(505332074L, check.getValue());
    }
    
    @Test public void testReverseNybbles() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            for(int j = 0; j < 64; j++) {
                n = (n << 1) + (rng.nextBoolean() ? 0 : 1);
            }
            long result = BitwiseStuff.reverseNybbles(n);
            long back = BitwiseStuff.reverseNybbles(result);
            try {
                check.update(Long.toHexString(result).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}

            assertEquals(n, back);
        }
        assertEquals(232648447L, check.getValue());
    }
    
    @Test public void testDosido() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            for(int j = 0; j < 64; j++) {
                n = (n << 1) + (rng.nextBoolean() ? 0 : 1);
            }
            long result = BitwiseStuff.dosido(n);
            long back = BitwiseStuff.dosido(result);
            //System.out.println(Long.toHexString(n) + " " + Long.toHexString(result) + " " + Long.toHexString(back));
            check.update(Long.toHexString(result).getBytes());
            assertEquals(n, back);
        }
        assertEquals(4127798722L, check.getValue());
    }
}