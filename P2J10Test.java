import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J10Test {
    
    private static void fill(boolean[] v, Random rng) {
        for(int i = 0; i < v.length; i++) {
            v[i] = rng.nextBoolean();
        }
        // Ensure that at least one element is true no matter what.
        v[rng.nextInt(v.length)] = true;
    }

    private static boolean[] conv(String bits) {
        int n = bits.length();
        boolean[] result = new boolean[n];
        for(int i = 0; i < n; i++) {
            result[i] = bits.charAt(i) == '1';
        }
        return result;
    }

    @Test public void testDissimilarityExplicit() {
        boolean[] f1 = conv("10101");
        boolean[] s1 = conv("10101");
        assertEquals("0", P2J10.matchingDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.jaccardDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.diceDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.rogersTanimonoDissimilarity(f1, s1).toString());
        assertEquals("2/5", P2J10.russellRaoDissimilarity(f1, s1).toString());
        assertEquals("0", P2J10.sokalSneathDissimilarity(f1, s1).toString());

        boolean[] f2 = conv("01111");
        boolean[] s2 = conv("00100");
        assertEquals("3/5", P2J10.matchingDissimilarity(f2, s2).toString());
        assertEquals("3/4", P2J10.jaccardDissimilarity(f2, s2).toString());
        assertEquals("3/5", P2J10.diceDissimilarity(f2, s2).toString());
        assertEquals("3/4", P2J10.rogersTanimonoDissimilarity(f2, s2).toString());
        assertEquals("4/5", P2J10.russellRaoDissimilarity(f2, s2).toString());
        assertEquals("6/7", P2J10.sokalSneathDissimilarity(f2, s2).toString());

        boolean[] f3 = conv("01110");
        boolean[] s3 = conv("11100");
        assertEquals("2/5", P2J10.matchingDissimilarity(f3, s3).toString());
        assertEquals("1/2", P2J10.jaccardDissimilarity(f3, s3).toString());
        assertEquals("1/3", P2J10.diceDissimilarity(f3, s3).toString());
        assertEquals("4/7", P2J10.rogersTanimonoDissimilarity(f3, s3).toString());
        assertEquals("3/5", P2J10.russellRaoDissimilarity(f3, s3).toString());
        assertEquals("2/3", P2J10.sokalSneathDissimilarity(f3, s3).toString());

        boolean[] f4 = conv("10011");
        boolean[] s4 = conv("11100");
        assertEquals("4/5", P2J10.matchingDissimilarity(f4, s4).toString());
        assertEquals("4/5", P2J10.jaccardDissimilarity(f4, s4).toString());
        assertEquals("2/3", P2J10.diceDissimilarity(f4, s4).toString());
        assertEquals("8/9", P2J10.rogersTanimonoDissimilarity(f4, s4).toString());
        assertEquals("4/5", P2J10.russellRaoDissimilarity(f4, s4).toString());
        assertEquals("8/9", P2J10.sokalSneathDissimilarity(f4, s4).toString());

        boolean[] f5 = conv("01100");
        boolean[] s5 = conv("11010");
        assertEquals("3/5", P2J10.matchingDissimilarity(f5, s5).toString());
        assertEquals("3/4", P2J10.jaccardDissimilarity(f5, s5).toString());
        assertEquals("3/5", P2J10.diceDissimilarity(f5, s5).toString());
        assertEquals("3/4", P2J10.rogersTanimonoDissimilarity(f5, s5).toString());
        assertEquals("4/5", P2J10.russellRaoDissimilarity(f5, s5).toString());
        assertEquals("6/7", P2J10.sokalSneathDissimilarity(f5, s5).toString());

        boolean[] f6 = conv("11010");
        boolean[] s6 = conv("10100");
        assertEquals("3/5", P2J10.matchingDissimilarity(f6, s6).toString());
        assertEquals("3/4", P2J10.jaccardDissimilarity(f6, s6).toString());
        assertEquals("3/5", P2J10.diceDissimilarity(f6, s6).toString());
        assertEquals("3/4", P2J10.rogersTanimonoDissimilarity(f6, s6).toString());
        assertEquals("4/5", P2J10.russellRaoDissimilarity(f6, s6).toString());
        assertEquals("6/7", P2J10.sokalSneathDissimilarity(f6, s6).toString());

        boolean[] f7 = conv("01101");
        boolean[] s7 = conv("10010");
        assertEquals("1", P2J10.matchingDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.jaccardDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.diceDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.rogersTanimonoDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.russellRaoDissimilarity(f7, s7).toString());
        assertEquals("1", P2J10.sokalSneathDissimilarity(f7, s7).toString());
    }

    private static final int N = 1000;
    
    @Test public void testJaccard() { testDissimilarityMass(619021331L, 0); }
    @Test public void testMatching() { testDissimilarityMass(2582992579L, 1); }
    @Test public void testDice() { testDissimilarityMass(864445653L, 2); }
    @Test public void testRogersTanimono() { testDissimilarityMass(2631246168L, 3); }
    @Test public void testRussellRao() { testDissimilarityMass(3219060315L, 4); }
    @Test public void testSokalSneath() { testDissimilarityMass(737788739L, 5); }
    
    private void testDissimilarityMass(long expected, int mode) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < P2J10Test.N; i++) {
            int nn = 5 + i / 20;
            boolean[] v1 = new boolean[nn], v2 = new boolean[nn];
            fill(v1, rng); fill(v2, rng);
            Fraction result;
            if(mode == 0) { result = P2J10.jaccardDissimilarity(v1, v2); }
            else if(mode == 1) { result = P2J10.matchingDissimilarity(v1, v2); }
            else if(mode == 2) { result = P2J10.diceDissimilarity(v1, v2); }
            else if(mode == 3) { result = P2J10.rogersTanimonoDissimilarity(v1, v2); }
            else if(mode == 4) { result = P2J10.russellRaoDissimilarity(v1, v2); }
            else { result = P2J10.sokalSneathDissimilarity(v1, v2); }
            check.update(result.getNum().intValue());
            check.update(result.getDen().intValue());
        }
        assertEquals(expected, check.getValue());
    }
}