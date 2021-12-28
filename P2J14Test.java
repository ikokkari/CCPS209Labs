import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.zip.CRC32;
import java.util.Random;

import org.junit.Test;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class P2J14Test {

    @Test public void testDistanceFromCharacterExplicit() {
        String s0 = "Hello world";
        int[] e0 = {4, 3, 2, 1, 0, 1, 1, 0, 1, 2, 3};
        assertArrayEquals(e0, P2J14.distanceFromCharacter(s0, 'o'));
        String s1 = "Bananarama";
        int[] e1 = {1, 0, 1, 0, 1, 0, 1, 0, 1, 0};
        assertArrayEquals(e1, P2J14.distanceFromCharacter(s1, 'a'));
        String s2 = "Monte Carlo";
        int[] e2 = {1, 0, 1, 2, 3, 4, 4, 3, 2, 1, 0};
        assertArrayEquals(e2, P2J14.distanceFromCharacter(s2, 'o'));
        String s3 = "Silentium est aureum";
        int[] e3 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
        assertArrayEquals(e3, P2J14.distanceFromCharacter(s3, 'S'));
    }

    @Test public void testDistanceFromCharacterUsingWarAndPeaceAsData() {
        CRC32 check = new CRC32();
        Random rng = new Random(12345);
        try(Scanner scan = new Scanner(new File("warandpeace.txt"))) {
            while(scan.hasNext()) {
                String line = scan.next();
                char c = line.charAt(rng.nextInt(line.length()));
                int[] result = P2J14.distanceFromCharacter(line, c);
                for(int e: result) { check.update(e); }
            }
        } catch (IOException e) {
            System.out.println("Unable to read file warandpeace.txt.");
            fail();
        }
        assertEquals(262460440L, check.getValue());
    }

    @Test public void testPushDominoesExplicit() {
        assertEquals("", P2J14.pushDominoes(""));
        assertEquals(".", P2J14.pushDominoes("."));
        assertEquals("LR", P2J14.pushDominoes("LR"));
        assertEquals("...", P2J14.pushDominoes("..."));
        assertEquals("LLL", P2J14.pushDominoes("..L"));
        assertEquals("LRLRLR", P2J14.pushDominoes("LRLRLR"));
        assertEquals("LLRR", P2J14.pushDominoes(".LR."));
        assertEquals("LL.RR.LLRRLL..", P2J14.pushDominoes(".L.R...LR..L.."));
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
        for(int i = 0; i < n; i++) {
            StringBuilder dominoes = new StringBuilder();
            for(int j = 0; j < m; j++) {
                int roll = rng.nextInt(100);
                if(roll < 60) { dominoes.append('.'); }
                else if(roll < 80) { dominoes.append('L'); }
                else { dominoes.append('R'); }
            }
            String result = P2J14.pushDominoes(dominoes.toString());
            assertEquals(dominoes.length(), result.length());
            for(int j = 0; j < result.length(); j++) {
                check.update((int)result.charAt(j));
            }
            if(++count == goal) {
                count = 0; goal += 2; m += 1;
            }
        }
        assertEquals(expected, check.getValue());
    }
}