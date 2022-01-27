import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TchoukaillonTest {

    @Test public void testMoveAndUndo() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        List<Integer> board = new ArrayList<>();
        List<Integer> store = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            for(int j = 2; j < 2 * i + 3; j++) {
                board.clear();
                store.clear();
                for(int k = 0; k < j; k++) {
                    board.add(rng.nextBoolean() ? k : rng.nextInt(1 + k) + (k == j - 1 ? 1 : 0));
                }
                store.addAll(board);
                for(int k = 1; k < j; k++) {
                    boolean b1 = Tchoukaillon.move(board, k);
                    check.update(b1 ? i: -i);
                    try {
                        check.update(board.toString().getBytes("UTF-8"));
                    } catch(UnsupportedEncodingException ignored) {}
                    boolean b2 = Tchoukaillon.undo(board, k);
                    check.update(b2 ? i: -i);
                    // Board after a legal move and its undo should equal the original board.
                    if(b1) { assertTrue(b2); assertEquals(store, board); }
                }
            }
        }
        assertEquals(1309501411L, check.getValue());
    }
    
    @Test public void testValue() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        List<Integer> board = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            for(int j = 2; j < 4 + i / 3; j++) {
                board.clear();
                for(int k = 0; k < j; k++) {
                    board.add(rng.nextBoolean() ? k : rng.nextInt(1 + k) + (k == j - 1 ? 1 : 0));
                }
                int v = Tchoukaillon.value(board);
                check.update(v);
            }
        }
    }
    
    @Test public void testPreviousSolvable() {
        CRC32 check = new CRC32();
        List<Integer> board = new ArrayList<>();
        board.add(0); board.add(1);
        for(int i = 1; i < 100000; i++) {
            int sum = 0;
            for(int j = board.size() - 1; j > 0; j--) {
                sum += board.get(j);
            }
            sum += board.get(0);
            assertEquals(sum, i);
            try {
                check.update(board.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            Tchoukaillon.previousSolvable(board);
        }
        assertEquals(280052111L, check.getValue());
    }
    
}
