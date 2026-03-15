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

    // --- isSimplePolygon explicit tests ---

    @Test public void testIsSimplePolygonExplicit() {
        int[] xs0 = {1, 0, -1};
        int[] ys0 = {-1, 0, 0};
        assertTrue(CompGeom.isSimplePolygon(xs0, ys0));
        int[] xs1 = {0, 4, -1, 1};
        int[] ys1 = {0, 2, 1, -3};
        assertTrue(CompGeom.isSimplePolygon(xs1, ys1));
        int[] xs2 = {3, 3, 6, 6};
        int[] ys2 = {2, 4, 2, 4};
        assertFalse(CompGeom.isSimplePolygon(xs2, ys2));
        int[] xs3 = {0, 0, 5, 5, 0};
        int[] ys3 = {1, 4, 3, -1, 2};
        assertFalse(CompGeom.isSimplePolygon(xs3, ys3));
        int BIG = 10_000;
        int[] xs4 = {2, BIG, 2, BIG-1};
        int[] ys4 = {2, 3, 4, 3};
        assertTrue(CompGeom.isSimplePolygon(xs4, ys4));
        int[] xs5 = {0, -1, 5, 0, 4, 1};
        int[] ys5 = {0, 3, 2, 0, -4, -2};
        assertFalse(CompGeom.isSimplePolygon(xs5, ys5));
    }

    @Test public void testIsSimplePolygonTriangle() {
        // Simple triangle
        assertTrue(CompGeom.isSimplePolygon(new int[]{0, 1, 0}, new int[]{0, 0, 1}));
    }

    @Test public void testIsSimplePolygonSquare() {
        // Square: counterclockwise and clockwise both valid
        assertTrue(CompGeom.isSimplePolygon(new int[]{0, 2, 2, 0}, new int[]{0, 0, 2, 2}));
        assertTrue(CompGeom.isSimplePolygon(new int[]{0, 0, 2, 2}, new int[]{0, 2, 2, 0}));
    }

    @Test public void testIsSimplePolygonTooFewPoints() {
        // Less than 3 points: not a polygon
        assertFalse(CompGeom.isSimplePolygon(new int[]{0, 1}, new int[]{0, 1}));
    }

    @Test public void testIsSimplePolygonCollinearPoints() {
        // Three collinear points
        assertFalse(CompGeom.isSimplePolygon(new int[]{0, 1, 2}, new int[]{0, 1, 2}));
        // Four points with three consecutive collinear
        assertFalse(CompGeom.isSimplePolygon(new int[]{0, 1, 2, 1}, new int[]{0, 0, 0, 1}));
    }

    @Test public void testIsSimplePolygonSelfIntersecting() {
        // Bowtie: edges cross
        assertFalse(CompGeom.isSimplePolygon(new int[]{0, 2, 0, 2}, new int[]{0, 2, 2, 0}));
        // Figure-8 shape
        assertFalse(CompGeom.isSimplePolygon(new int[]{0, 2, 0, 2}, new int[]{0, 0, 2, 2}));
    }

    @Test public void testIsSimplePolygonConcave() {
        // Concave but simple (L-shape)
        assertTrue(CompGeom.isSimplePolygon(
                new int[]{0, 3, 3, 2, 2, 0}, new int[]{0, 0, 1, 1, 3, 3}));
    }

    @Test public void testIsSimplePolygonConvex() {
        // Convex pentagon
        assertTrue(CompGeom.isSimplePolygon(
                new int[]{2, 0, -1, -1, 2}, new int[]{0, 2, 1, -1, -1}));
        // Convex hexagon
        assertTrue(CompGeom.isSimplePolygon(
                new int[]{2, 1, -1, -2, -1, 1}, new int[]{0, 2, 2, 0, -2, -2}));
    }

    // --- shoelaceArea explicit tests ---

    @Test public void testShoelaceAreaExplicit() {
        int[] xs1 = {0, 3, 3, 0};
        int[] ys1 = {0, 0, 3, 3};
        assertEquals(18, CompGeom.shoelaceArea(xs1, ys1));
        int[] xs2 = {2, 4, 6};
        int[] ys2 = {0, 7, 1};
        assertEquals(-26, CompGeom.shoelaceArea(xs2, ys2));
        int[] xs3 = {2, 6, 4};
        int[] ys3 = {0, 1, 7};
        assertEquals(26, CompGeom.shoelaceArea(xs3, ys3));
        int[] xs4 = {2, 6, 4, 4};
        int[] ys4 = {0, 2, 2, 6};
        assertEquals(12, CompGeom.shoelaceArea(xs4, ys4));
        int BIG = 10_001;
        int[] xs5 = {0, 1, 0};
        int[] ys5 = {0, 0, BIG};
        assertEquals(10001, CompGeom.shoelaceArea(xs5, ys5));
    }

    @Test public void testShoelaceAreaUnitSquare() {
        // CCW: positive area = 1, return 2
        assertEquals(2, CompGeom.shoelaceArea(
                new int[]{0, 1, 1, 0}, new int[]{0, 0, 1, 1}));
        // CW: negative, return -2
        assertEquals(-2, CompGeom.shoelaceArea(
                new int[]{0, 0, 1, 1}, new int[]{0, 1, 1, 0}));
    }

    @Test public void testShoelaceAreaRightTriangle() {
        // (0,0),(3,0),(0,4): area=6, return 12
        assertEquals(12, CompGeom.shoelaceArea(
                new int[]{0, 3, 0}, new int[]{0, 0, 4}));
    }

    @Test public void testShoelaceAreaHalfIntegerArea() {
        // (0,0),(1,0),(0,3): area=1.5, return 3
        assertEquals(3, CompGeom.shoelaceArea(
                new int[]{0, 1, 0}, new int[]{0, 0, 3}));
        // (0,0),(1,0),(0,1): area=0.5, return 1
        assertEquals(1, CompGeom.shoelaceArea(
                new int[]{0, 1, 0}, new int[]{0, 0, 1}));
    }

    @Test public void testShoelaceAreaRectangle() {
        // 2x5 rectangle: area=10, return 20
        assertEquals(20, CompGeom.shoelaceArea(
                new int[]{0, 5, 5, 0}, new int[]{0, 0, 2, 2}));
    }

    @Test public void testShoelaceAreaConcaveLShape() {
        // L-shape: 3x3 minus 1x2 corner = 9-2 = 7, return 14
        assertEquals(14, CompGeom.shoelaceArea(
                new int[]{0, 3, 3, 2, 2, 0}, new int[]{0, 0, 1, 1, 3, 3}));
    }

    @Test public void testShoelaceAreaTranslationInvariant() {
        // Area magnitude should not change under translation
        int[] xs = {0, 3, 3, 2, 2, 0};
        int[] ys = {0, 0, 1, 1, 3, 3};
        int area1 = CompGeom.shoelaceArea(xs, ys);
        // Translate by (10, 10)
        int[] xs2 = new int[xs.length];
        int[] ys2 = new int[ys.length];
        for (int i = 0; i < xs.length; i++) {
            xs2[i] = xs[i] + 10;
            ys2[i] = ys[i] + 10;
        }
        int area2 = CompGeom.shoelaceArea(xs2, ys2);
        assertEquals(area1, area2);
    }

    @Test public void testShoelaceAreaReversalNegates() {
        // Reversing the vertex order negates the signed area
        int[] xs = {0, 3, 1};
        int[] ys = {0, 0, 4};
        int areaCCW = CompGeom.shoelaceArea(xs, ys);
        int[] xsRev = {1, 3, 0};
        int[] ysRev = {4, 0, 0};
        int areaCW = CompGeom.shoelaceArea(xsRev, ysRev);
        assertEquals(areaCCW, -areaCW);
    }

    @Test public void testShoelaceAreaTriangleEqualsccw() {
        // For a triangle, shoelaceArea == ccw of the three points
        int[] xs = {2, 6, 4};
        int[] ys = {0, 1, 7};
        int area = CompGeom.shoelaceArea(xs, ys);
        int c = ccw(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]);
        assertEquals(c, area);
    }

    // --- CRC mass tests ---

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
                int[] xxs = new int[count], yys = new int[count];
                for(int r = 0; r < count; r++) {
                    for(int j = 0; j < count; j++) {
                        xxs[j] = xs[(r+j)%count];
                        yys[j] = ys[(r+j)%count];
                    }
                    assertTrue(CompGeom.isSimplePolygon(xxs, yys));
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