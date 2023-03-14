import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import org.junit.Test;
import java.util.Arrays;
import java.util.Random;
import java.util.zip.CRC32;

public class CompGeomTestTwo {

    private static int cross(int x0, int y0, int x1, int y1) {
        return x0 * y1 - x1 * y0;
    }
    
    private static int ccw(int x0, int y0, int x1, int y1, int x2, int y2) {
        return cross(x1 - x0, y1 - y0, x2 - x0, y2 - y0);
    }

    @Test public void testIsSimplePolygonExplicit() {
        int[] xs0 = {1, 0, -1}; // Small and simple
        int[] ys0 = {-1, 0, 0};
        assertTrue(CompGeom.isSimplePolygon(xs0, ys0));
        int[] xs1 = {0, 4, -1, 1}; // Wonky but legal
        int[] ys1 = {0, 2, 1, -3};
        assertTrue(CompGeom.isSimplePolygon(xs1, ys1));
        int[] xs2 = {3, 3, 6, 6}; // Bowtie with intersecting segments
        int[] ys2 = {2, 4, 2, 4};
        assertFalse(CompGeom.isSimplePolygon(xs2, ys2));
        int[] xs3 = {0, 0, 5, 5, 0}; // Three collinear corner points
        int[] ys3 = {1, 4, 3, -1, 2};
        assertFalse(CompGeom.isSimplePolygon(xs3, ys3));
        int BIG = 10_000;
        int[] xs4 = {2, BIG, 2, BIG-1}; // Deep sharp wedge
        int[] ys4 = {2, 3, 4, 3};
        assertTrue(CompGeom.isSimplePolygon(xs4, ys4));
        int[] xs5 = {0, -1, 5, 0, 4, 1}; // Common corner points between segments
        int[] ys5 = {0, 3, 2, 0, -4, -2};
        assertFalse(CompGeom.isSimplePolygon(xs5, ys5));
    }

    @Test public void testIsSimplePolygonHundred() {
        testIsPolygon(100, 2359285126L);
    }
    
    @Test public void testIsSimplePolygonTenThousand() {
        testIsPolygon(10_000, 2764570742L);
    }
    
    private void testIsPolygon(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 6 + i / 10;
            int[] xs = new int[nn], ys = new int[nn];
            for(int j = 0; j < nn; j++) {
                xs[j] = rng.nextInt(nn / 2) - nn/4;
                ys[j] = rng.nextInt(nn / 2) - nn/4;
            }
            int count = 3; int[] pxs, pys;
            do {
                pxs = Arrays.copyOfRange(xs, 0, count);
                pys = Arrays.copyOfRange(ys, 0, count);
            } while(CompGeom.isSimplePolygon(pxs, pys) && ++count < nn);
            check.update(count);
            if(--count > 2) {
                // Cyclic rotation of points cannot turn a polygon into a non-polygon.
                int[] xxs = new int[count], yys = new int[count];
                for(int r = 0; r < count; r++) {
                    for(int j = 0; j < count; j++) {
                        xxs[j] = xs[(r+j)%count];
                        yys[j] = ys[(r+j)%count];
                    }
                    assertTrue(CompGeom.isSimplePolygon(xxs, yys));
                    // Neither can mirroring and shearing.
                    int s = rng.nextInt(100) - 50;
                    for(int j = 0; j < count; j++) {
                        xxs[j] = ys[(r+j)%count] + s;
                        yys[j] = xs[(r+j)%count] - 3 * s;
                    }
                    assertTrue(CompGeom.isSimplePolygon(xxs, yys));
                }
            }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testShoelaceAreaExplicit() {
        int[] xs1 = {0, 3, 3, 0}; // Simple 3-by-3 square, twice its area equals 18
        int[] ys1 = {0, 0, 3, 3};
        assertEquals(18, CompGeom.shoelaceArea(xs1, ys1));
        int[] xs2 = {2, 4, 6}; // Single triangle, clockwise
        int[] ys2 = {0, 7, 1};
        assertEquals(-26, CompGeom.shoelaceArea(xs2, ys2));
        int[] xs3 = {2, 6, 4}; // Same triangle, but counterclockwise
        int[] ys3 = {0, 1, 7};
        assertEquals(26, CompGeom.shoelaceArea(xs3, ys3));
        int[] xs4 = {2, 6, 4, 4}; // Wedge
        int[] ys4 = {0, 2, 2, 6};
        assertEquals(12, CompGeom.shoelaceArea(xs4, ys4));
        int BIG = 10_001;
        int[] xs5 = {0, 1, 0}; // Triangle with area of integer plus one half
        int[] ys5 = {0, 0, BIG};
        assertEquals(10001, CompGeom.shoelaceArea(xs5, ys5));
    }

    @Test public void testShoelaceAreaHundred() {
        testShoelaceArea(100, 3417267926L);
    }
    
    @Test public void testShoelaceAreaTenThousand() {
        testShoelaceArea(10_000, 2580355296L);
    }
    
    private void testShoelaceArea(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 3 + i / 10;
            int[] xs = new int[nn], ys = new int[nn];
            for(int j = 0; j < nn; j++) {
                xs[j] = rng.nextInt(5 + nn);
                ys[j] = rng.nextInt(5 + nn);
            }
            int result = CompGeom.shoelaceArea(xs, ys);
            if(nn == 3) {
                int cr = ccw(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]);
                assertEquals(result, cr);
            }
            check.update(result);
        }
        assertEquals(expected, check.getValue());        
    }
}