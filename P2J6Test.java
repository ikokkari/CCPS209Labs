import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class P2J6Test {

    private static final int SEED = 12345;
    
    @Test public void testSumOfDistinctCubes() {
        // Explicit test cases
        String b1 = "[4, 3]";
        assertEquals(b1, P2J6.sumOfDistinctCubes(91).toString());
        
        String b2 = "[5]";
        assertEquals(b2, P2J6.sumOfDistinctCubes(125).toString());
        
        String b3 = "[4, 3, 2]";
        assertEquals(b3, P2J6.sumOfDistinctCubes(99).toString());
        
        String b4 = "[7, 2]";
        assertEquals(b4, P2J6.sumOfDistinctCubes(351).toString());
        
        String b5 = "[11, 4]";
        assertEquals(b5, P2J6.sumOfDistinctCubes(1395).toString());
        
        String b6 = "[]";
        assertEquals(b6, P2J6.sumOfDistinctCubes(2020).toString());

        String b7 = "[107, 19, 13, 7, 5]";
        assertEquals(b7, P2J6.sumOfDistinctCubes(1234567).toString());

        String b8 = "[995, 137, 18, 13, 4]";
        assertEquals(b8, P2J6.sumOfDistinctCubes(987654321).toString());

        // Pseudorandom fuzz tester
        CRC32 check = new CRC32();
        Random rng = new Random(SEED);
        int n = 1, step = 2, next = 10;
        while(n > 0) { // Keep going until n overflows and rolls back to negatives
            List<Integer> result = P2J6.sumOfDistinctCubes(n);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            // Increment n with regularly increasing steps
            n += rng.nextInt(step) + 1;
            if(n > next) {
                next = 2 * next;
                step = 2 * step;
            }
        }
        assertEquals(4219145223L, check.getValue());
    }
    
    private String createString(String alphabet, Random rng, int n) {
        StringBuilder result = new StringBuilder();
        for(int i = 0; i < n; i++) {
            result.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        }
        return result.toString();
    }
    
    @Test public void testForbiddenSubstrings() {
        // Explicit test cases
        List<String> tabu1 = Arrays.asList("XXX", "YY");
        String result1 = "[XXY, XYX, YXX, YXY]";
        assertEquals(result1, P2J6.forbiddenSubstrings("XY", 3, tabu1).toString());
        
        List<String> tabu2 = Arrays.asList("ABA", "BAA", "AAA");
        String result2 = "[AABBA, AABBB, ABBAB, ABBBA, ABBBB, BABBA, BABBB, BBABB, BBBAB, BBBBA, BBBBB]";
        assertEquals(result2, P2J6.forbiddenSubstrings("AB", 5, tabu2).toString()); 
        
        List<String> tabu3 = Arrays.asList("AB", "BA", "CC");
        String result3 = "[AAA, AAC, ACA, ACB, BBB, BBC, BCA, BCB, CAA, CAC, CBB, CBC]";
        assertEquals(result3, P2J6.forbiddenSubstrings("ABC", 3, tabu3).toString()); 
        
        List<String> tabu4 = Arrays.asList("DD", "DE", "ED", "EE");
        String result4 = "[DFDF, DFEF, DFFD, DFFE, DFFF, EFDF, EFEF, EFFD, EFFE, EFFF, FDFD, FDFE, FDFF, FEFD, FEFE, FEFF, FFDF, FFEF, FFFD, FFFE, FFFF]";
        assertEquals(result4, P2J6.forbiddenSubstrings("DEF", 4, tabu4).toString());
        
        // Pseudorandom fuzz tester
        CRC32 check = new CRC32();
        Random rng = new Random(SEED);
        String alphabet = "ABCDEF";
        List<String> tabu = new ArrayList<>();
        for(int i = 0; i < 500; i++) {
            int an = Math.max(2, rng.nextInt(alphabet.length()));
            String alpha = alphabet.substring(0, an);
            tabu.clear();
            int tn = rng.nextInt(10);
            for(int j = 0; j < tn; j++) {
                tabu.add(createString(alpha, rng, rng.nextInt(4) + 2));
            }
            int n = rng.nextInt(7) + 2;
            List<String> result = P2J6.forbiddenSubstrings(alpha, n, tabu);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(2852450563L, check.getValue());
    }
}