import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.util.Random;
import java.util.PriorityQueue;

public class CalendarQueueTest {

    private static final String[] MESSAGES = {
            "Joe", "Moe", "Tom", "Dick", "Harry", "Larry", "Barry", "Zeppo"
    };

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
            // Update the current year and day to match the earliest event in the queue.
            if(goldQueue.size() > 0) {
                CalendarEvent first = goldQueue.poll();
                goldQueue.offer(first);
                CalendarEvent current = new CalendarEvent(currentYear, currentDay, "Zzz");
                if (first.compareTo(current) >= 0) {
                    currentYear = first.getYear();
                    currentDay = first.getDay();
                }
            }
            // Create some new events to be pushed in the calendar queue.
            int pushCount = round < n-1 ? 1 + rng.nextInt(pushScale) : 0;
            for(int p = 0; p < pushCount; p++) {
                int pushYear, pushDay;
                // 90% of events occur at the current day, 10% in the future.
                if(rng.nextInt(100) < 90) {
                    pushYear = currentYear;
                    pushDay = currentDay;
                }
                else {
                    pushYear = currentYear + 1 + rng.nextInt(yearScale);
                    pushDay = rng.nextInt(CalendarEvent.DAYS_IN_YEAR);
                }
                // Create the new event and push it in both queues.
                String message = MESSAGES[rng.nextInt(MESSAGES.length)];
                CalendarEvent event = new CalendarEvent(pushYear, pushDay, message);
                testQueue.push(event);
                goldQueue.offer(event);
                assertEquals(goldQueue.size(), testQueue.getSize());
                // Advance the day by random amount, possibly staying in the same day.
                currentDay += rng.nextInt(3);
                if(currentDay >= CalendarEvent.DAYS_IN_YEAR) {
                    currentDay = currentDay % CalendarEvent.DAYS_IN_YEAR;
                    currentYear++;
                }
            }
            // Pop out some events from both queues, and verify that they are pairwise equal.
            // In the last round, pop out everything that is left in both queues.
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
        // In the end, everything that went in must have come out.
        assertEquals(0, testQueue.getSize());
    }
}