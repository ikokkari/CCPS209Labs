import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class BitwiseStuffTest {

    // --- countClusters explicit tests ---

    @Test public void testCountClustersZero() {
        assertEquals(0, BitwiseStuff.countClusters(0L));
    }

    @Test public void testCountClustersAllOnes() {
        // -1L in two's complement is all 64 bits on: one big cluster
        assertEquals(1, BitwiseStuff.countClusters(-1L));
    }

    @Test public void testCountClustersSingleBits() {
        // Single bit at position 0
        assertEquals(1, BitwiseStuff.countClusters(1L));
        // Single bit at position 63 (sign bit in signed interpretation)
        assertEquals(1, BitwiseStuff.countClusters(Long.MIN_VALUE));
        // Single bit in the middle
        assertEquals(1, BitwiseStuff.countClusters(1L << 31));
    }

    @Test public void testCountClustersContiguous() {
        // 0b111 = 7: one cluster of three
        assertEquals(1, BitwiseStuff.countClusters(7L));
        // 0xFF: one cluster of eight
        assertEquals(1, BitwiseStuff.countClusters(0xFFL));
    }

    @Test public void testCountClustersAlternating() {
        // 0b10101010: four separate singleton clusters
        assertEquals(4, BitwiseStuff.countClusters(0b10101010L));
        // 0x5555555555555555L: alternating 01 pattern = 32 clusters
        assertEquals(32, BitwiseStuff.countClusters(0x5555555555555555L));
        // 0xAAAAAAAAAAAAAAAAL: alternating 10 pattern = 32 clusters
        assertEquals(32, BitwiseStuff.countClusters(0xAAAAAAAAAAAAAAAAL));
    }

    @Test public void testCountClustersSpecExample() {
        // 0b100111011100011100000000011000010000011000000011111111110
        assertEquals(8, BitwiseStuff.countClusters(
                0b100111011100011100000000011000010000011000000011111111110L));
    }

    @Test public void testCountClustersPairsPattern() {
        // 0b11001100: two clusters of two
        assertEquals(2, BitwiseStuff.countClusters(0b11001100L));
        // 0b101: two singleton clusters
        assertEquals(2, BitwiseStuff.countClusters(0b101L));
    }

    @Test public void testCountClustersTopAndBottom() {
        // Clusters at the very top and bottom nybbles, zeros in between
        assertEquals(2, BitwiseStuff.countClusters(0xF00000000000000FL));
    }

    // --- reverseNybbles explicit tests ---

    @Test public void testReverseNybblesZero() {
        assertEquals(0L, BitwiseStuff.reverseNybbles(0L));
    }

    @Test public void testReverseNybblesAllOnes() {
        assertEquals(-1L, BitwiseStuff.reverseNybbles(-1L));
    }

    @Test public void testReverseNybblesSpecExample() {
        assertEquals(Long.parseUnsignedLong("6ccfa5aeec128e36", 16),
                BitwiseStuff.reverseNybbles(0x63e821ceea5afcc6L));
    }

    @Test public void testReverseNybblesSingleNybble() {
        // 0x000000000000000A -> 0xA000000000000000
        assertEquals(Long.parseUnsignedLong("A000000000000000", 16),
                BitwiseStuff.reverseNybbles(0xAL));
        // And vice versa
        assertEquals(0xAL,
                BitwiseStuff.reverseNybbles(Long.parseUnsignedLong("A000000000000000", 16)));
    }

    @Test public void testReverseNybblesSequential() {
        // 0x0123456789ABCDEF -> 0xFEDCBA9876543210
        assertEquals(Long.parseUnsignedLong("FEDCBA9876543210", 16),
                BitwiseStuff.reverseNybbles(0x0123456789ABCDEFL));
    }

    @Test public void testReverseNybblesHighBit() {
        // 0x8000000000000000 -> 0x0000000000000008
        assertEquals(8L, BitwiseStuff.reverseNybbles(Long.MIN_VALUE));
    }

    @Test public void testReverseNybblesIsInvolution() {
        // Reversing twice gives back the original
        long[] vals = {0L, -1L, 0x63e821ceea5afcc6L, 0x0123456789ABCDEFL,
                Long.MIN_VALUE, 0xAL, 42L, 0xDEADBEEFL};
        for (long v : vals) {
            assertEquals("reverseNybbles is not an involution for " + Long.toHexString(v),
                    v, BitwiseStuff.reverseNybbles(BitwiseStuff.reverseNybbles(v)));
        }
    }

    // --- dosido explicit tests ---

    @Test public void testDosidoZero() {
        assertEquals(0L, BitwiseStuff.dosido(0L));
    }

    @Test public void testDosidoAllOnes() {
        // Swapping 1s with 1s: no change
        assertEquals(-1L, BitwiseStuff.dosido(-1L));
    }

    @Test public void testDosidoSpecExample() {
        assertEquals(Long.parseUnsignedLong("40b8e724c33675f7", 16),
                BitwiseStuff.dosido(Long.parseUnsignedLong("8074db18c339bafb", 16)));
    }

    @Test public void testDosidoSingleBits() {
        // Bit 0 swaps with bit 1
        assertEquals(2L, BitwiseStuff.dosido(1L));
        assertEquals(1L, BitwiseStuff.dosido(2L));
    }

    @Test public void testDosidoAlternatingBits() {
        // 0x5555...5 (bits 0,2,4,...) -> 0xAAAA...A (bits 1,3,5,...)
        assertEquals(Long.parseUnsignedLong("AAAAAAAAAAAAAAAA", 16),
                BitwiseStuff.dosido(0x5555555555555555L));
        assertEquals(0x5555555555555555L,
                BitwiseStuff.dosido(Long.parseUnsignedLong("AAAAAAAAAAAAAAAA", 16)));
    }

    @Test public void testDosidoPairPatterns() {
        // 0x3 = 0b0011: both bits in pair are on, stays 0b0011
        assertEquals(0x3L, BitwiseStuff.dosido(0x3L));
        // 0xC = 0b1100: both bits in pair are on, stays 0b1100
        assertEquals(0xCL, BitwiseStuff.dosido(0xCL));
        // 0xF = 0b1111: all on, stays
        assertEquals(0xFL, BitwiseStuff.dosido(0xFL));
        // 0x6 = 0b0110: cross-pair swap -> 0b1001 = 0x9
        assertEquals(0x9L, BitwiseStuff.dosido(0x6L));
        assertEquals(0x6L, BitwiseStuff.dosido(0x9L));
    }

    @Test public void testDosidoIsInvolution() {
        // Dosido applied twice returns the original
        long[] vals = {0L, -1L, 1L, 2L, 0x5555555555555555L,
                Long.parseUnsignedLong("8074db18c339bafb", 16),
                42L, 0xDEADBEEFL, Long.MIN_VALUE, Long.MAX_VALUE};
        for (long v : vals) {
            assertEquals("dosido is not an involution for " + Long.toHexString(v),
                    v, BitwiseStuff.dosido(BitwiseStuff.dosido(v)));
        }
    }

    // --- bestRotateFill explicit tests ---

    @Test public void testBestRotateFillZero() {
        // All rotations of zero are zero, so k=0
        assertEquals(0, BitwiseStuff.bestRotateFill(0L));
    }

    @Test public void testBestRotateFillAllOnes() {
        // Already all 64 bits on, can't improve, k=0
        assertEquals(0, BitwiseStuff.bestRotateFill(-1L));
    }

    @Test public void testBestRotateFillSingleBit() {
        // Single bit: rotate by 1 gives 2 bits on, best possible
        assertEquals(1, BitwiseStuff.bestRotateFill(1L));
        assertEquals(1, BitwiseStuff.bestRotateFill(1L << 31));
    }

    @Test public void testBestRotateFillRegularPattern() {
        // 0x0F0F0F0F0F0F0F0F: bits on in low nybble of each byte.
        // Rotate by 4 fills the high nybbles -> all 64 bits on.
        assertEquals(4, BitwiseStuff.bestRotateFill(0x0F0F0F0F0F0F0F0FL));
    }

    @Test public void testBestRotateFillAlmostFull() {
        // All ones except bit 0: rotate left by 1 brings old bit 63 (=1)
        // into the gap via OR, filling all 64 bits.
        assertEquals(1, BitwiseStuff.bestRotateFill(-1L ^ 1L));
    }

    @Test public void testBestRotateFillSmallestKWins() {
        // When multiple k values give the same best bitcount, return the smallest k.
        // Two bits adjacent: 0b11. Rotate by 1 gives 0b110, OR = 0b111 (3 bits).
        // Rotate by 2 gives 0b1100, OR = 0b1111 (4 bits, better).
        // But let's check which k is actually best:
        long v = 3L; // bits 0,1 on
        // k=1: 0b11 | 0b110 = 0b111 -> 3 bits
        // k=2: 0b11 | 0b1100 = 0b1111 -> 4 bits
        // k=62: rotate left 62 = rotate right 2 -> bits 62,63 on
        //   OR = bits 0,1,62,63 = 4 bits (same as k=2)
        // k=2 < k=62, so k=2 wins
        assertEquals(2, BitwiseStuff.bestRotateFill(3L));
    }

    // --- CRC mass tests ---

    @Test public void testCountClustersMass() {
        Random rng = new Random(12345);
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            int prev = 0, curr, count = 0;
            for(int j = 0; j < 64; j++) {
                curr = (rng.nextInt(100) < 25) ? 1 - prev : prev;
                if(curr == 1 && prev == 0) { count++; }
                n = (n << 1) + curr;
                prev = curr;
            }
            int result = BitwiseStuff.countClusters(n);
            assertEquals(count, result);
        }
    }

    @Test public void testBestRotateFillMass() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            for(int j = 0; j < 64; j++) {
                n = (n << 1) + ((rng.nextInt(100) < 75) ? 0 : 1);
            }
            int result = BitwiseStuff.bestRotateFill(n);
            check.update(result);
        }
        assertEquals(505332074L, check.getValue());
    }

    @Test public void testReverseNybblesMass() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            for(int j = 0; j < 64; j++) {
                n = (n << 1) + (rng.nextBoolean() ? 0 : 1);
            }
            long result = BitwiseStuff.reverseNybbles(n);
            long back = BitwiseStuff.reverseNybbles(result);
            try {
                check.update(Long.toHexString(result).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            assertEquals(n, back);
        }
        assertEquals(232648447L, check.getValue());
    }

    @Test public void testDosidoMass() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            long n = 0;
            for(int j = 0; j < 64; j++) {
                n = (n << 1) + (rng.nextBoolean() ? 0 : 1);
            }
            long result = BitwiseStuff.dosido(n);
            long back = BitwiseStuff.dosido(result);
            check.update(Long.toHexString(result).getBytes());
            assertEquals(n, back);
        }
        assertEquals(4127798722L, check.getValue());
    }
}