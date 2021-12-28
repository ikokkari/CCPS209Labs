import org.junit.Test;
import java.util.zip.CRC32;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class P2J12Test {
    
    private static final int[] FOREST_FIRE_EXPECTED = {
        0, 1, 1, 2, 1, 1, 2, 2, 4, 4, 1, 1, 2, 1, 1, 2, 2, 4, 4, 2, 4, 4, 5, 5, 8,
        5, 5, 9, 1, 1, 2, 1, 1, 2, 2, 4, 4, 1, 1, 2, 1, 1, 2, 2, 4, 4, 2, 4, 4,
        5, 5, 8, 5, 5, 9, 9, 4, 4, 5, 5, 10, 5, 5, 10, 2, 10, 13, 11, 10, 8, 11,
        13, 10, 12, 10, 10, 12, 10, 11, 14, 20, 13    
    };
    
    private static final int[] REMY_SIGRIST_EXPECTED = {
        0, 0, 0, 1, 0, 2, 3, 4, 0, 3, 2, 5, 1, 6, 7, 8, 0, 7, 6, 9, 5, 10, 11, 12,
        4, 13, 14, 15, 16, 17, 18, 19, 0, 11, 10, 16, 9, 14, 13, 20, 12, 21, 22, 23,
        24, 25, 26, 27, 1, 28, 29 
    };
    
    private static final int ROUNDS = 10000;
    
    @Test public void testForestFire() {
        int[] actual = P2J12.forestFire(FOREST_FIRE_EXPECTED.length - 1);
        assertArrayEquals(FOREST_FIRE_EXPECTED, actual);
        
        CRC32 check = new CRC32();
        actual = P2J12.forestFire(ROUNDS);
        for(int e: actual) {
            check.update(e);
        }
        assertEquals(3940994222L, check.getValue());
    }
    
    @Test public void testRemySigrist() {
        int[] actual = P2J12.remySigrist(REMY_SIGRIST_EXPECTED.length - 1);
        assertArrayEquals(REMY_SIGRIST_EXPECTED, actual);
        
        CRC32 check = new CRC32();
        actual = P2J12.remySigrist(ROUNDS);
        for(int e: actual) {
            check.update(e);
        }
        assertEquals(186819056L, check.getValue());
    }
}