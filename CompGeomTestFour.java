import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

public class CompGeomTestFour {

    public static int cross(int x0, int y0, int x1, int y1) {
        return x0 * y1 - x1 * y0;
    }
    
    public static int ccw(int x0, int y0, int x1, int y1, int x2, int y2) {
        return cross(x1 - x0, y1 - y0, x2 - x0, y2 - y0);
    }
    
    @Test public void testSortCCWFifty() {
        testSortCCW(50, 1958535028L);
    }
    
    @Test public void testSortCCWThousand() {
        testSortCCW(1000, 131492039L);
    }
    
    @Test public void testSortCCWTenThousand() {
        testSortCCW(10_000, 2954539111L);
    }
    
    private void testSortCCW(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 3 + (i / 5);
            int[] xs = new int[nn], ys = new int[nn];
            boolean[][] taken = new boolean[nn][nn];
            int x, y;
            for(int j = 0; j < nn; j++) {
                do {
                    x = rng.nextInt(nn);
                    y = rng.nextInt(nn);
                } while(taken[x][y]);
                xs[j] = x - (nn/2); ys[j] = y - (nn/2); taken[x][y] = true;
            }
            int[] result = CompGeom.sortCCW(xs, ys);
            for(int j = 0; j < nn; j++) {
                check.update(result[j]);
            }
        }
        assertEquals(expected, check.getValue());
    }
    
    @Test public void testGrahamScanTen() {
        testGrahamScan(10, 3999836108L);
    }
    
    @Test public void testGrahamScanThousand() {
        testGrahamScan(1000, 364144433L);
    }
    
    private void testGrahamScan(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < n; i++) {
            int nn = 5 + (i / 5);
            int[] xs = new int[nn], ys = new int[nn];
            boolean[][] taken = new boolean[nn][nn];
            int x, y;
            for(int j = 0; j < nn; j++) {
                do {
                    x = rng.nextInt(nn);
                    y = rng.nextInt(nn);
                } while(taken[x][y]);
                xs[j] = x - (nn/2); ys[j] = y - (nn/2); taken[x][y] = true;
            }
            int[] result = CompGeom.grahamScan(xs, ys);
            nn = result.length;
            for(int j = 0; j < nn; j++) { 
                check.update(result[j]);
            }
            // Verify that every turn is left-handed around the convex hull.
            int prev = nn - 1, curr = 0, next = 1;
            do {
                int x0 = xs[result[prev]], y0 = ys[result[prev]];
                int x1 = xs[result[curr]], y1 = ys[result[curr]];
                int x2 = xs[result[next]], y2 = ys[result[next]];
                assertTrue(ccw(x0, y0, x1, y1, x2, y2) >= 0);
                prev = curr; curr = next; next = (next + 1) % nn;
            } while(curr != 0);
        }
        assertEquals(expected, check.getValue());
    }
    
    @Test public void testBoxFifty() {
        testBox(50);
    }
    
    private void testBox(int n) {
        Random rng = new Random(12345);
        List<Integer> indices = new ArrayList<>();
        for(int k = 0; k < n; k++) {
            int nn = k + 2;
            int[] xs = new int[nn*nn], ys = new int[nn*nn];
            indices.clear();
            int pos = 0;
            for(int i = 0; i < nn; i++) {
                for(int j = 0; j < nn; j++) {
                    xs[pos] = i;
                    ys[pos] = j;
                    indices.add(pos++);
                }
            }
            Collections.shuffle(indices, rng);
            int[] xxs = new int[nn*nn], yys = new int[nn*nn];
            for(int p = 0; p < nn*nn; p++) {
                xxs[p] = xs[indices.get(p)];
                yys[p] = ys[indices.get(p)];
            }
            int[] hull = CompGeom.grahamScan(xxs, yys);
            assertEquals(4, hull.length);
            int[][] expect = { {0, 0}, {nn-1, 0}, {nn-1, nn-1}, {0, nn-1} };
            for(int i = 0; i < 4; i++) {
                assertEquals(xxs[hull[i]], expect[i][0]);
                assertEquals(yys[hull[i]], expect[i][1]);
            }
        }
    }
    
    @Test public void verifyPicksTheorem() {
        Random rng = new Random(12345);
        for(int i = 0; i < 100; i++) {
            int n = 6 + (i / 5), m = 3 + 2 * i;
            int[] xs = new int[n], ys = new int[n];
            boolean[][] taken = new boolean[m][m];
            for(int j = 0; j < n; j++) {
                int x, y;
                do {
                    x = rng.nextInt(m); y = rng.nextInt(m);
                } while(taken[x][y]);
                xs[j] = x; ys[j] = y; taken[x][y] = true;
            }
            int[] hull = CompGeom.grahamScan(xs, ys);
            int nn = hull.length;
            int[] xxs = new int[nn]; int[] yys = new int[nn];
            for(int j = 0; j < nn; j++) {
                xxs[j] = xs[hull[j]]; yys[j] = ys[hull[j]];
            }
            int inside = 0, edges = 0;
            for(int x = 0; x < m; x++) {
                for(int y = 0; y < m; y++) {
                    int r = CompGeom.pointInPolygon(xxs, yys, x, y);
                    if(r == 1 || r == 2) { edges++; }
                    if(r == 3) { inside++;  }
                    int rr = CompGeom.pointInConvex(xxs, yys, x, y);
                    assertEquals(r, rr);
                }
            }
            int areaPick = 2 * inside + edges - 2;
            int areaShoe = CompGeom.shoelaceArea(xxs, yys);
            assertEquals(areaPick, areaShoe);
        }
    }
}
