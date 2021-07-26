import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolynomialTestTwo {
    
    private static final int SEED = 12345;
    private static final int TRIALS = 100000;
    
    private Polynomial createRandom(int deg, Random rng) {
        int[] c = new int[deg + 1];
        for(int j = 0; j < deg + 1; j++) {
            c[j] = rng.nextInt(20) - 10;
        }
        return new Polynomial(c);
    }
    
    // Utility method to check that polynomials p1 and p2 are equal, and also
    // update the checksum with the coefficients of these polynomials.
    private boolean polyEq(Polynomial p1, Polynomial p2, CRC32 check) {
        if(p1.getDegree() != p2.getDegree()) { return false; }
        for(int k = 0; k <= p1.getDegree(); k++) {
            if(check != null) { check.update(p1.getCoefficient(k)); }
            if(p1.getCoefficient(k) != p2.getCoefficient(k)) { return false; }
        }
        return true;
    }
    
    @Test public void testAdd() {
        int[] t01 = {0};
        int[] t02 = {-42, 99, 17, 101};
        int[] e0 = {-42, 99, 17, 101};
        Polynomial p01 = new Polynomial(t01);
        Polynomial p02 = new Polynomial(t02);
        Polynomial r0 = p01.add(p02);
        Polynomial p0e = new Polynomial(e0);
        assertTrue(polyEq(r0, p0e, null));
        
        // Highest terms may cancel each other out in addition.
        int[] t11 = {5, -5, 2, -2, 4};
        int[] t12 = {3, 5, -2, 2, -4};
        int[] e1 = {8};
        Polynomial p11 = new Polynomial(t11);
        Polynomial p12 = new Polynomial(t12);
        Polynomial r1 = p11.add(p12);
        Polynomial p1e = new Polynomial(e1);
        assertTrue(polyEq(r1, p1e, null));
        
        int[] t21 = {-3, 9, -2, 0, 0, 4};
        int[] t22 = {5, -7, 0, 1, 0, 0, 5};
        int[] e2 = {2, 2, -2, 1, 0, 4, 5};
        Polynomial p21 = new Polynomial(t21);
        Polynomial p22 = new Polynomial(t22);
        Polynomial r2 = p21.add(p22);
        Polynomial p2e = new Polynomial(e2);
        assertTrue(polyEq(r2, p2e, null));
        
        int[] t31 = {-1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 12345};
        int[] t32 = {-9, 1, 2, 3, 4, 5, 6};
        int[] e3 = {-10, 1, 2, 3, 4, 5, 6, 0, 0, 0, 12345};
        Polynomial p31 = new Polynomial(t31);
        Polynomial p32 = new Polynomial(t32);
        Polynomial r3 = p31.add(p32);
        Polynomial p3e = new Polynomial(e3);
        assertTrue(polyEq(r3, p3e, null));
    }
    
    @Test public void testMultiply() {
        int[] c1 = {7, -5, 3}; // 3x^2 - 5x + 7
        int[] c2 = {6, 0, 0, -4}; // -4x^3 + 6
        Polynomial p1 = new Polynomial(c1);
        Polynomial p2 = new Polynomial(c2);
        // Product of two polynomials must be equal both ways.
        Polynomial p3 = p1.multiply(p2);
        Polynomial p4 = p2.multiply(p1);
        assertTrue(polyEq(p3, p4, null));
        // The expected correct result of multiplying p1 and p2.
        int[] c5 = {42, -30, 18, -28, 20, -12};
        Polynomial p5 = new Polynomial(c5);
        assertTrue(polyEq(p3, p5, null));
        
        int[] c6 = {0, 1, 0, 0, 0, -2, 0, 0, 0, 0, 1};
        int[] c7 = {1, 2, -4};
        Polynomial p6 = new Polynomial(c6);
        Polynomial p7 = new Polynomial(c7);
        Polynomial p8 = p6.multiply(p7);
        Polynomial p9 = p7.multiply(p6);
        assertTrue(polyEq(p8, p9, null));
        assertTrue(polyEq(p9, p8, null));
        int[] c10 = {0, 1, 2, -4, 0, -2, -4, 8, 0, 0, 1, 2, -4};
        Polynomial p10 = new Polynomial(c10);
        assertTrue(polyEq(p8, p10, null));
    }
    
    @Test public void massTest() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < TRIALS; i++) {
            Polynomial p1 = createRandom(rng.nextInt(10 + i / 1000), rng);
            Polynomial p2 = createRandom(rng.nextInt(10 + i / 1000), rng);
            Polynomial p3 = p1.add(p2);
            Polynomial p4 = p2.add(p1);
            // If this assert fails, your add gives different results for p1+p2 and p2+p1.
            assertTrue(polyEq(p3, p4, check));
            Polynomial p5 = p1.multiply(p2);
            Polynomial p6 = p2.multiply(p1);
            // If this assert fails, your multiply gives different results for p1*p2 and p2*p1.
            assertTrue(polyEq(p5, p6, check));
        }
        // Checksum computed from the coefficients of all returned polynomials.
        assertEquals(529848787L, check.getValue());
    }
}