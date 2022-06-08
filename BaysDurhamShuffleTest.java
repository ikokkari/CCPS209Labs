import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class BaysDurhamShuffleTest {

    private static final int SEED = 12345;

    private static int[] EXPECTED = {
            0, 4, 1, 6, 5, 2, 9, 8, 10, 7, 13, 14, 12, 11, 16, 15, 17, 18, 20, 21, 22, 19, 25, 26,
            23, 28, 27, 24, 31, 30, 32, 29, 35, 36, 34, 33, 38, 37, 39, 40, 42, 43, 44, 41, 47, 48,
            45, 50, 49, 46, 53, 52, 54, 51, 57, 58, 56, 55, 60, 59, 61, 62, 64, 65, 66, 63, 69, 70,
            67, 72, 71, 68, 75, 74, 76, 73, 79, 80, 78, 77, 82, 81, 83, 84, 86, 87, 88, 85, 91, 92,
            89, 94, 93, 90, 97, 96, 98, 95, 101, 102
    };

    @Test public void testBaysDurhamShuffleToNaturals() {
        class Naturals extends Random {
            private int i = 0;
            @Override public int nextInt() { return i++; }
        }
        BaysDurhamShuffle bdrng = new BaysDurhamShuffle(new Naturals(), 3);
        for(int i = 0; i < EXPECTED.length; i++) {
            assertEquals(EXPECTED[i], bdrng.nextInt());
        }
    }

    @Test public void testBaysDurhamShuffleThousand() {
        testBaysDurhamShuffle(new Random(SEED), 1000, 10, 1936206508L);
    }

    @Test public void testBaysDurhamShuffleMillion() {
        testBaysDurhamShuffle(new Random(SEED), 1_000_000, 100, 3208359086L);
    }

    @Test public void testBaysDurhamShuffleHundredMillion() {
        testBaysDurhamShuffle(new Random(SEED), 100_000_000, 1000, 2788232226L);
    }

    private void testBaysDurhamShuffle(Random source, int n, int k, long expected) {
        Random bdrng = new BaysDurhamShuffle(source, k);
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
            int v = bdrng.nextInt();
            check.update(v);
        }
        assertEquals(expected, check.getValue());
    }
}
