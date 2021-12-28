import org.junit.Test;
import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class P2J11Test {
    
    @Test public void testBuildSuffixArray() {
        String t1 = "hello";
        List<Integer> s1 = Arrays.asList(1, 0, 2, 3, 4);
        assertEquals(s1, P2J11.buildSuffixArray(t1));
        
        String t2 = "aaaaaaaa";
        List<Integer> s2 = Arrays.asList(7, 6, 5, 4, 3, 2, 1, 0);
        assertEquals(s2, P2J11.buildSuffixArray(t2));
        
        // Note that in Unicode, space < uppercase < lowercase.
        String t3 = "Delenda est Carthago";
        List<Integer> s3 = Arrays.asList(
            11, 7, 12, 0, 6, 17, 13, 5, 1, 3, 8, 18, 16, 2, 4, 19, 14, 9, 10, 15
        );
        assertEquals(s3, P2J11.buildSuffixArray(t3));
        
        String t4 = "Joe or Moe, which one is the doer?";
        List<Integer> s4 = Arrays.asList(
            6, 28, 21, 17, 3, 24, 11, 10, 33, 0, 7, 15, 29, 27, 20, 2, 9,
            31, 16, 26, 13, 14, 22, 19, 1, 8, 30, 18, 4, 5, 32, 23, 25, 12
        );
        assertEquals(s4, P2J11.buildSuffixArray(t4));
        
        List<Integer> f1 = Arrays.asList(2, 3);
        assertEquals(f1, P2J11.find("l", t1, s1));
        
        List<Integer> f2 = Arrays.asList(0, 1, 2, 3, 4, 5);
        assertEquals(f2, P2J11.find("aaa", t2, s2));
        
        List<Integer> f3 = Collections.emptyList();
        assertEquals(f3, P2J11.find("ea", t3, s3));
        
        List<Integer> f4 = Arrays.asList(1, 8, 30);
        assertEquals(f4, P2J11.find("oe", t4, s4));
    }
    
    @Test public void testUsingWarAndPeaceAsData() {
        // Construct a version of War and Peace with everything in lowercase,
        // with newlines converted into single whitespaces.
        StringBuilder sb = new StringBuilder();
        try(Scanner scan = new Scanner(new File("warandpeace.txt"))) {
            while(scan.hasNext()) {
                String line = scan.next();
                sb.append(line.toLowerCase());
                sb.append(" ");
            }
        }
        catch(Exception e) {
            System.out.println("Unable to read file warandpeace.txt.");
            fail();
        }
        
        String text = sb.toString();
        List<Integer> suffix = P2J11.buildSuffixArray(text);
        
        String[] pats = {
            "hairpin", "dearest", "chicken", "germany", "soup",
            "when the butler with a bottle"
        };
        List<List<Integer>> expected = Arrays.asList(
                // hairpin
                Collections.emptyList(),
                // dearest
                Arrays.asList(401455, 589251, 673175, 756728, 762806, 824448, 824892,
                876326, 1874525, 2097431, 2824422, 2824638, 3001502, 3069811, 3070789),
                // chicken
                Arrays.asList(1000200, 1322792, 1323345, 1709728, 1858789, 2112805),
                // germany
                Arrays.asList(149169, 1625813, 2387228, 2621602),
                // soup
                Arrays.asList(147783, 546991, 772564, 1954975, 2370800, 2534921,
                2667437, 2751268, 3010037, 3010169),
                // when the butler with a bottle
                Collections.singletonList(149207)
        );
        for(int i = 0; i < pats.length; i++) {
            String pat = pats[i];
            List<Integer> expect = expected.get(i);
            List<Integer> find = P2J11.find(pat, text, suffix);
            for(int pos: find) {
                assertTrue(text.substring(pos).startsWith(pat));
            }
            assertEquals(expect, find);
        }
        
        CRC32 check = new CRC32();
        for(int i = 0; i < text.length(); i++) {
            check.update(text.charAt(i));
        }
        
        Random rng = new Random(12345);
        for(int i = 0; i < 100; i++) {
            int pos = rng.nextInt(1000000);
            int len = rng.nextInt(20) + 5;
            String pat = text.substring(pos, pos + len);
            List<Integer> find = P2J11.find(pat, text, suffix);
            //System.out.println("<" + pat + ">: " + find.size());
            for(int j: find) { check.update(j); }
        }
        assertEquals(3893756230L, check.getValue());
    }
}