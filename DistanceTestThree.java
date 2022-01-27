import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DistanceTestThree {

    private static final int SEED = 12345;
    private static final int PREC = 30;
    private static final Distance ZERO = new Distance(0, 1);
    
    @Test public void testApproximate() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        MathContext mc2 = new MathContext(PREC + 2, RoundingMode.HALF_UP);
        BigDecimal epsilon = new BigDecimal(1).scaleByPowerOfTen(PREC);
        for(int i = 1; i < 100; i++) {
            Distance d = ZERO;
            BigDecimal prev = new BigDecimal(0, mc2);
            for(int j = 0; j*j < i; j++) {
                int whole = rng.nextInt(i + 3) + 1;
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 2;
                Distance dd = new Distance(whole, base);
                d = d.add(dd);
                
                BigDecimal curr = d.approximate(mc2);
                try {
                    check.update(curr.toString().getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
                BigDecimal diff = curr.subtract(prev);
                assertTrue(diff.abs().compareTo(epsilon) < 0);
                prev = curr;
            }
        }
        assertEquals(4064272570L, check.getValue());
    }  
    
    @Test public void massTestCollections() {
        int N = 1000;
        Random rng = new Random(SEED);
        // Two collections of different general types that are supposed to contain
        // the exact same distances at all times.
        HashSet<Distance> hs = new HashSet<>();
        TreeSet<Distance> ts = new TreeSet<>();
        
        for(int i = 0; i < N; i++) {
            Distance d = new Distance(0, 1);
            for(int k = 0; k <= i % 20; k++) {
                int whole = rng.nextInt(i + 3);
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 1;
                d = d.add(new Distance(whole, base));
            }            
            // The distance object is either member or nonmember of both sets.
            assertEquals(hs.contains(d), ts.contains(d));
            // Add it to both.
            hs.add(d);
            ts.add(d);
            // Both collections must have the same size. If the sizes are not
            // equal, the direction of failure provides a hint about which of
            // the methods equals, compareTo and hashCode contains a bug.
            assertFalse(hs.size() < ts.size());
            assertFalse(hs.size() > ts.size());
        }
        // Both sets must now contain the same objects.
        for(Distance d: hs) { 
            assertTrue(ts.contains(d));
        }
        for(Distance d: ts) {
            assertTrue(hs.contains(d));
        }
    }
    
    @Test public void testCompareTo() {

        int[][][] testCases = {
                // First two taken from a comment in https://news.ycombinator.com/item?id=30057582
                // These two distances agree up to their 20th decimal place.
                {{1000000, 1}, {1000018, 1}, {1000036, 1}, {1000059, 1}, {1000083, 1}}, // 0
                {{1000003, 1}, {1000011, 1}, {1000048, 1}, {1000050, 1}, {1000084, 1}}, // 1
                // Slightly smaller versions of the first two distances.
                {{1000000, 1}, {1000018, 1}, {1000036, 1}, {1000059, 1}, {1000081, 1}}, // 2
                {{1000003, 1}, {1000011, 1}, {1000046, 1}, {1000050, 1}, {1000084, 1}}, // 3
                // Slightly larger versions of the first two distances.
                {{1000000, 1}, {1000018, 1}, {1000036, 1}, {1000060, 1}, {1000083, 1}}, // 4
                {{1000004, 1}, {1000011, 1}, {1000048, 1}, {1000052, 1}, {1000084, 1}}  // 5
        };

        ArrayList<Distance> distances = new ArrayList<>();
        for(int[][] testCase: testCases) {
            TreeMap<Integer, Integer> coeff = new TreeMap<>();
            for(int[] co: testCase) {
                coeff.put(co[0], co[1]);
            }
            distances.add(new Distance(coeff));
        }

        assertTrue(distances.get(0).compareTo(distances.get(1)) > 0);
        assertTrue(distances.get(1).compareTo(distances.get(2)) > 0);
        assertTrue(distances.get(2).compareTo(distances.get(3)) > 0);
        assertTrue(distances.get(1).compareTo(distances.get(0)) < 0);
        assertTrue(distances.get(2).compareTo(distances.get(1)) < 0);
        assertTrue(distances.get(3).compareTo(distances.get(2)) < 0);
        assertTrue(distances.get(4).compareTo(distances.get(0)) > 0);
        assertTrue(distances.get(5).compareTo(distances.get(1)) > 0);
        assertTrue(distances.get(5).compareTo(distances.get(4)) > 0);

        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 40;
        Distance[] ds = new Distance[3 * N];
        for(int i = 0; i < ds.length; i++) {
            // First N distances are just randomly created singleton terms.
            if(i < N) {
                int whole = rng.nextInt(i + 3);
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 1;
                ds[i] = new Distance(whole, base);
            }
            // The rest are created by adding previously created distances.
            else {
                int j1 = rng.nextInt(i);
                int j2 = rng.nextInt(i);
                ds[i] = ds[j1].add(ds[j2]);
            }
        }
        // Pairwise comparison of all distances created so far.
        for(int i = 0; i < 3 * N; i++) {
            for(int j = i + 1; j < 3 * N; j++) {
                int comp = ds[i].compareTo(ds[j]);
                comp = Integer.compare(comp, 0); // convert result to -1, 0, +1
                check.update(comp);
            }
        }
        assertEquals(1484089080L, check.getValue());
    }
}