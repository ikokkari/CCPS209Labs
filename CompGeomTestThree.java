import org.junit.Test;
import java.util.Arrays;
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
