import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.HashSet;
import java.util.zip.CRC32;

public class SigVectorTest {
    
    private static final int N = 42;
    private static final int SEED = 12345;
    private static final long[] keys = new long[N];
    // static block is executed once at class initialization
    static {
        Random rng = new Random(SEED);
        for(int i = 0; i < N; i++) {
            keys[i] = rng.nextLong() & 0x7FFFFFFFFFFFFFFFL;
        }
    }
    
    @Test public void testWithStackThousand() {
        testWithStack(1000);
    }
    
    @Test public void testWithStackMillion() {
        testWithStack(1000000);
    }
    
    private void testWithStack(int n) {
        Random rng = new Random(SEED);        
        long[] sigs = new long[n];
        int[] indices = new int[n];
        boolean[] state = new boolean[N];
        SigVector sv = new SigVector(N, keys);
        
        for(int i = 0; i < n; i++) {
            int index = rng.nextInt(N);
            assertEquals(state[index], sv.get(index));
            state[index] = !state[index];
            sigs[i] = sv.set(index, state[index]);
            indices[i] = index;
            assertEquals(state[index], sv.get(index));
        }
        for(int i = n - 1; i >= 0; i--) {
            int index = indices[i];
            assertEquals(state[index], sv.get(index));
            state[index] = !state[index]; 
            assertEquals(i > 0 ? sigs[i-1] : 0, sv.set(index, state[index]));
            assertEquals(state[index], sv.get(index));
        }
    }
    
    @Test public void testEqualsHundredThousand() {
        testEquals(100000, 350196673L);
    }
    
    @Test public void testEqualsTenThousand() {
        testEquals(10000, 4189266982L);
    }
    
    @Test public void testEqualsHundred() {
        testEquals(100, 8143682L);
    }
    
    private void testEquals(int n, long expected) {
        final int WINDOW = 20;
        Random rng = new Random(SEED);
        SigVector[] svs = new SigVector[n];
        int[] prev = new int[n];
        int[] bit = new int[n];
        HashSet<SigVector> vectors = new HashSet<>();
        svs[0] = new SigVector(N, keys);
        vectors.add(svs[0]);
        for(int i = 1; i < n; i++) {
            int j = i < WINDOW + 1 ? rng.nextInt(i): i - 1 - rng.nextInt(WINDOW);
            prev[i] = j;
            SigVector current = (SigVector)svs[j].clone();
            svs[i] = current;
            assertEquals(svs[j], current);
            SigVector current2 = (SigVector)current.clone();
            while(prev[j] > 0 && rng.nextBoolean()) {
                current2.set(bit[j], !current2.get(bit[j]));
                j = prev[j];
                assertEquals(svs[j], current2);
            }
            bit[i] = rng.nextInt(10 + i % (N - 10));
            if(bit[i] == bit[prev[i]]) {
                bit[i] = (bit[i] + 1) % N;
            }
            current.set(bit[i], !current.get(bit[i]));
            vectors.add(current);
        }
        // System.out.println(vectors.size());
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
            assertTrue(vectors.contains(svs[i]));
            SigVector flip = (SigVector)svs[i].clone(); 
            for(int j = 0; j < N; j++) {
                flip.set(j, !flip.get(j));
                if(vectors.contains(flip)) {
                    check.update(i); check.update(j);
                }
                flip.set(j, !flip.get(j));
            }
        }
        assertEquals(expected, check.getValue());
    }
}


