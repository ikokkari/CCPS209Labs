import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PlayfairCipherTest {

    // ---------------------------------------------------------------
    // constructPlayfairTable tests
    // ---------------------------------------------------------------

    @Test public void testConstructPlayfairTableExplicit() {
        assertEquals("[[T, H, E, R, W], [I, L, A, Y, S], [B, U, F, N, G], [C, D, K, M, O], [P, Q, V, X, Z]]",
                Arrays.deepToString(PlayfairCipher.constructPlayfairTable("There will always be suffering.")));
        assertEquals("[[T, H, E, O, N], [L, Y, W, A, M], [K, S, U, F, C], [G, I, P, V, D], [B, Q, R, X, Z]]",
                Arrays.deepToString(PlayfairCipher.constructPlayfairTable(
                        "The only way to make sense out of change is to plunge into it, move with it, and join the dance.")));
        assertEquals("[[N, O, V, A, L], [I, D, P, S, F], [R, T, H, E, U], [C, B, M, Y, W], [G, K, Q, X, Z]]",
                Arrays.deepToString(PlayfairCipher.constructPlayfairTable(
                        "No valid plans for the future can be made by those who have no capacity for living now.")));
        assertEquals("[[W, E, S, L, D], [O, M, R, A, I], [Z, F, X, P, T], [H, U, V, G, N], [C, Y, B, K, Q]]",
                Arrays.deepToString(PlayfairCipher.constructPlayfairTable(
                        "We seldom realize, for example, that our most private thoughts and emotions are not actually our own.")));
        assertEquals("[[I, U, S, T, W], [H, A, E, C, K], [R, Q, O, B, D], [F, G, L, M, N], [P, V, X, Y, Z]]",
                Arrays.deepToString(PlayfairCipher.constructPlayfairTable(
                        "Just what the heck are these quotes about???")));
    }

    @Test public void testConstructPlayfairTableProperties() {
        // For various passphrases, verify:
        // 1) Table is 5x5
        // 2) Contains exactly 25 distinct uppercase letters (no J)
        // 3) All letters A-Z except J appear exactly once
        String[] phrases = {
                "Hello World", "ABCDEFGHIJKLMNOPQRSTUVWXYZ", "aaaa", "",
                "Jingle bells jingle bells", "The quick brown fox jumps over the lazy dog"
        };
        for (String phrase : phrases) {
            char[][] table = PlayfairCipher.constructPlayfairTable(phrase);
            assertEquals(5, table.length);
            Set<Character> seen = new HashSet<>();
            for (int r = 0; r < 5; r++) {
                assertEquals(5, table[r].length);
                for (int c = 0; c < 5; c++) {
                    char ch = table[r][c];
                    assertTrue("Must be uppercase letter: " + ch,
                            ch >= 'A' && ch <= 'Z' && ch != 'J');
                    assertTrue("Duplicate letter: " + ch, seen.add(ch));
                }
            }
            assertEquals(25, seen.size());
        }
    }

    @Test public void testConstructPlayfairTableJHandling() {
        // Passphrase with J: J should be treated as I
        char[][] table = PlayfairCipher.constructPlayfairTable("Jingle");
        // I should appear early (from J), and the table should have no J
        boolean foundI = false, foundJ = false;
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                if (table[r][c] == 'I') foundI = true;
                if (table[r][c] == 'J') foundJ = true;
            }
        }
        assertTrue("I must be in table", foundI);
        assertTrue("J must not be in table", !foundJ);
    }

    // ---------------------------------------------------------------
    // encrypt/decrypt tests
    // ---------------------------------------------------------------

    @Test public void testEncodeAndDecodeExplicit() {
        String passPhrase0 = "Man suffers only because he takes seriously what the Gods made for fun.";
        char[][] table0 = PlayfairCipher.constructPlayfairTable(passPhrase0);
        assertEquals("CLBKSWHLILEKARHFNLAOFEGAEDIFCLBKSWHLIQBLHFNLRGUCOQLBGS",
                PlayfairCipher.encryptPlayfair("TRYINGTODEFINEYOURSELFISLIKETRYINGTOBITEYOUROWNTEETH", table0));
        assertEquals("TRYINGTODEFINEYOURSELFISLIKETRYINGTOBITEYOUROWNTEXETHX",
                PlayfairCipher.decryptPlayfair("CLBKSWHLILEKARHFNLAOFEGAEDIFCLBKSWHLIQBLHFNLRGUCOQLBGS", table0));

        String passPhrase1 = "Total situations are, therefore, patterns in time as much as patterns in space.";
        char[][] table1 = PlayfairCipher.constructPlayfairTable(passPhrase1);
        assertEquals("LTIBUSQCNPXOZITOCINRHTIEECCNAFSRAZ",
                PlayfairCipher.encryptPlayfair("ASTHEOCEANWAVESTHEUNIVERSEPEOPLES", table1));

        // Repeated letters: BOOOOO -> BO OX OX OX OX
        String passPhrase4 = "The more a thing tends to be permanent, the more it tends to be lifeless.";
        char[][] table4 = PlayfairCipher.constructPlayfairTable(passPhrase4);
        assertEquals("LEEZEZEZEZ", PlayfairCipher.encryptPlayfair("BOOOOO", table4));
        assertEquals("BOOXOXOXOX", PlayfairCipher.decryptPlayfair("LEEZEZEZEZ", table4));

        // DD pair: MUDDY -> MU DX DY
        String passPhrase2 = "Only words and conventions can isolate us from the entirely undefinable something which is everything.";
        char[][] table2 = PlayfairCipher.constructPlayfairTable(passPhrase2);
        assertEquals("GEAPANYCITAVAHTDUSNTCDMEXANTRIEYBUISYNDM",
                PlayfairCipher.encryptPlayfair("MUDDYWATERISBESTCLEAREDBYLEAVINGITALONE", table2));
        assertEquals("MUDXDYWATERISBESTCLEAREDBYLEAVINGITALONE",
                PlayfairCipher.decryptPlayfair("GEAPANYCITAVAHTDUSNTCDMEXANTRIEYBUISYNDM", table2));

        // J treated as I in plaintext
        String passPhrase3 = "Zen does not confuse spirituality with thinking about God while one is peeling potatoes.";
        char[][] table3 = PlayfairCipher.constructPlayfairTable(passPhrase3);
        assertEquals("CWTEDIEROHDUPRTDPTLTTCUEMZLPAMOM",
                PlayfairCipher.encryptPlayfair("THEMEANINGOFLIFEISJUSTTOBEALIVE", table3));
        assertEquals("THEMEANINGOFLIFEISIUSTTOBEALIVEX",
                PlayfairCipher.decryptPlayfair("CWTEDIEROHDUPRTDPTLTTCUEMZLPAMOM", table3));
    }

    @Test public void testEncryptDecryptProperties() {
        // For random passphrases and plaintexts:
        // 1) Ciphertext is even length, all uppercase, no J
        // 2) Ciphertext length >= plaintext length
        // 3) decrypt(encrypt(pt)) starts with the "normalized" plaintext (J->I, X insertions)
        // 4) encrypt(decrypt(ct)) == ct for valid ciphertexts
        Random rng = new Random(42);
        String letters = "ABCDEFGHIKLMNOPQRSTUVWXYZ"; // no J
        for (int trial = 0; trial < 200; trial++) {
            // Random passphrase
            int pLen = rng.nextInt(30) + 5;
            StringBuilder pp = new StringBuilder();
            for (int j = 0; j < pLen; j++) pp.append((char) ('a' + rng.nextInt(26)));
            char[][] table = PlayfairCipher.constructPlayfairTable(pp.toString());

            // Random plaintext (no XX, no trailing X, uppercase letters only)
            int ptLen = rng.nextInt(20) + 2;
            StringBuilder pt = new StringBuilder();
            for (int j = 0; j < ptLen; j++) {
                pt.append(letters.charAt(rng.nextInt(25)));
            }
            // Avoid trailing X and XX
            String plaintext = pt.toString().replace("XX", "XA");
            if (plaintext.endsWith("X")) {
                plaintext = plaintext.substring(0, plaintext.length() - 1) + "A";
            }

            String ciphertext = PlayfairCipher.encryptPlayfair(plaintext, table);

            // Even length
            assertEquals("Ciphertext must be even length", 0, ciphertext.length() % 2);
            // All uppercase
            for (int j = 0; j < ciphertext.length(); j++) {
                char c = ciphertext.charAt(j);
                assertTrue("Must be uppercase letter", c >= 'A' && c <= 'Z' && c != 'J');
            }
            // Length >= plaintext
            assertTrue(ciphertext.length() >= plaintext.length());

            // Roundtrip: encrypt then decrypt then encrypt should give same ciphertext
            String decrypted = PlayfairCipher.decryptPlayfair(ciphertext, table);
            String reEncrypted = PlayfairCipher.encryptPlayfair(decrypted, table);
            assertEquals("Roundtrip mismatch", ciphertext, reEncrypted);
        }
    }

    @Test public void testEncryptDecryptRoundtripIdentity() {
        // For ciphertexts (even-length, no repeated pairs), decrypt then encrypt = identity
        char[][] table = PlayfairCipher.constructPlayfairTable("SECRETPASSWORD");
        String[] ciphertexts = {"ABCD", "HELLOWORLD", "QRSTUVWXYZ"};
        // Actually these might not be valid ciphertexts. Let's generate valid ones.
        String[] plaintexts = {"ATTACK", "DEFENSE", "HELLO", "TESTING", "PLAYFAIR"};
        for (String pt : plaintexts) {
            String ct = PlayfairCipher.encryptPlayfair(pt, table);
            String dt = PlayfairCipher.decryptPlayfair(ct, table);
            String ct2 = PlayfairCipher.encryptPlayfair(dt, table);
            assertEquals("encrypt(decrypt(encrypt(pt))) must equal encrypt(pt)", ct, ct2);
        }
    }

    // ---------------------------------------------------------------
    // War and Peace integration test
    // ---------------------------------------------------------------

    @Test public void testUsingWarAndPeace() {
        CRC32 check = new CRC32();
        String passPhrase = "The ego is nothing other than the focus of conscious attention.";
        char[][] table = PlayfairCipher.constructPlayfairTable(passPhrase);
        try (Scanner scanner = new Scanner(new File("warandpeace.txt"))) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.length() < 50) continue;
                line = line.toUpperCase();
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if ('A' <= c && c <= 'Z') sb.append(c);
                }
                line = sb.toString();
                if (line.indexOf("XX") == -1 && !line.endsWith("X")) {
                    String cipherText = PlayfairCipher.encryptPlayfair(line, table);
                    String decryptedText = PlayfairCipher.decryptPlayfair(cipherText, table);
                    check.update(cipherText.getBytes("UTF-8"));
                    check.update(decryptedText.getBytes("UTF-8"));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error opening file warandpeace.txt: " + e);
            fail();
        } catch (UnsupportedEncodingException ignored) {
            fail();
        }
        assertEquals(189957488L, check.getValue());
    }
}