import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.Map;
import static org.junit.Assert.assertEquals;

public class DistanceTestTwo {

    public static final Distance ZERO = new Distance(0, 1);
    private static final int SEED = 123456;

    @Test public void testArithmeticExplicit() {

        Map<Integer, Integer> coeff1 = Map.of(5, 2, 10, 3);
        Distance d1 = new Distance(coeff1); // 2Sqrt[5] + 3Sqrt[10]

        Map<Integer, Integer> coeff2 = Map.of(3, -1, 7, 2, 10, -3);
        Distance d2 = new Distance(coeff2); // -Sqrt[3] + 2Sqrt[7] - 3Sqrt[10]

        Distance d3 = d1.add(d2);
        assertEquals("-Sqrt[3] + 2Sqrt[5] + 2Sqrt[7]", d3.toString());

        Distance d4 = d1.subtract(d2);
        assertEquals("Sqrt[3] + 2Sqrt[5] - 2Sqrt[7] + 6Sqrt[10]", d4.toString());

        // Addition should be commutative.
        assertEquals(d1.add(d2).toString(), d2.add(d1).toString());
        assertEquals(d1.add(d3).toString(), d3.add(d1).toString());
        assertEquals(d1.add(d4).toString(), d4.add(d1).toString());
        assertEquals(d2.add(d3).toString(), d3.add(d2).toString());
        assertEquals(d2.add(d4).toString(), d4.add(d2).toString());
        assertEquals(d3.add(d4).toString(), d4.add(d3).toString());
    }

    @Test public void testMultiplyExplicit() {
        Map<Integer, Integer> coeff1 = Map.of(5, 2, 10, 3);
        Distance d1 = new Distance(coeff1); // 2Sqrt[5] + 3Sqrt[10]

        Map<Integer, Integer> coeff2 = Map.of(3, -1, 7, 2, 10, -3);
        Distance d2 = new Distance(coeff2); // -Sqrt[3] + 2Sqrt[7] - 3Sqrt[10]

        Distance d3 = d1.multiply(d2);
        assertEquals("-90 - 30Sqrt[2] - 2Sqrt[15] - 3Sqrt[30] + 4Sqrt[35] + 6Sqrt[70]", d3.toString());

        Distance d4 = d1.multiply(d1);
        assertEquals("110 + 60Sqrt[2]", d4.toString());

        Map<Integer, Integer> coeff5 = Map.of(20, -1, 70, 2);
        Distance d5 = new Distance(coeff5); // -2Sqrt[5] + 2Sqrt[70]

        Distance d6 = d1.multiply(d5);
        assertEquals("-20 - 30Sqrt[2] + 60Sqrt[7] + 20Sqrt[14]", d6.toString());

        Map<Integer, Integer> coeff7 = Map.of(3, 2, 2, 3);
        Distance d7 = new Distance(coeff7); // 2Sqrt[3] + 3Sqrt[2]

        Map<Integer, Integer> coeff8 = Map.of(3, -2, 2, 3);
        Distance d8 = new Distance(coeff8); // -2Sqrt[3] + 3Sqrt[2]

        Distance d9 = d7.multiply(d8); // Some nice cancellation of roots going on here
        assertEquals("6", d9.toString());

        Map<Integer, Integer> coeff10 = Map.of(30, -1, 10, 1, 5, -1, 15, -1);
        Distance d10 = new Distance(coeff10);

        Map<Integer, Integer> coeff11 = Map.of(30, 1, 10, 1, 5, -1, 15, 1);
        Distance d11 = new Distance(coeff11);

        Distance d12 = d10.multiply(d11); // As is also here
        assertEquals("-30 - 40Sqrt[2]", d12.toString());

        // Multiplication should also be commutative.
        assertEquals(d1.multiply(d2).toString(), d2.multiply(d1).toString());
        assertEquals(d1.multiply(d3).toString(), d3.multiply(d1).toString());
        assertEquals(d1.multiply(d4).toString(), d4.multiply(d1).toString());
        assertEquals(d1.multiply(d5).toString(), d5.multiply(d1).toString());
        assertEquals(d2.multiply(d3).toString(), d3.multiply(d2).toString());
        assertEquals(d2.multiply(d4).toString(), d4.multiply(d2).toString());
        assertEquals(d2.multiply(d5).toString(), d5.multiply(d2).toString());
        assertEquals(d2.multiply(d6).toString(), d6.multiply(d2).toString());
        assertEquals(d5.multiply(d8).toString(), d8.multiply(d5).toString());
        assertEquals(d3.multiply(d9).toString(), d9.multiply(d3).toString());
    }

    @Test public void testAdd() {
        testArithmetic(true, 2784019965L);
    }
    
    @Test public void testSubtract() {
        testArithmetic(false, 1739788852L);
    }
    
    private void testArithmetic(boolean add, long expected) {
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 10_000;
        Distance[] ds = new Distance[3 * N];
        for(int i = 0; i < ds.length; i++) {
            if(i < N) { // First N distances are just randomly created.
                int whole = rng.nextInt(i + 3);
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 1;
                ds[i] = new Distance(whole, base);
            }
            else {
                // Rest of the distances are created by adding or subtracting
                // previously created distances with each other.
                int j1 = rng.nextInt(i);
                int j2 = rng.nextInt(i);
                ds[i] = add ? ds[j1].add(ds[j2]) : ds[j1].subtract(ds[j2]);
                // Subtracting any distance from itself must give zero result.
                Distance sub = ds[i].subtract(ds[i]);
                assertEquals(ZERO.toString(), sub.toString());
                // Adding a distance and then subtracting it right away must
                // produce the original distance.
                String si = ds[i].toString();
                String sii = ds[i].add(ds[i-1]).subtract(ds[i-1]).toString();
                assertEquals(si, sii);
                try {
                    check.update(ds[i].toString().getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
            }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testMultiply() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 50;
        Distance[] ds = new Distance[20 * N];
        for(int i = 0; i < ds.length; i++) {
            if(i < N) { // For first N, just create some random distances.
                int whole = rng.nextInt(2 + i/5);
                int base = rng.nextInt(4 * (i+1)) + 1;
                ds[i] = new Distance(whole, base);
                ds[i+1] = new Distance(-whole, base);
                i++;
            }
            else if(i < 10 * N) { // Create some more complex distances by addition.
                int j1 = rng.nextInt(i);
                int j2 = rng.nextInt(i);
                ds[i] = ds[j1].add(ds[j2]);
            }
            else { // Multiply random distances created in second part.
                int j1 = rng.nextInt(N) + N;
                int j2 = rng.nextInt(N) + N;
                ds[i] = ds[j1].multiply(ds[j2]);
            }
            try {
                check.update(ds[i].toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(4293496691L, check.getValue());
    }  
}