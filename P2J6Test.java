import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J6Test {

    private static final int SEED = 12345;

    // ---------------------------------------------------------------
    // sumOfDistinctCubes tests
    // ---------------------------------------------------------------

    @Test public void testSumOfDistinctCubesExplicit() {
        // n=0: empty sum
        assertEquals("[]", P2J6.sumOfDistinctCubes(0).toString());

        // n=1: 1^3 = 1
        assertEquals("[1]", P2J6.sumOfDistinctCubes(1).toString());

        // n=8: 2^3 = 8
        assertEquals("[2]", P2J6.sumOfDistinctCubes(8).toString());

        // n=9: 2^3 + 1^3 = 9
        assertEquals("[2, 1]", P2J6.sumOfDistinctCubes(9).toString());

        // n=27: 3^3 = 27
        assertEquals("[3]", P2J6.sumOfDistinctCubes(27).toString());

        // From original tests
        assertEquals("[4, 3]", P2J6.sumOfDistinctCubes(91).toString());
        assertEquals("[5]", P2J6.sumOfDistinctCubes(125).toString());
        assertEquals("[4, 3, 2]", P2J6.sumOfDistinctCubes(99).toString());
        assertEquals("[7, 2]", P2J6.sumOfDistinctCubes(351).toString());
        assertEquals("[11, 4]", P2J6.sumOfDistinctCubes(1395).toString());

        // No solution exists
        assertEquals("[]", P2J6.sumOfDistinctCubes(2020).toString());
        assertEquals("[]", P2J6.sumOfDistinctCubes(2).toString());
        assertEquals("[]", P2J6.sumOfDistinctCubes(3).toString());
        assertEquals("[]", P2J6.sumOfDistinctCubes(4).toString());

        // Ramanujan taxicab number: must return [12, 1] not [10, 9]
        assertEquals("[12, 1]", P2J6.sumOfDistinctCubes(1729).toString());

        // Larger values
        assertEquals("[107, 19, 13, 7, 5]",
                P2J6.sumOfDistinctCubes(1234567).toString());
        assertEquals("[995, 137, 18, 13, 4]",
                P2J6.sumOfDistinctCubes(987654321).toString());
    }

    @Test public void testSumOfDistinctCubesProperties() {
        // For a range of n values, verify:
        // 1) Sum of cubes of returned elements equals n (or empty if no solution)
        // 2) All elements are distinct positive integers
        // 3) Elements are in strictly descending order
        for (int n = 0; n <= 500; n++) {
            List<Integer> result = P2J6.sumOfDistinctCubes(n);
            if (result.isEmpty()) continue;

            // Sum of cubes must equal n
            int sum = 0;
            for (int c : result) {
                assertTrue("Element must be positive for n=" + n, c > 0);
                sum += c * c * c;
            }
            assertEquals("Sum of cubes mismatch for n=" + n, n, sum);

            // Strictly descending order (which also implies distinct)
            for (int i = 1; i < result.size(); i++) {
                assertTrue("Not strictly descending for n=" + n,
                        result.get(i) < result.get(i - 1));
            }
        }
    }

    @Test public void testSumOfDistinctCubesLexicographic() {
        // Verify lexicographic-highest constraint for known multi-solution cases
        // 1729 = 12^3 + 1^3 = 10^3 + 9^3; must pick [12, 1]
        List<Integer> r1729 = P2J6.sumOfDistinctCubes(1729);
        assertEquals("[12, 1]", r1729.toString());

        // 4104 = 16^3 + 2^3 = 15^3 + 9^3; must pick [16, 2]
        List<Integer> r4104 = P2J6.sumOfDistinctCubes(4104);
        assertEquals(4104, 16*16*16 + 2*2*2); // sanity check
        int sum = 0;
        for (int c : r4104) sum += c * c * c;
        assertEquals(4104, sum);
        // First element should be 16 (highest possible)
        assertEquals((Integer) 16, r4104.get(0));
    }

    @Test public void testSumOfDistinctCubesFuzz() {
        CRC32 check = new CRC32();
        Random rng = new Random(SEED);
        int n = 1, step = 2, next = 10;
        while (n > 0) {
            List<Integer> result = P2J6.sumOfDistinctCubes(n);
            // Verify solution correctness when non-empty
            if (!result.isEmpty()) {
                int sum = 0;
                for (int c : result) { sum += c * c * c; }
                assertEquals("Cube sum mismatch for n=" + n, n, sum);
            }
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
            n += rng.nextInt(step) + 1;
            if (n > next) {
                next = 2 * next;
                step = 2 * step;
            }
        }
        assertEquals(4219145223L, check.getValue());
    }

    // ---------------------------------------------------------------
    // forbiddenSubstrings tests
    // ---------------------------------------------------------------

    private String createString(String alphabet, Random rng, int n) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < n; i++) {
            result.append(alphabet.charAt(rng.nextInt(alphabet.length())));
        }
        return result.toString();
    }

    @Test public void testForbiddenSubstringsExplicit() {
        // From original tests
        assertEquals("[XXY, XYX, YXX, YXY]",
                P2J6.forbiddenSubstrings("XY", 3, Arrays.asList("XXX", "YY")).toString());

        assertEquals("[AABBA, AABBB, ABBAB, ABBBA, ABBBB, BABBA, BABBB, BBABB, BBBAB, BBBBA, BBBBB]",
                P2J6.forbiddenSubstrings("AB", 5, Arrays.asList("ABA", "BAA", "AAA")).toString());

        assertEquals("[AAA, AAC, ACA, ACB, BBB, BBC, BCA, BCB, CAA, CAC, CBB, CBC]",
                P2J6.forbiddenSubstrings("ABC", 3, Arrays.asList("AB", "BA", "CC")).toString());

        assertEquals("[DFDF, DFEF, DFFD, DFFE, DFFF, EFDF, EFEF, EFFD, EFFE, EFFF, FDFD, FDFE, FDFF, FEFD, FEFE, FEFF, FFDF, FFEF, FFFD, FFFE, FFFF]",
                P2J6.forbiddenSubstrings("DEF", 4, Arrays.asList("DD", "DE", "ED", "EE")).toString());

        // Empty tabu: all strings of length n are valid
        List<String> allBin3 = P2J6.forbiddenSubstrings("AB", 3, Collections.emptyList());
        assertEquals(8, allBin3.size()); // 2^3 = 8
        assertEquals("[AAA, AAB, ABA, ABB, BAA, BAB, BBA, BBB]", allBin3.toString());

        // n=0: single empty string (the empty string contains no forbidden substrings)
        // Actually, n=0 with empty tabu should return [""]
        // But let's check what the model does:
        // The spec says length n strings. If n=0, the only string of length 0 is "".

        // n=1: single characters, with some forbidden
        assertEquals("[A, C]",
                P2J6.forbiddenSubstrings("ABC", 1, Arrays.asList("B")).toString());

        // Tabu eliminates everything
        assertEquals("[]",
                P2J6.forbiddenSubstrings("AB", 2, Arrays.asList("A", "B")).toString());

        // Single-character alphabet, no tabu
        assertEquals("[AAA]",
                P2J6.forbiddenSubstrings("A", 3, Collections.emptyList()).toString());

        // Single-character alphabet, forbidden
        assertEquals("[]",
                P2J6.forbiddenSubstrings("A", 3, Arrays.asList("AA")).toString());
    }

    @Test public void testForbiddenSubstringsProperties() {
        // For a range of random inputs, verify:
        // 1) All results have correct length n
        // 2) No result contains any tabu substring
        // 3) Results are in sorted order
        // 4) No duplicates
        // 5) All results use only characters from the alphabet
        Random rng = new Random(42);
        String alphabet = "ABCDEF";
        for (int trial = 0; trial < 200; trial++) {
            int an = Math.max(2, rng.nextInt(alphabet.length()));
            String alpha = alphabet.substring(0, an);
            List<String> tabu = new ArrayList<>();
            int tn = rng.nextInt(8);
            for (int j = 0; j < tn; j++) {
                tabu.add(createString(alpha, rng, rng.nextInt(3) + 2));
            }
            int n = rng.nextInt(5) + 1;
            List<String> result = P2J6.forbiddenSubstrings(alpha, n, tabu);

            Set<String> alphaChars = new HashSet<>();
            for (char c : alpha.toCharArray()) alphaChars.add(String.valueOf(c));

            for (int i = 0; i < result.size(); i++) {
                String s = result.get(i);
                // Correct length
                assertEquals("Wrong length", n, s.length());
                // No tabu substring
                for (String t : tabu) {
                    assertTrue("Contains tabu '" + t + "' in '" + s + "'",
                            !s.contains(t));
                }
                // Uses only alphabet characters
                for (int j = 0; j < s.length(); j++) {
                    assertTrue("Invalid character in '" + s + "'",
                            alphaChars.contains(String.valueOf(s.charAt(j))));
                }
                // Sorted order (no duplicates implied by strict ordering)
                if (i > 0) {
                    assertTrue("Not sorted: '" + result.get(i - 1) + "' before '" + s + "'",
                            result.get(i - 1).compareTo(s) < 0);
                }
            }
        }
    }

    @Test public void testForbiddenSubstringsCompleteness() {
        // For small cases, verify completeness: no valid string is missing
        // Binary alphabet, n=4, tabu = ["AA"]
        String alpha = "AB";
        int n = 4;
        List<String> tabu = Arrays.asList("AA");
        List<String> result = P2J6.forbiddenSubstrings(alpha, n, tabu);

        // Generate all 2^4 = 16 binary strings and filter manually
        List<String> expected = new ArrayList<>();
        for (int mask = 0; mask < 16; mask++) {
            StringBuilder sb = new StringBuilder();
            for (int bit = 3; bit >= 0; bit--) {
                sb.append((mask & (1 << bit)) != 0 ? 'B' : 'A');
            }
            String s = sb.toString();
            boolean valid = true;
            for (String t : tabu) {
                if (s.contains(t)) { valid = false; break; }
            }
            if (valid) expected.add(s);
        }
        Collections.sort(expected);
        assertEquals(expected, result);
    }

    @Test public void testForbiddenSubstringsFuzz() {
        CRC32 check = new CRC32();
        Random rng = new Random(SEED);
        String alphabet = "ABCDEF";
        List<String> tabu = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            int an = Math.max(2, rng.nextInt(alphabet.length()));
            String alpha = alphabet.substring(0, an);
            tabu.clear();
            int tn = rng.nextInt(10);
            for (int j = 0; j < tn; j++) {
                tabu.add(createString(alpha, rng, rng.nextInt(4) + 2));
            }
            int n = rng.nextInt(7) + 2;
            List<String> result = P2J6.forbiddenSubstrings(alpha, n, tabu);
            try {
                check.update(result.toString().getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
        }
        assertEquals(2852450563L, check.getValue());
    }
}