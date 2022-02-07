import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;

public class FrogCrossingTest {

    @Test public void testMaximumFrogsExplicit() {
        int[] strength0 = {8, 7, 5};
        int[] stones0 = {0, 2, 4, 7, 9, 10, 13, 14, 16};
        assertEquals(3, FrogCrossing.maximumFrogs(strength0, stones0));

        int[] strength1 = {7, 5, 4};
        int[] stones1 = {0, 3, 4, 6, 8, 10, 11};
        assertEquals(2, FrogCrossing.maximumFrogs(strength1, stones1));

        int[] strength2 = {20, 17, 15, 13, 11};
        int[] stones2 = {7, 8, 11, 17, 20, 21, 24, 26, 31, 33, 38};
        assertEquals(4, FrogCrossing.maximumFrogs(strength2, stones2));
    }

    @Test public void testMaximumFrogsTwenty() {
        massTest(20, 2316408012L);
    }

    @Test public void testMaximumFrogsHundred() {
        massTest(100, 595606895L);
    }

    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345 + n);
        int frogs = 3, count = 0, goal = 10;
        for(int i = 0; i < n; i++) {
            int[] strength = new int[frogs];
            for(int j = 0; j < frogs; j++) {
                strength[j] = 3 + rng.nextInt(4 * frogs);
            }
            Arrays.sort(strength);
            // Seriously, Java, still no method to sort the array in descending order?
            int j1 = 0, j2 = strength.length-1;
            while(j1 < j2) {
                int tmp = strength[j1]; strength[j1] = strength[j2]; strength[j2] = tmp;
                j1++; j2--;
            }
            int m = frogs + rng.nextInt(2 * frogs);
            int[] stones = new int[m];
            for(int j = 1; j < stones.length; j++) {
                stones[j] = stones[j-1] + 1 + rng.nextInt(1 + frogs);
            }
            int result = FrogCrossing.maximumFrogs(strength, stones);
            check.update(result);
            // System.out.println("Stones at " + Arrays.toString(stones));
            // System.out.println("Strengths " + Arrays.toString(strength));
            // System.out.println(result);
            if(++count == goal) {
                count = 0; goal++; frogs++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}