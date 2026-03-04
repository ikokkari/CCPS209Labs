import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class DualTest {

    private static final double EPSILON_ONE = 0.000001;
    private static final double EPSILON_TWO = 0.00000000001;

    private Dual[] diffPoly(Dual[] coeff) {
        int n = coeff.length - 1;
        Dual[] result = new Dual[n];
        for(int i = 0; i < n; i++) {
            result[i] = coeff[i+1].multiply(new Dual(i+1, 0));
        }
        return result;
    }

    // ===== Arithmetic identity tests =====

    @Test public void testAddSubtract() {
        Dual a = new Dual(3.0, 1.0);
        Dual b = new Dual(7.0, 2.0);
        // a + b - b should give back a
        Dual result = a.add(b).subtract(b);
        assertTrue(Math.abs(result.getX() - a.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx() - a.getPx()) < EPSILON_TWO);
        // a - a should be zero
        Dual zero = a.subtract(a);
        assertTrue(Math.abs(zero.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(zero.getPx()) < EPSILON_TWO);
    }

    @Test public void testMultiplyByOne() {
        Dual a = new Dual(5.0, 3.0);
        Dual one = new Dual(1.0, 0.0);
        Dual result = a.multiply(one);
        assertTrue(Math.abs(result.getX() - a.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx() - a.getPx()) < EPSILON_TWO);
    }

    @Test public void testMultiplyByZero() {
        Dual a = new Dual(5.0, 3.0);
        Dual zero = new Dual(0.0, 0.0);
        Dual result = a.multiply(zero);
        assertTrue(Math.abs(result.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx()) < EPSILON_TWO);
    }

    @Test public void testDivideInverse() {
        // a / b * b should give back a
        Dual a = new Dual(6.0, 1.0);
        Dual b = new Dual(3.0, 0.5);
        Dual result = a.divide(b).multiply(b);
        assertTrue(Math.abs(result.getX() - a.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx() - a.getPx()) < EPSILON_TWO);
    }

    @Test public void testMultiplyCommutative() {
        Dual a = new Dual(2.0, 3.0);
        Dual b = new Dual(5.0, 7.0);
        Dual ab = a.multiply(b);
        Dual ba = b.multiply(a);
        assertTrue(Math.abs(ab.getX() - ba.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(ab.getPx() - ba.getPx()) < EPSILON_TWO);
    }

    @Test public void testMultiplyDistributive() {
        // a * (b + c) == a*b + a*c
        Dual a = new Dual(2.0, 1.0);
        Dual b = new Dual(3.0, 0.5);
        Dual c = new Dual(4.0, -0.3);
        Dual lhs = a.multiply(b.add(c));
        Dual rhs = a.multiply(b).add(a.multiply(c));
        assertTrue(Math.abs(lhs.getX() - rhs.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(lhs.getPx() - rhs.getPx()) < EPSILON_TWO);
    }

    // ===== Dual number specific: ε² = 0 =====

    @Test public void testEpsilonSquaredZero() {
        // (0 + 1ε) * (0 + 1ε) = (0 + 0ε), since ε² = 0
        Dual eps = new Dual(0.0, 1.0);
        Dual sq = eps.multiply(eps);
        assertTrue(Math.abs(sq.getX()) < EPSILON_TWO);
        assertTrue(Math.abs(sq.getPx()) < EPSILON_TWO);
    }

    // ===== Known derivative tests =====

    @Test public void testSinCosIdentity() {
        // sin²(x) + cos²(x) = 1 for various x, and its derivative = 0
        Random rng = new Random(9999);
        for (int i = 0; i < 100; i++) {
            Dual x = new Dual(rng.nextDouble() * 10 - 5, 1.0);
            Dual sinx = x.sin();
            Dual cosx = x.cos();
            Dual sum = sinx.multiply(sinx).add(cosx.multiply(cosx));
            assertTrue(Math.abs(sum.getX() - 1.0) < EPSILON_ONE);
            assertTrue(Math.abs(sum.getPx()) < EPSILON_ONE); // derivative of constant = 0
        }
    }

    @Test public void testExpLogInverse() {
        // log(exp(x)) = x, so derivative = 1
        Random rng = new Random(7777);
        for (int i = 0; i < 100; i++) {
            double xv = rng.nextDouble() * 4 - 2; // [-2, 2] to keep exp reasonable
            Dual x = new Dual(xv, 1.0);
            Dual result = x.exp().log();
            assertTrue(Math.abs(result.getX() - xv) < EPSILON_ONE);
            assertTrue(Math.abs(result.getPx() - 1.0) < EPSILON_ONE);
        }
    }

    @Test public void testPowKnownValues() {
        // d/dx x^3 = 3x^2
        Dual x = new Dual(2.0, 1.0);
        Dual r = x.pow(3);
        assertTrue(Math.abs(r.getX() - 8.0) < EPSILON_TWO);
        assertTrue(Math.abs(r.getPx() - 12.0) < EPSILON_TWO);
        // d/dx x^0.5 = 0.5 * x^(-0.5) at x=4: val=2, deriv=0.25
        Dual y = new Dual(4.0, 1.0);
        Dual s = y.pow(0.5);
        assertTrue(Math.abs(s.getX() - 2.0) < EPSILON_TWO);
        assertTrue(Math.abs(s.getPx() - 0.25) < EPSILON_TWO);
    }

    @Test public void testAbsPositiveAndNegative() {
        // |x| at x=3: val=3, deriv=1
        Dual pos = new Dual(3.0, 1.0);
        Dual rp = pos.abs();
        assertTrue(Math.abs(rp.getX() - 3.0) < EPSILON_TWO);
        assertTrue(Math.abs(rp.getPx() - 1.0) < EPSILON_TWO);
        // |x| at x=-3: val=3, deriv=-1
        Dual neg = new Dual(-3.0, 1.0);
        Dual rn = neg.abs();
        assertTrue(Math.abs(rn.getX() - 3.0) < EPSILON_TWO);
        assertTrue(Math.abs(rn.getPx() - (-1.0)) < EPSILON_TWO);
    }

    // ===== compareTo and equals =====

    @Test public void testCompareToAndEquals() {
        // equals ignores the ε component
        assertTrue(new Dual(3.0, 1.0).equals(new Dual(3.0, 99.0)));
        assertTrue(!new Dual(3.0).equals(new Dual(4.0)));
        assertTrue(!new Dual(3.0).equals("not a dual"));
        assertTrue(!new Dual(3.0).equals(null));
        // compareTo
        assertTrue(new Dual(1.0).compareTo(new Dual(2.0)) < 0);
        assertTrue(new Dual(2.0).compareTo(new Dual(2.0)) == 0);
        assertTrue(new Dual(3.0).compareTo(new Dual(2.0)) > 0);
        // NaN and negative zero edge cases
        assertTrue(new Dual(-0.0).compareTo(new Dual(0.0)) < 0); // Double.compare distinguishes
        assertEquals(new Dual(3.0, 1.0).hashCode(), new Dual(3.0, 42.0).hashCode());
    }

    // ===== Horner's rule polynomial tests =====

    @Test public void testHornerRuleConstant() {
        // Constant polynomial: P(x) = 7, P'(x) = 0
        Dual x = new Dual(3.0, 1.0);
        Dual[] poly = { new Dual(7.0) };
        Dual result = Dual.hornerRule(poly, x);
        assertTrue(Math.abs(result.getX() - 7.0) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx()) < EPSILON_TWO);
    }

    @Test public void testHornerRuleLinear() {
        // P(x) = 2 + 3x, P'(x) = 3, at x = 5: P(5) = 17, P'(5) = 3
        Dual x = new Dual(5.0, 1.0);
        Dual[] poly = { new Dual(2.0), new Dual(3.0) };
        Dual result = Dual.hornerRule(poly, x);
        assertTrue(Math.abs(result.getX() - 17.0) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx() - 3.0) < EPSILON_TWO);
    }

    @Test public void testHornerRuleQuadratic() {
        // P(x) = 2 + 3x + 5x^2, P'(x) = 3 + 10x, at x = 4: P = 94, P' = 43
        Dual x = new Dual(4.0, 1.0);
        Dual[] poly = { new Dual(2.0), new Dual(3.0), new Dual(5.0) };
        Dual result = Dual.hornerRule(poly, x);
        assertTrue(Math.abs(result.getX() - 94.0) < EPSILON_TWO);
        assertTrue(Math.abs(result.getPx() - 43.0) < EPSILON_TWO);
    }

    @Test public void testHornerRuleHundred() {
        testHornerRule(100);
    }

    @Test public void testHornerRuleTenThousand() {
        testHornerRule(10_000);
    }

    @Test public void testHornerRuleOneMillion() {
        testHornerRule(1_000_000);
    }

    private void testHornerRule(int n) {
        Random rng = new Random(12345 + n);
        int m = 3, count = 0, goal = 10;
        for(int i = 0; i < n; i++) {
            Dual[] poly = new Dual[m];
            for(int j = 0; j < m; j++) {
                poly[j] = new Dual(rng.nextDouble() * 10 - 5, 0);
            }
            Dual[] diff = diffPoly(poly);
            Dual x = new Dual(rng.nextDouble() * 2 - 1, 1);
            Dual fx = Dual.hornerRule(poly, x);
            Dual fpx = Dual.hornerRule(diff, x);
            double dNum = fx.getPx();
            double dSym = fpx.getX();
            assertTrue(Math.abs(dNum - dSym) < EPSILON_TWO);
            if(++count == goal) {
                m++; count = 0; goal *= 2;
            }
        }
    }

    // ===== Transcendental chain rule tests =====

    @Test public void testTranscendentalsHundred() {
        testTranscendentals(100);
    }

    @Test public void testTranscendentalsTenThousand() {
        testTranscendentals(10_000);
    }

    @Test public void testTranscendentalsOneMillion() {
        testTranscendentals(1_000_000);
    }

    private void testTranscendentals(int n) {
        Random rng = new Random(4242 + n);
        int m = 3, count = 0, goal = 10;
        for(int i = 0; i < n; i++) {
            Dual x = new Dual(rng.nextDouble() * 4 - 2, 1);
            double symX = x.getX(), symXp = 1;
            assertTrue(x.getX() == symX && x.getPx() == symXp);
            for(int j = 0; j < m; j++) {
                int op = rng.nextInt(6);
                // x = f(x), xp = fp(xp)
                if(op == 0 && Math.abs(symX) < 10000) { // sin
                    x = x.sin(); symXp = symXp * Math.cos(symX); symX = Math.sin(symX);
                }
                else if(op == 1 && Math.abs(symX) < 10000) { // cos
                    x = x.cos(); symXp = -symXp * Math.sin(symX); symX = Math.cos(symX);
                }
                else if(op == 2 && symX > 0.5 && symX < 5) { // exp
                    x = x.exp(); symXp = Math.exp(symX) * symXp; symX = Math.exp(symX);
                }
                else if(op == 3 && Math.abs(symX) > 1.0 && Math.abs(symX) < 10) { // pow
                    double k = rng.nextDouble() * 5 + 1.1;
                    x = x.abs().pow(k);
                    symXp = Math.signum(symX) * symXp;
                    symX = Math.abs(symX);
                    symXp = symXp * k * Math.pow(symX, k-1);
                    symX = Math.pow(symX, k);
                }
                else if(op == 4 && Math.abs(symX) > 0.1) { // log
                    x = x.abs();
                    symXp = Math.signum(symX) * symXp;
                    symX = Math.abs(symX);
                    x = x.log();
                    symXp = symXp / symX;
                    symX = Math.log(symX);
                }
                else { // div, the last resort that always works, and needs to be tested anyway
                    double dMan = rng.nextDouble() + 1;
                    double dExp = Math.pow(10, rng.nextInt(5));
                    double div = dMan * dExp;
                    x = x.divide(new Dual(div, 0));
                    double ddiv = div * div;
                    symX = symX / div;
                    symXp = (symXp * div) / ddiv;
                }

                if(Double.isInfinite(symX) || Double.isInfinite(symXp)) { break; }
                if(Math.abs(symXp) < EPSILON_TWO) { break; }
                double diff = Math.abs(x.getPx() - symXp);
                double m1 = Math.abs(x.getPx());
                double m2 = Math.abs(symXp);
                double maxx = m1 < m2 ? m2 : m1;
                assertTrue(diff / maxx < EPSILON_ONE);
            }
            if(++count == goal) {
                m++; count = 0; goal *= 2;
            }
        }
    }
}