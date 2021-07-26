import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class QuadTreeTest {

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
            if(ii == 0) { tree = WhiteQuad.get(); }
            else if(ii == 1) { tree = BlackQuad.get(); }
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