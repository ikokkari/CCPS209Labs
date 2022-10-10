import org.junit.Test;
import java.util.Random;

import static org.junit.Assert.assertEquals;

public class BeklemishevWormTest {

    @Test public void testRepeat() {
        Rope r1 = new StringRope("hello");
        Rope r2 = BeklemishevWorm.repeat(r1, 0);
        assertEquals(0, r2.length());
        Rope r3 = BeklemishevWorm.repeat(r1, 1);
        assertEquals(r1, r3);
        Rope r4 = BeklemishevWorm.repeat(r1, 2);
        assertEquals(new StringRope("hellohello"), r4);
        Rope r5 = BeklemishevWorm.repeat(r1, 200_000_000);
        assertEquals(1_000_000_000, r5.length());
        Random rng = new Random(12345);
        for(int i = 0; i < 100; i++) {
            int idx = rng.nextInt(r5.length());
            assertEquals("hello".charAt(idx % 5), r5.charAt(idx));
        }
        Rope r6 = new StringRope("");
        Rope r7 = BeklemishevWorm.repeat(r6, 100_000_000);
        assertEquals(0, r7.length());
    }

    @Test public void testWormStep() {
        Rope r1 = new StringRope("123");
        assertEquals("12222", BeklemishevWorm.wormStep(r1, 2).toString());
        Rope r2 = new StringRope("20202");
        assertEquals("2020111111", BeklemishevWorm.wormStep(r2, 5).toString());
        Rope r3 = new StringRope("321");
        assertEquals("320320320320320320320", BeklemishevWorm.wormStep(r3, 6).toString());
        Rope r4 = new StringRope("8");
        assertEquals("7777", BeklemishevWorm.wormStep(r4, 3).toString());
        Rope r5 = new StringRope("3042");
        assertEquals("30414141", BeklemishevWorm.wormStep(r5, 2).toString());
        Rope r6 = new StringRope("4242");
        assertEquals("424142414241", BeklemishevWorm.wormStep(r6, 2).toString());
        Rope r7 = new StringRope("1551");
        assertEquals("155015501550155015501550", BeklemishevWorm.wormStep(r7, 5).toString());
        Rope r8 = new StringRope("747");
        assertEquals("746666666", BeklemishevWorm.wormStep(r8, 6).toString());
    }

    @Test public void testIterateWorm() {
        assertEquals(24, BeklemishevWorm.iterateWorm("2031", 4));
        assertEquals(31, BeklemishevWorm.iterateWorm("313", 4));
        assertEquals(1, BeklemishevWorm.iterateWorm("2", 50));
        assertEquals(0, BeklemishevWorm.iterateWorm("2", 51));
        assertEquals(40470, BeklemishevWorm.iterateWorm("007", 10));
        assertEquals(21264, BeklemishevWorm.iterateWorm("123", 50));
        assertEquals(72, BeklemishevWorm.iterateWorm("202020", 100));
        assertEquals(5315864, BeklemishevWorm.iterateWorm("12321", 200));
        assertEquals(362889, BeklemishevWorm.iterateWorm("8", 10));
        assertEquals(1150, BeklemishevWorm.iterateWorm("4321", 10));
        assertEquals(7307240, BeklemishevWorm.iterateWorm("420", 42));
        assertEquals(750375878, BeklemishevWorm.iterateWorm("7", 30));
    }
}
