import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class RationalRootsTest {
    
    @Test public void testEvaluate() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < 10000; i++) {
            int n = 1 + i % 10;
            int[] coeffs = new int[n];
            for(int j = 0; j < coeffs.length; j++) {
                coeffs[j] = rng.nextInt(2 + i);
                if(rng.nextBoolean()) { coeffs[j] *= -1; }
            }
            int a = rng.nextInt(10 + i);
            int b = rng.nextInt(10 + i) + 1;
            Fraction x = new Fraction(a, b);
            Fraction result = RationalRoots.evaluate(coeffs, x);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(52768542L, check.getValue());
    }

    // Created with the aid of trusty old Wolfram Mathematica. All these problems
    // would be so much easier when solved in that language.
    private static final int[][] rationalTestCases = {
        {42, -73, -94, 165},
        {1503067, -3015954, 1512899},
        {-85, 170, 22, -44, -1, 2},
        {-420, 187, 564, -495, 108},
        {126, 348, 21, -290, -84, 58, 21},
        {444528, 1238328, 312522, -13923, -3556, 39, 10},
        {216, 1458, -413, -243, 15, 7},
        {319410, -207597, 48337, -4830, 176},
        {-528066, 213741, 9492, -3842, -42, 17},
        {-228690, -3676893, 3556303, 10523648, -6693372, 130896},
        {-1, 58, -1349, 16186, -107315, 390238, -716167, 510510}
    };
    
    // The expected correct answers to the previous rational test cases.
    
    private static final String[] rationalExpected = {
        "[-2/3, 3/5, 7/11]",
        "[1223/1229, 1229/1231]",
        "[1/2]",
        "[-3/4, 4/3, 5/3, 7/3]",
        "[-7/3, -3/7]",
        "[-7/2, -2/5]",
        "[-4, -1/7, 2]",
        "[42/11, 13/2, 65/8, 9]",
        "[42/17]",
        "[-11/18, -6/101, 5/9, 7/4, 99/2]",
        "[1/17, 1/13, 1/11, 1/7, 1/5, 1/3, 1/2]"
    };
    
    
    @Test public void testRationalRootsKnown() {
        for(int i = 0; i < rationalTestCases.length; i++) {
            int[] coeff = rationalTestCases[i];
            String expected = rationalExpected[i];
            String result = RationalRoots.rationalRoots(coeff).toString();
            assertEquals(expected, result);
        }
    }
    
    @Test public void testRationalRootsMass() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < 500; i++) {
            int n = 3 + i % 4;
            int[] coeffs = new int[n];
            for(int j = 1; j < n; j++) {
                int c = j == n - 1 ? 1 : rng.nextInt(5 + i);
                if(rng.nextBoolean()) { c = -c; }
                coeffs[j] = c;
            }
            if(coeffs[0] == 0) {
                coeffs[0] = rng.nextInt(5 + i) + 1;
            }
            int a = rng.nextInt(7) + 1;
            if(rng.nextBoolean()) { a = -a; }
            int b = rng.nextInt(7) + 1;
            if(b == a || b == -a) { b += rng.nextInt(10) + 1; }
            if(b % a == 0) { b += rng.nextBoolean() ? +1 : -1; }
            Fraction last = RationalRoots.evaluate(coeffs, new Fraction(a, b));
            coeffs[0] = -last.getNum().intValue();
            int den = last.getDen().intValue();
            for(int j = 1; j < coeffs.length; j++) {
                coeffs[j] *= den;
            }
            List<Fraction> result = RationalRoots.rationalRoots(coeffs);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(273735926L, check.getValue());
    }    
}