import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.util.*;
import java.util.zip.CRC32;

public strictfp class AccessCountArrayListTest {

    private static int TRIALS = 10000;
    private static int SEED = 87654;
    
    private void updateCheck(double x, CRC32 check) {
        long y = Double.doubleToRawLongBits(x);
        check.update((int)(y & 0x0000FFFF));
        check.update((int)(y >> 32));
    }
    
    @Test
    public void massTest() {
        AccessCountArrayList<Double> acad = new AccessCountArrayList<Double>();
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
