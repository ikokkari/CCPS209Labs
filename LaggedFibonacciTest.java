import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class LaggedFibonacciTest {

    @Test public void testLaggedFibonacciNaturals() {
        int[] Y = new int[10];
        for(int i = 0; i < 10; i++) { Y[i] = i; }
        int[] expectedValues = {
                15, 13, 11, 9, 7, 5, 18, 15, 12, 24, 20, 16, 27, 22, 17, 42, 35, 28, 51, 42, 33, 69, 57, 45,
                93, 77, 61, 120, 99, 78, 162, 134, 106, 213, 176, 139, 282, 233, 184, 375, 310, 245, 495, 409,
                323, 657, 543, 429, 870, 719, 568, 1152, 952, 752, 1527, 1262, 997, 2022, 1671, 1320, 2679, 2214,
                1749, 3549, 2933, 2317, 4701, 3885, 3069, 6228, 5147, 4066, 8250, 6818, 5386, 10929, 9032, 7135,
                14478, 11965, 9452, 19179, 15850, 12521, 25407, 20997, 16587, 33657, 27815, 21973, 44586, 36847,
                29108, 59064, 48812, 38560, 78243, 64662, 51081, 103650
        };
        testLaggedFibonacci(10, 7, 100, Y, 1046646200L, expectedValues);
    }

    private int[] createInitialY(int n, Random rng) {
        int[] Y = new int[n];
        for(int i = 0; i < n; i++) {
            Y[i] = rng.nextInt();
        }
        return Y;
    }

    private static final int SEED = 12345;

    @Test public void testLaggedFibonacciThousand() {
        Random rng = new Random(SEED);
        int[] Y = createInitialY(55, rng);
        testLaggedFibonacci(55, 24, 1000, Y,3801969616L, null);
    }

    @Test public void testLaggedFibonacciHundredThousand() {
        Random rng = new Random(SEED);
        int[] Y = createInitialY(159, rng);
        testLaggedFibonacci(159, 128, 100_000, Y,799741107L, null);
    }

    @Test public void testLaggedFibonacciHundredMillion() {
        Random rng = new Random(SEED);
        int[] Y = createInitialY(1279, rng);
        testLaggedFibonacci(1279, 418, 100_000_000, Y,2269526286L, null);
    }

    private void testLaggedFibonacci(int k, int j, int n, int[] Y, long expectedCheck, int[] expectedValues) {
        CRC32 check = new CRC32();
        LaggedFibonacci lfrng = new LaggedFibonacci(Y, k, j);
        Y[0] = 4242;
        for(int i = 0; i < n; i++) {
            int v = lfrng.nextInt();
            if(expectedValues != null) { assertEquals(expectedValues[i], v); }
            check.update(v);
        }
        // If this assert fails, your LaggedFibonacci didn't create a defensive copy of Y.
        assertEquals(4242, Y[0]);
        // Verify that the produced pseudorandom values are as expected.
        assertEquals(expectedCheck, check.getValue());
    }
}