import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.HashSet;
import java.util.zip.CRC32;

public class SigVectorTest {

    private static final int N = 500;
    private static final int SEED = 12345;
    private static final long[] keys = new long[N];

    static {
        Random rng = new Random(SEED);
        for(int i = 0; i < N; i++) {
            keys[i] = rng.nextLong() & 0x7FFFFFFFFFFFFFFFL;
        }
    }

    // --- Constructor and initial state ---

    @Test public void testInitialState() {
        SigVector sv = new SigVector(10, keys);
        // All bits initially false
        for (int i = 0; i < 10; i++) {
            assertFalse(sv.get(i));
        }
        // Initial signature is 0
        assertEquals(0L, sv.getSignature());
    }

    // --- set and get ---

    @Test public void testSetAndGet() {
        SigVector sv = new SigVector(10, keys);
        // Set bit 0 to true
        sv.set(0, true);
        assertTrue(sv.get(0));
        // Set bit 0 back to false
        sv.set(0, false);
        assertFalse(sv.get(0));
    }

    @Test public void testSetReturnsSig() {
        SigVector sv = new SigVector(10, keys);
        // set returns the signature after the operation
        long sig = sv.set(0, true);
        assertEquals(sv.getSignature(), sig);
        assertEquals(keys[0], sig);
    }

    @Test public void testSetSameValueNoChange() {
        SigVector sv = new SigVector(10, keys);
        sv.set(3, true);
        long sig1 = sv.getSignature();
        // Setting the same value again should not change the signature
        long sig2 = sv.set(3, true);
        assertEquals(sig1, sig2);
    }

    // --- Signature XOR properties ---

    @Test public void testSigFlipTwiceReturnsToZero() {
        SigVector sv = new SigVector(10, keys);
        sv.set(5, true);
        assertNotEquals(0L, sv.getSignature());
        sv.set(5, false);
        assertEquals(0L, sv.getSignature());
    }

    @Test public void testSigOrderIndependent() {
        // Setting bits 0,1,2 in any order should give the same signature
        SigVector sv1 = new SigVector(10, keys);
        sv1.set(0, true); sv1.set(1, true); sv1.set(2, true);

        SigVector sv2 = new SigVector(10, keys);
        sv2.set(2, true); sv2.set(0, true); sv2.set(1, true);

        assertEquals(sv1.getSignature(), sv2.getSignature());
    }

    @Test public void testSigSetAllThenUnsetAll() {
        // Setting all bits on then all off should return sig to 0
        SigVector sv = new SigVector(10, keys);
        for (int i = 0; i < 10; i++) { sv.set(i, true); }
        assertNotEquals(0L, sv.getSignature());
        for (int i = 0; i < 10; i++) { sv.set(i, false); }
        assertEquals(0L, sv.getSignature());
    }

    @Test public void testSigXorOfActiveKeys() {
        // Signature should be XOR of keys for all true bits
        SigVector sv = new SigVector(10, keys);
        sv.set(1, true);
        sv.set(4, true);
        sv.set(7, true);
        assertEquals(keys[1] ^ keys[4] ^ keys[7], sv.getSignature());
    }

    // --- Exceptions ---

    @Test public void testExceptions() {
        try {
            SigVector sv = new SigVector(N, keys);
            sv.set(-42, true);
            fail();
        } catch(IllegalArgumentException ignored) {}
        try {
            SigVector sv = new SigVector(N, keys);
            sv.set(N, false);
            fail();
        } catch(IllegalArgumentException ignored) {}
        try {
            SigVector sv = new SigVector(N, keys);
            sv.set(2, true);
            sv.get(N);
            fail();
        } catch(IllegalArgumentException ignored) {}
    }

    @Test public void testGetExceptionNegative() {
        try {
            SigVector sv = new SigVector(10, keys);
            sv.get(-1);
            fail();
        } catch(IllegalArgumentException ignored) {}
    }

    // --- clone ---

    @Test public void testCloneEquality() {
        SigVector sv = new SigVector(10, keys);
        sv.set(0, true);
        sv.set(5, true);
        SigVector clone = (SigVector) sv.clone();
        assertEquals(sv, clone);
        assertEquals(sv.getSignature(), clone.getSignature());
    }

    @Test public void testCloneIndependence() {
        SigVector sv = new SigVector(10, keys);
        sv.set(0, true);
        sv.set(5, true);
        SigVector clone = (SigVector) sv.clone();
        // Modify clone: original should be unaffected
        clone.set(3, true);
        assertFalse(sv.get(3)); // original unchanged
        assertNotEquals(sv.getSignature(), clone.getSignature());
        // Modify original: clone should be unaffected
        sv.set(7, true);
        assertFalse(clone.get(7));
    }

    @Test public void testCloneOfFreshVector() {
        SigVector sv = new SigVector(10, keys);
        SigVector clone = (SigVector) sv.clone();
        assertEquals(sv, clone);
        assertEquals(0L, clone.getSignature());
    }

    // --- equals and hashCode ---

    @Test public void testEqualsSameContent() {
        SigVector sv1 = new SigVector(10, keys);
        SigVector sv2 = new SigVector(10, keys);
        sv1.set(2, true); sv1.set(4, true);
        sv2.set(2, true); sv2.set(4, true);
        assertEquals(sv1, sv2);
        assertEquals(sv1.hashCode(), sv2.hashCode());
    }

    @Test public void testEqualsDifferentContent() {
        SigVector sv1 = new SigVector(10, keys);
        SigVector sv2 = new SigVector(10, keys);
        sv1.set(2, true);
        sv2.set(3, true);
        assertNotEquals(sv1, sv2);
    }

    @Test public void testEqualsNotSigVector() {
        SigVector sv = new SigVector(10, keys);
        assertNotEquals(sv, "not a SigVector");
        assertNotEquals(sv, null);
    }

    @Test public void testEqualsReflexive() {
        SigVector sv = new SigVector(10, keys);
        sv.set(1, true);
        assertEquals(sv, sv);
    }

    @Test public void testHashCodeConsistentWithEquals() {
        // Equal objects must have equal hash codes
        SigVector sv1 = new SigVector(10, keys);
        sv1.set(0, true); sv1.set(3, true); sv1.set(9, true);
        SigVector sv2 = (SigVector) sv1.clone();
        assertEquals(sv1, sv2);
        assertEquals(sv1.hashCode(), sv2.hashCode());
    }

    @Test public void testHashSetOperations() {
        // SigVectors should work correctly in a HashSet
        HashSet<SigVector> set = new HashSet<>();
        SigVector sv1 = new SigVector(10, keys);
        sv1.set(0, true);
        set.add(sv1);

        SigVector sv2 = new SigVector(10, keys);
        sv2.set(0, true);
        // sv2 should be found as equal to sv1
        assertTrue(set.contains(sv2));

        SigVector sv3 = new SigVector(10, keys);
        sv3.set(1, true);
        assertFalse(set.contains(sv3));
    }

    // --- CRC mass tests ---

    @Test public void testWithStackThousand() {
        testWithStack(1000, 1627073994L);
    }

    @Test public void testWithStackMillion() {
        testWithStack(1000000, 2229364248L);
    }

    private void testWithStack(int n, long expected) {
        Random rng = new Random(SEED);
        long[] sigs = new long[n];
        int[] indices = new int[n];
        boolean[] state = new boolean[N];
        SigVector sv = new SigVector(N, keys);
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
            int index = rng.nextInt(N);
            assertEquals(state[index], sv.get(index));
            state[index] = !state[index];
            sigs[i] = sv.set(index, state[index]);
            check.update((int)sv.getSignature());
            indices[i] = index;
            assertEquals(state[index], sv.get(index));
        }
        assertEquals(expected, check.getValue());
        for(int i = n - 1; i >= 0; i--) {
            int index = indices[i];
            assertEquals(state[index], sv.get(index));
            state[index] = !state[index];
            assertEquals(i > 0 ? sigs[i-1] : 0, sv.set(index, state[index]));
            assertEquals(state[index], sv.get(index));
        }
    }

    @Test public void testEqualsTenThousand() {
        testEquals(10000, 484159150L);
    }

    @Test public void testEqualsHundred() {
        testEquals(100, 2444746199L);
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