import org.junit.Test;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class P2J11Test {

    // ---------------------------------------------------------------
    // buildSuffixArray tests
    // ---------------------------------------------------------------

    @Test public void testBuildSuffixArrayExplicit() {
        // Single character
        assertEquals(Arrays.asList(0), P2J11.buildSuffixArray("a"));

        // Two characters
        assertEquals(Arrays.asList(0, 1), P2J11.buildSuffixArray("ab"));
        assertEquals(Arrays.asList(1, 0), P2J11.buildSuffixArray("ba"));

        // Spec example
        assertEquals(Arrays.asList(1, 0, 2, 3, 4), P2J11.buildSuffixArray("hello"));

        // All same characters: suffixes sorted longest-last to shortest-first
        assertEquals(Arrays.asList(7, 6, 5, 4, 3, 2, 1, 0), P2J11.buildSuffixArray("aaaaaaaa"));

        // Unicode ordering: space < uppercase < lowercase
        assertEquals(Arrays.asList(11, 7, 12, 0, 6, 17, 13, 5, 1, 3, 8, 18, 16, 2, 4, 19, 14, 9, 10, 15),
                P2J11.buildSuffixArray("Delenda est Carthago"));

        assertEquals(Arrays.asList(6, 28, 21, 17, 3, 24, 11, 10, 33, 0, 7, 15, 29, 27, 20, 2, 9,
                        31, 16, 26, 13, 14, 22, 19, 1, 8, 30, 18, 4, 5, 32, 23, 25, 12),
                P2J11.buildSuffixArray("Joe or Moe, which one is the doer?"));

        // "banana" - classic suffix array example
        // Suffixes: banana, anana, nana, ana, na, a
        // Sorted:   a(5), ana(3), anana(1), banana(0), na(4), nana(2)
        assertEquals(Arrays.asList(5, 3, 1, 0, 4, 2), P2J11.buildSuffixArray("banana"));
    }

    @Test public void testBuildSuffixArrayProperties() {
        // For random strings, verify:
        // 1) Result is a permutation of 0..n-1
        // 2) Suffixes are in lexicographic order
        Random rng = new Random(42);
        String chars = "abcdefgh ";
        for (int trial = 0; trial < 200; trial++) {
            int len = rng.nextInt(50) + 1;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) sb.append(chars.charAt(rng.nextInt(chars.length())));
            String text = sb.toString();
            List<Integer> sa = P2J11.buildSuffixArray(text);

            // Correct size
            assertEquals(len, sa.size());

            // Is a permutation
            HashSet<Integer> seen = new HashSet<>(sa);
            assertEquals("Not a permutation", len, seen.size());
            for (int idx : sa) {
                assertTrue(idx >= 0 && idx < len);
            }

            // Suffixes in lexicographic order
            for (int i = 1; i < sa.size(); i++) {
                String prev = text.substring(sa.get(i - 1));
                String curr = text.substring(sa.get(i));
                assertTrue("Suffix order wrong: '" + prev + "' vs '" + curr + "'",
                        prev.compareTo(curr) < 0);
            }
        }
    }

    // ---------------------------------------------------------------
    // find tests
    // ---------------------------------------------------------------

    @Test public void testFindExplicit() {
        String t1 = "hello";
        List<Integer> s1 = P2J11.buildSuffixArray(t1);
        assertEquals(Arrays.asList(2, 3), P2J11.find("l", t1, s1));
        assertEquals(Arrays.asList(2), P2J11.find("ll", t1, s1));
        assertEquals(Arrays.asList(0), P2J11.find("hello", t1, s1));
        assertEquals(Collections.emptyList(), P2J11.find("xyz", t1, s1));
        assertEquals(Arrays.asList(1), P2J11.find("ello", t1, s1));

        String t2 = "aaaaaaaa";
        List<Integer> s2 = P2J11.buildSuffixArray(t2);
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5), P2J11.find("aaa", t2, s2));
        assertEquals(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7), P2J11.find("a", t2, s2));

        String t3 = "Delenda est Carthago";
        List<Integer> s3 = P2J11.buildSuffixArray(t3);
        assertEquals(Collections.emptyList(), P2J11.find("ea", t3, s3));

        String t4 = "Joe or Moe, which one is the doer?";
        List<Integer> s4 = P2J11.buildSuffixArray(t4);
        assertEquals(Arrays.asList(1, 8, 30), P2J11.find("oe", t4, s4));

        // Pattern at very start and very end
        assertEquals(Arrays.asList(0), P2J11.find("Joe", t4, s4));
        assertEquals(Arrays.asList(33), P2J11.find("?", t4, s4));
    }

    @Test public void testFindProperties() {
        // For random texts and patterns:
        // 1) Every returned position actually contains the pattern
        // 2) No missed occurrences (brute-force verify)
        // 3) Positions are sorted ascending
        Random rng = new Random(99);
        String chars = "abcd";
        for (int trial = 0; trial < 200; trial++) {
            int len = rng.nextInt(100) + 5;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) sb.append(chars.charAt(rng.nextInt(chars.length())));
            String text = sb.toString();
            List<Integer> sa = P2J11.buildSuffixArray(text);

            // Pick a random substring as pattern
            int pstart = rng.nextInt(len - 1);
            int plen = rng.nextInt(Math.min(5, len - pstart)) + 1;
            String pattern = text.substring(pstart, pstart + plen);

            List<Integer> found = P2J11.find(pattern, text, sa);

            // All found positions contain the pattern
            for (int pos : found) {
                assertTrue("Pattern not at position " + pos,
                        text.substring(pos).startsWith(pattern));
            }

            // Sorted ascending
            for (int i = 1; i < found.size(); i++) {
                assertTrue("Not sorted", found.get(i) > found.get(i - 1));
            }

            // Brute-force: find all occurrences
            List<Integer> expected = new ArrayList<>();
            int idx = text.indexOf(pattern);
            while (idx >= 0) {
                expected.add(idx);
                idx = text.indexOf(pattern, idx + 1);
            }
            assertEquals("Missed or extra occurrences for pattern '" + pattern + "'",
                    expected, found);
        }
    }

    @Test public void testFindNotFound() {
        // Pattern that doesn't exist
        String text = "abcdefghij";
        List<Integer> sa = P2J11.buildSuffixArray(text);
        assertEquals(Collections.emptyList(), P2J11.find("xyz", text, sa));
        assertEquals(Collections.emptyList(), P2J11.find("zz", text, sa));
        // Pattern longer than text
        assertEquals(Collections.emptyList(), P2J11.find("abcdefghijk", text, sa));
    }

    // ---------------------------------------------------------------
    // War and Peace integration test (from original)
    // ---------------------------------------------------------------

    @Test public void testUsingWarAndPeaceAsData() {
        StringBuilder sb = new StringBuilder();
        try (Scanner scan = new Scanner(new File("warandpeace.txt"))) {
            while (scan.hasNext()) {
                String line = scan.next();
                sb.append(line.toLowerCase());
                sb.append(" ");
            }
        } catch (Exception e) {
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
                Collections.emptyList(),
                Arrays.asList(401455, 589251, 673175, 756728, 762806, 824448, 824892,
                        876326, 1874525, 2097431, 2824422, 2824638, 3001502, 3069811, 3070789),
                Arrays.asList(1000200, 1322792, 1323345, 1709728, 1858789, 2112805),
                Arrays.asList(149169, 1625813, 2387228, 2621602),
                Arrays.asList(147783, 546991, 772564, 1954975, 2370800, 2534921,
                        2667437, 2751268, 3010037, 3010169),
                Collections.singletonList(149207)
        );
        for (int i = 0; i < pats.length; i++) {
            String pat = pats[i];
            List<Integer> expect = expected.get(i);
            List<Integer> find = P2J11.find(pat, text, suffix);
            for (int pos : find) {
                assertTrue(text.substring(pos).startsWith(pat));
            }
            assertEquals(expect, find);
        }

        CRC32 check = new CRC32();
        for (int i = 0; i < text.length(); i++) {
            check.update(text.charAt(i));
        }

        Random rng = new Random(12345);
        for (int i = 0; i < 100; i++) {
            int pos = rng.nextInt(1000000);
            int len = rng.nextInt(20) + 5;
            String pat = text.substring(pos, pos + len);
            List<Integer> find = P2J11.find(pat, text, suffix);
            for (int j : find) { check.update(j); }
        }
        assertEquals(3893756230L, check.getValue());
    }
}