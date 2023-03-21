import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class P2J3Test {

    private static final int SEED = 12345;
    private static final int ROUNDS = 1000;
    
    @Test public void testPancakeScramble() throws IOException {
        // Explicit test cases
        assertEquals("", P2J3.pancakeScramble(""));
        assertEquals("alu", P2J3.pancakeScramble("lua"));
        assertEquals("heya", P2J3.pancakeScramble("yeah"));
        assertEquals("eoeawsm", P2J3.pancakeScramble("awesome"));
        assertEquals("enisrtpocmue cec", P2J3.pancakeScramble("computer science"));
        
        // Testing with War and Peace
        CRC32 check = new CRC32();
        BufferedReader fr = new BufferedReader(
            new InputStreamReader(new FileInputStream("warandpeace.txt"), StandardCharsets.UTF_8)
        );
        String line = fr.readLine();
        while(line != null) {
            String result = P2J3.pancakeScramble(line);
            try {
                check.update(result.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            line = fr.readLine();
        }
        fr.close();
        assertEquals(1606800991L, check.getValue());
    }
        
    @Test public void testReverseVowels() throws IOException {
        // Explicit test cases
        assertEquals("", P2J3.reverseVowels(""));
        assertEquals("e", P2J3.reverseVowels("e"));
        assertEquals("X", P2J3.reverseVowels("X"));
        assertEquals("Au", P2J3.reverseVowels("Ua"));
        assertEquals("eI", P2J3.reverseVowels("iE"));
        assertEquals("cDfghklM", P2J3.reverseVowels("cDfghklM"));
        assertEquals("LOL", P2J3.reverseVowels("LOL"));
        assertEquals("Jova, Pythan, C", P2J3.reverseVowels("Java, Python, C"));
        assertEquals("Wuuleemoolaa", P2J3.reverseVowels("Waaloomeeluu"));
        assertEquals("ThIs As LaKa ElL sIrCiStIc", P2J3.reverseVowels("ThIs Is LiKe AlL sArCaStIc"));
        assertEquals("Ent, uat, boa, oka", P2J3.reverseVowels("Ant, oat, boa, uke"));
        assertEquals("Stix nix hix pix", P2J3.reverseVowels("Stix nix hix pix"));
        assertEquals("UoIeAxxxuOiEa", P2J3.reverseVowels("AeIoUxxxaEiOu"));
        assertEquals("Lettor Y as not i vewel", P2J3.reverseVowels("Letter Y is not a vowel"));
        assertEquals("lewercoselettersanlyhero", P2J3.reverseVowels("lowercaselettersonlyhere"));
        assertEquals("!@#$%^&*(){}:;'[]'", P2J3.reverseVowels("!@#$%^&*(){}:;'[]'"));
        assertEquals("àáâäæãåāèéêëēėęîïíīįìôöòóœøōõûüùúū", P2J3.reverseVowels("àáâäæãåāèéêëēėęîïíīįìôöòóœøōõûüùúū"));

        // Testing with War and Peace
        CRC32 check = new CRC32();
        BufferedReader fr = new BufferedReader(
            new InputStreamReader(new FileInputStream("warandpeace.txt"), StandardCharsets.UTF_8)
        );
        String line = fr.readLine();
        while(line != null) {
            String result = P2J3.reverseVowels(line);
            try {
                check.update(result.getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            line = fr.readLine();
        }
        fr.close();
        assertEquals(3844894811L, check.getValue());        
    }
    
    @Test public void testReverseAscendingSubarrays() {
        // Explicit test cases
        int[] a1 = {42, 99, 17, 0};
        int[] b1 = {99, 42, 17, 0};
        P2J3.reverseAscendingSubarrays(a1);
        assertArrayEquals(b1, a1);
        
        int[] a2 = {4, 18, 99, 67, 71, 72, 100, 42, 66};
        int[] b2 = {99, 18, 4, 100, 72, 71, 67, 66, 42};
        P2J3.reverseAscendingSubarrays(a2);
        assertArrayEquals(b2, a2);
        
        int[] a3 = {-1000, 33, 999999999};
        int[] b3 = {999999999, 33, -1000};
        P2J3.reverseAscendingSubarrays(a3);
        assertArrayEquals(b3, a3);
        
        int[] a4 = {53, -99, -75, -16, -3, -18, -39, 42, 8};
        int[] b4 = {53, -3, -16, -75, -99, -18, 42, -39, 8};
        P2J3.reverseAscendingSubarrays(a4);
        assertArrayEquals(b4, a4);
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();
        for(int i = 0; i < ROUNDS; i++) {
            int len = rng.nextInt(1000);
            int[] a = new int[len];
            for(int j = 0; j < len; j++) {
                a[j] = rng.nextInt(1000000);
            }
            P2J3.reverseAscendingSubarrays(a);
            for(int j = 0; j < len; j++) {
                check.update(a[j]);
            }
        }
        assertEquals(3118921076L, check.getValue());
    }
}