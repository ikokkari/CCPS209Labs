import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Random;
import java.util.zip.CRC32;
import static org.junit.Assert.assertEquals;

public class P2J2Test {

    private static final int RUNS = 100000;
    private static final int SEED = 12345;
    
    @Test public void testRemoveDuplicates() {
        // Explicit test cases
        assertEquals("", P2J2.removeDuplicates(""));
        assertEquals("ilka", P2J2.removeDuplicates("ilkka"));
        assertEquals("aba", P2J2.removeDuplicates("aaaaaaaabaaaaaaa"));
        assertEquals("cdcdc", P2J2.removeDuplicates("ccccddccccdcccccc"));
        assertEquals("x", P2J2.removeDuplicates("x"));
        assertEquals("x", P2J2.removeDuplicates("xxxxxxxxxxxxx"));
        assertEquals("abcdefgh", P2J2.removeDuplicates("abcdefgh"));
        // Uppercase and lowercase versions of the same character are different.
        assertEquals("AabBCcdD", P2J2.removeDuplicates("AabBCcdD"));
        assertEquals("\u1234\u5678\u6666", P2J2.removeDuplicates(
        "\u1234\u5678\u5678\u5678\u5678\u5678\u5678\u6666"
        ));
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32();        
        for(int i = 0; i < RUNS; i++) {
            StringBuilder sb = new StringBuilder();
            int len = rng.nextInt(500);
            for(int j = 0; j < len; j++) {
                char c = (char)(1 + rng.nextInt(10000));
                int rep = rng.nextInt(10) + 1;
                for(int k = 0; k < rep; k++) {
                    sb.append(c);
                }
            }
            try {
                check.update(P2J2.removeDuplicates(sb.toString()).getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(2596651304L, check.getValue());
    }
    
    private char randomChar(Random rng) {
        return (char)(rng.nextInt(200) + 97);
    }
    
    private String buildString(Random rng, int len) {
        StringBuilder sb = new StringBuilder();
        for(int j = 0; j < len; j++) {
            sb.append(randomChar(rng));
        }
        return sb.toString();
    }
    
    @Test public void testUniqueCharacters() {
        // Explicit test cases
        assertEquals("abc", P2J2.uniqueCharacters("aaaaaabaaabbbaaababbbabbcbabababa"));
        assertEquals("ilka orne", P2J2.uniqueCharacters("ilkka kokkarinen"));
        assertEquals("", P2J2.uniqueCharacters(""));
        assertEquals("\u4444", P2J2.uniqueCharacters("\u4444\u4444\u4444"));
        assertEquals("aABbcCDd", P2J2.uniqueCharacters("aABbcCDd"));
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32(); 
        for(int i = 0; i < RUNS; i++) {
            int len = rng.nextInt(100) + (2 << rng.nextInt(5));
            String s = buildString(rng, len);
            String res = P2J2.uniqueCharacters(s);
            check.update(res.getBytes());
        }    
        assertEquals(3756363171L, check.getValue());
    }
    
    @Test
    public void testCountSafeSquaresRooks() {
        // Explicit test cases
        boolean[][] b1 = {
            {true , true , false},
            {false, false, false},
            {false, false, false}
        };
        assertEquals(2, P2J2.countSafeSquaresRooks(3, b1));
        
        boolean[][] b2 = {
            {true , false, false, false},
            {false, true , false, false},
            {false, false, true , false},
            {false, false, false, true }
        };
        assertEquals(0, P2J2.countSafeSquaresRooks(4, b2));
        
        boolean[][] b3 = {
            {true}
        };
        assertEquals(0, P2J2.countSafeSquaresRooks(1, b3));
        
        boolean[][] b4 = {
            {false}
        };
        assertEquals(1, P2J2.countSafeSquaresRooks(1, b4));
        
        boolean[][] b5 = {
            {false, false, false, false, false},
            {false, false, false, false, false},
            {false, false, true, false, false},
            {false, false, false, false, false},
            {false, false, false, false, false}
        };
        assertEquals(16, P2J2.countSafeSquaresRooks(5, b5));
        
        // Pseudorandom fuzz tester
        Random rng = new Random(SEED);
        CRC32 check = new CRC32(); 
        int total = 0, answer;
        LinkedList<Integer> qxl = new LinkedList<>();
        LinkedList<Integer> qyl = new LinkedList<>();
        for(int n = 3; n < 100; n++) {
            boolean[][] board = new boolean[n][n];
            int count = 1;
            for(int trials = 0; trials < n + 1; trials++) {
                answer = P2J2.countSafeSquaresRooks(n, board);
                total += answer;
                check.update(answer);
                int nx, ny;
                for(int i = 0; i < count; i++) {
                    do { // find a square that does not have a rook yet
                        nx = rng.nextInt(n);
                        ny = rng.nextInt(n);
                    } while(board[nx][ny]);
                    board[nx][ny] = true;
                    qxl.add(nx);
                    qyl.add(ny);
                    answer = P2J2.countSafeSquaresRooks(n, board);
                    total += answer;
                    check.update(answer);
                }
                for(int i = 0; i < count - 1; i++) {
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
    
    @Test public void testRecaman() {
        int[] inputs = { 1, 2, 3, 4, 5, 6, 15, 99, 222, 2654, 8732, 14872, 20000,
            76212, 98721, 114322, 158799, 178320, 221099, 317600 };
        int[] expected = { 1, 3, 6, 2, 7, 13, 24, 64, 47, 5457, 18416, 18382,
            14358, 340956, 298489, 199265, 351688, 183364, 364758, 657230 }; 
        CRC32 check = new CRC32();
        for(int i = 0; i < inputs.length; i++) {
            int rec = P2J2.recaman(inputs[i]);
            assertEquals(expected[i], rec);
            check.update(rec);
        }
        assertEquals(2348649420L, check.getValue());
    }
    
}