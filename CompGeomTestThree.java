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
            // Ensure that random triangle is not just a point or a line segment.
            do { 
                for(int j = 0; j < 3; j++) {
                    xs[j] = rng.nextInt(nn) - nn / 2;
                    ys[j] = rng.nextInt(nn) - nn / 2;
                }
            } while(ccw(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]) == 0);
            // Make sure triangle is counterclockwise.
            if(ccw(xs[0], ys[0], xs[1], ys[1], xs[2], ys[2]) <= 0) {
                int tmp = xs[0]; xs[0] = xs[1]; xs[1] = tmp;
                tmp = ys[0]; ys[0] = ys[1]; ys[1] = tmp;
            }
            if(rng.nextBoolean()) {
                x = rng.nextInt(nn) - nn / 2;
                y = rng.nextInt(nn) - nn / 2;
            } else { // Create a bunch more hits than sheer random chance would
                x = (xs[0] + xs[1] + xs[2]) / 3;
                y = (ys[0] + ys[1] + ys[2]) / 3;
            }
            int result = CompGeom.pointInConvex(xs, ys, x, y);
            check.update(result);
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testPointIntPolygonExplicit() {
        int[] xs0 = {3, 2, 2, 4, 3};
        int[] ys0 = {0, 1, 0, 0, 3};
        int x0 = 3;
        int y0 = 1;
        assertEquals(2, CompGeom.pointInPolygon(xs0, ys0, x0, y0));
        assertEquals(2, CompGeom.pointInPolygon(ys0, xs0, y0, x0));

        int[] xs1 = {6, 4, 3, 4, 5, 4, 5};
        int[] ys1 = {2, 0, 5, 3, 4, 5, 4};
        int x1 = -1;
        int y1 = 2;
        assertEquals(0, CompGeom.pointInPolygon(xs1, ys1, x1, y1));
        assertEquals(0, CompGeom.pointInPolygon(ys1, xs1, y1, x1));

        int[] xs2 = {5, 3, 2, 3, 3, 5, 5};
        int[] ys2 = {2, 5, 5, 2, 0, 1, 1};
        int x2 = 3;
        int y2 = 4;
        assertEquals(3, CompGeom.pointInPolygon(xs2, ys2, x2, y2));
        assertEquals(3, CompGeom.pointInPolygon(ys2, xs2, y2, x2));

        int[] xs3 = {5, 5, 7, 7, 2, 4, 4, 3};
        int[] ys3 = {4, 7, 2, 1, 6, 4, 5, 4};
        int x3 = 5;
        int y3 = 4;
        assertEquals(1, CompGeom.pointInPolygon(xs3, ys3, x3, y3));
        assertEquals(1, CompGeom.pointInPolygon(ys3, xs3, y3, x3));

        int[] xs4 = {4, 1, 1, 2, 0, 4};
        int[] ys4 = {0, 2, 0, 3, 1, 4};
        int x4 = 2;
        int y4 = 0;
        assertEquals(0, CompGeom.pointInPolygon(xs4, ys4, x4, y4));
        assertEquals(0, CompGeom.pointInPolygon(ys4, xs4, y4, x4));

        int[] xs5 = {1, 2, 7, 4, 4, 6, 4, 7};
        int[] ys5 = {0, 1, 4, 7, 2, 0, 5, 3};
        int x5 = 3;
        int y5 = 1;
        assertEquals(2, CompGeom.pointInPolygon(xs5, ys5, x5, y5));
        assertEquals(2, CompGeom.pointInPolygon(ys5, xs5, y5, x5));

        int[] xs6 = {3, 0, 6, 2, 3, 5, 5, 1};
        int[] ys6 = {0, 7, 0, 0, 7, 5, 0, 2};
        int x6 = 3;
        int y6 = 2;
        assertEquals(3, CompGeom.pointInPolygon(xs6, ys6, x6, y6));
        assertEquals(3, CompGeom.pointInPolygon(ys6, xs6, y6, x6));

        int[] xs7 = {2, 9, 5, 4, 8, 0, 1, 6, 1, 1, 7};
        int[] ys7 = {4, 9, 6, 2, 10, 1, 8, 1, 7, 6, 0};
        int x7 = 5;
        int y7 = 6;
        assertEquals(1, CompGeom.pointInPolygon(xs7, ys7, x7, y7));
        assertEquals(1, CompGeom.pointInPolygon(ys7, xs7, y7, x7));
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
