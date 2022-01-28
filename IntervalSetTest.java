import org.junit.Test;
import java.util.zip.CRC32;
import java.util.Random;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IntervalSetTest {

    @Test public void testAddExplicit() {
        IntervalSet is0 = new IntervalSet();
        assertEquals("[]", is0.toString());
        is0.add(10, 20);
        assertEquals("[10-20]", is0.toString());
        is0.add(40, 60);
        assertEquals("[10-20, 40-60]", is0.toString());
        is0.add(5, 25);
        assertEquals("[5-25, 40-60]", is0.toString());
        is0.add(20, 45);
        assertEquals("[5-60]", is0.toString());
        is0.add(70, 80);
        assertEquals("[5-60, 70-80]", is0.toString());
        is0.add(61, 69);
        assertEquals("[5-80]", is0.toString());
        is0.add(1, 3);
        assertEquals("[1-3, 5-80]", is0.toString());
        is0.add(90, 100);
        assertEquals("[1-3, 5-80, 90-100]", is0.toString());
        is0.add(2, 95);
        assertEquals("[1-100]", is0.toString());

        IntervalSet is1 = new IntervalSet();
        is1.add(42);
        assertEquals("[42]", is1.toString());
        is1.add(17);
        assertEquals("[17, 42]", is1.toString());
        is1.add(99);
        assertEquals("[17, 42, 99]", is1.toString());
        is1.add(41, 98);
        assertEquals("[17, 41-99]", is1.toString());
        is1.add(100, 110);
        assertEquals("[17, 41-110]", is1.toString());
        is1.add(0, 15);
        assertEquals("[0-15, 17, 41-110]", is1.toString());
        is1.add(16);
        assertEquals("[0-17, 41-110]", is1.toString());
        is1.add(17, 41);
        assertEquals("[0-110]", is1.toString());
    }

    @Test public void testContainsExplicit() {
        IntervalSet is = new IntervalSet();
        assertFalse(is.contains(1_000_000_000));
        assertFalse(is.contains(42, 99));
        is.add(42, 99);
        assertEquals("[42-99]", is.toString());
        assertTrue(is.contains(42, 99));
        assertTrue(is.contains(50, 72));
        assertFalse(is.contains(42, 100));
        assertFalse(is.contains(35, 70));
        is.add(10, 25);
        assertEquals("[10-25, 42-99]", is.toString());
        assertTrue(is.contains(42, 99));
        assertTrue(is.contains(86));
        assertTrue(is.contains(20));
        assertTrue(is.contains(24, 25));
        assertFalse(is.contains(25, 42));
        is.add(35, 60);
        assertEquals("[10-25, 35-99]", is.toString());
        assertTrue(is.contains(11, 14));
        assertTrue(is.contains(35, 99));
        assertTrue(is.contains(40, 98));
    }

    @Test public void testRemoveExplicit() {
        IntervalSet is = new IntervalSet();
        is.remove(42, 100); // nothing should happen, not even a crash
        assertEquals("[]", is.toString());
        is.add(10, 50);
        is.remove(5, 20);
        assertEquals("[21-50]", is.toString());
        is.remove(30, 33);
        assertEquals("[21-29, 34-50]", is.toString());
        is.remove(34, 37);
        assertEquals("[21-29, 38-50]", is.toString());
        is.remove(21, 29);
        assertEquals("[38-50]", is.toString());
        is.remove(45, 100);
        assertEquals("[38-44]", is.toString());
        is.remove(39, 42);
        assertEquals("[38, 43-44]", is.toString());
        is.remove(0, 42);
        assertEquals("[43-44]", is.toString());
        is.remove(44, 100);
        assertEquals("[43]", is.toString());
        is.remove(43);
        assertEquals("[]", is.toString());

        is.add(30, 66);
        is.remove(50);
        assertEquals("[30-49, 51-66]", is.toString());
        is.remove(40, 50);
        assertEquals("[30-39, 51-66]", is.toString());
        is.remove(55, 60);
        assertEquals("[30-39, 51-54, 61-66]", is.toString());
        is.remove(52, 64);
        assertEquals("[30-39, 51, 65-66]", is.toString());
        is.remove(35, 65);
        assertEquals("[30-34, 66]", is.toString());
        is.remove(20, 100);
        assertEquals("[]", is.toString());
    }

    @Test public void massTestOneHundred() {
        massTest(100, 1471632031L);
    }

    @Test public void massTestTenThousand() {
        massTest(10000, 1796090957L);
    }

    @Test public void massTestOneMillion() {
        massTest(1_000_000, 1101228305L);
    }

    private void massTest(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        IntervalSet is = new IntervalSet();
        int[] starts = new int[n], ends = new int[n];
        for(int i = 0; i < n; i++) {
            int start, end;
            if(i < 5 || rng.nextBoolean()) {
                start = rng.nextInt(20 * i + 10);
                end = start + rng.nextInt(5 * i + 10);
            }
            else {
                int js = rng.nextInt(i);
                int je = rng.nextInt(i);
                start = Math.max(0, starts[js] + rng.nextInt(3) - 1);
                end = Math.max(0, ends[je] + rng.nextInt(3) - 1);
                if(start > end) {
                    int tmp = start; start = end; end = tmp;
                }
            }
            starts[i] = start; ends[i] = end;
            check.update(is.contains(start, end) ? 42: 99);
            check.update(is.contains(start-3, start+3) ? 42: 99);
            check.update(is.contains(end-10, end+10) ? 42: 99);
            if(rng.nextBoolean()) {
                is.add(start, end);
                //System.out.println("Adding " + start + "-" + end + ": " + is);
            }
            else {
                is.remove(start, end);
                //System.out.println("Remove " + start + "-" + end + ": " + is);
            }
            try {
                check.update(is.toString().getBytes("UTF-8"));
            } catch(UnsupportedEncodingException ignored) {}
        }
        assertEquals(expected, check.getValue());
    }
}