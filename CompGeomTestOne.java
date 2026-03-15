import org.junit.Test;

import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompGeomTestOne {

    // --- cross explicit tests ---

    @Test public void testCross() {
        // Unit vectors: cross(i, j) = 1
        assertEquals(1, CompGeom.cross(1, 0, 0, 1));
        // Swapping arguments negates the result
        assertEquals(-1, CompGeom.cross(0, 1, 1, 0));
        // Scaled vectors
        assertEquals(12, CompGeom.cross(3, 0, 0, 4));
        // Parallel vectors: cross product is 0
        assertEquals(0, CompGeom.cross(1, 2, 2, 4));
        // Zero vector: cross product is 0
        assertEquals(0, CompGeom.cross(0, 0, 5, 3));
    }

    // --- ccw explicit tests ---

    @Test public void testCCW() {
        // Left turn (counterclockwise): positive
        assertTrue(CompGeom.ccw(0, 0, 1, 0, 0, 1) > 0);
        // Right turn (clockwise): negative
        assertTrue(CompGeom.ccw(0, 0, 0, 1, 1, 0) < 0);
        // Collinear: zero
        assertEquals(0, CompGeom.ccw(0, 0, 1, 1, 2, 2));
        // Collinear with middle point between endpoints
        assertEquals(0, CompGeom.ccw(0, 0, 2, 2, 1, 1));
        // All same point
        assertEquals(0, CompGeom.ccw(3, 3, 3, 3, 3, 3));
        // Large left turn
        assertTrue(CompGeom.ccw(0, 0, 100, 0, 50, 50) > 0);
        // Large right turn
        assertTrue(CompGeom.ccw(0, 0, 100, 0, 50, -50) < 0);
    }

    // --- segmentIntersect explicit tests ---

    @Test public void testSegmentIntersect() {
        // Standard crossing segments
        assertTrue(CompGeom.segmentIntersect(0, 1, 2, 0, -1, -1, 3, 1));
        assertTrue(CompGeom.segmentIntersect(0, 1, 2, 0, 0, 0, 0, 4));
        assertTrue(CompGeom.segmentIntersect(3, 2, 3, 0, 1, -1, 4, 3));
        assertTrue(CompGeom.segmentIntersect(0, 2, 2, 0, 0, 0, 1, 1));
        assertTrue(CompGeom.segmentIntersect(0, 0, 1, 1, 0, 2, 2, 0));
        assertTrue(CompGeom.segmentIntersect(-1, 4, 7, 2, 7, 2, 6, 10));
        assertTrue(CompGeom.segmentIntersect(0, 4, 4, 0, 1, 3, 3, 1));
        assertTrue(CompGeom.segmentIntersect(0, 0, 10, 2, 7, -1, 9, 2));
        assertTrue(CompGeom.segmentIntersect(1, 1, 3, 3, -1000, 1000, 1000, -995));
        assertTrue(CompGeom.segmentIntersect(0, 0, -1, -3, -2, 1, 2, -1));
        assertTrue(CompGeom.segmentIntersect(0, 0, -1, -1, 0, 0, 0, -1));
        assertTrue(CompGeom.segmentIntersect(-4, -5, 3, 4, -2, 7, 3, -8));
        assertTrue(CompGeom.segmentIntersect(-128, -150, 73, 76, -93, 150, 128, -162));

        assertFalse(CompGeom.segmentIntersect(1, 0, 5, 0, 3, 5, 7, -4));
        assertFalse(CompGeom.segmentIntersect(0, -1000, 5, 4, 0, 0, 5, 5));
        assertFalse(CompGeom.segmentIntersect(10, 10, 0, 20, 8, 11, -3, 21));
        assertFalse(CompGeom.segmentIntersect(0, 0, 10, 2, 7, -1, 11, 2));
        assertFalse(CompGeom.segmentIntersect(2, 3, 0, 0, 0, 1, 1, 2));
        assertFalse(CompGeom.segmentIntersect(-2, 3, 1, -5, 0, 2, 2, 1));
        assertFalse(CompGeom.segmentIntersect(-1, 2, 2, 2, 1, -2, 3, 3));
    }

    @Test public void testSegmentIntersectCollinear() {
        // Overlapping collinear segments
        assertTrue(CompGeom.segmentIntersect(0, 0, 2, 2, 1, 1, 3, 3));
        // Collinear touching at one endpoint
        assertTrue(CompGeom.segmentIntersect(0, 0, 1, 1, 1, 1, 2, 2));
        // Collinear with gap between them
        assertFalse(CompGeom.segmentIntersect(0, 0, 1, 1, 2, 2, 3, 3));
        // Collinear on x-axis, overlapping
        assertTrue(CompGeom.segmentIntersect(0, 0, 3, 0, 2, 0, 5, 0));
        // Collinear on x-axis, gap
        assertFalse(CompGeom.segmentIntersect(0, 0, 1, 0, 3, 0, 5, 0));
    }

    @Test public void testSegmentIntersectEndpointCases() {
        // Shared endpoint
        assertTrue(CompGeom.segmentIntersect(0, 0, 1, 1, 1, 1, 2, 0));
        // T-intersection: endpoint of one segment on the middle of the other
        assertTrue(CompGeom.segmentIntersect(0, 0, 2, 0, 1, 0, 1, 2));
        // Horizontal and vertical crossing
        assertTrue(CompGeom.segmentIntersect(0, 1, 4, 1, 2, 0, 2, 3));
        // Horizontal and vertical, near miss
        assertFalse(CompGeom.segmentIntersect(0, 1, 4, 1, 2, 2, 2, 3));
    }

    @Test public void testSegmentIntersectDegenerateSegments() {
        // Point segment on a line segment
        assertTrue(CompGeom.segmentIntersect(1, 1, 1, 1, 0, 0, 2, 2));
        // Point segment not on a line segment
        assertFalse(CompGeom.segmentIntersect(1, 2, 1, 2, 0, 0, 2, 2));
        // Two identical point segments
        assertTrue(CompGeom.segmentIntersect(1, 1, 1, 1, 1, 1, 1, 1));
        // Two different point segments
        assertFalse(CompGeom.segmentIntersect(1, 1, 1, 1, 2, 2, 2, 2));
    }

    @Test public void testSegmentIntersectParallel() {
        // Parallel horizontal segments (not collinear)
        assertFalse(CompGeom.segmentIntersect(0, 0, 2, 0, 0, 1, 2, 1));
        // Parallel diagonal segments
        assertFalse(CompGeom.segmentIntersect(0, 0, 2, 2, 0, 1, 2, 3));
    }

    @Test public void testSegmentIntersectSymmetry() {
        // Swapping segments shouldn't change the result
        int[][] cases = {
                {0,0,2,2,1,1,3,3}, {0,0,1,1,2,2,3,3},
                {1,1,1,1,0,0,2,2}, {0,0,2,0,1,0,1,2},
                {-4,-5,3,4,-2,7,3,-8}
        };
        for (int[] c : cases) {
            boolean r1 = CompGeom.segmentIntersect(c[0],c[1],c[2],c[3],c[4],c[5],c[6],c[7]);
            boolean r2 = CompGeom.segmentIntersect(c[4],c[5],c[6],c[7],c[0],c[1],c[2],c[3]);
            assertEquals("Symmetry failed", r1, r2);
            // Also reversing direction of both segments
            boolean r3 = CompGeom.segmentIntersect(c[2],c[3],c[0],c[1],c[6],c[7],c[4],c[5]);
            assertEquals("Direction reversal failed", r1, r3);
        }
    }

    // --- lineWithMostPoints explicit tests ---

    @Test public void testLineWithMostPointsAllCollinear() {
        assertEquals(5, CompGeom.lineWithMostPoints(
                new int[]{0, 1, 2, 3, 4}, new int[]{0, 1, 2, 3, 4}));
    }

    @Test public void testLineWithMostPointsNoThreeCollinear() {
        // Square corners: no three points are collinear
        assertEquals(2, CompGeom.lineWithMostPoints(
                new int[]{0, 1, 1, 0}, new int[]{0, 0, 1, 1}));
    }

    @Test public void testLineWithMostPointsMixed() {
        // Three on diagonal, two others off it
        assertEquals(3, CompGeom.lineWithMostPoints(
                new int[]{0, 1, 2, 0, 0}, new int[]{0, 1, 2, 1, 2}));
    }

    @Test public void testLineWithMostPointsSingle() {
        assertEquals(1, CompGeom.lineWithMostPoints(new int[]{5}, new int[]{3}));
    }

    @Test public void testLineWithMostPointsTwo() {
        assertEquals(2, CompGeom.lineWithMostPoints(
                new int[]{0, 1}, new int[]{0, 1}));
    }

    @Test public void testLineWithMostPointsGrid() {
        // 3x3 grid: rows, columns, and diagonals each have 3 points
        assertEquals(3, CompGeom.lineWithMostPoints(
                new int[]{0, 1, 2, 0, 1, 2, 0, 1, 2},
                new int[]{0, 0, 0, 1, 1, 1, 2, 2, 2}));
    }

    @Test public void testLineWithMostPointsHorizontalLine() {
        // Four points on horizontal line, one off it
        assertEquals(4, CompGeom.lineWithMostPoints(
                new int[]{0, 1, 2, 3, 1}, new int[]{0, 0, 0, 0, 1}));
    }

    @Test public void testLineWithMostPointsVertical() {
        // Vertical line: 4 collinear vertically, rest off
        assertEquals(4, CompGeom.lineWithMostPoints(
                new int[]{0, 0, 0, 0, 1, 2}, new int[]{0, 1, 2, 3, 0, 0}));
    }

    // --- CRC mass tests ---

    @Test public void testSegmentIntersectHundred() {
        testSegmentIntersect(100, 3714949161L);
    }

    @Test public void testSegmentIntersectMillion() {
        testSegmentIntersect(1_000_000, 1447273406L);
    }

    private void testSegmentIntersect(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int[] c = new int[8];
        for(int i = 0; i < n; i++) {
            int r = Math.min(3 + i / 3, 10000);
            int rr = r / 2;
            for(int j = 0; j < 8; j++) {
                c[j] = rng.nextInt(r) - rr;
            }
            boolean result = CompGeom.segmentIntersect(
                    c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7]);
            check.update(result ? i: 0);
            boolean result2 = CompGeom.segmentIntersect(
                    c[4], c[5], c[6], c[7], c[0], c[1], c[2], c[3]);
            assertEquals(result, result2);
            boolean result3 = CompGeom.segmentIntersect(
                    c[2], c[3], c[0], c[1], c[6], c[7], c[4], c[5]);
            assertEquals(result, result3);
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testLineWithMostPointsFifty() {
        testLineWithMostPoints(50, 1959874018L);
    }

    @Test public void testLineWithMostPointsThousand() {
        testLineWithMostPoints(1000, 2048170366L);
    }

    private void testLineWithMostPoints(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 3 + (i / 5);
            int[] xs = new int[nn], ys = new int[nn];
            boolean[][] taken = new boolean[nn][nn];
            for(int j = 0; j < nn; j++) {
                int x, y;
                do {
                    x = rng.nextInt(nn);
                    y = rng.nextInt(nn);
                } while(taken[x][y]);
                xs[j] = x - (nn/2); ys[j] = y - (nn/2); taken[x][y] = true;
            }
            int result = CompGeom.lineWithMostPoints(xs, ys);
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }
}