import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class QuadTreeTest {

    @Test public void massTestTwenty() {
        // Change the third parameter to true to see the results computed using your code.
        massTest(20, 2434345592L, false);
    }
    
    @Test public void massTestThousand() {
        massTest(1000, 2434099640L, false);
    }
    
    @Test public void massTestMillion() {
        massTest(1_000_000, 1206476339L, false);
    }
    
    // How many first randomly generated trees are used as building blocks
    // for the trees generated later in the mass test.
    private static final int CUTOFF = 1000;
    
    private void massTest(int n, long expected, boolean verbose) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        // Randomly generated trees in this masstest.
        QuadTree[] trees = new QuadTree[CUTOFF + 1];
        // The height of each tree.
        int[] height = new int[CUTOFF + 1];
        // So that we don't need to create two helper arrays all the time.
        int[] childIds = new int[4];
        QuadTree[] children = new QuadTree[4];
        
        for(int i = 0; i < n; i++) {
            // The index to use to the array.
            int ii = Math.min(i, CUTOFF);
            QuadTree tree;
            if(ii == 0) { tree = WhiteQuad.get(); }
            else if(ii == 1) { tree = BlackQuad.get(); }
            else {
                for(int j = 0; j < 4; j++) {
                    int c = rng.nextInt(Math.min(CUTOFF, i));
                    childIds[j] = c;
                    height[ii] = Math.max(height[ii], 1 + height[c]);
                    children[j] = trees[c];
                }   
                tree = QuadNode.of(children);
            }
            trees[ii] = tree;
            long area = tree.computeArea(height[ii]);
            assertTrue(area >= 0);
            check.update((int)(area & 0xFFFF)); // lowest 32 bits of long value
            check.update((int)((area >> 32) & 0xFFFF)); // highest 32 bits of long value
            if(verbose) {
                if(ii == 0) {
                    System.out.println("Tree 0 is a white leaf.");
                }
                else if(ii == 1) {
                    System.out.println("Tree 1 is a black leaf.");
                }
                else {
                    System.out.println("Tree " + ii + " is made of trees "
                    + Arrays.toString(childIds) + ".");
                }
                System.out.println("Its height is " + height[ii] + ", and its area is " + area + ".");
            }
        }
        assertEquals(expected, check.getValue());
    }
    
}
