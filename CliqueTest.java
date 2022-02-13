import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class CliqueTest {

    private boolean[][] createRandomAdjacencyMatrix(int n, int edges, Random rng) {
        boolean[][] adjacencyMatrix = new boolean[n][n];
        int u = rng.nextInt(n);
        while(edges > 0) {
            int v = rng.nextInt(n);
            if(u != v & adjacencyMatrix[u][v] == false) {
                adjacencyMatrix[u][v] = true;
                adjacencyMatrix[v][u] = true;
                edges--;
            }
            if(rng.nextInt(100) < 30) { u = rng.nextInt(n); }
        }
        return adjacencyMatrix;
    }

    @Test public void testFindFirstCliqueOneHundred() {
        test(100, 1568805173L);
    }

    private void test(int trials, long expected) {
        Random rng = new Random(12345 + trials);
        CRC32 check = new CRC32();
        int n = 3, count = 0, goal = 1;
        for(int i = 0; i < trials; i++) {
            int ee = (n*n) / 4;
            int edges = ee + rng.nextInt(1 + ee/2);
            boolean[][] adjacencyMatrix = createRandomAdjacencyMatrix(n, edges, rng);
            for(int m = 1; m <= n; m++) {
                int[] clique = Clique.findFirstClique(adjacencyMatrix, m);
                if(clique != null) {
                    // Verify that the found clique is the right length.
                    assertEquals(m, clique.length);
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
                }
                else { break; }
            }
            if(++count == goal) { count = 0; goal++; n++; }
        }
        // Just to make sure that you always returned the lexicographically first clique.
        assertEquals(expected, check.getValue());
    }
}