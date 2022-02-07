import org.junit.Test;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
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

    private static final String[] ZAKS_EXPECTED = {
            "0123", "1023", "2013", "0213", "1203", "2103", "3012", "0312", "1302", "3102", "0132", "1032",
            "2301", "3201", "0231", "2031", "3021", "0321", "1230", "2130", "3120", "1320", "2310", "3210"
    };

    private static final String[] LEXICOGRAPHIC_EXPECTED = {
            "0123", "0132", "0213", "0231", "0312", "0321", "1023", "1032", "1203", "1230", "1302", "1320",
            "2013", "2031", "2103", "2130", "2301", "2310", "3012", "3021", "3102", "3120", "3201", "3210"
    };

    @Test public void testZaksPermutationExplicit() {
        int[] perm = new int[4];
        String[] result = new String[24];
        ZaksPermutation zp = new ZaksPermutation(perm);
        for(int i = 0; i < 24; i++) {
            result[i] = permToString(perm);
            zp.next();
        }
        assertArrayEquals(ZAKS_EXPECTED, result);
    }

    @Test public void testZaksPermutationSeven() {
        int[] perm = new int[7];
        testPermutationGen(new ZaksPermutation(perm), perm, 2234769897L);
    }

    @Test public void testZaksPermutationTen() {
        int[] perm = new int[10];
        testPermutationGen(new ZaksPermutation(perm), perm, 721747669L);
    }

    @Test public void testLexicographicPermutationExplicit() {
        int[] perm = new int[4];
        String[] result = new String[24];
        LexicographicPermutation zp = new LexicographicPermutation(perm);
        for(int i = 0; i < 24; i++) {
            result[i] = permToString(perm);
            zp.next();
        }
        assertArrayEquals(LEXICOGRAPHIC_EXPECTED, result);
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
            for(int e: perm) { check.update(e); }
            boolean b = gen.next();
            assertTrue(i < f-1 || !b);
        }
        assertFalse(gen.next());
        assertEquals(expected, check.getValue());
    }
}