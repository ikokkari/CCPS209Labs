import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class RunoffVotingTest {

    // --- Condorcet Method explicit tests ---

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
        assertEquals(2, RunoffVoting.condorcetMethod(b4)); // Apple pie wins Condorcet

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

    @Test public void testCondorcetMethodTieBreaking() {
        // Two candidates, one ballot each way: exact tie in pairwise match.
        // Higher-numbered candidate (1) wins the pairwise tie, gets 1 match point.
        int[][] tie2 = { {0, 1}, {1, 0} };
        assertEquals(1, RunoffVoting.condorcetMethod(tie2));

        // Three candidates, perfect symmetry: every pairwise match is a tie.
        // All ties go to higher-numbered candidate.
        // Pairwise: 0v1 -> 1 wins, 0v2 -> 2 wins, 1v2 -> 2 wins.
        // Scores: 0=0, 1=1, 2=2. Winner is candidate 2.
        int[][] sym3 = {
                {0, 1, 2}, {1, 2, 0}, {2, 0, 1}
        };
        assertEquals(2, RunoffVoting.condorcetMethod(sym3));

        // Four candidates, all pairwise matches tied (4 voters cycling).
        // All ties broken in favour of higher number => candidate 3 wins.
        int[][] sym4 = {
                {0, 1, 2, 3}, {1, 2, 3, 0}, {2, 3, 0, 1}, {3, 0, 1, 2}
        };
        assertEquals(3, RunoffVoting.condorcetMethod(sym4));

        // Tied match points: two candidates each win 1 match point.
        // 0 beats 1, 2 beats 0, 1 vs 2 tied -> 2 gets it.
        // Scores: 0=1, 1=0, 2=2? Let's compute carefully:
        // 0v1: ballot 0 has 0>1, ballot 1 has 1>0 => tied, 1 wins
        // 0v2: ballot 0 has 0>2, ballot 1 has 2>0 => tied, 2 wins
        // 1v2: ballot 0 has 1>2, ballot 1 has 2>1 => tied, 2 wins
        // Scores: 0=0, 1=1, 2=2. Winner = 2.
        int[][] tie_scores = { {0, 1, 2}, {2, 1, 0} };
        assertEquals(2, RunoffVoting.condorcetMethod(tie_scores));
    }

    @Test public void testCondorcetMethodUnanimous() {
        // All voters agree: candidate 2 is first choice among 3 candidates.
        int[][] unan = {
                {2, 1, 0}, {2, 1, 0}, {2, 1, 0}, {2, 1, 0}, {2, 1, 0}
        };
        assertEquals(2, RunoffVoting.condorcetMethod(unan));

        // All voters agree: candidate 0 is first choice among 4 candidates.
        // Candidate 0 wins all pairwise matches -> 3 match points.
        int[][] unan4 = {
                {0, 1, 2, 3}, {0, 1, 2, 3}, {0, 1, 2, 3}
        };
        assertEquals(0, RunoffVoting.condorcetMethod(unan4));
    }

    @Test public void testCondorcetMethodSingleBallot() {
        // Single ballot: each pairwise match is 1-0, higher-ranked wins.
        // Ballot ranks: 2 > 0 > 3 > 1
        // 0v1: 0 wins. 0v2: 2 wins. 0v3: 0 wins. 1v2: 2 wins. 1v3: 3 wins. 2v3: 2 wins.
        // Scores: 0=2, 1=0, 2=3, 3=1. Winner = 2.
        int[][] single = { {2, 0, 3, 1} };
        assertEquals(2, RunoffVoting.condorcetMethod(single));
    }

    @Test public void testCondorcetMethodCondorcetWinnerExists() {
        // Classic Condorcet winner: candidate 1 beats every other candidate pairwise.
        // 3 voters, 3 candidates.
        int[][] cw = {
                {1, 0, 2},
                {1, 2, 0},
                {0, 1, 2}
        };
        // 1v0: ballots 0,1 rank 1>0; ballot 2 ranks 0>1. So 1 wins 2-1.
        // 1v2: ballots 0,2 rank 1>2; ballot 1 ranks 1>2. So 1 wins 3-0.
        // 0v2: ballots 0,2 rank 0>2; ballot 1 ranks 2>0. So 0 wins 2-1.
        // Scores: 0=1, 1=2, 2=0. Winner = 1.
        assertEquals(1, RunoffVoting.condorcetMethod(cw));
    }

    @Test public void testCondorcetMethodCyclicPreferences() {
        // Rock-paper-scissors cycle: 0 beats 1, 1 beats 2, 2 beats 0.
        // Each gets 1 match point, all tied -> highest numbered wins.
        int[][] rps = {
                {0, 1, 2},
                {1, 2, 0},
                {2, 0, 1}
        };
        // 0v1: 0>1 in ballot 0 and 2, 1>0 in ballot 1 => 0 wins 2-1
        // 0v2: 0>2 in ballot 0, 2>0 in ballot 1 and 2 => 2 wins 2-1
        // 1v2: 1>2 in ballot 0 and 1, 2>1 in ballot 2 => 1 wins 2-1
        // Scores: 0=1, 1=1, 2=1. Tied, highest number wins => 2.
        assertEquals(2, RunoffVoting.condorcetMethod(rps));
    }

    @Test public void testCondorcetMethodFiveCandidates() {
        // Larger election with 5 candidates and 7 voters.
        int[][] big = {
                {4, 3, 2, 1, 0},
                {4, 3, 2, 1, 0},
                {4, 3, 2, 1, 0},
                {0, 1, 2, 3, 4},
                {0, 1, 2, 3, 4},
                {1, 0, 2, 3, 4},
                {3, 4, 2, 0, 1}
        };
        // 4 appears first in 3 ballots, 0 in 2, 1 in 1, 3 in 1.
        // Let's trace pairwise:
        // 4v0: 4>0 in ballots 0,1,2,6; 0>4 in ballots 3,4,5 => 4 wins 4-3
        // 4v1: 4>1 in ballots 0,1,2,6; 1>4 in ballots 3,4,5 => 4 wins 4-3
        // 4v2: 4>2 in ballots 0,1,2,6; 2>4 in ballots 3,4,5 => 4 wins 4-3
        // 4v3: 4>3 in ballots 0,1,2; 3>4 in ballots 3,4,5,6 => 3 wins 4-3
        // 3v0: 3>0 in ballots 0,1,2,6; 0>3 in ballots 3,4,5 => 3 wins 4-3
        // 3v1: 3>1 in ballots 0,1,2,6; 1>3 in ballots 3,4,5 => 3 wins 4-3
        // 3v2: 3>2 in ballots 0,1,2,6; 2>3 in ballots 3,4,5 => 3 wins 4-3
        // 0v1: 0>1 in ballots 0,1,2,3,4; 1>0 in ballots 5,6 => 0 wins 5-2
        // 0v2: 0>2 in ballots 0,1,2,3,4,5; 2>0 in ballot 6 => 0 wins 6-1
        // 1v2: 1>2 in ballots 0,1,2,3,4,5; 2>1 in ballot 6 => 1 wins 6-1
        // Scores: 0=2, 1=1, 2=0, 3=4, 4=3. Winner = 3.
        assertEquals(3, RunoffVoting.condorcetMethod(big));
    }

    // --- Instant Runoff explicit tests ---

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
        assertEquals(1, RunoffVoting.instantRunoff(b4)); // Apple pie loses IRV
    }

    @Test public void testInstantRunoffTieBreakElimination() {
        // Three candidates, each has exactly 1 first-choice ballot.
        // All tied at 1 ballot each. Eliminate lowest-numbered (candidate 0).
        // After 0 eliminated, ballot 0 {0,1,2} goes to candidate 1.
        // Now: 1 has 2 ballots, 2 has 1 ballot. 2/3 > 1/2 so 1 wins? No, need > half of 3.
        // 2 > 1.5, yes. Winner = 1.
        int[][] tie3 = {
                {0, 1, 2},
                {1, 2, 0},
                {2, 0, 1}
        };
        assertEquals(1, RunoffVoting.instantRunoff(tie3));

        // Now make it so ballot redistribution after eliminating 0 goes to 2 instead.
        int[][] tie3b = {
                {0, 2, 1},
                {1, 2, 0},
                {2, 0, 1}
        };
        // Eliminate 0. Ballot 0 {0,2,1} goes to 2. Now 2 has 2 ballots, 1 has 1. Winner = 2.
        assertEquals(2, RunoffVoting.instantRunoff(tie3b));
    }

    @Test public void testInstantRunoffExactlyHalfNotEnough() {
        // Four ballots, two candidates each with 2 first-choice votes.
        // 2/4 = exactly half, not a majority. Must eliminate the lower-numbered.
        int[][] half = {
                {0, 1}, {0, 1}, {1, 0}, {1, 0}
        };
        // Each has 2 of 4 = exactly half. Eliminate candidate 0.
        // Ballots 0,1 go to 1. Now 1 has all 4. Winner = 1.
        assertEquals(1, RunoffVoting.instantRunoff(half));

        // Three candidates: 0 has 2, 1 has 2, 2 has 1. Total 5.
        // Nobody has > 2.5. Candidate 2 has fewest (1), eliminated.
        // After redistribution, check who gets majority.
        int[][] half3 = {
                {0, 1, 2}, {0, 2, 1}, {1, 0, 2}, {1, 2, 0}, {2, 0, 1}
        };
        // Eliminate 2 (1 ballot). Ballot 4 {2,0,1} goes to 0. Now 0=3, 1=2. 3 > 2.5, winner = 0.
        assertEquals(0, RunoffVoting.instantRunoff(half3));
    }

    @Test public void testInstantRunoffCandidateWithNoFirstChoiceVotes() {
        // Candidate 1 appears in ballots but is nobody's first choice.
        // Per spec, treated as pre-eliminated, should not come alive later.
        int[][] noFirst = {
                {0, 1, 2},
                {0, 1, 2},
                {2, 1, 0},
                {2, 1, 0},
                {2, 0, 1}
        };
        // First choices: 0 has 2, 1 has 0 (pre-eliminated), 2 has 3.
        // 2 has 3/5 > 2.5, so 2 wins immediately.
        assertEquals(2, RunoffVoting.instantRunoff(noFirst));

        // More nuanced: no one has majority, and candidate with zero votes is skipped.
        int[][] noFirst2 = {
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {2, 1, 0, 3},
                {3, 1, 2, 0},
                {3, 1, 0, 2}
        };
        // First choices: 0=2, 1=0 (pre-eliminated), 2=1, 3=2. Total = 5.
        // Nobody > 2.5. Fewest (among those with ballots): 2 has 1. Eliminate 2.
        // Ballot 2 {2,1,0,3}: next non-eliminated after 2 -> 1 is pre-eliminated -> 0.
        // Now 0=3, 3=2. 3 > 2.5, winner = 0.
        assertEquals(0, RunoffVoting.instantRunoff(noFirst2));
    }

    @Test public void testInstantRunoffRedistributionChain() {
        // Test that redistribution properly skips already-eliminated candidates.
        // 5 candidates, 7 ballots.
        int[][] chain = {
                {0, 1, 2, 3, 4},  // first choice: 0
                {1, 0, 2, 3, 4},  // first choice: 1
                {2, 1, 0, 3, 4},  // first choice: 2
                {3, 2, 1, 0, 4},  // first choice: 3
                {4, 3, 2, 1, 0},  // first choice: 4
                {4, 3, 2, 1, 0},  // first choice: 4
                {4, 3, 2, 1, 0},  // first choice: 4
        };
        // Round 0: 0=1, 1=1, 2=1, 3=1, 4=3. Need > 3.5 to win.
        // Tied at 1 among {0,1,2,3}. Eliminate lowest = 0.
        // Ballot 0 {0,1,2,3,4}: goes to candidate 1.
        // Round 1: 1=2, 2=1, 3=1, 4=3. Still no majority.
        // Tied at 1 among {2,3}. Eliminate lowest = 2.
        // Ballot 2 {2,1,0,3,4}: next non-eliminated after 2 -> 1 (alive). Goes to 1.
        // Round 2: 1=3, 3=1, 4=3. Need > 3.5 still. No majority.
        // Fewest = 3 with 1 ballot. Eliminate 3.
        // Ballot 3 {3,2,1,0,4}: next non-eliminated after 3 -> 2 eliminated -> 1. Goes to 1.
        // Round 3: 1=4, 4=3. 4 > 3.5. Winner = 1.
        assertEquals(1, RunoffVoting.instantRunoff(chain));
    }

    @Test public void testInstantRunoffUnanimous() {
        // All voters agree on first choice: immediate majority.
        int[][] unan = {
                {2, 0, 1}, {2, 1, 0}, {2, 0, 1}
        };
        assertEquals(2, RunoffVoting.instantRunoff(unan));
    }

    @Test public void testInstantRunoffTwoRoundElimination() {
        // Four candidates, two rounds of elimination needed.
        int[][] twoRound = {
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {0, 1, 2, 3},
                {1, 0, 2, 3},
                {1, 0, 2, 3},
                {2, 3, 0, 1},
                {2, 3, 0, 1},
                {3, 2, 1, 0},
                {3, 2, 1, 0},
        };
        // Round 0: 0=3, 1=2, 2=2, 3=2. Need > 4.5. No winner.
        // Tied at 2: {1,2,3}. Eliminate lowest = 1.
        // Ballots 3,4 {1,0,2,3}: go to 0.
        // Round 1: 0=5, 2=2, 3=2. 5 > 4.5. Winner = 0.
        assertEquals(0, RunoffVoting.instantRunoff(twoRound));
    }

    @Test public void testCondorcetAndInstantRunoffDisagree() {
        // Demonstrate the fundamental difference: Condorcet picks the broad
        // consensus candidate, IRV picks the one who survives elimination rounds.
        // Apple pie scenario from spec.
        int[][] b4 = {
                {0, 2, 1, 3}, {1, 2, 3, 0}, {3, 2, 1, 0}, {1, 2, 0, 3}, {0, 2, 1, 3}, {3, 2, 0, 1}
        };
        // Condorcet: candidate 2 (apple pie, everyone's second choice) wins.
        assertEquals(2, RunoffVoting.condorcetMethod(b4));
        // IRV: candidate 2 has zero first-choice votes, pre-eliminated. Candidate 1 wins.
        assertEquals(1, RunoffVoting.instantRunoff(b4));
    }

    @Test public void testInstantRunoffTennessee() {
        // Tennessee capital example under IRV. Memphis (2) has plurality (42)
        // but not majority. Under IRV, smaller candidates get eliminated.
        int[][] tn = new int[100][4];
        // 0 = Chattanooga, 1 = Knoxville, 2 = Memphis, 3 = Nashville
        for(int b = 0; b < 42; b++) {
            tn[b][0] = 2; tn[b][1] = 3; tn[b][2] = 0; tn[b][3] = 1;
        }
        for(int b = 42; b < 68; b++) {
            tn[b][0] = 3; tn[b][1] = 0; tn[b][2] = 1; tn[b][3] = 2;
        }
        for(int b = 68; b < 83; b++) {
            tn[b][0] = 0; tn[b][1] = 1; tn[b][2] = 3; tn[b][3] = 2;
        }
        for(int b = 83; b < 100; b++) {
            tn[b][0] = 1; tn[b][1] = 0; tn[b][2] = 3; tn[b][3] = 2;
        }
        // Round 0: 0=15, 1=17, 2=42, 3=26. Need > 50. None.
        // Fewest = 0 (Chattanooga, 15). Eliminate 0.
        // Chattanooga voters' next choice is 1 (Knoxville).
        // Round 1: 1=32, 2=42, 3=26. Need > 50. None.
        // Fewest = 3 (Nashville, 26). Eliminate 3.
        // Nashville voters' next choice: 0 eliminated -> 1 (Knoxville).
        // Round 2: 1=58, 2=42. 58 > 50. Winner = Knoxville.
        assertEquals(1, RunoffVoting.instantRunoff(tn));
    }

    // --- CRC mass tests ---

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