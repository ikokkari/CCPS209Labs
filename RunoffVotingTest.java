import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class RunoffVotingTest {
    
    @Test public void testCondorcetMethodTen() {
        testInstantRunoff(10, 877767557L, true);
    }
    
    @Test public void testCondorcetMethodHundred() {
        testInstantRunoff(100, 4279680524L, true);
    }
    
    @Test public void testInstantRunoffTen() {
        testInstantRunoff(10, 1206957385L, false);
    }
    
    @Test public void testInstantRunoffHundred() {
        testInstantRunoff(100, 926962316L, false);
    }
    
    private void testInstantRunoff(int n, long expected, boolean condorcet) {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
            int V = 10 * i * i + 1; // number of voters
            int C = 2 * i + 2; // number of candidates
            int[][] ballots = new int[V][C];
            int[] cands = new int[C];
            for(int c = 0; c < C; c++) { cands[c] = c; }
            for(int v = 0; v < V; v++) {
                System.arraycopy(cands, 0, ballots[v], 0, C);
                for(int k = 0; k < 3; k++) {
                    int p1 = rng.nextInt(C);
                    int p2 = rng.nextInt(C);
                    int tmp = cands[p1]; cands[p1] = cands[p2]; cands[p2] = tmp;
                }
            }
            int result = condorcet? RunoffVoting.condorcetMethod(ballots) : RunoffVoting.instantRunoff(ballots);
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }    
}