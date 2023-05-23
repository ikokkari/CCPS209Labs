import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class RunoffVotingTest {
    
    @Test public void testCondorcetMethodExplicit() {
        // Some made up test cases
        
        int[][] b0 = { // Two candidates, two ballots
            {0, 1}, {1, 0}
        };
        assertEquals(1, RunoffVoting.condorcetMethod(b0));
        int[][] b1 = { // Three candidates, six ballots
            {0, 1, 2}, {0, 2, 1}, {1, 2, 0}, {2, 0, 1}, {1, 0, 2}, {2, 1, 0}
        };
        assertEquals(2, RunoffVoting.condorcetMethod(b1));
        int[][] b2 = { // Four candidates, six ballots
            {3, 2, 1, 0}, {0, 1, 2, 3}, {2, 1, 3, 0}, {0, 3, 1, 2}, {2, 1, 0, 3}, {3, 2, 1, 0}
        };
        assertEquals(3, RunoffVoting.condorcetMethod(b2));
        int[][] b3 = { // Three candidates, six ballots
            {2, 1, 0}, {2, 0, 1}, {0, 2, 1}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}
        };
        assertEquals(2, RunoffVoting.condorcetMethod(b3));
        int[][] b4 = { // Four candidates, 2 being everyone's second choice
            {0, 2, 1, 3}, {1, 2, 3, 0}, {3, 2, 1, 0}, {1, 2, 0, 3}, {0, 2, 1, 3}, {3, 2, 0, 1}  
        };
        assertEquals(2, RunoffVoting.condorcetMethod(b4)); // Apple pie wins
        
        // From Wikipedia page on Condorcet: voting for the capital of Tennessee
        int[][] tn = new int[100][4];
        // 0 = Chattanooga, 1 = Knoxville, 2 = Memphis, 3 = Nashville
        for(int b = 0; b < 42; b++) { // voters close to Memphis
            tn[b][0] = 2; tn[b][1] = 3; tn[b][2] = 0; tn[b][3] = 1;
        }
        for(int b = 42; b < 68; b++) { // voters close to Nashville
            tn[b][0] = 3; tn[b][1] = 0; tn[b][2] = 1; tn[b][3] = 2;
        }
        for(int b = 68; b < 83; b++) { // voters close to Chattanooga
             tn[b][0] = 0; tn[b][1] = 1; tn[b][2] = 3; tn[b][3] = 2;   
        }
        for(int b = 83; b < 100; b++) { // voters close to Knoxville
             tn[b][0] = 1; tn[b][1] = 0; tn[b][2] = 3; tn[b][3] = 2;   
        }
        // Nashville is chosen as the capital city of Tennessee.
        assertEquals(3, RunoffVoting.condorcetMethod(tn));
    }
    
    @Test public void testInstantRunoffExplicit() {
        // Some made up test cases
        
        int[][] b0 = { // Two candidates, two ballots
            {0, 1}, {1, 0}
        };
        assertEquals(1, RunoffVoting.instantRunoff(b0));
        int[][] b1 = { // Three candidates, six ballots
            {0, 1, 2}, {0, 2, 1}, {1, 2, 0}, {2, 0, 1}, {1, 0, 2}, {2, 1, 0}
        };
        assertEquals(2, RunoffVoting.instantRunoff(b1));
        int[][] b2 = { // Four candidates, six ballots
            {3, 2, 1, 0}, {0, 1, 2, 3}, {2, 1, 3, 0}, {0, 3, 1, 2}, {2, 1, 0, 3}, {3, 2, 1, 0}
        };
        assertEquals(3, RunoffVoting.instantRunoff(b2));
        int[][] b3 = { // Three candidates, six ballots
            {2, 1, 0}, {2, 0, 1}, {0, 2, 1}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}
        };
        assertEquals(2, RunoffVoting.instantRunoff(b3));
        int[][] b4 = { // Four candidates, 2 being everyone's second choice
            {0, 2, 1, 3}, {1, 2, 3, 0}, {3, 2, 1, 0}, {1, 2, 0, 3}, {0, 2, 1, 3}, {3, 2, 0, 1}  
        };
        assertEquals(1, RunoffVoting.instantRunoff(b4)); // Apple pie loses
    }
    
    @Test public void testCondorcetMethodTen() {
        test(10, 3430738225L, true);
    }
    
    @Test public void testCondorcetMethodHundred() {
        test(100, 1823942237L, true);
    }
    
    @Test public void testInstantRunoffTen() {
        test(10, 540292676L, false);
    }
    
    @Test public void testInstantRunoffHundred() {
        test(100, 2562966533L, false);
    }

    // Private test method used to test n pseudorandom items of Condorcet or Instant Runoff methods.
    private void test(int n, long expected, boolean condorcet) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
            int V = 10 * i * i + 1; // number of voters
            int C = 2 * i/10 + 2; // number of candidates
            int[][] ballots = new int[V][C];
            int[] cands = new int[C];
            for(int c = 0; c < C; c++) { cands[c] = c; }
            for(int v = 0; v < V; v++) {
                // Create the ballots so that voters tend to have similar preferences.
                System.arraycopy(cands, 0, ballots[v], 0, C);
                for(int k = 0; k < 3; k++) {
                    int p1 = rng.nextInt(C);
                    int p2 = rng.nextInt(C);
                    int tmp = cands[p1]; cands[p1] = cands[p2]; cands[p2] = tmp;
                }
            }
            int result = condorcet? RunoffVoting.condorcetMethod(ballots) : RunoffVoting.instantRunoff(ballots);
            // Shuffling the ballots before recounting surely can't change anything, yes?
            for(int v = 1; v < V; v++) {
                int vv = rng.nextInt(v + 1);
                int[] tmp = ballots[v]; ballots[v] = ballots[vv]; ballots[vv] = tmp;
            }
            int result2 = condorcet? RunoffVoting.condorcetMethod(ballots) : RunoffVoting.instantRunoff(ballots);
            assertEquals(result, result2); 
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }    
}