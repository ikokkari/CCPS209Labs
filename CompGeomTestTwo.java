import static org.junit.Assert.*;
import org.junit.Test;
import java.util.*;
import java.util.zip.CRC32;
import java.util.Random;

public class CompGeomTestTwo {

    private static int cross(int x0, int y0, int x1, int y1) {
        return x0 * y1 - x1 * y0;
    }
    
    private static int ccw(int x0, int y0, int x1, int y1, int x2, int y2) {
        return cross(x1 - x0, y1 - y0, x2 - x0, y2 - y0);
    }
    
    @Test public void testIsPolygonHundred() {
        testIsPolygon(100, 2359285126L);
    }
    
    @Test public void testIsPolygonTenThousand() {
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