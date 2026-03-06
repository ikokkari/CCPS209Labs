import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class P2J2Test {

    private static final int RUNS = 100000;
    private static final int SEED = 12345;

    // ---------------------------------------------------------------
    // removeDuplicates tests
    // ---------------------------------------------------------------

    @Test public void testRemoveDuplicatesExplicit() {
        // Empty string
        assertEquals("", P2J2.removeDuplicates(""));
        // Single character
        assertEquals("x", P2J2.removeDuplicates("x"));
        // All identical characters
        assertEquals("x", P2J2.removeDuplicates("xxxxxxxxxxxxx"));
        // No consecutive duplicates (already "clean")
        assertEquals("abcdefgh", P2J2.removeDuplicates("abcdefgh"));
        // Alternating characters (no runs)
        assertEquals("ababab", P2J2.removeDuplicates("ababab"));
        // Standard cases from spec
        assertEquals("Kokarinen", P2J2.removeDuplicates("Kokkarinen"));
        assertEquals("abxaxa", P2J2.removeDuplicates("aaaabbxxxxaaxa"));
        // Runs at beginning, middle, and end
        assertEquals("ilka", P2J2.removeDuplicates("ilkka"));
        assertEquals("aba", P2J2.removeDuplicates("aaaaaaaabaaaaaaa"));
        assertEquals("cdcdc", P2J2.removeDuplicates("ccccddccccdcccccc"));
        // Case sensitivity: upper and lower are distinct
        assertEquals("AabBCcdD", P2J2.removeDuplicates("AabBCcdD"));
        assertEquals("AabBCc", P2J2.removeDuplicates("AaabbBBBCc"));
        // Two-character string with and without duplicate
        assertEquals("ab", P2J2.removeDuplicates("ab"));
        assertEquals("a", P2J2.removeDuplicates("aa"));
        // Unicode characters
        assertEquals("\u1234\u5678\u6666", P2J2.removeDuplicates(
                "\u1234\u5678\u5678\u5678\u5678\u5678\u5678\u6666"
        ));
        // Spaces and punctuation
        assertEquals(" helo world! ", P2J2.removeDuplicates("  helo  world!!  "));
    }

    @Test public void testRemoveDuplicatesProperties() {
        // Property: result never has two consecutive equal characters
        Random rng = new Random(42);
        for (int i = 0; i < 1000; i++) {
            int len = rng.nextInt(200);
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < len; j++) {
                sb.append((char) ('a' + rng.nextInt(5)));
            }
            String result = P2J2.removeDuplicates(sb.toString());
            for (int j = 1; j < result.length(); j++) {
                assertTrue("Consecutive duplicates found in result: " + result,
                        result.charAt(j) != result.charAt(j - 1));
            }
            // Property: result length <= original length
            assertTrue(result.length() <= sb.length());
            // Property: already-clean string is idempotent
            assertEquals(result, P2J2.removeDuplicates(result));
        }
    }

    @Test public void testRemoveDuplicatesFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < RUNS; i++) {
            StringBuilder sb = new StringBuilder();
            int len = rng.nextInt(500);
            for (int j = 0; j < len; j++) {
                char c = (char) (1 + rng.nextInt(10000));
                int rep = rng.nextInt(10) + 1;
                for (int k = 0; k < rep; k++) {
                    sb.append(c);
                }
            }
            try {
                check.update(P2J2.removeDuplicates(sb.toString()).getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ignored) { }
        }
        assertEquals(2596651304L, check.getValue());
    }

    // ---------------------------------------------------------------
    // uniqueCharacters tests
    // ---------------------------------------------------------------

    private char randomChar(Random rng) {
        return (char) (rng.nextInt(200) + 97);
    }

    private String buildString(Random rng, int len) {
        StringBuilder sb = new StringBuilder();
        for (int j = 0; j < len; j++) {
            sb.append(randomChar(rng));
        }
        return sb.toString();
    }

    @Test public void testUniqueCharactersExplicit() {
        // Empty string
        assertEquals("", P2J2.uniqueCharacters(""));
        // Single character
        assertEquals("a", P2J2.uniqueCharacters("a"));
        // All same character
        assertEquals("\u4444", P2J2.uniqueCharacters("\u4444\u4444\u4444"));
        // Already all unique
        assertEquals("abcdef", P2J2.uniqueCharacters("abcdef"));
        // Spec examples
        assertEquals("abc", P2J2.uniqueCharacters("aaaaaabaaabbbaaababbbabbcbabababa"));
        assertEquals("Kokarine", P2J2.uniqueCharacters("Kokkarinen"));
        assertEquals("ilka orne", P2J2.uniqueCharacters("ilkka kokkarinen"));
        assertEquals("abxA", P2J2.uniqueCharacters("aaaabbxxAxxaaxa"));
        // Case sensitivity
        assertEquals("aABbcCDd", P2J2.uniqueCharacters("aABbcCDd"));
        assertEquals("aA", P2J2.uniqueCharacters("aAaAaAaA"));
        // Two characters
        assertEquals("ab", P2J2.uniqueCharacters("ab"));
        assertEquals("a", P2J2.uniqueCharacters("aa"));
        assertEquals("ba", P2J2.uniqueCharacters("baaaa"));
        // Spaces and punctuation
        assertEquals("helo wrd!", P2J2.uniqueCharacters("hello world!"));
    }

    @Test public void testUniqueCharactersProperties() {
        // Property: result has all distinct characters
        Random rng = new Random(42);
        for (int i = 0; i < 1000; i++) {
            int len = rng.nextInt(300) + 1;
            String s = buildString(rng, len);
            String result = P2J2.uniqueCharacters(s);
            // All characters in result must be distinct
            HashSet<Character> seen = new HashSet<>();
            for (int j = 0; j < result.length(); j++) {
                assertTrue("Duplicate character in result: " + result.charAt(j),
                        seen.add(result.charAt(j)));
            }
            // Every character in the original must appear in the result
            for (int j = 0; j < s.length(); j++) {
                assertTrue("Missing character: " + s.charAt(j),
                        result.indexOf(s.charAt(j)) >= 0);
            }
            // Result length <= original length
            assertTrue(result.length() <= s.length());
            // Idempotent: applying again gives same result
            assertEquals(result, P2J2.uniqueCharacters(result));
        }
    }

    @Test public void testUniqueCharactersFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < RUNS; i++) {
            int len = rng.nextInt(100) + (2 << rng.nextInt(5));
            String s = buildString(rng, len);
            String res = P2J2.uniqueCharacters(s);
            check.update(res.getBytes());
        }
        assertEquals(3756363171L, check.getValue());
    }

    // ---------------------------------------------------------------
    // countSafeSquaresRooks tests
    // ---------------------------------------------------------------

    @Test public void testCountSafeSquaresRooksExplicit() {
        // 1x1 board with rook: 0 safe squares
        boolean[][] b3 = {{true}};
        assertEquals(0, P2J2.countSafeSquaresRooks(1, b3));

        // 1x1 board, no rook: 1 safe square
        boolean[][] b4 = {{false}};
        assertEquals(1, P2J2.countSafeSquaresRooks(1, b4));

        // 3x3 board, two rooks in same row
        boolean[][] b1 = {
                {true, true, false},
                {false, false, false},
                {false, false, false}
        };
        assertEquals(2, P2J2.countSafeSquaresRooks(3, b1));  // 2 safe rows * 1 safe col

        // 4x4 board, diagonal rooks: all rows and cols unsafe
        boolean[][] b2 = {
                {true, false, false, false},
                {false, true, false, false},
                {false, false, true, false},
                {false, false, false, true}
        };
        assertEquals(0, P2J2.countSafeSquaresRooks(4, b2));

        // 5x5 board, single rook in center
        boolean[][] b5 = {
                {false, false, false, false, false},
                {false, false, false, false, false},
                {false, false, true, false, false},
                {false, false, false, false, false},
                {false, false, false, false, false}
        };
        assertEquals(16, P2J2.countSafeSquaresRooks(5, b5));  // 4 safe rows * 4 safe cols

        // Empty board: all squares safe
        boolean[][] b6 = new boolean[5][5];
        assertEquals(25, P2J2.countSafeSquaresRooks(5, b6));

        // Full board: 0 safe squares
        boolean[][] b7 = new boolean[3][3];
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) b7[i][j] = true;
        assertEquals(0, P2J2.countSafeSquaresRooks(3, b7));

        // Rooks in one row only: that row unsafe, all cols unsafe => 0
        boolean[][] b8 = {
                {true, true, true},
                {false, false, false},
                {false, false, false}
        };
        assertEquals(0, P2J2.countSafeSquaresRooks(3, b8));

        // Rooks in one column only: all rows unsafe via that column
        boolean[][] b9 = {
                {true, false, false},
                {true, false, false},
                {true, false, false}
        };
        assertEquals(0, P2J2.countSafeSquaresRooks(3, b9));

        // Two rooks sharing neither row nor column
        boolean[][] b10 = {
                {true, false, false, false},
                {false, false, false, false},
                {false, false, false, true},
                {false, false, false, false}
        };
        assertEquals(4, P2J2.countSafeSquaresRooks(4, b10));  // 2 safe rows * 2 safe cols

        // 2x2 board, single corner rook
        boolean[][] b11 = {
                {true, false},
                {false, false}
        };
        assertEquals(1, P2J2.countSafeSquaresRooks(2, b11));  // 1 safe row * 1 safe col
    }

    @Test public void testCountSafeSquaresRooksProperties() {
        // Verify the product-of-safe formula: result = safeRows * safeCols
        Random rng = new Random(42);
        for (int trial = 0; trial < 500; trial++) {
            int n = rng.nextInt(20) + 2;
            boolean[][] board = new boolean[n][n];
            int rookCount = rng.nextInt(n * n / 2 + 1);
            for (int r = 0; r < rookCount; r++) {
                board[rng.nextInt(n)][rng.nextInt(n)] = true;
            }
            int result = P2J2.countSafeSquaresRooks(n, board);
            // Cross-validate with reference
            int expected = referenceSafeSquares(n, board);
            assertEquals("Mismatch for n=" + n, expected, result);
            // Result must be non-negative and at most n*n
            assertTrue(result >= 0);
            assertTrue(result <= n * n);
        }
    }

    private static int referenceSafeSquares(int n, boolean[][] rooks) {
        boolean[] unsafeRow = new boolean[n];
        boolean[] unsafeCol = new boolean[n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (rooks[i][j]) {
                    unsafeRow[i] = true;
                    unsafeCol[j] = true;
                }
            }
        }
        int sr = 0, sc = 0;
        for (int i = 0; i < n; i++) {
            if (!unsafeRow[i]) sr++;
            if (!unsafeCol[i]) sc++;
        }
        return sr * sc;
    }

    @Test public void testCountSafeSquaresRooksFuzz() {
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        int total = 0, answer;
        LinkedList<Integer> qxl = new LinkedList<>();
        LinkedList<Integer> qyl = new LinkedList<>();
        for (int n = 3; n < 100; n++) {
            boolean[][] board = new boolean[n][n];
            int count = 1;
            for (int trials = 0; trials < n + 1; trials++) {
                answer = P2J2.countSafeSquaresRooks(n, board);
                total += answer;
                check.update(answer);
                int nx, ny;
                for (int i = 0; i < count; i++) {
                    do {
                        nx = rng.nextInt(n);
                        ny = rng.nextInt(n);
                    } while (board[nx][ny]);
                    board[nx][ny] = true;
                    qxl.add(nx);
                    qyl.add(ny);
                    answer = P2J2.countSafeSquaresRooks(n, board);
                    total += answer;
                    check.update(answer);
                }
                for (int i = 0; i < count - 1; i++) {
                    nx = qxl.removeFirst();
                    ny = qyl.removeFirst();
                    board[nx][ny] = false;
                    answer = P2J2.countSafeSquaresRooks(n, board);
                    total += answer;
                    check.update(answer);
                }
                count++;
            }
        }
        assertEquals(23172158, total);
        assertEquals(3221249387L, check.getValue());
    }

    // ---------------------------------------------------------------
    // recaman tests
    // ---------------------------------------------------------------

    @Test public void testRecamanExplicit() {
        // First several terms of the sequence (OEIS A005132, but 1-indexed here)
        // a(1)=1, a(2)=3, a(3)=6, a(4)=2, a(5)=7, a(6)=13, a(7)=20
        assertEquals(1, P2J2.recaman(1));
        assertEquals(3, P2J2.recaman(2));
        assertEquals(6, P2J2.recaman(3));
        assertEquals(2, P2J2.recaman(4));
        assertEquals(7, P2J2.recaman(5));
        assertEquals(13, P2J2.recaman(6));
        assertEquals(20, P2J2.recaman(7));
        assertEquals(12, P2J2.recaman(8));
        assertEquals(21, P2J2.recaman(9));
        assertEquals(11, P2J2.recaman(10));

        // Larger values from the spec and OEIS
        assertEquals(24, P2J2.recaman(15));
        assertEquals(62, P2J2.recaman(19));
        assertEquals(64, P2J2.recaman(99));
    }

    @Test public void testRecamanKnownValues() {
        // Spot-check larger values from original test
        int[] inputs = {1, 2, 3, 4, 5, 6, 15, 99, 222, 2654, 8732, 14872, 20000,
                76212, 98721, 114322, 158799, 178320, 221099, 317600};
        int[] expected = {1, 3, 6, 2, 7, 13, 24, 64, 47, 5457, 18416, 18382,
                14358, 340956, 298489, 199265, 351688, 183364, 364758, 657230};
        CRC32 check = new CRC32();
        for (int i = 0; i < inputs.length; i++) {
            int rec = P2J2.recaman(inputs[i]);
            assertEquals("recaman(" + inputs[i] + ")", expected[i], rec);
            check.update(rec);
        }
        assertEquals(2348649420L, check.getValue());
    }

    @Test public void testRecamanSequenceConsistency() {
        // Compute a reference sequence and verify each student term matches
        int maxN = 500;
        int[] ref = new int[maxN + 1];
        ref[1] = 1;
        HashSet<Integer> seen = new HashSet<>();
        seen.add(1);
        for (int i = 2; i <= maxN; i++) {
            int back = ref[i - 1] - i;
            if (back > 0 && !seen.contains(back)) {
                ref[i] = back;
            } else {
                ref[i] = ref[i - 1] + i;
            }
            seen.add(ref[i]);
        }
        // Now verify against the student method
        for (int i = 1; i <= maxN; i++) {
            assertEquals("recaman(" + i + ")", ref[i], P2J2.recaman(i));
        }
    }

    @Test public void testRecamanFuzz() {
        // Fuzz test with CRC over a range of n values
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for (int i = 0; i < 200; i++) {
            int n = rng.nextInt(50000) + 1;
            int rec = P2J2.recaman(n);
            assertTrue("recaman(" + n + ") must be positive", rec > 0);
            check.update(rec);
        }
        assertEquals(447416781L, check.getValue());
    }
}