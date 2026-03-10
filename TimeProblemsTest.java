import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class TimeProblemsTest {

    private static final int[] daysInMonth = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    // --- countFridayThirteens explicit tests ---

    @Test public void testCountFridayThirteensExplicit() {
        // No Friday the 13th in this range (Jan-Apr 2022)
        LocalDate startDate0 = LocalDate.of(2022, 1, 1);
        LocalDate endDate0 = LocalDate.of(2022, 4, 30);
        assertEquals(0, TimeProblems.countFridayThirteens(startDate0, endDate0));

        // Single day that IS Friday the 13th (May 13 2022 was Friday)
        LocalDate startDate1 = LocalDate.of(2022, 5, 13);
        LocalDate endDate1 = LocalDate.of(2022, 5, 13);
        assertEquals(1, TimeProblems.countFridayThirteens(startDate1, endDate1));

        LocalDate startDate2 = LocalDate.of(1905, 4, 13);
        LocalDate endDate2 = LocalDate.of(1905, 11, 9);
        assertEquals(1, TimeProblems.countFridayThirteens(startDate2, endDate2));

        LocalDate startDate3 = LocalDate.of(1912, 12, 21);
        LocalDate endDate3 = LocalDate.of(1956, 12, 22);
        assertEquals(75, TimeProblems.countFridayThirteens(startDate3, endDate3));

        LocalDate startDate4 = LocalDate.of(1901, 9, 13);
        LocalDate endDate4 = LocalDate.of(1912, 1, 13);
        assertEquals(19, TimeProblems.countFridayThirteens(startDate4, endDate4));

        LocalDate startDate5 = LocalDate.of(2187, 6, 29);
        LocalDate endDate5 = LocalDate.of(2283, 4, 13);
        assertEquals(166, TimeProblems.countFridayThirteens(startDate5, endDate5));

        LocalDate startDate6 = LocalDate.of(1936, 4, 17);
        LocalDate endDate6 = LocalDate.of(1941, 6, 13);
        assertEquals(8, TimeProblems.countFridayThirteens(startDate6, endDate6));

        LocalDate startDate7 = LocalDate.of(1982, 8, 13);
        LocalDate endDate7 = LocalDate.of(2012, 12, 21);
        assertEquals(53, TimeProblems.countFridayThirteens(startDate7, endDate7));

        LocalDate startDate8 = LocalDate.of(2164, 4, 13);
        LocalDate endDate8 = LocalDate.of(2337, 8, 13);
        assertEquals(301, TimeProblems.countFridayThirteens(startDate8, endDate8));

        // He's taken everything this old earth can give...
        LocalDate startDate9 = LocalDate.of(2525, 1, 1);
        LocalDate endDate9 = LocalDate.of(9595, 12, 31);
        assertEquals(12163, TimeProblems.countFridayThirteens(startDate9, endDate9));

        // Era of fossil fuels depletion before the coming of the Patagonian civilization
        LocalDate startDate10 = LocalDate.of(6200, 1, 1);
        LocalDate endDate10 = LocalDate.of(106200, 12, 31);
        assertEquals(172001, TimeProblems.countFridayThirteens(startDate10, endDate10));
    }

    @Test public void testCountFridayThirteensBoundaryInclusion() {
        // Start exactly on a Friday the 13th: it should be counted (inclusive)
        // Sep 13, 2013 was a Friday
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(2013, 9, 13), LocalDate.of(2013, 9, 30)));

        // End exactly on a Friday the 13th: it should be counted (inclusive)
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(2013, 9, 1), LocalDate.of(2013, 9, 13)));

        // Start one day AFTER a Friday the 13th: should miss it
        assertEquals(0, TimeProblems.countFridayThirteens(
                LocalDate.of(2013, 9, 14), LocalDate.of(2013, 9, 30)));

        // End one day BEFORE a Friday the 13th: should miss it
        assertEquals(0, TimeProblems.countFridayThirteens(
                LocalDate.of(2013, 9, 1), LocalDate.of(2013, 9, 12)));

        // Single day range that is the 13th but NOT a Friday (Sep 13, 2012 = Thursday)
        assertEquals(0, TimeProblems.countFridayThirteens(
                LocalDate.of(2012, 9, 13), LocalDate.of(2012, 9, 13)));

        // Single day range that is a Friday but NOT the 13th
        assertEquals(0, TimeProblems.countFridayThirteens(
                LocalDate.of(2013, 9, 6), LocalDate.of(2013, 9, 6)));
    }

    @Test public void testCountFridayThirteensFullYear() {
        // Every calendar year has at least one and at most three Friday the 13ths.
        // 2015 had three: Feb 13, Mar 13, Nov 13
        assertEquals(3, TimeProblems.countFridayThirteens(
                LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 31)));

        // 2016 had one: May 13
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 31)));

        // 2017 had two: Jan 13, Oct 13
        assertEquals(2, TimeProblems.countFridayThirteens(
                LocalDate.of(2017, 1, 1), LocalDate.of(2017, 12, 31)));

        // 2023 had two: Jan 13, Oct 13
        assertEquals(2, TimeProblems.countFridayThirteens(
                LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31)));

        // 2024 had two: Sep 13, Dec 13
        assertEquals(2, TimeProblems.countFridayThirteens(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31)));
    }

    @Test public void testCountFridayThirteensLeapYear() {
        // Feb 13 2004 was a Friday, and 2004 is a leap year
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(2004, 2, 1), LocalDate.of(2004, 2, 29)));

        // Feb 13 2032 is a Friday in a leap year
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(2032, 2, 13), LocalDate.of(2032, 2, 13)));

        // Century years: 1900 not leap, 2000 is leap
        // Feb 13, 1903 was a Friday (non-leap year)
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(1903, 2, 1), LocalDate.of(1903, 2, 28)));
    }

    @Test public void testCountFridayThirteensSameMonth() {
        // Range entirely within a month that contains a Friday 13th
        // but the range excludes it
        // Aug 13, 2021 was a Friday
        assertEquals(0, TimeProblems.countFridayThirteens(
                LocalDate.of(2021, 8, 14), LocalDate.of(2021, 8, 31)));
        assertEquals(1, TimeProblems.countFridayThirteens(
                LocalDate.of(2021, 8, 1), LocalDate.of(2021, 8, 31)));

        // Range within a month with no 13th being Friday
        assertEquals(0, TimeProblems.countFridayThirteens(
                LocalDate.of(2021, 7, 1), LocalDate.of(2021, 7, 31)));
    }

    // --- countFridayThirteens mass/CRC test ---

    @Test public void testCountFridayThirteensMass() {
        Random rng = new Random(4242);
        CRC32 check = new CRC32();
        for(int i = 0; i < 3000; i++) {
            int y1 = 1900 + rng.nextInt(1 + (i+2)/5);
            int y2 = y1 + rng.nextInt(3 + i/300);
            int m1 = rng.nextInt(12) + 1;
            int m2 = rng.nextInt(12) + 1;
            int d1 = rng.nextInt(100) < 50 ? 12 + rng.nextInt(3) : rng.nextInt(daysInMonth[m1]) + 1;
            int d2 = rng.nextInt(100) < 50 ? 12 + rng.nextInt(3) : rng.nextInt(daysInMonth[m2]) + 1;
            LocalDate startDate = LocalDate.of(y1, m1, d1);
            LocalDate endDate = LocalDate.of(y2, m2, d2);
            if(startDate.compareTo(endDate) > 0) {
                LocalDate tmp = startDate; startDate = endDate; endDate = tmp;
            }
            int result = TimeProblems.countFridayThirteens(startDate, endDate);
            check.update(result);
        }
        assertEquals(2254518609L, check.getValue());
    }

    // --- dayAfterSeconds explicit tests ---

    @Test public void testDayAfterSecondsExplicit() {
        // Zero seconds: same day
        // Jan 1, 2020 was a Wednesday
        assertEquals("WEDNESDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 1, 12, 0, 0), 0));

        // Exactly one day = 86400 seconds later
        assertEquals("THURSDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), 86400));

        // One second before midnight: still same day
        assertEquals("WEDNESDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 1, 23, 59, 58), 1));

        // One second that crosses midnight: next day
        assertEquals("THURSDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 1, 23, 59, 59), 1));

        // Exactly one week = 604800 seconds: same day of week
        assertEquals("WEDNESDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), 604800));

        // Gigasecond (10^9 seconds) from Jan 1, 2020 midnight
        // 10^9 seconds = 11574 days + 1 hour 46 min 40 sec
        // 11574 days = 1653 weeks + 3 days => Wednesday + 3 = Saturday
        assertEquals("SATURDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 1, 0, 0, 0), 1_000_000_000L));

        // Negative seconds: going back in time
        // Go back one day from Thursday to Wednesday
        assertEquals("WEDNESDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 2, 12, 0, 0), -86400));

        // Go back one second from midnight Thursday into Wednesday
        assertEquals("WEDNESDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 1, 2, 0, 0, 0), -1));

        // Large negative: go back a gigasecond
        // From Saturday Jul 18, 2051 back 10^9 seconds = Jan 1, 2020 = Wednesday
        assertEquals("WEDNESDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2051, 9, 9, 1, 46, 40), -1_000_000_000L));
    }

    @Test public void testDayAfterSecondsLeapDay() {
        // Feb 28, 2020 (leap year) at 23:00 + 3600 seconds = Feb 29 (Saturday)
        // Feb 28, 2020 was a Friday
        assertEquals("SATURDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2020, 2, 28, 23, 0, 0), 3600));

        // Feb 28, 2019 (non-leap) at 23:00 + 3600 seconds = Mar 1 (Friday)
        // Feb 28, 2019 was a Thursday
        assertEquals("FRIDAY", TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2019, 2, 28, 23, 0, 0), 3600));
    }

    @Test public void testDayAfterSecondsLargeValues() {
        // Very large number of seconds: about 100 years forward
        // 100 years ~ 3_155_760_000 seconds
        long hundredYearsSec = 365L * 100 * 86400 + 24 * 86400; // ~100 years + 24 leap days
        String result = TimeProblems.dayAfterSeconds(
                LocalDateTime.of(2000, 1, 1, 0, 0, 0), hundredYearsSec);
        // Just verify it returns a valid day name
        assert(result.equals("MONDAY") || result.equals("TUESDAY") ||
                result.equals("WEDNESDAY") || result.equals("THURSDAY") ||
                result.equals("FRIDAY") || result.equals("SATURDAY") ||
                result.equals("SUNDAY"));

        // Very large negative
        result = TimeProblems.dayAfterSeconds(
                LocalDateTime.of(3000, 6, 15, 12, 0, 0), -hundredYearsSec);
        assert(result.equals("MONDAY") || result.equals("TUESDAY") ||
                result.equals("WEDNESDAY") || result.equals("THURSDAY") ||
                result.equals("FRIDAY") || result.equals("SATURDAY") ||
                result.equals("SUNDAY"));
    }

    // --- dayAfterSeconds mass/CRC test ---

    @Test public void testDayAfterSecondsMass() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 10000; i++) {
            int y = 1900 + rng.nextInt(2000);
            int mo = rng.nextInt(12) + 1;
            int d = rng.nextInt(daysInMonth[mo]) + 1;
            int h = rng.nextInt(24);
            int mi = rng.nextInt(60);
            int s = rng.nextInt(60);
            LocalDateTime now = LocalDateTime.of(y, mo, d, h, mi, s);
            long seconds = 1000;
            while(seconds <= 1_000_000_000L) {
                String result = TimeProblems.dayAfterSeconds(now, seconds);
                try {
                    check.update(result.getBytes("UTF-8"));
                } catch(UnsupportedEncodingException ignored) {}
                seconds *= 10;
            }
        }
        assertEquals(1758684803L, check.getValue());
    }

    // --- whatHourIsItThere explicit tests ---

    private static final String[] ourZones = {
            "America/Fort_Nelson", "Arctic/Longyearbyen", "Africa/Casablanca", "Europe/Kirov",
            "Atlantic/Canary", "Asia/Chongqing", "Europe/Amsterdam", "America/Indiana/Knox",
            "Atlantic/Faroe", "Pacific/Marquesas", "Africa/Douala", "America/Hermosillo",
            "Canada/Central", "Europe/Minsk", "Pacific/Kosrae", "Europe/Madrid", "Indian/Mayotte",
            "Navajo", "America/North_Dakota/New_Salem", "Pacific/Guadalcanal", "Africa/Lubumbashi",
            "America/Martinique", "America/Argentina/Jujuy", "Indian/Maldives", "Asia/Ho_Chi_Minh",
            "Pacific/Pitcairn", "Australia/Canberra", "Canada/Newfoundland", "Eire", "US/Hawaii",
            "Asia/Vladivostok", "America/Cayman", "America/Anchorage", "Antarctica/Rothera",
            "Asia/Novokuznetsk", "Indian/Antananarivo", "Africa/Timbuktu", "Hongkong"
    };

    @Test public void testWhatHourIsItThereExplicit() {
        // Same zone: hour should be identical
        assertEquals(14, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 6, 15, 14, 30, 0),
                "America/Fort_Nelson", "America/Fort_Nelson"));

        // Same zone at midnight
        assertEquals(0, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 3, 1, 0, 0, 0),
                "Europe/Amsterdam", "Europe/Amsterdam"));

        // Same zone at hour 23
        assertEquals(23, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 7, 4, 23, 59, 59),
                "US/Hawaii", "US/Hawaii"));
    }

    @Test public void testWhatHourIsItThereKnownOffsets() {
        // Toronto (EST, UTC-5) to London (GMT, UTC+0) in winter: +5 hours
        // Jan 15, 2020 noon in Toronto -> 5pm in London
        assertEquals(17, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 12, 0, 0),
                "America/Toronto", "Europe/London"));

        // Toronto to Hongkong: HKT is UTC+8, so +13 hours from EST
        // Jan 15, 2020 noon in Toronto -> 1am next day in HK
        assertEquals(1, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 12, 0, 0),
                "America/Toronto", "Hongkong"));

        // Hawaii (UTC-10) to Hongkong (UTC+8): +18 hours
        // Jan 15, 2020 6am in Hawaii -> midnight next day in HK
        assertEquals(0, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 6, 0, 0),
                "US/Hawaii", "Hongkong"));
    }

    @Test public void testWhatHourIsItThereHalfHourZones() {
        // Marquesas is UTC-9:30, one of the rare half-hour offset zones
        // Maldives is UTC+5, so difference is 14:30
        // Noon in Marquesas -> 14.5 hours later -> 2:30am next day, hour = 2
        assertEquals(2, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 6, 15, 12, 0, 0),
                "Pacific/Marquesas", "Indian/Maldives"));

        // Newfoundland is UTC-3:30 (NST) in winter
        // Noon in Newfoundland -> 3:30pm in London (UTC+0) in winter, hour = 15
        assertEquals(15, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 12, 0, 0),
                "Canada/Newfoundland", "Europe/London"));
    }

    @Test public void testWhatHourIsItThereDSTTransition() {
        // This tests why the date matters, not just the time.
        // Amsterdam observes DST: CET (UTC+1) in winter, CEST (UTC+2) in summer.
        // Minsk is permanently UTC+3 (no DST).

        // Winter: Amsterdam UTC+1, Minsk UTC+3, difference = +2
        // Jan 15, 2020 noon in Amsterdam -> 2pm in Minsk
        assertEquals(14, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 12, 0, 0),
                "Europe/Amsterdam", "Europe/Minsk"));

        // Summer: Amsterdam UTC+2, Minsk UTC+3, difference = +1
        // Jul 15, 2020 noon in Amsterdam -> 1pm in Minsk
        assertEquals(13, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 7, 15, 12, 0, 0),
                "Europe/Amsterdam", "Europe/Minsk"));

        // Similarly with Eire (Ireland): UTC+0 winter (GMT), UTC+1 summer (IST)
        // Winter: noon in Eire -> 3pm in Minsk
        assertEquals(15, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 12, 0, 0),
                "Eire", "Europe/Minsk"));

        // Summer: noon in Eire -> 2pm in Minsk
        assertEquals(14, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 7, 15, 12, 0, 0),
                "Eire", "Europe/Minsk"));
    }

    @Test public void testWhatHourIsItThereAroundMidnight() {
        // When the conversion crosses midnight in either direction
        // Anchorage (AKST, UTC-9) in winter to Amsterdam (CET, UTC+1): +10
        // 11pm in Anchorage -> 9am next day in Amsterdam
        assertEquals(9, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 23, 0, 0),
                "America/Anchorage", "Europe/Amsterdam"));

        // The reverse: noon in Amsterdam -> 2am same day in Anchorage
        assertEquals(2, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 12, 0, 0),
                "Europe/Amsterdam", "America/Anchorage"));

        // Vladivostok (UTC+10) to Hawaii (UTC-10): -20 hours
        // Midnight (0:00) in Vladivostok -> 4am previous day in Hawaii, hour = 4
        assertEquals(4, TimeProblems.whatHourIsItThere(
                LocalDateTime.of(2020, 1, 15, 0, 0, 0),
                "Asia/Vladivostok", "US/Hawaii"));
    }

    // --- whatHourIsItThere mass/CRC test ---

    @Test public void testWhatHourIsItThereMass() {
        Random rng = new Random(12345);
        CRC32 check = new CRC32();
        for(int i = 0; i < 100_000; i++) {
            String hereZone = ourZones[rng.nextInt(ourZones.length)];
            String thereZone = ourZones[rng.nextInt(ourZones.length)];
            int y = 2020;
            int mo = rng.nextInt(12) + 1;
            int d = rng.nextInt(daysInMonth[mo]) + 1;
            int h = rng.nextInt(24);
            int mi = rng.nextInt(60);
            int s = rng.nextInt(60);
            LocalDateTime hereTime = LocalDateTime.of(y, mo, d, h, mi, s);
            int result = TimeProblems.whatHourIsItThere(hereTime, hereZone, thereZone);
            check.update(result);
        }
        assertEquals(3443549537L, check.getValue());
    }
}