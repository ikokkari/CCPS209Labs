import java.util.Random;
import org.junit.Test;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J13Test {

    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

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

    @Test public void testCountDistinctSubstringsExplicit() {
        assertEquals(1, P2J13.countDistinctSubstrings("Z"));
        assertEquals(3, P2J13.countDistinctSubstrings("aaa"));
        assertEquals(15, P2J13.countDistinctSubstrings("banana"));
        assertEquals(19, P2J13.countDistinctSubstrings("uuuXuuu"));
        assertEquals(300, P2J13.countDistinctSubstrings("baabaaboobaabaabaaboobaabaabaaboo"));
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