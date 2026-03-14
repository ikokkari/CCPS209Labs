import org.junit.Test;
import java.util.Random;
import java.util.zip.CRC32;
import java.util.HashSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class RopeTestTwo {

    // --- equals explicit tests ---

    @Test public void testEqualsExplicit() {
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("world");
        assertEquals(r1, r1);
        assertEquals(r2, r2);
        assertFalse(r1.equals(r2));
        Rope r3 = new StringRope("helloworld");
        Rope r4 = new ConcatRope(r1, r2);
        assertEquals(r3, r4);
        Rope r5 = new SubRope(r3, 3, 8);
        Rope r6 = new StringRope("lowor");
        assertEquals(r5, r6);
        assertFalse(r1.equals(r6));
        assertFalse(r2.equals(r6));
    }

    @Test public void testEqualsReflexive() {
        Rope r = new ConcatRope(new StringRope("abc"), new StringRope("def"));
        assertEquals(r, r);
    }

    @Test public void testEqualsNullAndNonRope() {
        Rope r = new StringRope("hello");
        assertFalse(r.equals(null));
        assertFalse(r.equals("hello")); // String is not a Rope
        assertFalse(r.equals(42));
    }

    @Test public void testEqualsSameContentDifferentStructure() {
        // Three different ways to build "abcdef"
        Rope r1 = new StringRope("abcdef");
        Rope r2 = new ConcatRope(new StringRope("abc"), new StringRope("def"));
        Rope r3 = new SubRope(new StringRope("XXabcdefYY"), 2, 8);
        assertEquals(r1, r2);
        assertEquals(r2, r3);
        assertEquals(r1, r3);
    }

    @Test public void testEqualsDifferentLengths() {
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("hell");
        assertNotEquals(r1, r2);
        assertNotEquals(r2, r1);
    }

    @Test public void testEqualsEmptyRopes() {
        Rope e1 = new StringRope("");
        Rope e2 = new SubRope(new StringRope("abc"), 1, 1);
        assertEquals(e1, e2);
        // Empty not equal to non-empty
        assertNotEquals(e1, new StringRope("a"));
    }

    @Test public void testEqualsSamePrefix() {
        // Same prefix, different last character
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("hella");
        assertNotEquals(r1, r2);
    }

    // --- hashCode explicit tests ---

    @Test public void testHashCodeConsistentWithEquals() {
        Rope r1 = new StringRope("helloworld");
        Rope r2 = new ConcatRope(new StringRope("hello"), new StringRope("world"));
        assertEquals(r1, r2);
        assertEquals(r1.hashCode(), r2.hashCode());

        Rope r3 = new SubRope(new StringRope("XXhelloworldXX"), 2, 12);
        assertEquals(r1, r3);
        assertEquals(r1.hashCode(), r3.hashCode());
    }

    @Test public void testHashCodeCached() {
        Rope r = new StringRope("test");
        int h1 = r.hashCode();
        int h2 = r.hashCode();
        assertEquals(h1, h2); // Must return same value every time
    }

    @Test public void testHashCodeNonNegative() {
        // Hash codes should be non-negative (the implementation chops sign bit)
        String[] tests = {"", "hello", "world", "a", "zzzzz", "ABCDEF"};
        for (String s : tests) {
            Rope r = new StringRope(s);
            assertTrue("hashCode should be non-negative for '" + s + "'",
                    r.hashCode() >= 0);
        }
    }

    @Test public void testHashCodeDifferentForDifferentContent() {
        // While not guaranteed, different short strings should usually hash differently
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("world");
        // These two specific strings do hash differently
        assertNotEquals(r1.hashCode(), r2.hashCode());
    }

    // --- compareTo explicit tests ---

    @Test public void testCompareExplicit() {
        Rope r1 = new StringRope("hello");
        Rope r2 = new StringRope("hella");
        assertTrue(r1.compareTo(r2) > 0);
        assertTrue(r2.compareTo(r1) < 0);
        assertTrue(r1.compareTo(r1) == 0);
        assertTrue(r2.compareTo(r2) == 0);

        Rope r3 = new ConcatRope(r1, r2); // "hellohella"
        Rope r4 = new ConcatRope(r2, r1); // "hellahello"
        assertTrue(r1.compareTo(r3) < 0);
        assertTrue(r2.compareTo(r3) < 0);
        assertTrue(r3.compareTo(r1) > 0);
        assertTrue(r3.compareTo(r2) > 0);
        assertTrue(r3.compareTo(r4) > 0);
        assertTrue(r4.compareTo(r3) < 0);

        Rope r5 = new SubRope(r3, 0, 5); // "hello"
        Rope r6 = new SubRope(r3, 5, 9); // "hell"
        assertTrue(r5.compareTo(r6) > 0);
        assertTrue(r6.compareTo(r5) < 0);
        assertTrue(r1.compareTo(r5) == 0);
        assertTrue(r1.compareTo(r6) > 0);
        assertTrue(r2.compareTo(r6) > 0);
    }

    @Test public void testCompareEmptyRopes() {
        Rope empty1 = new StringRope("");
        Rope empty2 = new SubRope(new StringRope("abc"), 2, 2);
        Rope nonEmpty = new StringRope("a");

        assertEquals(0, empty1.compareTo(empty2));
        assertTrue(nonEmpty.compareTo(empty1) > 0);
        assertTrue(empty1.compareTo(nonEmpty) < 0);
    }

    @Test public void testComparePrefixRelation() {
        // A proper prefix is always less than the full string
        Rope abc = new StringRope("abc");
        Rope abcd = new StringRope("abcd");
        assertTrue(abc.compareTo(abcd) < 0);
        assertTrue(abcd.compareTo(abc) > 0);
    }

    @Test public void testCompareSingleCharacters() {
        Rope a = new StringRope("a");
        Rope b = new StringRope("b");
        Rope z = new StringRope("z");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(a) > 0);
        assertTrue(a.compareTo(z) < 0);
        assertEquals(0, a.compareTo(new StringRope("a")));
    }

    @Test public void testCompareAcrossRopeTypes() {
        // Same content via different rope types should compare as equal
        Rope s = new StringRope("abc");
        Rope c = new ConcatRope(new StringRope("a"), new StringRope("bc"));
        Rope sub = new SubRope(new StringRope("xabcy"), 1, 4);
        assertEquals(0, s.compareTo(c));
        assertEquals(0, c.compareTo(sub));
        assertEquals(0, s.compareTo(sub));
    }

    @Test public void testCompareTransitivity() {
        Rope a = new StringRope("abc");
        Rope b = new StringRope("abd");
        Rope c = new StringRope("abe");
        assertTrue(a.compareTo(b) < 0);
        assertTrue(b.compareTo(c) < 0);
        assertTrue(a.compareTo(c) < 0); // transitivity
    }

    @Test public void testCompareAntiSymmetry() {
        Rope r1 = new ConcatRope(new StringRope("ab"), new StringRope("z"));
        Rope r2 = new StringRope("aba");
        // 'z' > 'a' at position 2
        assertTrue(r1.compareTo(r2) > 0);
        assertTrue(r2.compareTo(r1) < 0);
    }

    @Test public void testCompareConsistentWithEquals() {
        // compareTo returns 0 if and only if equals returns true
        Rope r1 = new StringRope("test");
        Rope r2 = new ConcatRope(new StringRope("te"), new StringRope("st"));
        assertEquals(0, r1.compareTo(r2));
        assertTrue(r1.equals(r2));

        Rope r3 = new StringRope("tess");
        assertNotEquals(0, r1.compareTo(r3));
        assertFalse(r1.equals(r3));
    }

    // --- HashSet and TreeSet integration ---

    @Test public void testHashSetContains() {
        HashSet<Rope> set = new HashSet<>();
        Rope r1 = new StringRope("hello");
        set.add(r1);
        // A structurally different rope with same content should be found
        Rope r2 = new ConcatRope(new StringRope("hel"), new StringRope("lo"));
        assertTrue(set.contains(r2));
        // Different content should not be found
        assertFalse(set.contains(new StringRope("world")));
    }

    @Test public void testTreeSetOrdering() {
        TreeSet<Rope> set = new TreeSet<>();
        set.add(new StringRope("banana"));
        set.add(new StringRope("apple"));
        set.add(new StringRope("cherry"));
        set.add(new ConcatRope(new StringRope("ba"), new StringRope("nana"))); // duplicate
        assertEquals(3, set.size()); // duplicate removed
        assertEquals("apple", set.first().toString());
        assertEquals("cherry", set.last().toString());
    }

    // --- CRC mass tests ---

    @Test public void massTestComparisonsHundred() {
        massTestComparisons(100, 1600454116L);
    }

    @Test public void massTestComparisonsTenThousand() {
        massTestComparisons(10_000, 2319695085L);
    }

    @Test public void massTestComparisonsHundredThousand() {
        massTestComparisons(100_000, 2947062926L);
    }

    private void massTestComparisons(int n, long expected) {
        Random rng = new Random(12345 + n);
        CRC32 check = new CRC32();
        Rope[] ropes = new Rope[n];
        String[] initial = {
                "aaaaa", "abab", "babb", "bbbaba"
        };
        final int MAXLEN = 1000;

        HashSet<Rope> ropeHash = new HashSet<>();
        TreeSet<Rope> ropeTree = new TreeSet<>();

        for(int i = 0; i < n; i++) {
            if(i < initial.length) {
                ropes[i] = new StringRope(initial[i]);
            }
            else {
                Rope r1 = ropes[rng.nextInt(i)];
                Rope r2 = ropes[rng.nextInt(i)];
                if(rng.nextInt(100) < 50 && r1.length() < MAXLEN && r2.length() < MAXLEN) {
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
            ropeHash.add(ropes[i]);
            ropeTree.add(ropes[i]);
            assertFalse(ropeHash.size() < ropeTree.size());
            assertFalse(ropeHash.size() > ropeTree.size());
            if(i > 0) {
                check.update(ropes[i].compareTo(ropes[i-1]) < 0 ? 42: 99);
            }
        }
        assertEquals(expected, check.getValue());
    }
}