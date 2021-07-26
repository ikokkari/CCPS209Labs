import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class DistanceTestTwo {

    public static final Distance ZERO = new Distance(0, 1);
    private static final int SEED = 123456;

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
                check.update(ds[i].toString().getBytes());
            }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testMultiply() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int N = 100;
        Distance[] ds = new Distance[3 * N];
        for(int i = 0; i < 3 * N; i++) {
            if(i < N) { // For first N, just create some random distances.
                int whole = rng.nextInt(i + 3);
                if(rng.nextBoolean()) { whole = -whole; }
                int base = rng.nextInt(10 * (i + 3)) + 1;
                ds[i] = new Distance(whole, base);
            }
            else if(i < 2 * N) { // Create some more complex distances by addition.
                int j1 = rng.nextInt(i);
                int j2 = rng.nextInt(i);
                ds[i] = ds[j1].add(ds[j2]);
            }
            else { // For last third, multiply random distances created in second part.
                int j1 = rng.nextInt(N) + N;
                int j2 = rng.nextInt(N) + N;
                ds[i] = ds[j1].multiply(ds[j2]);
            }
            check.update(ds[i].toString().getBytes());
        }
        assertEquals(2108081313L, check.getValue());
    }  
}