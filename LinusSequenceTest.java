import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class LinusSequenceTest {

    @Test public void testMaximalRepeatedSuffix() {
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

    private static String toStr(boolean[] seq) {
        StringBuilder result = new StringBuilder();
        for (boolean b : seq) {
            result.append(b ? '2' : '1');
        }
        return result.toString();
    }
    
    @Test public void testLinusSequencePrefix() {
        // System.out.println(toStr(LinusSequence.linusSequence(1000)));
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