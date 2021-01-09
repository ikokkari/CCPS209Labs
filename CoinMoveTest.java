import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;
import java.util.*;
import java.util.zip.CRC32;

public class CoinMoveTest {
    
    @Test public void testCoinMove() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        List<List<Integer>> nbs = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            int n = rng.nextInt(i / 3 + 2) + 2;
            int c = rng.nextInt(3 * n + 3) + 2;
            int[] curr = new int[n];
            int[] next = new int[n];
            nbs.clear();
            // Create random neighbours for each position
            for(int j = 0; j < n; j++) {
                ArrayList<Integer> nb = new ArrayList<>();
                while(rng.nextDouble() < .7) {
                    int nn = rng.nextInt(n);
                    if(nn == j) { nn = (nn + 1) % n; }
                    nb.add(nn);
                }
                nbs.add(nb);
            }
            // Initialize coins in each position
            for(int j = 0; j < c; j++) {
                curr[rng.nextInt(n)]++;
            }
            //System.out.println(nbs);
            //System.out.println(Arrays.toString(curr));
            CoinMove.coinStep(curr, next, nbs);
            //System.out.println(Arrays.toString(next));
            //System.out.println("");
            check.update(Arrays.toString(next).getBytes());
        }
        assertEquals(2310893907L, check.getValue());
    }
    
    @Test public void testPeriod() {
        Random rng = new Random(7777);
        CRC32 check = new CRC32();
        List<List<Integer>> nbs = new ArrayList<>();
        for(int i = 0; i < 300; i++) {
            int n = rng.nextInt(15) + 2;
            int c = rng.nextInt(3 * n + 3) + 2;
            int[] curr = new int[n];
            nbs.clear();
            // Create random neighbours for each position
            for(int j = 0; j < n; j++) {
                ArrayList<Integer> nb = new ArrayList<>();
                while(nb.size() == 0 || rng.nextDouble() < .7) {
                    int nn = rng.nextInt(n);
                    if(nn == j) { nn = (nn + 1) % n; }
                    nb.add(nn);
                }
                nbs.add(nb);
            }
            // Initialize coins in each position
            for(int j = 0; j < c; j++) {
                curr[rng.nextInt(n)]++;
            }
            //System.out.println(nbs);
            //System.out.println(Arrays.toString(curr));
            int result = CoinMove.period(curr, nbs);
            //System.out.println(result);
            check.update(result);
        }
        assertEquals(1281024593L, check.getValue());
    }
}