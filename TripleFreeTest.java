import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.util.Random;

import java.io.*;
import java.util.*;
import java.util.zip.CRC32;

public class TripleFreeTest {

    @Test public void testFirstTen() {
        massTest(10, 2533123764L);
    }

    @Test public void testFirstFifty() {
        massTest(50, 3173595071L);
    }

    // Only the most hardcore of students will dare uncomment this test.
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
            //System.out.println(i + ": " + result.size() + " " + result);
            check.update(result.toString().getBytes());
        }
        assertEquals(expected, check.getValue());
    }

}
