import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class TowersTest {

    // Change this to some larger number to see that many results printed.
    private static final int PRINT_FIRST_RESULTS = 0;

    @Test public void testTowersExplicit() {
        int[] t0 = {3, 1};
        assertEquals(1, Towers.minimizeTowers(t0));
        int[] t1 = {7, 1, 8, 4};
        assertEquals(2, Towers.minimizeTowers(t1));
        int[] t2 = {4, 1, 7, 8};
        assertEquals(3, Towers.minimizeTowers(t2));
        int[] t3 = {6, 2, 3, 1, 7, 8};
        assertEquals(4, Towers.minimizeTowers(t3));
        int[] t4 = {14, 9, 7, 12, 4, 1};
        assertEquals(2, Towers.minimizeTowers(t4));
        int[] t5 = {2, 16, 20, 7, 10, 17, 13, 14, 3, 1, 4};
        assertEquals(5, Towers.minimizeTowers(t5));
        int[] t6 = {18, 7, 13, 15, 12, 2, 5, 3, 14, 9, 4, 1};
        assertEquals(3, Towers.minimizeTowers(t6));
    }

    @Test public void testTowersHundred() {
        massTestTowers(100, 3706310906L);
    }

    @Test public void testTowersTenThousand() {
        massTestTowers(10_000, 2821774497L);
    }

    @Test public void testTowersThirtyThousand() {
        massTestTowers(30_000, 141835131L);
    }

    private void massTestTowers(int n, long expected) {
        Random rng = new Random(n);
        CRC32 check = new CRC32();
        int[] blocks = {};
        int count = 0, goal = 1, m = 0;
        for(int i = 0; i < n; i++) {
            if(++count == goal) {
                blocks = new int[++m];
                int curr = 1;
                for(int j = 0; j < m; j++) {
                    blocks[j] = curr;
                    curr += rng.nextInt(3) + 1;
                }
                count = 0;
                goal = goal + (m < 3 ? 0: 1);
            }
            for(int j = 0; j < 3; j++) {
                int i1 = (i + j) % m;
                int i2 = (i + j + 1 + rng.nextInt(4)) % m;
                int tmp = blocks[i1];
                blocks[i1] = blocks[i2];
                blocks[i2] = tmp;
            }
            int result = Towers.minimizeTowers(blocks);
            if(i < PRINT_FIRST_RESULTS) {
                System.out.print(Arrays.toString(blocks) + " ");
                System.out.println(result);
            }
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }

}
