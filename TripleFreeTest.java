import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class TripleFreeTest {

    @Test public void testTripleFree() {
        assertEquals(TripleFree.tripleFree(1).toString(), "[1]");
        assertEquals(TripleFree.tripleFree(2).toString(), "[1, 2]");
        assertEquals(TripleFree.tripleFree(3).toString(), "[2, 3]");
        assertEquals(TripleFree.tripleFree(4).toString(), "[1, 3, 4]");
        assertEquals(TripleFree.tripleFree(5).toString(), "[1, 2, 4, 5]");
        assertEquals(TripleFree.tripleFree(6).toString(), "[2, 3, 5, 6]");
        assertEquals(TripleFree.tripleFree(7).toString(), "[3, 4, 6, 7]");
        assertEquals(TripleFree.tripleFree(8).toString(), "[4, 5, 7, 8]");
        assertEquals(TripleFree.tripleFree(9).toString(), "[1, 2, 6, 8, 9]");
        assertEquals(TripleFree.tripleFree(10).toString(), "[1, 6, 7, 9, 10]");
    }
    
    @Test public void testFirstTwenty() {
        massTest(20, 3937650381L);
    }

    @Test public void testFirstFifty() {
        massTest(50, 3173595071L);
    }

    // Only the most hardcore students will dare uncomment this test.
    /*
    @Test public void testFirstHundred() {
        massTest(100, 1174711062L);
    }
    */
    // Solution for n = 100:
    // [1, 5, 7, 10, 11, 14, 16, 24, 26, 29, 30, 33, 35, 39, 66, 70, 72, 75, 76, 79, 81, 89, 91, 94, 95, 98, 100]

    private void massTest(int n, long expected) {
        CRC32 check = new CRC32();
        for(int i = 1; i <= n; i++) {
            List<Integer> result = TripleFree.tripleFree(i);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(expected, check.getValue());
    }
}