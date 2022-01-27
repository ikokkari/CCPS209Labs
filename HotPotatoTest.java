import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class HotPotatoTest {

    @Test public void testHotPotatoFifty() {
        testHotPotato(50, 2745874825L);
    }
    
    @Test public void testHotPotatoTwoHundred() {
        testHotPotato(200, 2094549067L);
    }
    
    private void testHotPotato(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int[] tmp = new int[n];
        for(int i = 0; i < n; i++) {
            int nn = 3 + i / 7;
            int t = 2 + i / 10;
            int[][] enemies = new int[nn][];
            for(int j = 0; j < nn; j++) {
                int count;
                do {
                    count = 0;
                    for(int k = 0; k < nn; k++) {
                        if(k != j && rng.nextInt(nn) < Math.min(nn - 1, 3)) {
                            tmp[count++] = k;
                        }
                    }
                } while(count == 0);
                enemies[j] = Arrays.copyOfRange(tmp, 0, count);
            }
            Fraction[] result = HotPotato.hotPotato(enemies, t);
            try {
                check.update(Arrays.deepToString(result).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(expected, check.getValue());
    }
    
}
