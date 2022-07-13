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
        assertEquals(8, q1.computeArea(2));
        assertEquals(512, q1.computeArea(5));
        assertFalse(q1.isOneColour());
        QuadTree q2 = QuadNode.of(BLACK, BLACK, BLACK, BLACK);
        assertEquals(4, q2.computeArea(1));
        assertEquals(16, q2.computeArea(2));
        assertEquals(4096, q2.computeArea(6));
        assertTrue(q2.isOneColour());
        QuadTree q3 = QuadNode.of(q1, q2, q1, q2);
        assertFalse(q3.isOneColour());
        assertEquals(192, q3.computeArea(4));
        assertEquals(3072, q3.computeArea(6));
        QuadTree q4 = QuadNode.of(q1, q2, q3, BLACK);
        assertEquals(3328, q4.computeArea(6));
    }

    @Test public void massTestHundred() {
        massTest(100, 3130507033L);
    }

    @Test public void massTestThousand() {
        massTest(1000, 2159387927L);
    }

    @Test public void massTestHundredThousand() {
        massTest(100000, 3359260450L);
    }

    @Test public void massTestMillion() {
        massTest(100000, 3359260450L);
    }

    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        // Randomly generated trees in this mass test.
        QuadTree[] trees = new QuadTree[n];
        int[] height = new int[n];
        trees[0] = WHITE;
        trees[1] = BLACK;
        height[0] = height[1] = 1;
        QuadTree[] children = new QuadTree[4];

        for(int i = 2; i < n; i++) {
            for(int j = 0; j < 4; j++) {
                int c = rng.nextInt(1 + (int)Math.sqrt(i));
                children[j] = trees[c];
                height[i] = Math.max(height[i], 1 + height[c]);
            }
            trees[i] = QuadNode.of(children);
            long area = trees[i].computeArea(height[i]);
            assertTrue(area >= 0);
            check.update((int)(area & 0xFFFF)); // lowest 32 bits of long value
            check.update((int)((area >> 32) & 0xFFFF)); // highest 32 bits of long value
        }
        assertEquals(expected, check.getValue());
    }   
}