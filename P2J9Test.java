import org.junit.Test;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J9Test {
    
    @Test public void testSubtractSquareExplicit() {
        int[] coldStates = {0, 2, 5, 7, 10, 12, 15, 17, 20, 22, 34, 39};
        boolean[] actual = P2J9.subtractSquare(40);
        int pos = 0;
        for(int i = 0; i < 40; i++) {
            if(!actual[i]) {
                assertEquals(i, coldStates[pos++]);
            }
        }
    }
    
    @Test public void testSumOfTwoDistinctSquaresExplicit() {
        int[] workingStates = {5, 10, 13, 17, 20, 25, 26, 29, 34, 37, 40, 41, 45, 50, 52};
        boolean[] actual = P2J9.sumOfTwoDistinctSquares(53);
        int pos = 0;
        for(int i = 0; i < 53; i++) {
            if(actual[i]) {
                assertEquals(i, workingStates[pos++]);
            }
        }
    }
    
    @Test public void testSubtractSquareThousand() {
        test(1000, 4122798422L, 0);
    }
    
    @Test public void testSubtractSquareMillion() {
        test(1_000_000, 1504185187L, 0);
    }
    
    @Test public void testSubtractSquareTenMillion() {
        test(10_000_000, 3315207453L, 0);
    }
    
    @Test public void testSumOfTwoDistinctSquaresThousand() {
        test(1000, 4110419952L, 1);
    }
    
    @Test public void testSumOfTwoDistinctSquaresMillion() {
        test(1_000_000, 2362619161L, 1);
    }
    
    // Same test harness for both methods.
    private void test(int n, long expected, int mode) {
        CRC32 check = new CRC32();
        boolean[] result;
        if(mode == 0) { 
            result = P2J9.subtractSquare(n); 
        }
        else {
            result = P2J9.sumOfTwoDistinctSquares(n);
        }
        for(int i = 0; i < n; i++) {
            check.update(result[i] ? i : 0);
        }
        assertEquals(expected, check.getValue());
    }
}
