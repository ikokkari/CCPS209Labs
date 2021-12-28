import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class ManhattanTest {

    @Test public void testTotalArea() {
        /* Explicit test cases */
        int[] s1 = {2, 6, 9, 12, 15};
        int[] e1 = {3, 8, 10, 14, 20};
        int[] h1 = {3, 3, 4, 3, 2};
        assertEquals(29, Manhattan.totalArea(s1, e1, h1));
        
        int[] s2 = {3, 7, 8, 17, 24, 26, 27};
        int[] e2 = {5, 18, 18, 28, 37, 29, 41};
        int[] h2 = {1, 3, 2, 2, 3, 2, 1};
        assertEquals(90, Manhattan.totalArea(s2, e2, h2));
        
        /* Pseudorandom fuzz tester */
        CRC32 check = new CRC32();
        Random rng = new Random(777);
        for(int i = 2; i < 500; i++) {
            int n = rng.nextInt(3 * i) + 1;
            int[] s = new int[n];
            int[] e = new int[n];
            int[] h = new int[n];
            for(int j = 0; j < n; j++) {
                s[j] = rng.nextInt(4 * n);
                
            }
            Arrays.sort(s);
            for(int j = 0; j < n; j++) {
                int w = 1 + rng.nextInt(2 * n);
                e[j] = s[j] + w;
                if(j > 0 && s[j-1] == s[j]) { e[j] = Math.max(e[j], e[j-1] + 1); }
                h[j] = 1 + (j / w) + rng.nextInt(3);    
            }
            int r = Manhattan.totalArea(s, e, h);
            check.update(r);
        }
        assertEquals(2174298203L, check.getValue());
    }
}