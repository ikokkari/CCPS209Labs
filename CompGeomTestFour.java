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

    // --- sortCCW explicit tests ---

    @Test public void testSortCCWTriangle() {
        // Points: 0=(0,0), 1=(2,0), 2=(1,2). Ref=0 (lowest y, lowest x).
        // CCW from ref: 1 is rightward, 2 is up-left. So [0, 1, 2].
        int[] xs = {0, 2, 1}, ys = {0, 0, 2};
        int[] result = CompGeom.sortCCW(xs, ys);
        assertEquals(0, result[0]); // ref point first
        assertEquals(1, result[1]);
        assertEquals(2, result[2]);
    }

    @Test public void testSortCCWSquare() {
        // (0,0),(2,0),(2,2),(0,2). Ref=0. CCW: 1(right), 2(up-right), 3(up).
        int[] xs = {0, 2, 2, 0}, ys = {0, 0, 2, 2};
        int[] result = CompGeom.sortCCW(xs, ys);
        assertEquals(0, result[0]);
        assertEquals(1, result[1]);
        assertEquals(2, result[2]);
        assertEquals(3, result[3]);
    }

    @Test public void testSortCCWRefPointSelection() {
        // Points: 0=(3,0), 1=(0,0), 2=(1,1). Ref=1 (lowest y, then lowest x).
        int[] xs = {3, 0, 1}, ys = {0, 0, 1};
        int[] result = CompGeom.sortCCW(xs, ys);
        assertEquals(1, result[0]); // ref is index 1 (x=0, y=0)
    }

    @Test public void testSortCCWPermutation() {
        // Result must be a permutation of 0..n-1
        int[] xs = {0, 4, 4, 0, 2}, ys = {0, 0, 4, 4, 2};
        int[] result = CompGeom.sortCCW(xs, ys);
        assertEquals(5, result.length);
        boolean[] seen = new boolean[5];
        for (int idx : result) {
            assertTrue(idx >= 0 && idx < 5);
            seen[idx] = true;
        }
        for (boolean s : seen) { assertTrue(s); }
    }

    // --- grahamScan explicit tests ---

    @Test public void testGrahamScanTriangle() {
        // All 3 points are on the hull.
        int[] xs = {0, 3, 0}, ys = {0, 0, 3};
        int[] hull = CompGeom.grahamScan(xs, ys);
        assertEquals(3, hull.length);
        verifyConvexHull(xs, ys, hull);
    }

    @Test public void testGrahamScanSquare() {
        // All 4 corners on the hull.
        int[] xs = {0, 2, 2, 0}, ys = {0, 0, 2, 2};
        int[] hull = CompGeom.grahamScan(xs, ys);
        assertEquals(4, hull.length);
        verifyConvexHull(xs, ys, hull);
    }

    @Test public void testGrahamScanInteriorPointExcluded() {
        // Square with a point in the center: hull should have 4 points.
        int[] xs = {0, 4, 4, 0, 2}, ys = {0, 0, 4, 4, 2};
        int[] hull = CompGeom.grahamScan(xs, ys);
        assertEquals(4, hull.length);
        verifyConvexHull(xs, ys, hull);
        // The interior point (2,2) should not be on the hull
        for (int h : hull) {
            assertTrue(!(xs[h] == 2 && ys[h] == 2));
        }
    }

    @Test public void testGrahamScanManyInterior() {
        // 4 corners of a 5x5 square plus 6 interior points
        int[] xs = {0, 5, 5, 0, 1, 2, 3, 4, 2, 3};
        int[] ys = {0, 0, 5, 5, 1, 1, 1, 1, 3, 3};
        int[] hull = CompGeom.grahamScan(xs, ys);
        assertEquals(4, hull.length);
        verifyConvexHull(xs, ys, hull);
    }

    @Test public void testGrahamScanCollinearOnEdge() {
        // Points along bottom edge: collinear points should be eliminated by Graham scan.
        // (0,0),(1,0),(2,0),(3,0),(0,3),(3,3)
        int[] xs = {0, 1, 2, 3, 0, 3}, ys = {0, 0, 0, 0, 3, 3};
        int[] hull = CompGeom.grahamScan(xs, ys);
        // Hull should be 4 corners: (0,0),(3,0),(3,3),(0,3) — collinear points eliminated
        assertEquals(4, hull.length);
        verifyConvexHull(xs, ys, hull);
    }

    @Test public void testGrahamScanAllPointsOnHull() {
        // Pentagon: all points are hull vertices
        int[] xs = {2, 4, 3, 0, 1}, ys = {0, 1, 4, 3, 1};
        int[] hull = CompGeom.grahamScan(xs, ys);
        assertEquals(5, hull.length);
        verifyConvexHull(xs, ys, hull);
    }

    @Test public void testGrahamScanHullContainsAllPoints() {
        // Every original point should be inside or on the hull
        int[] xs = {0, 10, 10, 0, 3, 7, 5, 2, 8, 4};
        int[] ys = {0, 0, 10, 10, 2, 3, 5, 8, 7, 4};
        int[] hull = CompGeom.grahamScan(xs, ys);
        verifyConvexHull(xs, ys, hull);
        // Extract hull polygon
        int[] hxs = new int[hull.length], hys = new int[hull.length];
        for (int i = 0; i < hull.length; i++) {
            hxs[i] = xs[hull[i]]; hys[i] = ys[hull[i]];
        }
        // Every point should be inside or on the hull
        for (int i = 0; i < xs.length; i++) {
            int r = CompGeom.pointInConvex(hxs, hys, xs[i], ys[i]);
            assertTrue("Point (" + xs[i] + "," + ys[i] + ") not in hull, result=" + r,
                    r >= 1);
        }
    }

    // Helper: verify hull has all left-hand turns
    private void verifyConvexHull(int[] xs, int[] ys, int[] hull) {
        int nn = hull.length;
        for (int i = 0; i < nn; i++) {
            int prev = hull[(i - 1 + nn) % nn];
            int curr = hull[i];
            int next = hull[(i + 1) % nn];
            assertTrue("Right-hand turn in hull at index " + i,
                    ccw(xs[prev], ys[prev], xs[curr], ys[curr], xs[next], ys[next]) >= 0);
        }
    }

    // --- CRC mass tests ---

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
                    if(r == 3) { inside++; }
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