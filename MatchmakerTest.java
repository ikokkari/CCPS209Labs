import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;

public class MatchmakerTest {

    @Test public void explicitTestCases() {
        int[][] boys0 = { // all boys have same preferences
            {0, 1, 2}, {0, 1, 2}, {0, 1, 2}
        };
        int[][] girls0 = { // as do girls
            {2, 0, 1}, {2, 0, 1}, {2, 0, 1}
        };
        int[] exp0 = {1, 2, 0};
        assertArrayEquals(exp0, Matchmaker.galeShapley(boys0, girls0));

        int[][] boys1 = { // some diversity of opinion
            {1, 2, 3, 0}, {2, 3, 0, 1}, {3, 0, 1, 2}, {0, 1, 2, 3}
        };
        int[][] girls1 = {
            {3, 1, 2, 0}, {1, 2, 0, 3}, {2, 0, 3, 1}, {0, 3, 1, 2}
        };
        int[] exp1 = {1, 2, 3, 0};
        assertArrayEquals(exp1, Matchmaker.galeShapley(boys1, girls1));

        int[][] boys2 = { // every boy prefers their own numerical counterpart
                {0, 1, 2, 3}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 0, 2, 1}
        };
        int[][] girls2 = { // and so does every girl
                {0, 3, 2, 1}, {1, 3, 0, 2}, {2, 0, 3, 1}, {3, 1, 2, 0}
        };
        int[] exp2 = {0, 1, 2, 3};
        assertArrayEquals(exp2, Matchmaker.galeShapley(boys2, girls2));
        // Organizing a Sadie Hawkins day should not make any difference in this case.
        assertArrayEquals(exp2, Matchmaker.galeShapley(girls2, boys2));

        int[][] boys3 = { // every boy prefers the next higher numbered girl
                {1, 0, 3, 4, 2}, {2, 4, 1, 0, 3}, {3, 0, 4, 2, 1}, {4, 0, 1, 2, 3}, {0, 1, 2, 3, 4}
        };
        int[][] girls3 = { // but unfortunately, so does every girl
                {1, 2, 0, 4, 3}, {2, 4, 0, 1, 3}, {3, 4, 0, 1, 2}, {4, 1, 0, 3, 2}, {0, 2, 3, 1, 4}
        };
        int[] exp3 = {1, 2, 3, 4, 0}; // algorithm is biased to be optimal for the boys
        assertArrayEquals(exp3, Matchmaker.galeShapley(boys3, girls3));
        // "♬ Girls who love boys who love girls who love boys... ♬"
        assertArrayEquals(exp3, Matchmaker.galeShapley(girls3, boys3));
    }
    
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
        testGaleShapley(10, 3138738373L);
    }
    
    @Test public void testGaleShapleyHundred() {
        testGaleShapley(100, 76880458L);
    }
    
    @Test public void testGaleShapleyTwoThousand() {
        testGaleShapley(2000, 1327000569L);
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