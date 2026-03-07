import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.CRC32;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class P2J14Test {

    // ---------------------------------------------------------------
    // distanceFromCharacter tests
    // ---------------------------------------------------------------

    @Test public void testDistanceFromCharacterExplicit() {
        // Original tests
        assertArrayEquals(new int[]{4, 3, 2, 1, 0, 1, 1, 0, 1, 2, 3},
                P2J14.distanceFromCharacter("Hello world", 'o'));
        assertArrayEquals(new int[]{1, 0, 1, 0, 1, 0, 1, 0, 1, 0},
                P2J14.distanceFromCharacter("Bananarama", 'a'));
        assertArrayEquals(new int[]{1, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0},
                P2J14.distanceFromCharacter("Monte Carlo", 'o'));

        // c only at start: distances increase monotonically
        assertArrayEquals(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19},
                P2J14.distanceFromCharacter("Silentium est aureum", 'S'));

        // c only at end
        assertArrayEquals(new int[]{4, 3, 2, 1, 0},
                P2J14.distanceFromCharacter("abcde", 'e'));

        // Single character text
        assertArrayEquals(new int[]{0}, P2J14.distanceFromCharacter("x", 'x'));

        // c everywhere: all zeros
        assertArrayEquals(new int[]{0, 0, 0, 0},
                P2J14.distanceFromCharacter("aaaa", 'a'));

        // Spec example: "hello world" with 'e'
        assertArrayEquals(new int[]{1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9},
                P2J14.distanceFromCharacter("hello world", 'e'));

        // c at both ends
        assertArrayEquals(new int[]{0, 1, 2, 1, 0},
                P2J14.distanceFromCharacter("axxxa", 'a'));
    }

    @Test public void testDistanceFromCharacterProperties() {
        // For random strings:
        // 1) Result length == text length
        // 2) Positions where c occurs have distance 0
        // 3) All distances are non-negative
        // 4) Each distance is correct (brute-force verify nearest c)
        Random rng = new Random(42);
        String chars = "abcdef";
        for (int trial = 0; trial < 300; trial++) {
            int len = rng.nextInt(50) + 1;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) sb.append(chars.charAt(rng.nextInt(chars.length())));
            String text = sb.toString();
            char c = text.charAt(rng.nextInt(len));
            int[] result = P2J14.distanceFromCharacter(text, c);

            assertEquals(len, result.length);
            for (int i = 0; i < len; i++) {
                assertTrue("Distance must be non-negative", result[i] >= 0);
                if (text.charAt(i) == c) {
                    assertEquals("Position of c should have distance 0", 0, result[i]);
                }
                // Brute-force: find nearest c
                int minDist = len + 1;
                for (int j = 0; j < len; j++) {
                    if (text.charAt(j) == c) {
                        minDist = Math.min(minDist, Math.abs(i - j));
                    }
                }
                assertEquals("Distance mismatch at position " + i, minDist, result[i]);
            }
        }
    }

    @Test public void testDistanceFromCharacterUsingWarAndPeaceAsData() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        try (Scanner scan = new Scanner(new File("warandpeace.txt"))) {
            while (scan.hasNext()) {
                String line = scan.next();
                char c = line.charAt(rng.nextInt(line.length()));
                int[] result = P2J14.distanceFromCharacter(line, c);
                for (int e : result) { check.update(e); }
            }
        } catch (IOException e) {
            System.out.println("Unable to read file warandpeace.txt.");
            fail();
        }
        assertEquals(262460440L, check.getValue());
    }

    // ---------------------------------------------------------------
    // pushDominoes tests
    // ---------------------------------------------------------------

    @Test public void testPushDominoesExplicit() {
        // Empty and trivial
        assertEquals("", P2J14.pushDominoes(""));
        assertEquals(".", P2J14.pushDominoes("."));
        assertEquals("R", P2J14.pushDominoes("R"));
        assertEquals("L", P2J14.pushDominoes("L"));

        // Original tests
        assertEquals("LR", P2J14.pushDominoes("LR"));
        assertEquals("RL", P2J14.pushDominoes("RL"));
        assertEquals("...", P2J14.pushDominoes("..."));
        assertEquals("LLL", P2J14.pushDominoes("..L"));
        assertEquals("LRLRLR", P2J14.pushDominoes("LRLRLR"));
        assertEquals("LLRR", P2J14.pushDominoes(".LR."));
        assertEquals("LL.RR.LLRRLL..", P2J14.pushDominoes(".L.R...LR..L.."));

        // Spec examples
        assertEquals("RRRR", P2J14.pushDominoes("R..."));
        assertEquals(".RR.LL", P2J14.pushDominoes(".R...L"));

        // R..L with odd gap: middle stays standing
        assertEquals("R.L", P2J14.pushDominoes("R.L"));
        assertEquals("RR.LL", P2J14.pushDominoes("R...L"));

        // R..L with even gap: they meet
        assertEquals("RRLL", P2J14.pushDominoes("R..L"));
        assertEquals("RRRLLL", P2J14.pushDominoes("R....L"));

        // Multiple R pushes
        assertEquals("RRRRRR", P2J14.pushDominoes("R.R..."));
    }

    @Test public void testPushDominoesProperties() {
        // For random inputs:
        // 1) Result length == input length
        // 2) Result only contains '.', 'L', 'R'
        // 3) Result is a fixed point (applying pushDominoes again gives same result)
        // 4) Any 'L' or 'R' in input stays the same in result
        Random rng = new Random(42);
        for (int trial = 0; trial < 300; trial++) {
            int len = rng.nextInt(30) + 1;
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                int roll = rng.nextInt(100);
                if (roll < 60) sb.append('.');
                else if (roll < 80) sb.append('L');
                else sb.append('R');
            }
            String input = sb.toString();
            String result = P2J14.pushDominoes(input);

            assertEquals("Length mismatch", len, result.length());
            for (int j = 0; j < len; j++) {
                char c = result.charAt(j);
                assertTrue("Invalid char: " + c, c == '.' || c == 'L' || c == 'R');
            }
            // Fixed point: equilibrium
            assertEquals("Not a fixed point", result, P2J14.pushDominoes(result));
            // Original L and R are preserved
            for (int j = 0; j < len; j++) {
                if (input.charAt(j) == 'L' || input.charAt(j) == 'R') {
                    assertEquals("Original L/R changed at " + j,
                            input.charAt(j), result.charAt(j));
                }
            }
        }
    }

    @Test public void testPushDominoesHundred() {
        testPushDominoes(100, 3675808482L);
    }

    @Test public void testPushDominoesTenThousand() {
        testPushDominoes(10_000, 3066361233L);
    }

    private void testPushDominoes(int n, long expected) {
        CRC32 check = new CRC32();
        Random rng = new Random(12345 + n);
        int count = 0, goal = 5, m = 3;
        for (int i = 0; i < n; i++) {
            StringBuilder dominoes = new StringBuilder();
            for (int j = 0; j < m; j++) {
                int roll = rng.nextInt(100);
                if (roll < 60) dominoes.append('.');
                else if (roll < 80) dominoes.append('L');
                else dominoes.append('R');
            }
            String result = P2J14.pushDominoes(dominoes.toString());
            assertEquals(dominoes.length(), result.length());
            for (int j = 0; j < result.length(); j++) {
                check.update((int) result.charAt(j));
            }
            if (++count == goal) {
                count = 0; goal += 2; m += 1;
            }
        }
        assertEquals(expected, check.getValue());
    }
}