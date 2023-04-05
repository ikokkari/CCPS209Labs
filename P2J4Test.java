import org.junit.Test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J4Test {

    private static final int SEED = 12345;
    
    @Test public void testFirstMissingPositive() {
        // Explicit test cases
        List<Integer> a0 = Arrays.asList();
        assertEquals(1, P2J4.firstMissingPositive(a0));

        List<Integer> a1 = Arrays.asList(42, 99, 66);
        assertEquals(1, P2J4.firstMissingPositive(a1));
        
        List<Integer> a2 = Arrays.asList(6, 1, 7, 4, 5, 2);
        assertEquals(3, P2J4.firstMissingPositive(a2));
        
        List<Integer> a3 = Arrays.asList(9, 3, 8, 1, 4, 7, 7);
        assertEquals(2, P2J4.firstMissingPositive(a3));
        
        List<Integer> a4 = Arrays.asList(10, 9, 8, 7, 6, 5, 4, 3, 2, 1);
        assertEquals(11, P2J4.firstMissingPositive(a4));

        List<Integer> a5 = Arrays.asList(1, 2, 2, 4);
        assertEquals(3, P2J4.firstMissingPositive(a5));

        List<Integer> a6 = Arrays.asList(1, 2, 2, 3);
        assertEquals(4, P2J4.firstMissingPositive(a6));

        // Verify that you are using the equals method of Integer instead of ==.
        List<Integer> a7 = new ArrayList<>();
        for(int i = 1; i < 200; i++) {
            for(int j = 1; j < 3; j++) {
                a7.add(i);
            }
        }
        assertEquals(200, P2J4.firstMissingPositive(a7));
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED+1);
        List<Integer> items = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            items.clear();
            int miss = 1 + rng.nextInt(1000);
            for(int j = 1; j < miss; j++) {
                int rep = 1 + rng.nextInt(10);
                for(int k = 0; k < rep; k++) { items.add(j); }
            }
            int more = rng.nextInt(i + 2);
            for(int j = 0; j < more; j++) {
                items.add(rng.nextInt(100000000));
            }
            Collections.sort(items);
            int ans = P2J4.firstMissingPositive(items);
            assertEquals(miss, ans);
        }
    }
    
    @Test public void testRunningMedianOfThree() {
        // Explicit test cases
        List<Integer> a1 = Arrays.asList(16, 42, 99, 17, 2);
        List<Integer> b1 = Arrays.asList(16, 42, 42, 42, 17);
        assertEquals(b1, P2J4.runningMedianOfThree(a1));
        
        List<Integer> a2 = Arrays.asList(99, -10, 0, -5, -8, 999);
        List<Integer> b2 = Arrays.asList(99, -10, 0, -5, -5, -5);
        assertEquals(b2, P2J4.runningMedianOfThree(a2));
        
        List<Integer> a3 = Collections.singletonList(13);
        List<Integer> b3 = Collections.singletonList(13);
        assertEquals(b3, P2J4.runningMedianOfThree(a3));
        
        List<Integer> a4 = Arrays.asList(13, 98);
        List<Integer> b4 = Arrays.asList(13, 98);
        assertEquals(b4, P2J4.runningMedianOfThree(a4));
        
        List<Integer> a5 = Arrays.asList(777, 666, 555, 444, 333, 222, 111);
        List<Integer> b5 = Arrays.asList(777, 666, 666, 555, 444, 333, 222);
        assertEquals(b5, P2J4.runningMedianOfThree(a5));

        List<Integer> a6 = Arrays.asList(17, 99, 42);
        List<Integer> b6 = Arrays.asList(17, 99, 42);
        assertEquals(b6, P2J4.runningMedianOfThree(a6));

        List<Integer> a7 = new ArrayList<>();
        List<Integer> b7 = new ArrayList<>();
        assertEquals(b7, P2J4.runningMedianOfThree(a7));
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        List<Integer> items = new ArrayList<>();
        for(int i = 0; i < 4000; i++) {
            items.clear();
            for(int j = 0; j < i; j++) { items.add(rng.nextInt(100000)); }
            List<Integer> ans = P2J4.runningMedianOfThree(items);
            check.update(ans.size());
            for(int e: ans) { check.update(e); }
        }
        assertEquals(4053632720L, check.getValue());
    }
    
    @Test public void testSortByElementFrequency() {
        // Explicit test cases
        List<Integer> a1 = Arrays.asList(42, 42, 17, 42, 42, 17, 5, 5);
        List<Integer> b1 = Arrays.asList(42, 42, 42, 42, 5, 5, 17, 17);
        P2J4.sortByElementFrequency(a1);
        assertEquals(b1, a1);
        
        List<Integer> a2 = Arrays.asList(6, 3, 6, 3, 6, 3, 6, 3, 6);
        List<Integer> b2 = Arrays.asList(6, 6, 6, 6, 6, 3, 3, 3, 3);
        P2J4.sortByElementFrequency(a2);
        assertEquals(b2, a2);
        
        List<Integer> a3 = Arrays.asList(42, 17, 99, -10, 5);
        List<Integer> b3 = Arrays.asList(-10, 5, 17, 42, 99);
        P2J4.sortByElementFrequency(a3);
        assertEquals(b3, a3);
        
        List<Integer> a4 = Arrays.asList(101, 101, 101, 101, 101, 101, 101, 101, 101);
        List<Integer> b4 = Arrays.asList(101, 101, 101, 101, 101, 101, 101, 101, 101);
        P2J4.sortByElementFrequency(a4);
        assertEquals(b4, a4);
        
        List<Integer> a5 = Arrays.asList(67, 4, 101, 67, 67, 67, 4, 4, 4, 4, 101, 4);
        List<Integer> b5 = Arrays.asList(4, 4, 4, 4, 4, 4, 67, 67, 67, 67, 101, 101);
        P2J4.sortByElementFrequency(a5);
        assertEquals(b5, a5);

        // Let's see if you do the element comparisons correctly without overflows.
        int v1 = Integer.MAX_VALUE;
        int v2 = Integer.MIN_VALUE;
        int v3 = Integer.MAX_VALUE - 1;
        int v4 = Integer.MIN_VALUE + 1;
        List<Integer> a6 = Arrays.asList(v1, v2, v3, v4, v4, v3, v2, v1);
        List<Integer> b6 = Arrays.asList(v2, v2, v4, v4, v3, v3, v1, v1);
        P2J4.sortByElementFrequency(a6);
        assertEquals(a6, b6);

        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        List<Integer> items = new ArrayList<>();
        for(int i = 0; i < 100; i++) {
            items.clear();
            for(int j = 0; j < i; j++) {
                int rep = rng.nextInt(100);
                int e = rng.nextInt(2_000_000_000) - 1_000_000_000;
                for(int k = 0; k < rep; k++) { items.add(e); }
            }
            Collections.shuffle(items, rng);
            P2J4.sortByElementFrequency(items);
            for(int e: items) { check.update(e); }
        }
        assertEquals(981235996L, check.getValue());
    }
    
    @Test public void testFactorFactorial() {
        // Expected answers for factorials from 0 to 9.
        List<List<Integer>> expected = Arrays.asList(
                Collections.emptyList(), // 0!
                Collections.emptyList(), // 1!
                Collections.singletonList(2), // 2!
                Arrays.asList(2, 3), // 3!
                Arrays.asList(2, 2, 2, 3), // 4!
                Arrays.asList(2, 2, 2, 3, 5), // 5!
                Arrays.asList(2, 2, 2, 2, 3, 3, 5), // 6!
                Arrays.asList(2, 2, 2, 2, 3, 3, 5, 7), // 7!
                Arrays.asList(2, 2, 2, 2, 2, 2, 2, 3, 3, 5, 7), // 8!
                Arrays.asList(2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 5, 7) // 9!
        );
        
        CRC32 check = new CRC32();
        for(int n = 0; n < 1000; n++) {
            List<Integer> ans = P2J4.factorFactorial(n);
            check.update(ans.size());
            if(n < expected.size()) {
                assertEquals(expected.get(n), ans);
            }
            for(int e: ans) { check.update(e); }
        }
        assertEquals(775274151L, check.getValue());
    }
}
