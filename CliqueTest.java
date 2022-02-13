import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class CliqueTest {

    private boolean[][] createRandomAdjacencyMatrix(int n, int edges, Random rng) {
        boolean[][] adjacencyMatrix = new boolean[n][n];
        int u = rng.nextInt(n);
        while(edges > 0) {
            int v = rng.nextInt(n);
            if(u != v & !adjacencyMatrix[u][v]) {
                adjacencyMatrix[u][v] = true;
                adjacencyMatrix[v][u] = true;
                edges--;
            }
            if(rng.nextInt(100) < 30) { u = rng.nextInt(n); }
        }
        return adjacencyMatrix;
    }

    @Test public void testFindFirstCliqueOneHundred() {
        test(100, 1381342682L);
    }

    @Test public void testFindFirstCliqueFiveHundred() {
        test(500, 3729285310L);
    }

    @Test public void testFindFirstCliqueOneThousand() {
        test(1000, 3825507316L);
    }

    private void test(int trials, long expected) {
        Random rng = new Random(12345 + trials);
        CRC32 check = new CRC32();
        int n = 3, count = 0, goal = 1;
        for(int i = 0; i < trials; i++) {
            int ee = (n*n) / 4;
            int edges = ee + rng.nextInt(1 + ee/2);
            boolean[][] adjacencyMatrix = createRandomAdjacencyMatrix(n, edges, rng);
            int[] clique = Clique.findFirstClique(adjacencyMatrix);
            assertNotNull(clique);
            // Verify that the found clique actually is a clique.
            for(int ii = 0; ii < clique.length; ii++) {
                for(int jj = ii+1; jj < clique.length; jj++) {
                    // All nodes in the clique are distinct.
                    assertNotEquals(clique[ii], clique[jj]);
                    // All nodes in the clique are connected by an edge.
                    assertTrue(adjacencyMatrix[clique[ii]][clique[jj]]);
                }
            }
            // Update the checksum with the elements of the found clique.
            for(int u: clique) { check.update(u); }
            // Increment the problem size counter.
            if(++count == goal) { count = 0; goal++; n++; }
        }
        // Just to make sure that you always returned the lexicographically first clique.
        assertEquals(expected, check.getValue());
    }
}