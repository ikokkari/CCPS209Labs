import static org.junit.Assert.assertEquals;
import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

// Just to get to use the most exotic of all the Java speficier keywords.

public strictfp class AccessCountArrayListTest {

    private static final int TRIALS = 10000;
    private static final int SEED = 87654;
    
    private void updateCheck(double x, CRC32 check) {
        long y = Double.doubleToRawLongBits(x);
        check.update((int)(y & 0x0000FFFF));
        check.update((int)(y >> 32));
    }
    
    @Test
    public void massTest() {
        AccessCountArrayList<Double> acad = new AccessCountArrayList<>();
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int idx;
        for(int i = 0; i < TRIALS; i++) {
            acad.clear();
            acad.resetCount();
            int len = rng.nextInt(1000) + 50;
            for(int j = 0; j < len; j++) {
                acad.add(rng.nextDouble());
            }
            for(int j = 0; j < len; j++) {
                if(rng.nextBoolean()) {
                    idx = rng.nextInt(len);
                    updateCheck(acad.set(idx, rng.nextDouble()), check);
                }
                if(rng.nextBoolean()) {
                    idx = rng.nextInt(len);
                    updateCheck(acad.get(idx), check);
                }
                check.update(acad.getAccessCount());
            }
        }
        assertEquals(3163640888L, check.getValue());
    }  
}
