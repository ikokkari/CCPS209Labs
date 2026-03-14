import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.PriorityQueue;

public class CalendarQueueTest {

    private static final String[] MESSAGES = {
            "Joe", "Moe", "Tom", "Dick", "Harry", "Larry", "Barry", "Zeppo"
    };

    // --- Explicit tests ---

    @Test public void testExplicit() {
        CalendarQueue queue = new CalendarQueue(0, 4);
        CalendarEvent e1 = new CalendarEvent(3, 5, "Joe");
        CalendarEvent e2 = new CalendarEvent(1, 5, "Moe");
        CalendarEvent e3 = new CalendarEvent(0, 4, "Larry");
        CalendarEvent e4 = new CalendarEvent(0, 4, "Curly");
        CalendarEvent e5 = new CalendarEvent(2, 5, "Tom");
        CalendarEvent e6 = new CalendarEvent(10, 7, "Dick");
        queue.push(e1);
        queue.push(e2);
        queue.push(e3);
        queue.push(e4);
        queue.push(e5);
        queue.push(e6);
        assertEquals(e4, queue.pop());
        assertEquals(e3, queue.pop());
        assertEquals(e2, queue.pop());
        assertEquals(e5, queue.pop());
        assertEquals(e1, queue.pop());
        assertEquals(e6, queue.pop());
    }

    @Test public void testSinglePushPop() {
        CalendarQueue q = new CalendarQueue(0, 0);
        CalendarEvent e = new CalendarEvent(0, 0, "hello");
        q.push(e);
        assertEquals(1, q.getSize());
        assertEquals(e, q.pop());
        assertEquals(0, q.getSize());
    }

    @Test public void testSameDaySortedByMessage() {
        // Same year, same day: events sorted lexicographically by message
        CalendarQueue q = new CalendarQueue(0, 0);
        CalendarEvent z = new CalendarEvent(0, 0, "Zoe");
        CalendarEvent a = new CalendarEvent(0, 0, "Alice");
        CalendarEvent b = new CalendarEvent(0, 0, "Bob");
        q.push(z);
        q.push(a);
        q.push(b);
        assertEquals(a, q.pop());
        assertEquals(b, q.pop());
        assertEquals(z, q.pop());
    }

    @Test public void testSameDayDifferentYears() {
        // Same day, different years: sorted by year
        CalendarQueue q = new CalendarQueue(2020, 5);
        CalendarEvent e2022 = new CalendarEvent(2022, 5, "A");
        CalendarEvent e2020 = new CalendarEvent(2020, 5, "A");
        CalendarEvent e2021 = new CalendarEvent(2021, 5, "A");
        q.push(e2022);
        q.push(e2020);
        q.push(e2021);
        assertEquals(e2020, q.pop());
        assertEquals(e2021, q.pop());
        assertEquals(e2022, q.pop());
    }

    @Test public void testDifferentDays() {
        // Events on different days within the same year
        CalendarQueue q = new CalendarQueue(0, 0);
        CalendarEvent d5 = new CalendarEvent(0, 5, "five");
        CalendarEvent d2 = new CalendarEvent(0, 2, "two");
        CalendarEvent d8 = new CalendarEvent(0, 8, "eight");
        q.push(d5);
        q.push(d2);
        q.push(d8);
        assertEquals(d2, q.pop());
        assertEquals(d5, q.pop());
        assertEquals(d8, q.pop());
    }

    @Test public void testYearRollover() {
        // Current day is 8, events span across year boundary
        CalendarQueue q = new CalendarQueue(0, 8);
        CalendarEvent nextYear = new CalendarEvent(1, 3, "next year");
        CalendarEvent thisYear = new CalendarEvent(0, 9, "this year");
        q.push(nextYear);
        q.push(thisYear);
        assertEquals(thisYear, q.pop());
        assertEquals(nextYear, q.pop());
    }

    @Test public void testPushAfterPop() {
        // Pop an event, then push a future event, then pop it
        CalendarQueue q = new CalendarQueue(0, 0);
        CalendarEvent first = new CalendarEvent(0, 0, "first");
        q.push(first);
        assertEquals(first, q.pop());
        CalendarEvent second = new CalendarEvent(0, 5, "second");
        q.push(second);
        assertEquals(second, q.pop());
    }

    @Test public void testDuplicateEvents() {
        // Duplicate events (same year, day, message) should both be stored and popped
        CalendarQueue q = new CalendarQueue(0, 0);
        CalendarEvent e1 = new CalendarEvent(0, 0, "dup");
        CalendarEvent e2 = new CalendarEvent(0, 0, "dup");
        q.push(e1);
        q.push(e2);
        assertEquals(2, q.getSize());
        assertEquals(e1, q.pop());
        assertEquals(e2, q.pop());
        assertEquals(0, q.getSize());
    }

    @Test public void testMessageTiebreak() {
        // Multiple events same day same year: message determines order
        CalendarQueue q = new CalendarQueue(2025, 3);
        q.push(new CalendarEvent(2025, 3, "Charlie"));
        q.push(new CalendarEvent(2025, 3, "Alice"));
        q.push(new CalendarEvent(2025, 3, "Bob"));
        q.push(new CalendarEvent(2025, 3, "Alice")); // duplicate
        assertEquals(new CalendarEvent(2025, 3, "Alice"), q.pop());
        assertEquals(new CalendarEvent(2025, 3, "Alice"), q.pop());
        assertEquals(new CalendarEvent(2025, 3, "Bob"), q.pop());
        assertEquals(new CalendarEvent(2025, 3, "Charlie"), q.pop());
    }

    @Test public void testSizeTracking() {
        CalendarQueue q = new CalendarQueue(0, 0);
        assertEquals(0, q.getSize());
        q.push(new CalendarEvent(0, 0, "a"));
        assertEquals(1, q.getSize());
        q.push(new CalendarEvent(0, 1, "b"));
        assertEquals(2, q.getSize());
        q.push(new CalendarEvent(0, 2, "c"));
        assertEquals(3, q.getSize());
        q.pop();
        assertEquals(2, q.getSize());
        q.pop();
        assertEquals(1, q.getSize());
        q.pop();
        assertEquals(0, q.getSize());
    }

    @Test public void testNearFutureBeforeFarFuture() {
        // Far future events should not interfere with near-future pops
        CalendarQueue q = new CalendarQueue(0, 0);
        q.push(new CalendarEvent(1000, 5, "far"));
        q.push(new CalendarEvent(0, 0, "now"));
        assertEquals(new CalendarEvent(0, 0, "now"), q.pop());
        assertEquals(new CalendarEvent(1000, 5, "far"), q.pop());
    }

    @Test public void testEventsOnEveryDay() {
        // One event on each of the DAYS_IN_YEAR days
        CalendarQueue q = new CalendarQueue(0, 0);
        for (int d = CalendarEvent.DAYS_IN_YEAR - 1; d >= 0; d--) {
            q.push(new CalendarEvent(0, d, "day" + d));
        }
        for (int d = 0; d < CalendarEvent.DAYS_IN_YEAR; d++) {
            CalendarEvent e = q.pop();
            assertEquals(d, e.getDay());
        }
    }

    @Test public void testMultipleYearsMultipleDays() {
        // Events spanning several years and days, pushed in reverse order
        CalendarQueue q = new CalendarQueue(0, 0);
        q.push(new CalendarEvent(2, 3, "last"));
        q.push(new CalendarEvent(0, 5, "first"));
        q.push(new CalendarEvent(1, 0, "second"));
        q.push(new CalendarEvent(1, 9, "third"));
        assertEquals(new CalendarEvent(0, 5, "first"), q.pop());
        assertEquals(new CalendarEvent(1, 0, "second"), q.pop());
        assertEquals(new CalendarEvent(1, 9, "third"), q.pop());
        assertEquals(new CalendarEvent(2, 3, "last"), q.pop());
    }

    @Test public void testCurrentDayNotZero() {
        // Queue starts at a non-zero day; events before currentDay in same year are unreachable
        // (spec says those won't be pushed, so just verify events at/after current day work)
        CalendarQueue q = new CalendarQueue(5, 7);
        q.push(new CalendarEvent(5, 7, "now"));
        q.push(new CalendarEvent(5, 9, "later"));
        q.push(new CalendarEvent(6, 2, "next year"));
        assertEquals(new CalendarEvent(5, 7, "now"), q.pop());
        assertEquals(new CalendarEvent(5, 9, "later"), q.pop());
        assertEquals(new CalendarEvent(6, 2, "next year"), q.pop());
    }

    // --- CRC fuzz tests ---

    @Test public void testOneHundred() { fuzzTest(100); }

    @Test public void testOneThousand() { fuzzTest(1000); }

    @Test public void testTenThousand() { fuzzTest(10000); }

    private void fuzzTest(int n) {
        Random rng = new Random(12345 + n);
        int currentYear = 2021, currentDay = 0;
        int count = 0, goal = 10, pushScale = 5, yearScale = 5;
        CalendarQueue testQueue = new CalendarQueue(currentYear, currentDay);
        PriorityQueue<CalendarEvent> goldQueue = new PriorityQueue<>();
        for(int round = 0; round < n; round++) {
            if(goldQueue.size() > 0) {
                CalendarEvent first = goldQueue.poll();
                goldQueue.offer(first);
                CalendarEvent current = new CalendarEvent(currentYear, currentDay, "\uffff");
                if (first.compareTo(current) >= 0) {
                    currentYear = first.getYear();
                    currentDay = first.getDay();
                }
            }
            int pushCount = round < n-1 ? 1 + rng.nextInt(pushScale) : 0;
            for(int p = 0; p < pushCount; p++) {
                int pushYear, pushDay;
                if(rng.nextInt(100) < 90) {
                    pushYear = currentYear;
                    pushDay = currentDay;
                }
                else {
                    pushYear = currentYear + 1 + rng.nextInt(yearScale);
                    pushDay = rng.nextInt(CalendarEvent.DAYS_IN_YEAR);
                }
                String message = MESSAGES[rng.nextInt(MESSAGES.length)];
                CalendarEvent event = new CalendarEvent(pushYear, pushDay, message);
                testQueue.push(event);
                goldQueue.offer(event);
                assertEquals(goldQueue.size(), testQueue.getSize());
                currentDay += rng.nextInt(3);
                if(currentDay >= CalendarEvent.DAYS_IN_YEAR) {
                    currentDay = currentDay % CalendarEvent.DAYS_IN_YEAR;
                    currentYear++;
                }
            }
            int popCount = round == n-1 ? goldQueue.size() : Math.min(rng.nextInt(pushScale), goldQueue.size() - 1);
            for(int p = 0; p < popCount; p++) {
                CalendarEvent expected = goldQueue.poll();
                CalendarEvent actual = testQueue.pop();
                assertEquals(expected, actual);
                assertEquals(goldQueue.size(), testQueue.getSize());
            }
            if(++count == goal) {
                count = 0;
                goal = 2 * goal;
                pushScale = 3 * pushScale / 2;
                yearScale = 3 * yearScale / 2;
            }
        }
        assertEquals(0, testQueue.getSize());
    }
}