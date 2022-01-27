import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
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
                while(rng.nextInt(100) < 70) {
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
            CoinMove.coinStep(curr, next, nbs);
            for(int e: next) { check.update(e); }
        }
        assertEquals(561039959L, check.getValue());
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
                while(nb.size() == 0 || rng.nextInt(100) < 70) {
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
            int result = CoinMove.period(curr, nbs);
            check.update(result);
        }
        assertEquals(3003526382L, check.getValue());
    }
}