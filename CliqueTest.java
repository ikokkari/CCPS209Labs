import org.junit.Test;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class CliqueTest {

    private boolean[][] createRandomAdjacencyMatrix(int n, int edges, Random rng) {
        ArrayList<Integer> indices = new ArrayList<>();
        for(int i = 0; i < n; i++) {
            indices.add(i);
        }
        boolean[][] adjacencyMatrix = new boolean[n][n];
        int pct = rng.nextInt(40) + 40;
        while(edges > 0) {
            Collections.shuffle(indices, rng);
            int upTo = 1;
            while(upTo < n && rng.nextInt(100) < pct) { upTo++; }
            for(int i = 1; i < upTo; i++) {
                int u = indices.get(i);
                for(int j = 0; j < i; j++) {
                    int v = indices.get(j);
                    if(!adjacencyMatrix[u][v]) {
                        edges--;
                        adjacencyMatrix[u][v] = true;
                        adjacencyMatrix[v][u] = true;
                    }
                }
            }
        }
        return adjacencyMatrix;
    }

    @Test public void testFindFirstCliqueOneHundred() {
        testClique(100, 2899382865L);
    }

    @Test public void testFindFirstCliqueFiveHundred() {
        testClique(500, 1058405530L);
    }

    @Test public void testFindFirstCliqueOneThousand() {
        testClique(1000, 2204406524L);
    }

    private void testClique(int trials, long expected) {
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