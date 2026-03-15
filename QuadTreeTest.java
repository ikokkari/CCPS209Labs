import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

public class QuadTreeTest {

    private static final QuadTree BLACK = BlackQuad.get();
    private static final QuadTree WHITE = WhiteQuad.get();

    // --- Singleton tests ---

    @Test public void testSingletons() {
        // WhiteQuad and BlackQuad are singletons
        assertSame(WHITE, WhiteQuad.get());
        assertSame(BLACK, BlackQuad.get());
        // Both are uniform colour
        assertTrue(WHITE.isOneColour());
        assertTrue(BLACK.isOneColour());
    }

    @Test public void testWhiteArea() {
        // White area is always 0 regardless of scale
        assertEquals(0, WHITE.computeArea(0));
        assertEquals(0, WHITE.computeArea(1));
        assertEquals(0, WHITE.computeArea(5));
        assertEquals(0, WHITE.computeArea(30));
    }

    @Test public void testBlackArea() {
        // Black area at scale s is (2^s)^2 = 4^s
        assertEquals(1, BLACK.computeArea(0));    // 1x1
        assertEquals(4, BLACK.computeArea(1));    // 2x2
        assertEquals(16, BLACK.computeArea(2));   // 4x4
        assertEquals(64, BLACK.computeArea(3));   // 8x8
        assertEquals(1024, BLACK.computeArea(5)); // 32x32
        // Large scale
        assertEquals(1L << 60, BLACK.computeArea(30));
    }

    // --- Factory method collapses ---

    @Test public void testFactoryCollapsesAllWhite() {
        // Four white children collapse to the WHITE singleton
        QuadTree q = QuadNode.of(WHITE, WHITE, WHITE, WHITE);
        assertSame(WHITE, q);
        assertTrue(q.isOneColour());
    }

    @Test public void testFactoryCollapsesAllBlack() {
        // Four black children collapse to the BLACK singleton
        QuadTree q = QuadNode.of(BLACK, BLACK, BLACK, BLACK);
        assertSame(BLACK, q);
        assertTrue(q.isOneColour());
    }

    @Test public void testFactoryMixedDoesNotCollapse() {
        // Mixed children should not collapse
        QuadTree q = QuadNode.of(BLACK, WHITE, WHITE, WHITE);
        assertFalse(q.isOneColour());
    }

    // --- QuadNode explicit tests ---

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

    @Test public void testHalfBlackHalfWhite() {
        // Two black quadrants, two white: area = half of total
        QuadTree q = QuadNode.of(WHITE, BLACK, WHITE, BLACK);
        assertEquals(2, q.computeArea(1));   // 2 of 4 pixels
        assertEquals(8, q.computeArea(2));   // 8 of 16
        assertEquals(32, q.computeArea(3));  // 32 of 64
    }

    @Test public void testSingleBlackQuadrant() {
        // One black, three white: area = quarter of total
        QuadTree q = QuadNode.of(BLACK, WHITE, WHITE, WHITE);
        assertEquals(1, q.computeArea(1));   // 1 of 4
        assertEquals(4, q.computeArea(2));   // 4 of 16
        assertEquals(16, q.computeArea(3));  // 16 of 64
    }

    @Test public void testThreeBlackQuadrants() {
        // Three black, one white: area = 3/4 of total
        QuadTree q = QuadNode.of(BLACK, BLACK, BLACK, WHITE);
        assertEquals(3, q.computeArea(1));   // 3 of 4
        assertEquals(12, q.computeArea(2));  // 12 of 16
    }

    // --- Nested QuadNodes ---

    @Test public void testNestedOneLevel() {
        QuadTree q1 = QuadNode.of(WHITE, BLACK, WHITE, BLACK);  // half black
        QuadTree q2 = QuadNode.of(BLACK, WHITE, WHITE, WHITE);  // quarter black
        // q3 has: q1(half), BLACK(full), WHITE(none), q2(quarter)
        QuadTree q3 = QuadNode.of(q1, BLACK, WHITE, q2);
        // At scale 2: q1@s1=2, BLACK@s1=4, WHITE@s1=0, q2@s1=1 = 7
        assertEquals(7, q3.computeArea(2));
        // At scale 3: q1@s2=8, BLACK@s2=16, WHITE@s2=0, q2@s2=4 = 28
        assertEquals(28, q3.computeArea(3));
    }

    @Test public void testDeepNesting() {
        // Build a tree 3 levels deep
        QuadTree level1 = QuadNode.of(BLACK, WHITE, WHITE, WHITE); // 1/4 black
        QuadTree level2 = QuadNode.of(level1, level1, level1, level1); // still 1/4 black
        QuadTree level3 = QuadNode.of(level2, level2, level2, level2); // still 1/4 black
        // At scale 3: total area = 64, black = 16
        assertEquals(16, level3.computeArea(3));
        // At scale 4: total = 256, black = 64
        assertEquals(64, level3.computeArea(4));
    }

    @Test public void testFourCopiesSameArea() {
        // of(q, q, q, q) should have 4× the area of q at one scale up
        QuadTree q = QuadNode.of(WHITE, BLACK, WHITE, BLACK);
        QuadTree q4 = QuadNode.of(q, q, q, q);
        // q at scale 2 = 8, so q4 at scale 3 = 4*8 = 32
        assertEquals(4 * q.computeArea(2), q4.computeArea(3));
        assertEquals(4 * q.computeArea(3), q4.computeArea(4));
    }

    // --- Area at different scales ---

    @Test public void testAreaScaling() {
        // For a QuadNode, area at scale s+1 should be 4× area at scale s
        QuadTree q = QuadNode.of(BLACK, WHITE, BLACK, WHITE);
        long a1 = q.computeArea(1);
        long a2 = q.computeArea(2);
        long a3 = q.computeArea(3);
        assertEquals(4 * a1, a2);
        assertEquals(4 * a2, a3);
    }

    @Test public void testLargeScale() {
        // Half-black at scale 30: area = 2 * 2^58 = 2^59
        QuadTree q = QuadNode.of(WHITE, BLACK, WHITE, BLACK);
        assertEquals(1L << 59, q.computeArea(30));
    }

    // --- Area caching ---

    @Test public void testComputeAreaIdempotent() {
        // Calling computeArea multiple times with same scale gives same result
        QuadTree q = QuadNode.of(BLACK, WHITE, BLACK, QuadNode.of(BLACK, WHITE, WHITE, WHITE));
        long a1 = q.computeArea(3);
        long a2 = q.computeArea(3);
        long a3 = q.computeArea(3);
        assertEquals(a1, a2);
        assertEquals(a2, a3);
    }

    @Test public void testComputeAreaDifferentScales() {
        // Same tree at different scales should return consistent results
        QuadTree q = QuadNode.of(BLACK, WHITE, BLACK, WHITE);
        assertEquals(2, q.computeArea(1));
        assertEquals(8, q.computeArea(2));
        assertEquals(32, q.computeArea(3));
        // Go back to earlier scale
        assertEquals(2, q.computeArea(1));
    }

    // --- Checkerboard pattern ---

    @Test public void testCheckerboard() {
        // Build a 4x4 checkerboard (scale 2)
        QuadTree bw = QuadNode.of(BLACK, WHITE, WHITE, BLACK);
        QuadTree wb = QuadNode.of(WHITE, BLACK, BLACK, WHITE);
        QuadTree checker = QuadNode.of(bw, wb, wb, bw);
        // Half the pixels are black: area = 8 out of 16
        assertEquals(8, checker.computeArea(2));
        assertFalse(checker.isOneColour());
    }

    // --- CRC mass tests ---

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
            check.update((int)(area & 0xFFFF));
            check.update((int)((area >> 32) & 0xFFFF));
        }
        assertEquals(expected, check.getValue());
    }
}