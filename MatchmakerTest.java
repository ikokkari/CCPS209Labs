import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import java.util.Arrays;

public class MatchmakerTest {

    private void fillRandomPermutation(Random rng, int[] a) {
        int n = a.length;
        // Fill the array with 0, ..., n-1
        for(int i = 0; i < n; i++) { a[i] = i; }
        // Knuth shuffle, the correct way to create a random permutation so that
        // every one of the n! possible permutations is equally likely. (Assume
        // that rng has enough entropy to produce all the possible results.)
        for(int i = 1; i < n; i++) {
            int j = rng.nextInt(i + 1);
            int tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
    }
    
    @Test public void testGaleShapleyTen() {
        testGaleShapley(10, 827861309L);
    }
    
    @Test public void testGaleShapleyHundred() {
        testGaleShapley(100, 843398654L);
    }
    
    @Test public void testGaleShapleyTwoThousand() {
        testGaleShapley(2000, 908980302L);
    }
    
    private void testGaleShapley(int rounds, long expected) {
        Random rng = new Random(rounds);
        CRC32 check = new CRC32();
        int count = 1, goal = 1, n = 1;
        int[][] boysPref = new int[n][n], girlsPref = new int[n][n];
        
        for(int i = 0; i < rounds; i++) {
            for(int j = 0; j < n; j++) {
                fillRandomPermutation(rng, boysPref[j]);
                fillRandomPermutation(rng, girlsPref[j]);
            }
            int[] result = Matchmaker.galeShapley(boysPref, girlsPref);
            for(int e: result) { check.update(e); }
            if(count++ == goal) {
                count = 0;
                goal++;
                n++;
                boysPref = new int[n][n];
                girlsPref = new int[n][n];
            }
        }
        assertEquals(expected, check.getValue());
    }
}