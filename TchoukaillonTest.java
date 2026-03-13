import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TchoukaillonTest {

    // Helper to create a mutable ArrayList from varargs
    private static List<Integer> board(Integer... vals) {
        return new ArrayList<>(Arrays.asList(vals));
    }

    // --- move explicit tests ---

    @Test public void testMoveSpecExample() {
        // {3,1,2,0,4,5} k=4: position 4 has 4 seeds, sow to positions 0-3
        List<Integer> b = board(3, 1, 2, 0, 4, 5);
        assertTrue(Tchoukaillon.move(b, 4));
        assertEquals(board(4, 2, 3, 1, 0, 5), b);
    }

    @Test public void testMoveIllegal() {
        // board[3]=0, not 3: illegal
        List<Integer> b = board(3, 1, 2, 0, 4, 5);
        List<Integer> original = board(3, 1, 2, 0, 4, 5);
        assertFalse(Tchoukaillon.move(b, 3));
        assertEquals(original, b); // board unchanged

        // board[2]=2 == k=2: legal, but board[1]=2 != 1 is irrelevant (only position k matters)
        // Actually wait: board[2]=2 IS legal for k=2
        List<Integer> b2 = board(0, 2, 1);
        assertFalse(Tchoukaillon.move(b2, 1)); // board[1]=2 != 1
    }

    @Test public void testMoveK1() {
        // k=1: sow 1 seed from position 1 to position 0
        List<Integer> b = board(0, 1);
        assertTrue(Tchoukaillon.move(b, 1));
        assertEquals(board(1), b); // trailing zero trimmed

        List<Integer> b2 = board(3, 1, 2);
        assertTrue(Tchoukaillon.move(b2, 1));
        assertEquals(board(4, 0, 2), b2);
    }

    @Test public void testMoveK2() {
        // k=2: sow 2 seeds to positions 0,1
        List<Integer> b = board(0, 0, 2);
        assertTrue(Tchoukaillon.move(b, 2));
        assertEquals(board(1, 1), b); // trailing zero trimmed
    }

    @Test public void testMoveTrimsTrailingZeros() {
        // Move that creates trailing zeros which should be trimmed
        List<Integer> b = board(0, 0, 0, 3);
        assertTrue(Tchoukaillon.move(b, 3));
        assertEquals(board(1, 1, 1), b); // position 3 becomes 0 and is last -> trimmed
    }

    @Test public void testMoveLegalAtMultiplePositions() {
        // Board where k=1 is legal (board[1]=1)
        List<Integer> b = board(3, 1, 2, 0, 4, 5);
        assertTrue(Tchoukaillon.move(b, 1));
        assertEquals(board(4, 0, 2, 0, 4, 5), b);
    }

    // --- undo explicit tests ---

    @Test public void testUndoSpecExample() {
        // Undo the spec example move
        List<Integer> b = board(4, 2, 3, 1, 0, 5);
        assertTrue(Tchoukaillon.undo(b, 4));
        assertEquals(board(3, 1, 2, 0, 4, 5), b);
    }

    @Test public void testUndoExtendsBoard() {
        // k == board.size(): must extend the board with a new position
        List<Integer> b = board(1, 1, 1);
        assertTrue(Tchoukaillon.undo(b, 3));
        assertEquals(board(0, 0, 0, 3), b);
    }

    @Test public void testUndoIllegalPositionNotZero() {
        // Position k is not 0: illegal
        List<Integer> b = board(3, 1, 2, 0, 4, 5);
        assertFalse(Tchoukaillon.undo(b, 2)); // board[2]=2, not 0
    }

    @Test public void testUndoIllegalPredecessorZero() {
        // Spec example: undo k=3 on {3,1,0,0,4}: position 2 has 0 seeds, can't give back
        List<Integer> b = board(3, 1, 0, 0, 4);
        assertFalse(Tchoukaillon.undo(b, 3));
    }

    @Test public void testUndoK1() {
        // Undo k=1: board must have size 1, extend to [val, 0], then collect
        List<Integer> b = board(1);
        assertTrue(Tchoukaillon.undo(b, 1));
        assertEquals(board(0, 1), b);
    }

    @Test public void testUndoK1Illegal() {
        // board[1]=1, not 0: illegal for undo at k=1
        List<Integer> b = board(1, 1);
        assertFalse(Tchoukaillon.undo(b, 1));
    }

    // --- move/undo round-trip tests ---

    @Test public void testMoveUndoRoundTrip() {
        // move then undo at same k should restore original board
        List<Integer> original = board(3, 1, 2, 0, 4, 5);
        List<Integer> b = board(3, 1, 2, 0, 4, 5);
        assertTrue(Tchoukaillon.move(b, 4));
        assertTrue(Tchoukaillon.undo(b, 4));
        assertEquals(original, b);

        // Another: k=2
        List<Integer> orig2 = board(0, 0, 2, 3);
        List<Integer> b2 = board(0, 0, 2, 3);
        assertTrue(Tchoukaillon.move(b2, 2));
        assertEquals(board(1, 1, 0, 3), b2);
        assertTrue(Tchoukaillon.undo(b2, 2));
        assertEquals(orig2, b2);
    }

    @Test public void testUndoMoveRoundTrip() {
        // undo then move at same k should also restore
        List<Integer> original = board(1, 1, 1);
        List<Integer> b = board(1, 1, 1);
        assertTrue(Tchoukaillon.undo(b, 3));
        assertEquals(board(0, 0, 0, 3), b);
        assertTrue(Tchoukaillon.move(b, 3));
        assertEquals(original, b);
    }

    // --- value explicit tests ---

    @Test public void testValueSingleElement() {
        // No legal moves possible (no position k>=1), value = board[0]
        assertEquals(0, Tchoukaillon.value(board(0)));
        assertEquals(2, Tchoukaillon.value(board(2)));
        assertEquals(5, Tchoukaillon.value(board(5)));
    }

    @Test public void testValueSimpleSolvable() {
        // [0,1]: move k=1 -> [1]. Value = 1. Fully solvable.
        assertEquals(1, Tchoukaillon.value(board(0, 1)));

        // [0,0,2]: move k=2 -> [1,1] -> k=1 -> [2]. Value = 2.
        assertEquals(2, Tchoukaillon.value(board(0, 0, 2)));

        // [0,1,2]: solvable for n=3
        assertEquals(3, Tchoukaillon.value(board(0, 1, 2)));
    }

    @Test public void testValueUnsolvable() {
        // [0,0,0,3]: move k=3 -> [1,1,1] -> k=1 -> [2,0,1] -> no more. Value = 2, not 3.
        assertEquals(2, Tchoukaillon.value(board(0, 0, 0, 3)));

        // [0,2,1]: no legal moves (k=1: board[1]=2≠1, k=2: board[2]=1≠2). Value = 0.
        assertEquals(0, Tchoukaillon.value(board(0, 2, 1)));

        // [0,0,0,0,4]: value is only 2
        assertEquals(2, Tchoukaillon.value(board(0, 0, 0, 0, 4)));
    }

    @Test public void testValueKnownSolvableChain() {
        // The unique solvable boards for n=1..6 should all have value = n
        assertEquals(1, Tchoukaillon.value(board(0, 1)));
        assertEquals(2, Tchoukaillon.value(board(0, 0, 2)));
        assertEquals(3, Tchoukaillon.value(board(0, 1, 2)));
        assertEquals(4, Tchoukaillon.value(board(0, 0, 1, 3)));
        assertEquals(5, Tchoukaillon.value(board(0, 1, 1, 3)));
        assertEquals(6, Tchoukaillon.value(board(0, 0, 0, 2, 4)));
    }

    @Test public void testValueWithSeedsAlreadyHome() {
        // board[0] counts as seeds already home
        assertEquals(2, Tchoukaillon.value(board(1, 1)));  // k=1: [2], value=2
        assertEquals(3, Tchoukaillon.value(board(1, 0, 2)));  // k=2: [2,1]->[3], value=3
    }

    @Test public void testValueMultipleMoves() {
        // Requires choosing the right sequence of moves
        assertEquals(7, Tchoukaillon.value(board(0, 1, 0, 3, 0, 5)));
        assertEquals(3, Tchoukaillon.value(board(0, 1, 0, 3)));
    }

    // --- previousSolvable explicit tests ---

    @Test public void testPreviousSolvableChain() {
        // Verify the first several solvable boards in the chain
        List<Integer> b = board(0, 1); // n=1
        String[] expected = {
                "[0, 0, 2]",       // n=2
                "[0, 1, 2]",       // n=3
                "[0, 0, 1, 3]",    // n=4
                "[0, 1, 1, 3]",    // n=5
                "[0, 0, 0, 2, 4]", // n=6
                "[0, 1, 0, 2, 4]", // n=7
                "[0, 0, 2, 2, 4]", // n=8
                "[0, 1, 2, 2, 4]", // n=9
                "[0, 0, 1, 1, 3, 5]", // n=10
        };
        for (int i = 0; i < expected.length; i++) {
            Tchoukaillon.previousSolvable(b);
            assertEquals("Solvable board for n=" + (i + 2), expected[i], b.toString());
        }
    }

    @Test public void testPreviousSolvableMaintainsSum() {
        // Each call to previousSolvable increases total seeds by exactly 1
        List<Integer> b = board(0, 1);
        for (int n = 2; n <= 20; n++) {
            Tchoukaillon.previousSolvable(b);
            int sum = 0;
            for (int v : b) { sum += v; }
            assertEquals("Total seeds should be " + n, n, sum);
        }
    }

    @Test public void testPreviousSolvableStartsWithZero() {
        // board[0] should always be 0 after previousSolvable (canonicalized)
        List<Integer> b = board(0, 1);
        for (int i = 0; i < 20; i++) {
            Tchoukaillon.previousSolvable(b);
            assertEquals("board[0] should be 0", 0, (int) b.get(0));
        }
    }

    // --- CRC mass tests ---

    @Test public void testMoveAndUndo() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        List<Integer> b = new ArrayList<>();
        List<Integer> store = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            for(int j = 2; j < 2 * i + 3; j++) {
                b.clear();
                store.clear();
                for(int k = 0; k < j; k++) {
                    b.add(rng.nextBoolean() ? k : rng.nextInt(1 + k) + (k == j - 1 ? 1 : 0));
                }
                store.addAll(b);
                for(int k = 1; k < j; k++) {
                    boolean b1 = Tchoukaillon.move(b, k);
                    check.update(b1 ? i: -i);
                    try {
                        check.update(b.toString().getBytes("UTF-8"));
                    } catch(UnsupportedEncodingException ignored) {}
                    boolean b2 = Tchoukaillon.undo(b, k);
                    check.update(b2 ? i: -i);
                    if(b1) { assertTrue(b2); assertEquals(store, b); }
                }
            }
        }
        assertEquals(1309501411L, check.getValue());
    }

    @Test public void testValue() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        List<Integer> b = new ArrayList<>();
        for(int i = 0; i < 50; i++) {
            for(int j = 2; j < 4 + i / 3; j++) {
                b.clear();
                for(int k = 0; k < j; k++) {
                    b.add(rng.nextBoolean() ? k : rng.nextInt(1 + k) + (k == j - 1 ? 1 : 0));
                }
                int v = Tchoukaillon.value(b);
                check.update(v);
            }
        }
    }

    @Test public void testPreviousSolvable() {
        CRC32 check = new CRC32();
        List<Integer> b = new ArrayList<>();
        b.add(0); b.add(1);
        for(int i = 1; i < 100000; i++) {
            int sum = 0;
            for(int j = b.size() - 1; j > 0; j--) {
                sum += b.get(j);
            }
            sum += b.get(0);
            assertEquals(sum, i);
            try {
                check.update(b.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
            Tchoukaillon.previousSolvable(b);
        }
        assertEquals(280052111L, check.getValue());
    }
}