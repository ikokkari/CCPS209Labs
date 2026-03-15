import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class CompGeomTestThree {

    public static int cross(int x0, int y0, int x1, int y1) {
        return x0 * y1 - x1 * y0;
    }

    public static int ccw(int x0, int y0, int x1, int y1, int x2, int y2) {
        return cross(x1 - x0, y1 - y0, x2 - x0, y2 - y0);
    }

    // --- pointInConvex explicit tests ---

    @Test public void testPointInConvexTriangle() {
        // CCW triangle: (0,0),(4,0),(0,4)
        int[] xs = {0, 4, 0}, ys = {0, 0, 4};
        // Corners -> 1
        assertEquals(1, CompGeom.pointInConvex(xs, ys, 0, 0));
        assertEquals(1, CompGeom.pointInConvex(xs, ys, 4, 0));
        assertEquals(1, CompGeom.pointInConvex(xs, ys, 0, 4));
        // Edges -> 2
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 2, 0)); // bottom edge
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 0, 2)); // left edge
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 2, 2)); // hypotenuse
        // Inside -> 3
        assertEquals(3, CompGeom.pointInConvex(xs, ys, 1, 1));
        // Outside -> 0
        assertEquals(0, CompGeom.pointInConvex(xs, ys, 3, 3));
        assertEquals(0, CompGeom.pointInConvex(xs, ys, -1, 0));
        assertEquals(0, CompGeom.pointInConvex(xs, ys, 5, 0));
        assertEquals(0, CompGeom.pointInConvex(xs, ys, 0, -1));
    }

    @Test public void testPointInConvexSquare() {
        // CCW square: (0,0),(3,0),(3,3),(0,3)
        int[] xs = {0, 3, 3, 0}, ys = {0, 0, 3, 3};
        assertEquals(1, CompGeom.pointInConvex(xs, ys, 0, 0));  // corner
        assertEquals(1, CompGeom.pointInConvex(xs, ys, 3, 3));  // corner
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 1, 0));  // bottom edge
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 3, 1));  // right edge
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 2, 3));  // top edge
        assertEquals(2, CompGeom.pointInConvex(xs, ys, 0, 2));  // left edge
        assertEquals(3, CompGeom.pointInConvex(xs, ys, 1, 1));  // inside
        assertEquals(3, CompGeom.pointInConvex(xs, ys, 2, 2));  // inside
        assertEquals(0, CompGeom.pointInConvex(xs, ys, 4, 1));  // outside right
        assertEquals(0, CompGeom.pointInConvex(xs, ys, -1, 1)); // outside left
        assertEquals(0, CompGeom.pointInConvex(xs, ys, 1, -1)); // outside below
        assertEquals(0, CompGeom.pointInConvex(xs, ys, 1, 4));  // outside above
    }

    @Test public void testPointInConvexAllFourResults() {
        // Verify all four return values on a single convex polygon
        // Pentagon CCW: (5,2),(4,5),(1,5),(0,2),(3,0)
        int[] xs = {5, 4, 1, 0, 3}, ys = {2, 5, 5, 2, 0};
        // A corner
        assertEquals(1, CompGeom.pointInConvex(xs, ys, 5, 2));
        // Far outside
        assertEquals(0, CompGeom.pointInConvex(xs, ys, -5, -5));
        // Centroid-ish should be inside
        assertEquals(3, CompGeom.pointInConvex(xs, ys, 3, 3));
    }

    // --- pointInPolygon explicit tests ---

    @Test public void testPointInPolygonExplicit() {
        int[] xs0 = {3, 2, 2, 4, 3};
        int[] ys0 = {0, 1, 0, 0, 3};
        assertEquals(2, CompGeom.pointInPolygon(xs0, ys0, 3, 1));
        assertEquals(2, CompGeom.pointInPolygon(ys0, xs0, 1, 3));

        int[] xs1 = {6, 4, 3, 4, 5, 4, 5};
        int[] ys1 = {2, 0, 5, 3, 4, 5, 4};
        assertEquals(0, CompGeom.pointInPolygon(xs1, ys1, -1, 2));
        assertEquals(0, CompGeom.pointInPolygon(ys1, xs1, 2, -1));

        int[] xs2 = {5, 3, 2, 3, 3, 5, 5};
        int[] ys2 = {2, 5, 5, 2, 0, 1, 1};
        assertEquals(3, CompGeom.pointInPolygon(xs2, ys2, 3, 4));
        assertEquals(3, CompGeom.pointInPolygon(ys2, xs2, 4, 3));

        int[] xs3 = {5, 5, 7, 7, 2, 4, 4, 3};
        int[] ys3 = {4, 7, 2, 1, 6, 4, 5, 4};
        assertEquals(1, CompGeom.pointInPolygon(xs3, ys3, 5, 4));
        assertEquals(1, CompGeom.pointInPolygon(ys3, xs3, 4, 5));

        int[] xs4 = {4, 1, 1, 2, 0, 4};
        int[] ys4 = {0, 2, 0, 3, 1, 4};
        assertEquals(0, CompGeom.pointInPolygon(xs4, ys4, 2, 0));
        assertEquals(0, CompGeom.pointInPolygon(ys4, xs4, 0, 2));

        int[] xs5 = {1, 2, 7, 4, 4, 6, 4, 7};
        int[] ys5 = {0, 1, 4, 7, 2, 0, 5, 3};
        assertEquals(2, CompGeom.pointInPolygon(xs5, ys5, 3, 1));
        assertEquals(2, CompGeom.pointInPolygon(ys5, xs5, 1, 3));

        int[] xs6 = {3, 0, 6, 2, 3, 5, 5, 1};
        int[] ys6 = {0, 7, 0, 0, 7, 5, 0, 2};
        assertEquals(3, CompGeom.pointInPolygon(xs6, ys6, 3, 2));
        assertEquals(3, CompGeom.pointInPolygon(ys6, xs6, 2, 3));

        int[] xs7 = {2, 9, 5, 4, 8, 0, 1, 6, 1, 1, 7};
        int[] ys7 = {4, 9, 6, 2, 10, 1, 8, 1, 7, 6, 0};
        assertEquals(1, CompGeom.pointInPolygon(xs7, ys7, 5, 6));
        assertEquals(1, CompGeom.pointInPolygon(ys7, xs7, 6, 5));
    }

    @Test public void testPointInPolygonSimpleTriangle() {
        // Simple triangle (0,0),(6,0),(0,6) — all four result types
        int[] xs = {0, 6, 0}, ys = {0, 0, 6};
        assertEquals(1, CompGeom.pointInPolygon(xs, ys, 0, 0));  // corner
        assertEquals(1, CompGeom.pointInPolygon(xs, ys, 6, 0));  // corner
        assertEquals(2, CompGeom.pointInPolygon(xs, ys, 3, 0));  // bottom edge
        assertEquals(0, CompGeom.pointInPolygon(xs, ys, -1, 0)); // outside
        assertEquals(0, CompGeom.pointInPolygon(xs, ys, 4, 4));  // outside (beyond hypotenuse)
    }

    @Test public void testPointInPolygonSquare() {
        // Square (0,0),(4,0),(4,4),(0,4)
        int[] xs = {0, 4, 4, 0}, ys = {0, 0, 4, 4};
        assertEquals(3, CompGeom.pointInPolygon(xs, ys, 2, 2));  // inside
        assertEquals(2, CompGeom.pointInPolygon(xs, ys, 2, 0));  // bottom edge
        assertEquals(1, CompGeom.pointInPolygon(xs, ys, 4, 4));  // corner
        assertEquals(0, CompGeom.pointInPolygon(xs, ys, 5, 2));  // outside
    }

    @Test public void testPointInPolygonConcaveLShape() {
        // L-shape: (0,0),(4,0),(4,2),(2,2),(2,4),(0,4)
        int[] xs = {0, 4, 4, 2, 2, 0}, ys = {0, 0, 2, 2, 4, 4};
        assertEquals(3, CompGeom.pointInPolygon(xs, ys, 1, 1));  // inside lower rect
        assertEquals(3, CompGeom.pointInPolygon(xs, ys, 1, 3));  // inside upper rect
        assertEquals(3, CompGeom.pointInPolygon(xs, ys, 3, 1));  // inside lower right
        assertEquals(0, CompGeom.pointInPolygon(xs, ys, 3, 3));  // in the cut corner
        assertEquals(1, CompGeom.pointInPolygon(xs, ys, 2, 2));  // corner point
        assertEquals(1, CompGeom.pointInPolygon(xs, ys, 0, 0));  // corner
        assertEquals(2, CompGeom.pointInPolygon(xs, ys, 2, 0));  // bottom edge
        assertEquals(0, CompGeom.pointInPolygon(xs, ys, 5, 1));  // far outside
    }

    @Test public void testPointInPolygonMirrorSymmetry() {
        // The explicit tests verify mirror symmetry by testing (xs,ys,x,y) and (ys,xs,y,x)
        // Here we add one more: result should be same under coordinate swap for a symmetric polygon
        int[] xs = {0, 4, 4, 0}, ys = {0, 0, 4, 4};
        // (1,2) and (2,1) should both be inside a square
        assertEquals(3, CompGeom.pointInPolygon(xs, ys, 1, 2));
        assertEquals(3, CompGeom.pointInPolygon(xs, ys, 2, 1));
    }

    // --- CRC mass tests ---

    @Test public void testPointInConvexHundred() {
        testPointInConvex(100, 3409262577L);
    }

    @Test public void testPointInConvexMillion() {
        testPointInConvex(1_000_000, 625739499L);
    }

    private void testPointInConvex(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        int[] xs = new int[3], ys = new int[3];
        int x, y;
        for(int i = 0; i < n; i++) {
            int nn = i / 5 + 3;
            do {
                for(int j = 0; j < 3; j++) {
                    xs[j] = rng.nextInt(nn) - nn / 2;
                    ys[j] = rng.nextInt(nn) - nn / 2;
                }
            } while(ccw(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]) == 0);
            if(ccw(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]) <= 0) {
                int tmp = xs[0]; xs[0] = xs[1]; xs[1] = tmp;
                tmp = ys[0]; ys[0] = ys[1]; ys[1] = tmp;
            }
            if(rng.nextBoolean()) {
                x = rng.nextInt(nn) - nn / 2;
                y = rng.nextInt(nn) - nn / 2;
            } else {
                x = (xs[0] + xs[1] + xs[2]) / 3;
                y = (ys[0] + ys[1] + ys[2]) / 3;
            }
            int result = CompGeom.pointInConvex(xs, ys, x, y);
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testPointInPolygonHundred() {
        testPointInPolygon(100, 1295708785L);
    }

    @Test public void testPointInPolygonTenThousand() {
        testPointInPolygon(10_000, 3132411435L);
    }

    private void testPointInPolygon(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 3 + i / 10;
            int[] xs = new int[nn], ys = new int[nn];
            for(int j = 0; j < nn; j++) {
                xs[j] = rng.nextInt(nn);
                ys[j] = rng.nextInt(nn);
            }
            int x, y;
            if(rng.nextBoolean()) {
                x = rng.nextInt(nn + 2) - 1;
                y = rng.nextInt(nn + 2) - 1;
            }
            else {
                x = (xs[0] + xs[1] + xs[2]) / 3;
                y = (ys[0] + ys[1] + ys[2]) / 3;
            }
            int result = CompGeom.pointInPolygon(xs, ys, x, y);
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }
}