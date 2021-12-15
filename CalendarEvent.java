// A simple data class to be used with the CalendarQueue lab problem. The objects
// of this class are immutable calendar events, each one with the year and the day
// of the event.

public final class CalendarEvent implements Comparable<CalendarEvent> {
    private final int year, day;
    private final String message;

    public static final int DAYS_IN_YEAR = 10;

    public CalendarEvent(int year, int day, String message) {
        assert 0 <= day && day < DAYS_IN_YEAR;
        this.year = year;
        this.day = day;
        this.message = message;
    }

    // Accessor methods for the data fields. Since this class is designed to be
    // immutable, we do not provide public mutators for the data fields.

    public int getYear() { return year; }
    public int getDay() { return day; }
    public String getMessage() { return message; }

    @Override public String toString() {
        return "(" + year + ", " + day + ", " + message + ")";
    }

    @Override public int compareTo(CalendarEvent other) {
        // The year of the event is used as the primary comparison key.
        if(this.year < other.year) { return -1; }
        if(this.year > other.year) { return +1; }
        // If the years are equal, use day as the secondary comparison key.
        if(this.day < other.day) { return -1; }
        if(this.day > other.day) { return +1; }
        // If the days are also equal, resolve order using the message as
        // the tertiary key, so that this ordering is total.
        return this.message.compareTo(other.message);
    }

    // Compute the hash code of the object by combining the hash codes of the
    // data fields that can effect equality comparison using the bitwise xor
    // operator, denoted in Java by the caret character ^.
    @Override public int hashCode() {
        return this.day ^ this.year ^ this.message.hashCode();
    }

    // Once the method compareTo has been implemented, the equality comparison
    // is basically a one-liner after verifying the type of the other object.
    @Override public boolean equals(Object other) {
        if(other instanceof CalendarEvent) {
            return this.compareTo((CalendarEvent) other) == 0;
        }
        else {
            return false;
        }
    }
}