import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.zip.CRC32;

import static org.junit.Assert.assertEquals;

public class TimeProblemsTest {

    private static final int[] daysInMonth = { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };

    @Test public void testCountFridayThirteensExplicit() {
        LocalDate startDate0 = LocalDate.of(2022, 1, 1);
        LocalDate endDate0 = LocalDate.of(2022, 4, 30);
        assertEquals(0, TimeProblems.countFridayThirteens(startDate0, endDate0));

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

    @Test public void testDayAfterSeconds() {
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

    // Since the set of Zone Ids might change in future versions of Java, here is
    // a random sampler that will remain fixed in this tester.
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
    
    @Test public void testWhatHourIsItThere() {
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