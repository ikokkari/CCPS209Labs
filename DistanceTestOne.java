import org.junit.Test;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.TreeMap;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class DistanceTestOne {

    private static final int SEED = 123456;

    @Test public void testExtractSquares() {
        // Some explicit test cases
        assertEquals(1, Distance.extractSquares(1));
        assertEquals(1, Distance.extractSquares(11));
        assertEquals(1, Distance.extractSquares(17));
        assertEquals(6, Distance.extractSquares(2 * 2 * 3 * 3));
        assertEquals(2, Distance.extractSquares(2 * 2 * 3 * 5 * 7));
        assertEquals(7, Distance.extractSquares(2 * 7 * 7 * 11 * 13));
        assertEquals(5, Distance.extractSquares(2 * 5 * 5));
        assertEquals(19, Distance.extractSquares(2 * 5 * 11 * 19 * 19));
        assertEquals(2 * 39, Distance.extractSquares(2 * 2 * 2 * 5 * 39 * 39 * 109));
        assertEquals(7 * 7 * 13, Distance.extractSquares(2 * 7 * 7 * 7 * 7 * 11 * 13 * 13));
        assertEquals(5 * 5 * 5, Distance.extractSquares(5 * 5 * 5 * 5 * 5 * 5));
        assertEquals(1000, Distance.extractSquares(1_000_000));

        // Pseudorandom fuzz tester
        CRC32 check = new CRC32();
        for(int n = 0; n < 100_000; n++) {
            int sp = Distance.extractSquares(n);
            assertEquals(0, n % ((long) sp * sp));
            int a = n / (sp*sp); // Integer division truncates
            assertEquals(n, sp*sp*a);
            check.update(sp);
        }
        assertEquals(4222950952L, check.getValue());
    }

    // To clarify the expected behaviour of toString, here are some test cases
    // that demonstrate how the toString method is supposed to behave.
    
    private static final int[][][] testCases = {
        { {61, 3} }, // each term is {root, coefficient}, so this means 3Sqrt[61]
        { {5, 0} },
        { {1003, -42} },
        { {1, -1}, {2, 3}, {10, -1}, {17, 2}  },
        { {5, -1}, {3, -1}, {2, -1} }, 
        { {99, 2}, {999, 2}, {9999, 2} },
        { {123, 3}, {127, 5}, {3, 18} },
        { {5, 1}, {10, 1}, {15, 1}, {20, 1}, {25, 1}, {30, 1} },
        { {2, -1}, {3, -1}, {5, -1}, {7, -1}, {11, -1}, {13, -1} },
        // Terms whose roots are equal after extracting the square must be combined.
        { {2, -1}, {4, -1}, {8, -1}, {16, -1}, {32, -1}, {64, -1}, {128, -1} },
        { {10*10, 5}, {100*100, -5}, {1000*1000, 5} },
        { {5, 1}, {10, -1}, {15, 1}, {20,-1} },
        { {11, 4}, {23, 4}, {11*4, -2}, {23*4, -2} }
    };

    // The returned String objects must have precisely these characters, no more and no less.
    // This is how Wolfram Mathematica would emit these objects in their symbolic form.
    private static final String[] expected = {
        "3Sqrt[61]",
        "0",
        "-42Sqrt[1003]",
        "-1 + 3Sqrt[2] - Sqrt[10] + 2Sqrt[17]",
        "-Sqrt[2] - Sqrt[3] - Sqrt[5]",
        "6Sqrt[11] + 6Sqrt[111] + 6Sqrt[1111]",
        "18Sqrt[3] + 3Sqrt[123] + 5Sqrt[127]",
        "5 + 3Sqrt[5] + Sqrt[10] + Sqrt[15] + Sqrt[30]",
        "-Sqrt[2] - Sqrt[3] - Sqrt[5] - Sqrt[7] - Sqrt[11] - Sqrt[13]",
        "-14 - 15Sqrt[2]",
        "4550",
        "-Sqrt[5] - Sqrt[10] + Sqrt[15]",
        "0"
    };
    
    @Test public void testToString() {
        int i = 0;
        for(int[][] testCase: testCases) {
            TreeMap<Integer, Integer> coeff = new TreeMap<>();
            for(int[] co: testCase) {
                coeff.put(co[0], co[1]);
            }
            Distance d = new Distance(coeff);
            assertEquals(expected[i], d.toString());
            i++;
        }
    }
    
    @Test public void testConstruction() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10_000; i++) {
            int whole = rng.nextInt(3 * (i + 2));
            if(rng.nextBoolean()) { whole = -whole; }
            int base = rng.nextInt(3 * (i + 2)) + 1;
            Distance d = new Distance(whole, base);
            String rep = d.toString();
            try {
                check.update(rep.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(4065287689L, check.getValue());
    }    
}