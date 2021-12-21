import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class QuadTreeTest {

    private static final QuadTree BLACK = BlackQuad.get();
    private static final QuadTree WHITE = WhiteQuad.get();

    @Test public void testExplicit() {
        QuadTree q1 = QuadNode.of(WHITE, BLACK, WHITE, BLACK);
        assertEquals(2, q1.computeArea(1));
        assertEquals(4, q1.computeArea(2));
        assertEquals(32, q1.computeArea(5));
        assertFalse(q1.isOneColour());
        QuadTree q2 = QuadNode.of(BLACK, BLACK, BLACK, BLACK);
        assertEquals(2, q2.computeArea(1));
        assertTrue(q2.isOneColour());
        QuadTree q3 = QuadNode.of(q1, q2, q1, q2);
        assertFalse(q3.isOneColour());
        assertEquals(32, q3.computeArea(4));
        assertEquals(128, q3.computeArea(6));
        QuadTree q4 = QuadNode.of(q1, q2, q3, BLACK);
        assertEquals(160, q4.computeArea(6));
    }

    @Test public void massTestTwenty() {
        // Change the third parameter to true to see the results computed using your code.
        massTest(20, 2434345592L);
    }
    
    @Test public void massTestThousand() {
        massTest(1000, 2434099640L);
    }
    
    @Test public void massTestMillion() {
        massTest(1_000_000, 1206476339L);
    }
    
    // How many first randomly generated trees are used as building blocks
    // for the trees generated later in the mass test.
    private static final int CUTOFF = 1000;
    
    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        // Randomly generated trees in this masstest.
        QuadTree[] trees = new QuadTree[CUTOFF + 1];
        // The height of each tree.
        int[] height = new int[CUTOFF + 1];
        QuadTree[] children = new QuadTree[4];
        // Statistics for the end.
        int maxHeight = 0;
        long maxArea = 0;
        for(int i = 0; i < n; i++) {            
            int ii = Math.min(i, CUTOFF); // The index to use to the array.
            QuadTree tree; // The quadtree constructed this round from the existing trees.
            if(ii == 0) { tree = WHITE; }
            else if(ii == 1) { tree = BLACK; }
            else {
                for(int j = 0; j < 4; j++) {
                    int c = rng.nextInt(Math.min(CUTOFF, i));
                    height[ii] = Math.max(height[ii], 1 + height[c]);
                    maxHeight = Math.max(maxHeight, height[ii]);
                    children[j] = trees[c];
                }   
                tree = QuadNode.of(children);
            }
            trees[ii] = tree;
            long area = tree.computeArea(height[ii]);
            maxArea = Math.max(maxArea, area);
            assertTrue(area >= 0);
            check.update((int)(area & 0xFFFF)); // lowest 32 bits of long value
            check.update((int)((area >> 32) & 0xFFFF)); // highest 32 bits of long value
        }
        assertEquals(expected, check.getValue());
    }   
}