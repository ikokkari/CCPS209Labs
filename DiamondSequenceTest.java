import org.junit.Test;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class DiamondSequenceTest {
    
    @Test
    public void knownInitialPrefix() {
        Iterator<Integer> it = new DiamondSequence();
        assertEquals(Integer.valueOf(1), it.next());
        assertEquals(Integer.valueOf(3), it.next());
        assertEquals(Integer.valueOf(2), it.next());
        assertEquals(Integer.valueOf(6), it.next());
        assertEquals(Integer.valueOf(8), it.next());
        assertEquals(Integer.valueOf(4), it.next());
        assertEquals(Integer.valueOf(11), it.next());
        assertEquals(Integer.valueOf(5), it.next());
        assertEquals(Integer.valueOf(14), it.next());
        assertEquals(Integer.valueOf(16), it.next());
        assertEquals(Integer.valueOf(7), it.next());
        assertEquals(Integer.valueOf(19), it.next());
        assertEquals(Integer.valueOf(21), it.next());
        assertEquals(Integer.valueOf(9), it.next());
        assertEquals(Integer.valueOf(24), it.next());
    }
    
    @Test
    public void testSelfReferentiality() {
        Map<Integer, Integer> mustBe = new HashMap<>();
        Iterator<Integer> it = new DiamondSequence();
        for(int k = 1; k < 1_000_000; k++) {
            int v = it.next();
            if(mustBe.containsKey(k)) {
                // Cast to long is necessary to disambiguate between assertEquals overloadings.
                assertEquals(v, (long)mustBe.get(k));
                mustBe.remove(k);
            }
            if(v > k) {
                mustBe.put(v, k);
            }
        }
    }
    
    @Test
    public void firstHundredMillion() {
        int count = 100_000_000;
        CRC32 check = new CRC32();
        Iterator<Integer> it = new DiamondSequence();
        while(--count > 0) {
            int i = it.next();
            if(count % 1000 == 0) { check.update(i); }
        }
        assertEquals(2546749209L, check.getValue());
    }
}
