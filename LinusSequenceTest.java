import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LinusSequenceTest {

    // Helper: convert "12" string to boolean array (1=false, 2=true)
    private static boolean[] s2b(String s) {
        boolean[] bits = new boolean[s.length()];
        for (int i = 0; i < s.length(); i++) {
            bits[i] = s.charAt(i) == '2';
        }
        return bits;
    }

    private static String toStr(boolean[] seq) {
        StringBuilder result = new StringBuilder();
        for (boolean b : seq) {
            result.append(b ? '2' : '1');
        }
        return result.toString();
    }

    // --- maximalRepeatedSuffix explicit tests ---

    @Test public void testMRSMinimalCases() {
        // n=1: single element, no room for repeated suffix
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("2"), 1));
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("1"), 1));
    }

    @Test public void testMRSSizeTwo() {
        // "11": suffix "1" appears twice -> MRS = 1
        assertEquals(1, LinusSequence.maximalRepeatedSuffix(s2b("11"), 2));
        // "22": same thing -> MRS = 1
        assertEquals(1, LinusSequence.maximalRepeatedSuffix(s2b("22"), 2));
        // "12": no repeated suffix -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("12"), 2));
        // "21": no repeated suffix -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("21"), 2));
    }

    @Test public void testMRSSizeThree() {
        // "121": no β works -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("121"), 3));
        // "122": β="2" (last two chars are "22") -> 1
        assertEquals(1, LinusSequence.maximalRepeatedSuffix(s2b("122"), 3));
        // "112": no repeated suffix -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("112"), 3));
        // "111": β="1" (last two are "11") -> 1
        assertEquals(1, LinusSequence.maximalRepeatedSuffix(s2b("111"), 3));
    }

    @Test public void testMRSSizeFour() {
        // "1212": β="12" (full half repeat) -> 2
        assertEquals(2, LinusSequence.maximalRepeatedSuffix(s2b("1212"), 4));
        // "2222": β="22" -> 2
        assertEquals(2, LinusSequence.maximalRepeatedSuffix(s2b("2222"), 4));
        // "1122": β="2" (last two are "22") -> 1
        assertEquals(1, LinusSequence.maximalRepeatedSuffix(s2b("1122"), 4));
        // "2112": no repeated suffix -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(s2b("2112"), 4));
    }

    @Test public void testMRSFullHalfRepeat() {
        // "121121": αββ where β="121" -> 3
        assertEquals(3, LinusSequence.maximalRepeatedSuffix(s2b("121121"), 6));
        // "211211": β="211" -> 3
        assertEquals(3, LinusSequence.maximalRepeatedSuffix(s2b("211211"), 6));
        // "12211221": β="1221" (full half) -> 4
        assertEquals(4, LinusSequence.maximalRepeatedSuffix(s2b("12211221"), 8));
    }

    @Test public void testMRSUsesNNotFullArray() {
        // The n parameter should restrict attention to first n elements.
        boolean[] bits = s2b("12211221");
        // n=4: look at "1221" -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(bits, 4));
        // n=6: look at "122112" -> 0
        assertEquals(0, LinusSequence.maximalRepeatedSuffix(bits, 6));
        // n=8: look at "12211221" -> 4
        assertEquals(4, LinusSequence.maximalRepeatedSuffix(bits, 8));
    }

    @Test public void testMRSOnLinusPrefixes() {
        // Verify MRS on successive prefixes of the Linus sequence itself.
        boolean[] linus = LinusSequence.linusSequence(20);
        // These values come from the Linus construction: each prefix has a known MRS.
        int[] expected = {
                // n=1..20:
                0, 0, 0, 1, 0, 1, 0, 0, 2, 1,
                0, 3, 2, 1, 0, 1, 0, 0, 3, 1
        };
        for (int n = 1; n <= 20; n++) {
            assertEquals("MRS of linus[:" + n + "]",
                    expected[n - 1], LinusSequence.maximalRepeatedSuffix(linus, n));
        }
    }

    // --- maximalRepeatedSuffix CRC mass test ---

    @Test public void testMaximalRepeatedSuffixMass() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        for(int i = 0; i < 500; i++) {
            int nn = i + 5;
            boolean[] bits = new boolean[nn];
            for(int j = 0; j <= i; j++) {
                int p = rng.nextInt(nn / 2);
                int q = nn - 2 * p;
                for(int k = 0; k < q; k++) {
                    bits[k] = rng.nextBoolean();
                }
                for(int k = 0; k < p; k++) {
                    boolean b = rng.nextBoolean();
                    bits[q + k] = bits[q + p + k] = b;
                }
                int result = LinusSequence.maximalRepeatedSuffix(bits, nn);
                check.update(result);
            }
        }
        assertEquals(2544580030L, check.getValue());
    }

    // --- linusSequence explicit tests ---

    @Test public void testLinusSequenceFirstElements() {
        // n=1: just "2"
        assertEquals("2", toStr(LinusSequence.linusSequence(1)));
        // n=2: "21"
        assertEquals("21", toStr(LinusSequence.linusSequence(2)));
        // n=5
        assertEquals("21221", toStr(LinusSequence.linusSequence(5)));
        // n=10
        assertEquals("2122112122", toStr(LinusSequence.linusSequence(10)));
        // n=20
        assertEquals("21221121221211221222", toStr(LinusSequence.linusSequence(20)));
    }

    @Test public void testLinusSequenceStartsWithTwo() {
        // First element must be true (= '2')
        boolean[] seq = LinusSequence.linusSequence(1);
        assertTrue("First element of Linus sequence must be true (= '2')", seq[0]);
    }

    @Test public void testLinusSequenceSecondElement() {
        // Second element should be false (= '1') based on the rule:
        // extending "2" by '1' gives "21" with MRS=0
        // extending "2" by '2' gives "22" with MRS=1
        // '1' gives shorter MRS, so second element is '1' (false).
        boolean[] seq = LinusSequence.linusSequence(2);
        assertEquals("21", toStr(seq));
    }

    @Test public void testLinusSequenceNeverStartsWithRepeat() {
        // The Linus sequence minimizes MRS at each step. Verify that the MRS
        // after each extension is always the minimum of the two choices.
        boolean[] seq = LinusSequence.linusSequence(100);
        for (int i = 2; i < 100; i++) {
            boolean original = seq[i];
            // Compute MRS with chosen value
            int chosen = LinusSequence.maximalRepeatedSuffix(seq, i + 1);
            // Try the other value
            seq[i] = !original;
            int other = LinusSequence.maximalRepeatedSuffix(seq, i + 1);
            // Restore
            seq[i] = original;
            assertTrue("Linus element " + i + " should minimize MRS: chose " + chosen + " vs " + other,
                    chosen <= other);
        }
    }

    @Test public void testLinusSequencePrefix() {
        String expected = // First thousand elements of Linus sequence for the alphabet "12"
                "2122112122121122122211212211222122112122121122122211212211211122121122111211221211212211"
                        + "2111221211221112112221221121221211221222112122112221221121221211221222112111221211221112"
                        + "1122121121221121112212112211121122121122212211212212112212221121221122212211212212112212"
                        + "2211211122121122111211221211212211211122121122111211212211211122121122122211212211222122"
                        + "1121221211221222112122112221221112112212112122112111221211221112112212112122112111221222"
                        + "1121221122212211212212112212221121221122212211212211121122121121221121112212112211121122"
                        + "1211212211211122122211212211222122112122121122122211212211222122121122122211212211211122"
                        + "1211221112112212112122112111221211221112112221221121221211221222112122112221221121221211"
                        + "2212221121112212112211121122121121221121112212112211121122121122212211212212112212221121"
                        + "2211222122112122121122122211211122121122111211221211212211211122121122111211212211211122"
                        + "1211221222112122112221221121221211221222112122112221221112112212112122112111221211221112"
                        + "11221211212211211122122211212211";

        String result = toStr(LinusSequence.linusSequence(1000));
        assertEquals(expected, result);
    }

    @Test public void testLinusSequenceTwentyThousand() {
        CRC32 check = new CRC32();
        boolean[] result = LinusSequence.linusSequence(20000);
        for(int i = 0; i < result.length; i++) {
            check.update(result[i] ? i : -i);
        }
        assertEquals(354076375L, check.getValue());
    }
}