import org.junit.Test;
import java.util.Random;
import static org.junit.Assert.assertTrue;

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
            double dNum = fx.getXp();
            double dSym = fpx.getX();
            assertTrue(Math.abs(dNum - dSym) < EPSILON_TWO);
            if(++count == goal) {
                m++; count = 0; goal *= 2;
            }
        }
    }

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
            assertTrue(x.getX() == symX && x.getXp() == symXp);
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

                // I will be the first to admit that I have absolutely no idea how to do this
                // properly. If some expert of numerical analysis and floating point numbers
                // sees this, please let me how to do this better.
                if(Double.isInfinite(symX) || Double.isInfinite(symXp)) { break; }
                if(Math.abs(symXp) < EPSILON_TWO) { break; }
                double diff = Math.abs(x.getXp() - symXp);
                double m1 = Math.abs(x.getXp());
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