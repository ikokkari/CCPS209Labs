import java.util.Random;
import org.junit.Test;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J13Test {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    // --- countDistinctSubstrings explicit tests ---

    @Test public void testCountDistinctSubstringsExplicit() {
        assertEquals(1, P2J13.countDistinctSubstrings("Z"));
        assertEquals(3, P2J13.countDistinctSubstrings("aaa"));
        assertEquals(15, P2J13.countDistinctSubstrings("banana"));
        assertEquals(19, P2J13.countDistinctSubstrings("uuuXuuu"));
        assertEquals(300, P2J13.countDistinctSubstrings("baabaaboobaabaabaaboobaabaabaaboo"));
    }

    @Test public void testCountDistinctSubstringsEmpty() {
        assertEquals(0, P2J13.countDistinctSubstrings(""));
    }

    @Test public void testCountDistinctSubstringsTwoChars() {
        // "ab": substrings are "a", "b", "ab" = 3
        assertEquals(3, P2J13.countDistinctSubstrings("ab"));
        // "aa": substrings are "a", "aa" = 2
        assertEquals(2, P2J13.countDistinctSubstrings("aa"));
    }

    @Test public void testCountDistinctSubstringsAllDistinct() {
        // All characters different: n*(n+1)/2 substrings
        // "abc": 3+2+1 = 6
        assertEquals(6, P2J13.countDistinctSubstrings("abc"));
        // "abcde": 5+4+3+2+1 = 15
        assertEquals(15, P2J13.countDistinctSubstrings("abcde"));
    }

    @Test public void testCountDistinctSubstringsAllSame() {
        // "aaaa": only 4 substrings (a, aa, aaa, aaaa)
        assertEquals(4, P2J13.countDistinctSubstrings("aaaa"));
    }

    @Test public void testCountDistinctSubstringsWithOverlap() {
        // "abab": 7 distinct substrings
        assertEquals(7, P2J13.countDistinctSubstrings("abab"));
        // "aab": 5 (a, aa, aab, ab, b)
        assertEquals(5, P2J13.countDistinctSubstrings("aab"));
        // "abba": 8 (a, b, ab, bb, ba, abb, bba, abba)
        assertEquals(8, P2J13.countDistinctSubstrings("abba"));
        // "abcabc": many overlapping substrings
        assertEquals(15, P2J13.countDistinctSubstrings("abcabc"));
    }

    @Test public void testCountDistinctSubstringDontBeShlemiel() {
        StringBuilder text = new StringBuilder();
        for(int i = 0; i < 1000; i++) {
            text.append('$');
        }
        for(int i = 1000; i < 2000; i++) {
            assertEquals(i, P2J13.countDistinctSubstrings(text.toString()));
            text.append('$');
        }
    }

    // --- reverseSubstringsBetweenParentheses explicit tests ---

    @Test public void reverseSubstringsBetweenParenthesesExplicit() {
        assertEquals("Z", P2J13.reverseSubstringsBetweenParentheses("Z"));
        assertEquals("Z", P2J13.reverseSubstringsBetweenParentheses("(Z)"));
        assertEquals("YXXY", P2J13.reverseSubstringsBetweenParentheses("((XY)(YX))"));
        assertEquals("abcd", P2J13.reverseSubstringsBetweenParentheses("abcd"));
        assertEquals("acbd", P2J13.reverseSubstringsBetweenParentheses("a(bc)d"));
        assertEquals("dbca", P2J13.reverseSubstringsBetweenParentheses("(a(bc)d)"));
        assertEquals("XYZ", P2J13.reverseSubstringsBetweenParentheses("()X(())Y((()()))Z(())"));
        assertEquals("", P2J13.reverseSubstringsBetweenParentheses("(()(()())())"));
        assertEquals("ebcda", P2J13.reverseSubstringsBetweenParentheses("(((((a(b(c)d)e)))))"));
    }

    @Test public void testReverseEmpty() {
        assertEquals("", P2J13.reverseSubstringsBetweenParentheses(""));
    }

    @Test public void testReverseNoParentheses() {
        // No parentheses: string unchanged
        assertEquals("hello", P2J13.reverseSubstringsBetweenParentheses("hello"));
    }

    @Test public void testReverseEmptyParentheses() {
        // Empty parens produce nothing
        assertEquals("", P2J13.reverseSubstringsBetweenParentheses("()"));
        assertEquals("ab", P2J13.reverseSubstringsBetweenParentheses("a()b"));
    }

    @Test public void testReverseSingleCharInParens() {
        // Single char reversed is itself
        assertEquals("abc", P2J13.reverseSubstringsBetweenParentheses("a(b)c"));
    }

    @Test public void testReverseSimple() {
        // "(abc)" -> "cba"
        assertEquals("cba", P2J13.reverseSubstringsBetweenParentheses("(abc)"));
    }

    @Test public void testReverseMultipleSeparate() {
        // Multiple non-nested groups
        assertEquals("abc", P2J13.reverseSubstringsBetweenParentheses("(a)(b)(c)"));
        assertEquals("dcfe", P2J13.reverseSubstringsBetweenParentheses("(cd)(ef)"));
    }

    @Test public void testReverseDoubleNested() {
        // Double nesting reverses twice = original order
        assertEquals("abc", P2J13.reverseSubstringsBetweenParentheses("((abc))"));
    }

    @Test public void testReverseTripleNested() {
        // Triple nesting reverses three times = reversed
        assertEquals("cba", P2J13.reverseSubstringsBetweenParentheses("(((abc)))"));
    }

    @Test public void testReverseDeepNested() {
        // "x(ab(cd)ef)y": inner cd->dc giving abdcef, then reverse all: fecdba, prefix x suffix y
        assertEquals("xfecdbay", P2J13.reverseSubstringsBetweenParentheses("x(ab(cd)ef)y"));
    }

    @Test public void testReverseComplexNested() {
        // "a(b(c(d)e)f)g": innermost (d)->d, giving bcdef, then (cde)->edc, giving bedc f,
        // then (bedcf)->fcdeb, giving afcdebg
        assertEquals("afcdebg", P2J13.reverseSubstringsBetweenParentheses("a(b(c(d)e)f)g"));
    }

    // --- CRC mass tests ---

    @Test public void testReverseSubstringsBetweenParenthesesHundred() {
        testReverseSubstringsBetweenParentheses(100, 2217732059L);
    }

    @Test public void testReverseSubstringsBetweenParenthesesTenThousand() {
        testReverseSubstringsBetweenParentheses(10_000, 1796567666L);
    }

    @Test public void testReverseSubstringsBetweenParenthesesHundredThousand() {
        testReverseSubstringsBetweenParentheses(100_000, 1760148165L);
    }

    private void testReverseSubstringsBetweenParentheses(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        int count = 0, goal = 2, m = 7;
        for(int i = 0; i < n; i++) {
            StringBuilder text = new StringBuilder();
            int openCount = 0;
            while(text.length() < m) {
                int choice = rng.nextInt(100);
                if(choice < 30) {
                    text.append('('); openCount++;
                }
                else if(openCount > 0 && choice < 60) {
                    text.append(')'); openCount--;
                }
                else {
                    text.append(ALPHABET.charAt(rng.nextInt(ALPHABET.length())));
                }
            }
            while(openCount > 0) {
                text.append(')');
                openCount--;
            }
            String result = P2J13.reverseSubstringsBetweenParentheses(text.toString());
            for(int j = 0; j < result.length(); j++) {
                check.update((int)(result.charAt(j)));
            }
            if(++count == goal) {
                count = 0; goal += 2; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }

    @Test public void testCountDistinctSubstringsHundred() {
        testCountDistinctSubstrings(100, 3997080533L);
    }

    @Test public void testCountDistinctSubstringsThousand() {
        testCountDistinctSubstrings(1000, 1195123337L);
    }

    @Test public void testCountDistinctSubstringsTenThousand() {
        testCountDistinctSubstrings(10000, 2175212746L);
    }

    private void testCountDistinctSubstrings(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        int count = 0, goal = 3, m = 3;
        for(int i = 0; i < n; i++) {
            StringBuilder text = new StringBuilder();
            int a = Math.min(ALPHABET.length(), 1 + i % m);
            while(text.length() < m) {
                if(text.length() > 2 && rng.nextBoolean()) {
                    int s = rng.nextInt(text.length() - 2);
                    int e = s + rng.nextInt(text.length() - s);
                    text.append(text.substring(s, e));
                }
                else {
                    text.append(ALPHABET.charAt(rng.nextInt(a)));
                }
            }
            int result = P2J13.countDistinctSubstrings(text.toString());
            check.update(result);
            if(++count == goal) {
                count = 0; goal += 2; m++;
            }
        }
        assertEquals(expected, check.getValue());
    }
}