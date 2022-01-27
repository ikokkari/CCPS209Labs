import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class P2J7Test {

    private static final int SEED = 76543;

    @Test public void testHuntingtonHill() {
        // Explicit test cases
        
        int[] a1 = {42};
        int[] b1 = {5};
        assertArrayEquals(b1, P2J7.huntingtonHill(a1, 5));
        
        int[] a2 = {3, 4};
        int[] b2 = {2, 2};
        assertArrayEquals(b2, P2J7.huntingtonHill(a2, 4));
        
        int[] a3 = {18, 17};
        int[] b3 = {4, 3};
        assertArrayEquals(b3, P2J7.huntingtonHill(a3, 7));
        
        int[] a4 = {17, 3, 4, 10, 11, 14};
        int[] b4 = {2, 1, 1, 1, 1, 2};
        assertArrayEquals(b4, P2J7.huntingtonHill(a4, 8));
        
        int[] a5 = {13, 15, 20, 33, 45, 55, 60, 82};
        int[] b5 = {1, 1, 2, 3, 3, 4, 5, 6};
        assertArrayEquals(b5, P2J7.huntingtonHill(a5, 25));       
        
        int[] a6 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] b6 = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        assertArrayEquals(b6, P2J7.huntingtonHill(a6, 55));
        
        // Making sure that your code doesn't silently do int overflows.
        int[] a7 = {1000000, 999999, 999998};
        int[] b7 = {34, 33, 33};
        assertArrayEquals(b7, P2J7.huntingtonHill(a7, 100));
        
        int[] a8 = {1000000000, 999999999, 999999998};
        int[] b8 = {334, 333, 333};
        assertArrayEquals(b8, P2J7.huntingtonHill(a8, 1000));       
       
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        HashSet<Integer> seen = new HashSet<>();
        int scale = 1;
        for(int i = 1; i < 400; i++) {
            if(i % 50 == 0) { scale *= 10; }
            int[] pops = new int[i + 1];
            seen.clear();
            for(int j = 0; j < pops.length; j++) {
                int p;
                do {
                    if(j > 0 && rng.nextInt(100) < 20) {
                        p = pops[j-1] + 1;
                    }
                    else {
                        p = (rng.nextInt(50) + 1) * scale;
                        p += rng.nextInt(p);
                    }
                } while(seen.contains(p));
                assert p > 0;
                seen.add(p);
                pops[j] = p;
            }
            int seats = 2 * i + rng.nextInt(10 * i + 2);
            int[] result = P2J7.huntingtonHill(pops, seats);
            try {
                check.update(Arrays.toString(result).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(325067613L, check.getValue());
    }

    @Test public void testJosephus() {
        // Explicit test cases
        List<Integer> a1 = Arrays.asList(42, 99, 17, 5);
        String b1 = "[17, 99, 5, 42]";
        assertEquals(b1, P2J7.josephus(a1, 3).toString());
        
        List<String> a2 = Arrays.asList("joe", "moe", "bob", "rob", "tom");
        String b2 = "[moe, rob, joe, tom, bob]";
        assertEquals(b2, P2J7.josephus(a2, 2).toString());
        
        List<Double> a3 = Collections.singletonList(123.456);
        String b3 = "[123.456]";
        assertEquals(b3, P2J7.josephus(a3, 99).toString());
        
        List<Character> a4 = Arrays.asList(
        '\u047C', '\u042b', '\u0413', '\u042f', '\u04cb', '\u0410', '\u0415'
        );
        String b4 = "[Ӌ, Г, Ы, Я, Е, Ѽ, А]";
        assertEquals(b4, P2J7.josephus(a4, 5).toString());
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        List<String> people = Arrays.asList(
                "bob", "ted", "alice", "ringo",
                "ross", "rachel", "joey",
                "phoebe", "chandler", "monica",
                "charlie", "alan", "walden"
            );
        List<String> items = new ArrayList<>();
        for(int i = 0; i < 1000; i++) {
            String next = people.get(i % people.size());
            if(i >= people.size()) { next += i; }
            items.add(next);
            int k = rng.nextInt(2 * i + 2) + 1;
            items = P2J7.josephus(items, k);
            try {
                check.update(items.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(3746131365L, check.getValue());
    }
}