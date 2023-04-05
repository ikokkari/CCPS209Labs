import org.junit.Test;

import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CompGeomTestOne {

    @Test public void testSegmentIntersectHundred() {
        testSegmentIntersect(100, 3714949161L);
    }
    
    @Test public void testSegmentIntersectMillion() {
        testSegmentIntersect(1_000_000, 1447273406L);
    }
    
    // Explicit test cases
    @Test public void testSegmentIntersect() {
        // First four arguments are segment (x0, y0)-(x1, y1), the last four are segment (x2, y2)-(x3, y3)
        assertTrue(CompGeom.segmentIntersect(0, 1, 2, 0, -1, -1, 3, 1));
        assertTrue(CompGeom.segmentIntersect(0, 1, 2, 0, 0, 0, 0, 4));
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

        // If this one gives wrong answer, you are using multiplication that overflows.
        assertTrue(CompGeom.segmentIntersect(-128, -150, 73, 76, -93, 150, 128, -162));
        
        assertFalse(CompGeom.segmentIntersect(1, 0, 5, 0, 3, 5, 7, -4));
        assertFalse(CompGeom.segmentIntersect(1, 0, 5, 0, 3, 5, 7, -4));
        assertFalse(CompGeom.segmentIntersect(0, -1000, 5, 4, 0, 0, 5, 5));
        assertFalse(CompGeom.segmentIntersect(10, 10, 0, 20, 8, 11, -3, 21));
        assertFalse(CompGeom.segmentIntersect(0, 0, 10, 2, 7, -1, 11, 2));
        assertFalse(CompGeom.segmentIntersect(2, 3, 0, 0, 0, 1, 1, 2));
        assertFalse(CompGeom.segmentIntersect(-2, 3, 1, -5, 0, 2, 2, 1));
        assertFalse(CompGeom.segmentIntersect(-1, 2, 2, 2, 1, -2, 3, 3));
    }
    
    // Pseudorandom fuzz tester
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
                    c[0], c[1], c[2], c[3], c[4], c[5], c[6], c[7]
            );
            check.update(result ? i: 0);
            // Swapping the roles of segments should not affect the result.
            boolean result2 = CompGeom.segmentIntersect(
                    c[4], c[5], c[6], c[7], c[0], c[1], c[2], c[3]
            );
            assertEquals(result, result2);
            // Swapping the directions of segments also should not affect the result.
            boolean result3 = CompGeom.segmentIntersect(
                    c[2], c[3], c[0], c[1], c[6], c[7], c[4], c[5]
            );
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