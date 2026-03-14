import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class RopeTestOne {

    // --- StringRope explicit tests ---

    @Test public void testStringRopeBasics() {
        Rope r = new StringRope("hello");
        assertEquals(5, r.length());
        assertEquals("hello", r.toString());
        assertEquals('h', r.charAt(0));
        assertEquals('o', r.charAt(4));
    }

    @Test public void testStringRopeEmpty() {
        Rope r = new StringRope("");
        assertEquals(0, r.length());
        assertEquals("", r.toString());
    }

    @Test public void testStringRopeSingleChar() {
        Rope r = new StringRope("x");
        assertEquals(1, r.length());
        assertEquals('x', r.charAt(0));
    }

    // --- ConcatRope explicit tests ---

    @Test public void testConcatRopeBasics() {
        Rope left = new StringRope("hello");
        Rope right = new StringRope("world");
        Rope concat = new ConcatRope(left, right);
        assertEquals(10, concat.length());
        assertEquals("helloworld", concat.toString());
    }

    @Test public void testConcatRopeCharAtBoundary() {
        Rope left = new StringRope("hello");
        Rope right = new StringRope("world");
        Rope concat = new ConcatRope(left, right);
        // Last char of left piece
        assertEquals('o', concat.charAt(4));
        // First char of right piece
        assertEquals('w', concat.charAt(5));
    }

    @Test public void testConcatRopeWithEmptyLeft() {
        Rope empty = new StringRope("");
        Rope right = new StringRope("abc");
        Rope concat = new ConcatRope(empty, right);
        assertEquals(3, concat.length());
        assertEquals("abc", concat.toString());
        assertEquals('a', concat.charAt(0));
    }

    @Test public void testConcatRopeWithEmptyRight() {
        Rope left = new StringRope("abc");
        Rope empty = new StringRope("");
        Rope concat = new ConcatRope(left, empty);
        assertEquals(3, concat.length());
        assertEquals("abc", concat.toString());
    }

    @Test public void testConcatRopeNested() {
        Rope r1 = new StringRope("ab");
        Rope r2 = new StringRope("cd");
        Rope r3 = new StringRope("ef");
        Rope c1 = new ConcatRope(r1, r2); // "abcd"
        Rope c2 = new ConcatRope(c1, r3); // "abcdef"
        assertEquals(6, c2.length());
        assertEquals("abcdef", c2.toString());
        assertEquals('a', c2.charAt(0));
        assertEquals('d', c2.charAt(3));
        assertEquals('f', c2.charAt(5));
    }

    @Test public void testConcatRopeDoubled() {
        Rope r = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        Rope doubled = new ConcatRope(r, r);
        assertEquals(20, doubled.length());
        assertEquals("helloworldhelloworld", doubled.toString());
        // Chars from second copy
        assertEquals('h', doubled.charAt(10));
        assertEquals('d', doubled.charAt(19));
    }

    // --- SubRope explicit tests ---

    @Test public void testSubRopeBasics() {
        Rope r = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        Rope sub = new SubRope(r, 2, 8); // "llowor"
        assertEquals(6, sub.length());
        assertEquals("llowor", sub.toString());
        assertEquals('l', sub.charAt(0));
        assertEquals('r', sub.charAt(5));
    }

    @Test public void testSubRopeEmpty() {
        Rope r = new StringRope("hello");
        // Empty subrope in the middle
        Rope empty = new SubRope(r, 3, 3);
        assertEquals(0, empty.length());
        assertEquals("", empty.toString());
        // Empty subrope at end (start == end == length)
        Rope emptyEnd = new SubRope(r, 5, 5);
        assertEquals(0, emptyEnd.length());
    }

    @Test public void testSubRopeEmptyFromEmpty() {
        Rope empty1 = new SubRope(new StringRope("hello"), 3, 3);
        Rope empty2 = new SubRope(empty1, 0, 0);
        assertEquals(0, empty2.length());
        assertEquals("", empty2.toString());
    }

    @Test public void testSubRopeFullLength() {
        Rope r = new StringRope("hello");
        Rope full = new SubRope(r, 0, 5);
        assertEquals(5, full.length());
        assertEquals("hello", full.toString());
    }

    @Test public void testSubRopeCrossingConcatBoundary() {
        Rope r = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        // Subrope crossing the boundary between left and right
        Rope cross = new SubRope(r, 3, 7); // "lowo"
        assertEquals(4, cross.length());
        assertEquals("lowo", cross.toString());
    }

    @Test public void testSubRopeEntirelyInLeft() {
        Rope r = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        Rope left = new SubRope(r, 0, 3); // "hel"
        assertEquals("hel", left.toString());
    }

    @Test public void testSubRopeEntirelyInRight() {
        Rope r = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        Rope right = new SubRope(r, 7, 10); // "rld"
        assertEquals("rld", right.toString());
    }

    @Test public void testSubRopeOfSubRope() {
        Rope r = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        Rope sub1 = new SubRope(r, 2, 8); // "llowor"
        Rope sub2 = new SubRope(sub1, 1, 4); // "low"
        assertEquals(3, sub2.length());
        assertEquals("low", sub2.toString());
    }

    // --- Exception tests ---

    @Test public void testExceptions() {
        Rope r1 = new StringRope("Learning the ropes");
        testCharAtExceptions(r1);

        Rope r2 = new ConcatRope(r1, r1);
        testCharAtExceptions(r2);

        Rope r3 = new SubRope(r1, 4, 12); // "ning the"
        testCharAtExceptions(r3);

        Rope r4 = new ConcatRope(r1, r2);
        testCharAtExceptions(r4);

        try {
            // Empty subrope at end: should NOT throw
            new SubRope(r1, r1.length(), r1.length());
        } catch(IndexOutOfBoundsException ignored) {
            fail("Empty subrope at end should not throw");
        }
    }

    @Test public void testSubRopeConstructorExceptions() {
        Rope r = new StringRope("hello");
        // start < 0
        try { new SubRope(r, -1, 3); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
        // start > end
        try { new SubRope(r, 4, 2); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
        // end > length
        try { new SubRope(r, 0, 6); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
    }

    @Test public void testStringRopeCharAtException() {
        Rope r = new StringRope("abc");
        try { r.charAt(-1); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
        try { r.charAt(3); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
    }

    @Test public void testSubRopeCharAtException() {
        Rope r = new SubRope(new StringRope("hello"), 1, 4); // "ell"
        try { r.charAt(-1); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
        try { r.charAt(3); fail(); }
        catch (IndexOutOfBoundsException ignored) {}
    }

    private void testCharAtExceptions(Rope r) {
        try { r.charAt(-1); fail(); }
        catch(IndexOutOfBoundsException ignored) {}
        try { r.charAt(r.length()); fail(); }
        catch(IndexOutOfBoundsException ignored) {}
    }

    // --- equals, hashCode, compareTo ---

    @Test public void testEqualsAndHashCode() {
        // Same content built via different structures
        Rope r1 = new StringRope("helloworld");
        Rope r2 = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        // SubRope equals original content
        Rope r3 = new SubRope(new StringRope("XXhelloworldXX"), 2, 12);
        assertEquals(r1, r3);
    }

    @Test public void testNotEquals() {
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("world");
        assertNotEquals(r1, r2);

        // Different length
        Rope r3 = new StringRope("hell");
        assertNotEquals(r1, r3);
    }

    @Test public void testCompareTo() {
        Rope abc = new StringRope("abc");
        Rope abd = new StringRope("abd");
        Rope abcd = new StringRope("abcd");
        Rope abc2 = new ConcatRope(new StringRope("ab"), new StringRope("c"));

        assertTrue(abc.compareTo(abd) < 0);
        assertTrue(abd.compareTo(abc) > 0);
        assertTrue(abc.compareTo(abcd) < 0); // prefix is less
        assertTrue(abcd.compareTo(abc) > 0);
        assertEquals(0, abc.compareTo(abc2)); // equal content
    }

    // --- Giant rope test ---

    @Test public void testSimpleKnownCases() {
        String s1 = "hello";
        String s2 = "world";

        Rope r1 = new StringRope(s1);
        Rope r2 = new StringRope(s2);
        assertEquals(s1.length(), r1.length());
        assertEquals(s1, r1.toString());
        assertEquals('e', r1.charAt(1));
        assertEquals('d', r2.charAt(4));

        Rope r3 = new ConcatRope(r1, r2);
        Rope r4 = new ConcatRope(r3, r3);
        assertEquals(s1.length() + s2.length(), r3.length());
        assertEquals(s1 + s2, r3.toString());
        assertEquals(2 * r3.length(), r4.length());
        assertEquals(s1 + s2 + s1 + s2, r4.toString());

        Rope r5 = new SubRope(r3, 2, 8);
        assertEquals(8 - 2, r5.length());
        assertEquals("llowor", r5.toString());
        assertEquals('o', r5.charAt(2));
        assertEquals('o', r5.charAt(4));

        Rope r6 = new SubRope(r4, 15, 15);
        assertEquals(0, r6.length());
        assertEquals("", r6.toString());
        Rope r7 = new SubRope(r6, 0, 0);
        assertEquals(0, r7.length());
        assertEquals("", r7.toString());

        // Giant rope with over a billion characters
        Rope giant = r3;
        int expectedLength = r3.length();
        while(giant.length() < 1_000_000_000) {
            giant = new ConcatRope(giant, giant);
            expectedLength *= 2;
            assertEquals(expectedLength, giant.length());
        }
        assertEquals(1342177280, giant.length());
        assertEquals('h', giant.charAt(0));
        assertEquals('e', giant.charAt(11));
        assertEquals('l', giant.charAt(5432));
        assertEquals('l', giant.charAt(39393));
        assertEquals('o', giant.charAt(444444));
        assertEquals('w', giant.charAt(998855));
        assertEquals('o', giant.charAt(4398536));
        assertEquals('r', giant.charAt(7777777));
        assertEquals('l', giant.charAt(81828388));
        assertEquals('d', giant.charAt(999999999));
    }

    // --- CRC mass tests ---

    @Test public void testOneHundred() {
        test(100, 4028914464L);
    }

    @Test public void testOneThousand() {
        test(1000, 3273852294L);
    }

    @Test public void testOneMillion() {
        test(1_000_000, 2206054464L);
    }

    private void test(int n, long expected) {
        String[] initial = {
                "Special olympians are the real heroes.",
                "I'm gonna give 'em everything I've got.",
                "But who's gonna help me?",
                "We'll help, Meatloaf. But how?",
                "By returning this coupon today!"
        };
        final int MAXLEN = 1_000_000_000;
        Rope[] ropes = new Rope[n];
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        for(int i = 0; i < n; i++) {
            if(i < initial.length) {
                ropes[i] = new StringRope(initial[i]);
            }
            else {
                Rope r1 = ropes[rng.nextInt(i)];
                Rope r2 = ropes[rng.nextInt(i)];
                if(rng.nextInt(100) < 75 && r1.length() < MAXLEN && r2.length() < MAXLEN) {
                    ropes[i] = new ConcatRope(r1, r2);
                }
                else {
                    int len = r1.length();
                    int start, end;
                    if(len > 0) {
                        start = rng.nextInt(len);
                        end = start + rng.nextInt(len - start);
                    }
                    else {
                        end = start = 0;
                    }
                    ropes[i] = new SubRope(r1, start, end);
                }
            }
            int len = ropes[i].length();
            int k = 0, step = len / 4 + 1;
            while(k < len) {
                check.update(ropes[i].charAt(k));
                k += rng.nextInt(step) + 1;
            }
        }
        assertEquals(expected, check.getValue());
    }
}