import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.*;
import java.util.zip.CRC32;

public class TenFourTest {

    @Test public void testShortestPathThousand() {
        testShortestPath(500, 1154212991L);
    }
    
    private void testShortestPath(int n, long expected) {
        /* Explicit test cases */
        assertEquals("[4, 2, 24, 12, 6]", TenFour.shortestPath(6, 100).toString());
        assertEquals("[4, 2, 24, 12, 6, 3, 34, 17]", TenFour.shortestPath(17, 100).toString());
        assertEquals("[4, 2, 24, 12, 124, 62, 31]", TenFour.shortestPath(31, 1000).toString());
        assertEquals("[4, 2, 1, 14, 144, 72, 36]", TenFour.shortestPath(36, 1000).toString());
        assertEquals("[4, 40, 20, 10, 104, 1040, 520, 260, 130, 65]", TenFour.shortestPath(65, 10000).toString());
        assertEquals("[4, 2, 24, 12, 124, 62, 624, 312, 156, 78, 39, 394, 197]",
            TenFour.shortestPath(197, 1_000_000).toString());
        
        /* Pseudorandom fuzz tester */
        CRC32 check = new CRC32();
        for(int i = 1; i < n; i++) {
            int limit = 100 * n + 1;
            List<Integer> result;
            do {
                result = TenFour.shortestPath(i, limit);
                limit = limit * 2;
            } while(result.size() == 0);
            // System.out.println(i + ": " + result);
            check.update(result.toString().getBytes());
        }
        assertEquals(expected, check.getValue());
    }  
}