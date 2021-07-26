import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class PolynomialTestOne {
    
    @Test public void testPolynomial() {
        // Explicit test cases
        int[] c1 = {5, 0, -2, 3};
        Polynomial p1 = new Polynomial(c1);
        assertEquals(3, p1.getDegree());
        assertEquals(-2, p1.getCoefficient(2));
        assertEquals(5, p1.evaluate(0));
        assertEquals(21, p1.evaluate(2));

        // Your Polynomial is supposed to create a defensive copy
        // of the coefficient array given to it, so modifying that
        // argument array later should not affect the contents of
        // your Polynomial object.
        c1[3] = 5555;
        assertEquals(3, p1.getCoefficient(3)); // still 3, not 5555

        // Another explicit polynomial.
        int[] c2 = {42, 99, -3, -10, -4};
        Polynomial p2 = new Polynomial(c2);
        assertEquals(4, p2.getDegree());
        assertEquals(99, p2.getCoefficient(1));
        assertEquals(-4, p2.getCoefficient(4));
        assertEquals(0, p2.getCoefficient(5));
        assertEquals(0, p2.getCoefficient(12345));
        
        int[] c3 = {99, 0, 0, 0, 0, 0, 0, 0};
        Polynomial p3 = new Polynomial(c3);
        assertEquals(0, p3.getDegree());
        assertEquals(99, p3.getCoefficient(0));
        
        int[] c4 = {0, 0, 0, 0};
        Polynomial p4 = new Polynomial(c4);
        assertEquals(0, p4.getDegree());
        assertEquals(0, p4.getCoefficient(0));
        assertEquals(0, p4.getCoefficient(1000));
    }
    
    private static final int SEED = 12345;
    private static final int TRIALS = 1000000;
    
    @Test public void massTest() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < TRIALS; i++) {
            int deg = rng.nextInt(10);
            int[] c = new int[deg + 1];
            for(int j = 0; j < deg + 1; j++) {
                c[j] = rng.nextInt(20) - 10;
            }
            Polynomial p = new Polynomial(c);
            check.update(p.getDegree());          
            for(int j = -5; j <= 5; j++) {
                check.update((int) p.evaluate(j));
            }
        }
        assertEquals(427606002L, check.getValue());
    }
}