import org.junit.Test;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PlayfairCipherTest {

    @Test public void testConstructPlayfairTableExplicit() {
        String passPhrase0 = "There will always be suffering.";
        char[][] table0 = PlayfairCipher.constructPlayfairTable(passPhrase0);
        assertEquals(
                "[[T, H, E, R, W], [I, L, A, Y, S], [B, U, F, N, G], [C, D, K, M, O], [P, Q, V, X, Z]]",
                Arrays.deepToString(table0)
        );
        String passPhrase1 = "The only way to make sense out of change is to plunge into it, move with it, and join the dance.";
        char[][] table1 = PlayfairCipher.constructPlayfairTable(passPhrase1);
        assertEquals(
                "[[T, H, E, O, N], [L, Y, W, A, M], [K, S, U, F, C], [G, I, P, V, D], [B, Q, R, X, Z]]",
                Arrays.deepToString(table1)
        );
        String passPhrase2 = "No valid plans for the future can be made by those who have no capacity for living now.";
        char[][] table2 = PlayfairCipher.constructPlayfairTable(passPhrase2);
        assertEquals(
                "[[N, O, V, A, L], [I, D, P, S, F], [R, T, H, E, U], [C, B, M, Y, W], [G, K, Q, X, Z]]",
                Arrays.deepToString(table2)
        );
        String passPhrase3 = "We seldom realize, for example, that our most private thoughts and emotions are not actually our own.";
        char[][] table3 = PlayfairCipher.constructPlayfairTable(passPhrase3);
        assertEquals(
                "[[W, E, S, L, D], [O, M, R, A, I], [Z, F, X, P, T], [H, U, V, G, N], [C, Y, B, K, Q]]",
                Arrays.deepToString(table3)
        );
        String passPhrase4 = "Just what the heck are these quotes about???";
        char[][] table4 = PlayfairCipher.constructPlayfairTable(passPhrase4);
        assertEquals(
                "[[I, U, S, T, W], [H, A, E, C, K], [R, Q, O, B, D], [F, G, L, M, N], [P, V, X, Y, Z]]",
                Arrays.deepToString(table4)
        );
    }

    @Test public void testEncodeAndDecode() {
        String plainText0 = "TRYINGTODEFINEYOURSELFISLIKETRYINGTOBITEYOUROWNTEETH";
        String passPhrase0 = "Man suffers only because he takes seriously what the Gods made for fun.";
        char[][] table0 = PlayfairCipher.constructPlayfairTable(passPhrase0);
        String cipherText0 = PlayfairCipher.encryptPlayfair(plainText0, table0);
        assertEquals("CLBKSWHLILEKARHFNLAOFEGAEDIFCLBKSWHLIQBLHFNLRGUCOQLBGS", cipherText0);
        String decryptedText0 = PlayfairCipher.decryptPlayfair(cipherText0, table0);
        assertEquals("TRYINGTODEFINEYOURSELFISLIKETRYINGTOBITEYOUROWNTEXETHX", decryptedText0);

        String plainText1 = "ASTHEOCEANWAVESTHEUNIVERSEPEOPLES";
        String passPhrase1 = "Total situations are, therefore, patterns in time as much as patterns in space.";
        char[][] table1 = PlayfairCipher.constructPlayfairTable(passPhrase1);
        String cipherText1 = PlayfairCipher.encryptPlayfair(plainText1, table1);
        assertEquals("LTIBUSQCNPXOZITOCINRHTIEECCNAFSRAZ", cipherText1);
        String decryptedText1 = PlayfairCipher.decryptPlayfair(cipherText1, table1);
        assertEquals("ASTHEOCEANWAVESTHEUNIVERSEPEOPLESX", decryptedText1);

        String plainText2 = "MUDDYWATERISBESTCLEAREDBYLEAVINGITALONE";
        String passPhrase2 = "Only words and conventions can isolate us from the entirely undefinable something which is everything.";
        char[][] table2 = PlayfairCipher.constructPlayfairTable(passPhrase2);
        String cipherText2 = PlayfairCipher.encryptPlayfair(plainText2, table2);
        assertEquals("GEAPANYCITAVAHTDUSNTCDMEXANTRIEYBUISYNDM", cipherText2);
        String decryptedText2 = PlayfairCipher.decryptPlayfair(cipherText2, table2);
        assertEquals("MUDXDYWATERISBESTCLEAREDBYLEAVINGITALONE", decryptedText2);

        String plainText3 = "THEMEANINGOFLIFEISJUSTTOBEALIVE";
        String passPhrase3 = "Zen does not confuse spirituality with thinking about God while one is peeling potatoes.";
        char[][] table3 = PlayfairCipher.constructPlayfairTable(passPhrase3);
        String cipherText3 = PlayfairCipher.encryptPlayfair(plainText3, table3);
        assertEquals("CWTEDIEROHDUPRTDPTLTTCUEMZLPAMOM", cipherText3);
        String decryptedText3 = PlayfairCipher.decryptPlayfair(cipherText3, table3);
        assertEquals("THEMEANINGOFLIFEISIUSTTOBEALIVEX", decryptedText3);

        String plainText4 = "BOOOOO";
        String passPhrase4 = "The more a thing tends to be permanent, the more it tends to be lifeless.";
        char[][] table4 = PlayfairCipher.constructPlayfairTable(passPhrase4);
        String cipherText4 = PlayfairCipher.encryptPlayfair(plainText4, table4);
        assertEquals("LEEZEZEZEZ", cipherText4);
        String decryptedText4 = PlayfairCipher.decryptPlayfair(cipherText4, table4);
        assertEquals("BOOXOXOXOX", decryptedText4);
    }

    @Test public void testUsingWarAndPeace() {
        CRC32 check = new CRC32();
        String passPhrase = "The ego is nothing other than the focus of conscious attention.";
        char[][] table = PlayfairCipher.constructPlayfairTable(passPhrase);
        try(Scanner scanner = new Scanner(new File("warandpeace.txt"))) {
            while(scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if(line.length() < 50) { continue; }
                line = line.toUpperCase();
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < line.length(); i++) {
                    char c = line.charAt(i);
                    if('A' <= c && c <= 'Z') { sb.append(c); }
                }
                line = sb.toString();
                if(line.indexOf("XX") == -1 && !line.endsWith("X")) { // The nineteenth century was not as sexxy as ours.
                    String cipherText = PlayfairCipher.encryptPlayfair(line, table);
                    String decryptedText = PlayfairCipher.decryptPlayfair(cipherText, table);
                    check.update(cipherText.getBytes("UTF-8"));
                    check.update(decryptedText.getBytes("UTF-8"));
                }
            }
        }
        catch(FileNotFoundException e) {
            System.out.println("Error opening file warandpeace.txt: " + e);
            fail();
        }
        catch(UnsupportedEncodingException ignored) {
            System.out.println("The test is not working. This is not supposed to happen.");
            fail();
        }
        assertEquals(189957488L, check.getValue());
    }
}