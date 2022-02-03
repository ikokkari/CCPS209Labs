import org.junit.Test;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class PermutationGeneratorTest {

    private static String permToString(int[] perm) {
        StringBuilder result = new StringBuilder();
        for(int e: perm) {
            result.append(e);
        }
        return result.toString();
    }

    @Test public void testZaksSequenceElementExplicit() {
        assertEquals(2, ZaksPermutation.zaksSequenceElement(2, 0));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(3, 0));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(3, 1));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(3, 2));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(3, 3));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(3, 4));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(4, 0));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(4, 13));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(4, 22));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(5, 6));
        assertEquals(4, ZaksPermutation.zaksSequenceElement(5, 77));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(5, 110));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(6, 19));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(6,  255));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(6, 699));
        assertEquals(4, ZaksPermutation.zaksSequenceElement(7, 89));
        assertEquals(4, ZaksPermutation.zaksSequenceElement(7, 2411));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(7, 2412));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(12, 123456789));
        assertEquals(3, ZaksPermutation.zaksSequenceElement(12, 147878787));
        assertEquals(2, ZaksPermutation.zaksSequenceElement(12, 171717234));
    }

    @Test public void testZaksPermutationFour() {
        int[] perm = new int[4];
        testPermutationGen(new ZaksPermutation(perm), perm, 3321535687L);
    }

    @Test public void testZaksPermutationSeven() {
        int[] perm = new int[7];
        testPermutationGen(new ZaksPermutation(perm), perm, 3656943312L);
    }

    @Test public void testZaksPermutationTen() {
        int[] perm = new int[10];
        testPermutationGen(new ZaksPermutation(perm), perm, 3227210311L);
    }

    @Test public void testLexicographicPermutationFour() {
        int[] perm = new int[4];
        testPermutationGen(new LexicographicPermutation(perm), perm, 850034859L);
    }

    @Test public void testLexicographicPermutationSeven() {
        int[] perm = new int[7];
        testPermutationGen(new LexicographicPermutation(perm), perm, 137736586L);
    }

    @Test public void testLexicographicPermutationTen() {
        int[] perm = new int[10];
        testPermutationGen(new LexicographicPermutation(perm), perm, 2447996374L);
    }

    private void testPermutationGen(PermutationGenerator gen, int[] perm, long expected) {
        int n = perm.length;
        int f = 1;
        for(int i = 2; i <= n; i++) { f = f * i; }
        CRC32 check = new CRC32();
        for(int i = 0; i < f; i++) {
            for(int j = 0; j < n; j++) {
                check.update(perm[j]);
            }
            boolean b = gen.next();
            assertTrue(i < f-1 || !b);
        }
        assertFalse(gen.next());
        assertEquals(expected, check.getValue());
    }
}