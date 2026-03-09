import org.junit.Test;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

public class CliqueTest {

    // --- Helper methods ---

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

    private boolean[][] completeGraph(int n) {
        boolean[][] adj = new boolean[n][n];
        for(int i = 0; i < n; i++) {
            for(int j = 0; j < n; j++) {
                if(i != j) { adj[i][j] = true; }
            }
        }
        return adj;
    }

    private boolean[][] edgelessGraph(int n) {
        return new boolean[n][n];
    }

    private boolean[][] fromEdges(int n, int[][] edges) {
        boolean[][] adj = new boolean[n][n];
        for(int[] e : edges) {
            adj[e[0]][e[1]] = true;
            adj[e[1]][e[0]] = true;
        }
        return adj;
    }

    private void verifyClique(boolean[][] adj, int[] clique) {
        assertNotNull(clique);
        for(int i = 0; i < clique.length; i++) {
            for(int j = i + 1; j < clique.length; j++) {
                assertNotEquals("Clique nodes must be distinct",
                        clique[i], clique[j]);
                assertTrue("Clique nodes must be connected",
                        adj[clique[i]][clique[j]]);
            }
        }
    }

    // --- Explicit tests ---

    @Test public void testEmptyGraph() {
        boolean[][] adj = new boolean[0][0];
        int[] clique = Clique.findFirstClique(adj);
        assertNotNull(clique);
        assertEquals(0, clique.length);
    }

    @Test public void testSingleNode() {
        boolean[][] adj = edgelessGraph(1);
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0}, clique);
    }

    @Test public void testTwoNodesNoEdge() {
        boolean[][] adj = edgelessGraph(2);
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0}, clique);
    }

    @Test public void testTwoNodesWithEdge() {
        boolean[][] adj = completeGraph(2);
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1}, clique);
    }

    @Test public void testCompleteGraphK4() {
        boolean[][] adj = completeGraph(4);
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1, 2, 3}, clique);
    }

    @Test public void testCompleteGraphK5() {
        boolean[][] adj = completeGraph(5);
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1, 2, 3, 4}, clique);
    }

    @Test public void testTriangleWithPendant() {
        // Triangle {0,1,2} plus edge 2-3. Max clique is {0,1,2}.
        boolean[][] adj = fromEdges(4, new int[][]{
                {0,1}, {1,2}, {0,2}, {2,3}
        });
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1, 2}, clique);
    }

    @Test public void testTwoDisjointTriangles() {
        // {0,1,2} and {3,4,5}: lex-first max clique is {0,1,2}.
        boolean[][] adj = fromEdges(6, new int[][]{
                {0,1}, {1,2}, {0,2}, {3,4}, {4,5}, {3,5}
        });
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1, 2}, clique);
    }

    @Test public void testPathGraph() {
        // Path 0-1-2-3-4: max clique size 2, lex-first is {0,1}.
        boolean[][] adj = fromEdges(5, new int[][]{
                {0,1}, {1,2}, {2,3}, {3,4}
        });
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1}, clique);
    }

    @Test public void testCycleOfFive() {
        // 5-cycle: max clique size 2, lex-first is {0,1}.
        boolean[][] adj = fromEdges(5, new int[][]{
                {0,1}, {1,2}, {2,3}, {3,4}, {4,0}
        });
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1}, clique);
    }

    @Test public void testK5MinusOneEdge() {
        // K5 with edge (0,4) removed: max clique is {0,1,2,3}.
        boolean[][] adj = completeGraph(5);
        adj[0][4] = false;
        adj[4][0] = false;
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1, 2, 3}, clique);
    }

    @Test public void testStarGraph() {
        // Node 0 connected to all others, no other edges. Max clique = {0,1}.
        boolean[][] adj = fromEdges(5, new int[][]{
                {0,1}, {0,2}, {0,3}, {0,4}
        });
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 1}, clique);
    }

    @Test public void testEdgelessGraph() {
        // No edges among 4 nodes: max clique is {0}.
        boolean[][] adj = edgelessGraph(4);
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0}, clique);
    }

    @Test public void testCompleteBipartiteK33() {
        // K_{3,3}: {0,1,2} x {3,4,5}. Max clique = 2, lex-first is {0,3}.
        boolean[][] adj = new boolean[6][6];
        for(int i = 0; i < 3; i++) {
            for(int j = 3; j < 6; j++) {
                adj[i][j] = true;
                adj[j][i] = true;
            }
        }
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{0, 3}, clique);
    }

    @Test public void testCliqueNotAtStart() {
        // Clique {2,3,4,5} but nodes 0,1 are isolated. Lex-first max clique is {2,3,4,5}.
        boolean[][] adj = new boolean[6][6];
        for(int i = 2; i < 6; i++) {
            for(int j = i + 1; j < 6; j++) {
                adj[i][j] = true;
                adj[j][i] = true;
            }
        }
        int[] clique = Clique.findFirstClique(adj);
        assertArrayEquals(new int[]{2, 3, 4, 5}, clique);
    }

    // --- Mass / CRC tests ---

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
            int ee = (n * n) / 4;
            int edges = ee + rng.nextInt(1 + ee / 2);
            boolean[][] adjacencyMatrix = createRandomAdjacencyMatrix(n, edges, rng);
            int[] clique = Clique.findFirstClique(adjacencyMatrix);
            assertNotNull(clique);
            // Verify that the found clique actually is a clique.
            verifyClique(adjacencyMatrix, clique);
            // Verify ascending order.
            for(int ii = 1; ii < clique.length; ii++) {
                assertTrue("Clique must be in ascending order",
                        clique[ii] > clique[ii - 1]);
            }
            // Update the checksum with the elements of the found clique.
            for(int u: clique) { check.update(u); }
            // Increment the problem size counter.
            if(++count == goal) { count = 0; goal++; n++; }
        }
        assertEquals(expected, check.getValue());
    }
}