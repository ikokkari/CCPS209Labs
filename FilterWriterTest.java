import org.junit.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Scanner;
import java.util.function.BiPredicate;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class FilterWriterTest {

    // Unicode blocks for combining characters.
    private static final int[][] combining = {
        {0x0300, 0x036f}, {0x1ab0, 0x1aff}, {0x1dc0, 0x1dff},
        {0x20d0, 0x20ff}, {0xfe20, 0xfe2f}
    };
    
    private static final String vowels = "AEIOUYaeiouy";
    private static final String consonants = "BCDFGHJKLMNPQRSTVWXZbcdfghjklmnpqrstvwxz";
    
    
    private static final BiPredicate<Character, Character> mustSwitch = (c1, c2) -> {
        boolean c1vowel = vowels.indexOf(c1) > -1;
        boolean c2vowel = vowels.indexOf(c2) > -1;
        boolean c1consonant = consonants.indexOf(c1) > -1;
        boolean c2consonant = consonants.indexOf(c2) > -1;
        return !(c1vowel && c2vowel || c1consonant && c2consonant);
    };
    
    @Test public void testMustSwitch() throws IOException {
        CRC32 check = new CRC32();
        Scanner sc = new Scanner(new FileReader("warandpeace.txt"));
        StringWriter sw = new StringWriter();
        FilterWriter fw = new FilterWriter(sw, mustSwitch, '$');
        int count = 1000;
        while(sc.hasNextLine() && count > 0) {
            String line = sc.nextLine();
            if(line.length() < 10) { continue; }
            count--;
            fw.write(line);
            fw.write("\n");
        }
        sc.close();
        String result = sw.toString();
        try {
            check.update(result.getBytes("UTF-8"));
        } catch(UnsupportedEncodingException ignored) {}
        assertEquals(3533446551L, check.getValue());
    }
    
    // Predicate that rejects all Unicode combining characters.
    private static final BiPredicate<Character, Character> dezalgo = (c1, c2) -> {
        for(int[] tabu: combining) {
            if(tabu[0] <= c2 && c2 <= tabu[1]) { return false; }
        }
        return true;
    };    
    
    @Test public void testDezalgo() throws IOException {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        Scanner sc = new Scanner(new FileReader("warandpeace.txt"));
        StringWriter sw = new StringWriter();
        FilterWriter fw = new FilterWriter(sw, dezalgo, '$');
        int count = 1000;
        while(sc.hasNextLine() && count > 0) {
            String line = sc.nextLine();
            if(line.length() < 10) { continue; }
            count--;
            StringBuilder sb = new StringBuilder();
            for(int j = 0; j < line.length(); j++) {
                sb.append(line.charAt(j));
                int n = rng.nextInt(5);
                for(int k = 0; k < n; k++) {
                    int[] block = combining[rng.nextInt(combining.length)];
                    sb.append((char)(rng.nextInt(block[1] - block[0]) + block[0]));
                }
            }
            String ss = sb.toString();
            fw.write(ss);
            fw.write("\n");
        }
        sc.close();
        String result = sw.toString();
        try {
            check.update(result.getBytes("UTF-8"));
        } catch(UnsupportedEncodingException ignored) {}
        assertEquals(1191567916L, check.getValue());
    }    
}