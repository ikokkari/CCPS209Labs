import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

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
        int[] c1 = {0, 5, -3, 1, 99, -5};
        int[] c2 = {-4, 2, 3, 0, -99, 5};
        Polynomial p1 = new Polynomial(c1);
        Polynomial p2 = new Polynomial(c2);
        Polynomial p3 = p1.add(p2);
        Polynomial p4 = p2.add(p1);
        
        assertEquals(3, p3.getDegree());
        assertEquals(3, p4.getDegree());
        assertEquals(-4, p3.getCoefficient(0));
        assertEquals(-4, p4.getCoefficient(0));
        assertEquals(7, p3.getCoefficient(1));
        assertEquals(7, p4.getCoefficient(1));
        assertEquals(0, p3.getCoefficient(2));
        assertEquals(0, p4.getCoefficient(2));
        assertEquals(1, p3.getCoefficient(3));
        assertEquals(1, p4.getCoefficient(3));
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
            Polynomial p1 = createRandom(rng.nextInt(10), rng);
            Polynomial p2 = createRandom(rng.nextInt(10), rng);
            Polynomial p3 = p1.add(p2);
            Polynomial p4 = p2.add(p1);
            assertTrue(polyEq(p3, p4, check));
            Polynomial p5 = p1.multiply(p2);
            Polynomial p6 = p2.multiply(p1);
            assertTrue(polyEq(p5, p6, check));
        }
        assertEquals(2427324440L, check.getValue());
    }
}