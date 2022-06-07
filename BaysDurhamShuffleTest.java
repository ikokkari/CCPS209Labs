import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class BaysDurhamShuffleTest {

    private static final int SEED = 12345;

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
