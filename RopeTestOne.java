import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class RopeTestOne {

    @Test public void testSimpleKnownCases() {
        String s1 = "hello";
        String s2 = "world";

        // Turning ordinary strings into ropes
        Rope r1 = new StringRope(s1); // "hello"
        Rope r2 = new StringRope(s2); // "world"
        assertEquals(s1.length(), r1.length());
        assertEquals(s1, r1.toString());
        assertEquals('e', r1.charAt(1));
        assertEquals('d', r2.charAt(4));

        // Rope concatenation
        Rope r3 = new ConcatRope(r1, r2); // "helloworld"
        Rope r4 = new ConcatRope(r3, r3); // "helloworldhelloworld"
        assertEquals(s1.length() + s2.length(), r3.length());
        assertEquals(s1 + s2, r3.toString());
        assertEquals(2 * r3.length(), r4.length());
        assertEquals(s1 + s2 + s1 + s2, r4.toString());

        // Subrope extraction
        Rope r5 = new SubRope(r3, 2, 8); // llowor
        assertEquals(8 - 2, r5.length());
        assertEquals("llowor", r5.toString());
        assertEquals('o', r5.charAt(2));
        assertEquals('o', r5.charAt(4));

        // Special case of slicing out an empty subrope.
        Rope r6 = new SubRope(r4, 15, 15);
        assertEquals(0, r6.length());
        assertEquals("", r6.toString());
        // Even from the empty rope, an empty subrope can be extracted.
        Rope r7 = new SubRope(r6, 0, 0);
        assertEquals(0, r7.length());
        assertEquals("", r7.toString());

        // Let's finish off this test in style with a humongous instance of Rope.
        Rope giant = r3;
        int expectedLength = r3.length();
        while(giant.length() < 1_000_000_000) {
            giant = new ConcatRope(giant, giant);
            expectedLength *= 2;
            assertEquals(expectedLength, giant.length());
        }
        // The resulting giant rope is a sequence of exactly 1,342,177,280 characters.
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

    @Test public void testExceptions() {
        Rope r1 = new StringRope("Learning the ropes");
        testExceptions(r1);

        Rope r2 = new ConcatRope(r1, r1); // "Learning the ropesLearning the ropes"
        testExceptions(r2);

        Rope r3 = new SubRope(r1, 4, 12); // "ning the"
        testExceptions(r3);

        Rope r4 = new ConcatRope(r1, r2);
        testExceptions(r4);

        try {
            // This one should not fail, but extract an empty subrope.
            Rope r5 = new SubRope(r1, r1.length(), r1.length());
        }
        catch(IndexOutOfBoundsException ignored) {
            fail();
        }
    }

    private void testExceptions(Rope r) {
        try { // Negative index must throw IndexOutOfBoundsException.
            r.charAt(-1);
            fail(); // This line must be unreachable
        }
        catch(IndexOutOfBoundsException ignored) {}
        try { // Index greater or equal to length must throw IndexOutOfBoundsException.
            r.charAt(r.length());
            fail(); // This line must be unreachable
        }
        catch(IndexOutOfBoundsException ignored) {}
    }

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
        // RIP Meatloaf,  January 20, 2022
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
                // Concatenate the ropes 75% of the time, but only if the result length fits in an int.
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
            // System.out.println(i + ": (" + ropes[i].length() + ") <" + ropes[i] + ">");
            int len = ropes[i].length();
            // Since these ropes can get pretty long, we'll just randomly sample some characters.
            int k = 0, step = len / 4 + 1;
            while(k < len) {
                check.update(ropes[i].charAt(k));
                k += rng.nextInt(step) + 1;
            }
        }
        assertEquals(expected, check.getValue());
    }

}
