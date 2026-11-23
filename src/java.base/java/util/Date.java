package java.util;

public class Date implements Cloneable, Comparable<Date> {
    private static final String[] DAY_NAMES = {
        "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"
    };

    private static final String[] MONTH_NAMES = {
        "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    private long time;

    public Date() {
        this(System.currentTimeMillis());
    }

    public Date(long date) {
        time = date;
    }

    @Deprecated
    public Date(int year, int month, int date) {
        this(year, month, date, 0, 0, 0);
    }

    @Deprecated
    public Date(int year, int month, int date, int hrs, int min) {
        this(year, month, date, hrs, min, 0);
    }

    @Deprecated
    public Date(int year, int month, int date, int hrs, int min, int sec) {
        GregorianCalendar cal = new GregorianCalendar(year + 1900, month, date, hrs, min, sec);
        time = cal.getTimeInMillis();
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean before(Date when) {
        return getTime() < when.getTime();
    }

    public boolean after(Date when) {
        return getTime() > when.getTime();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Date) && getTime() == ((Date)obj).getTime();
    }

    @Override
    public int hashCode() {
        long ht = getTime();
        return (int)ht ^ (int)(ht >> 32);
    }

    @Override
    public int compareTo(Date anotherDate) {
        return Long.compare(getTime(), anotherDate.getTime());
    }

    @Override
    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    /* Format: "EEE MMM dd HH:mm:ss zzz yyyy" */
    @Override
    public String toString() {
        GregorianCalendar cal = getCalendar();
        TimeZone zone = cal.getTimeZone();
        StringBuilder sb = new StringBuilder(32);
        sb.append(DAY_NAMES[cal.get(Calendar.DAY_OF_WEEK) - 1]).append(' ');
        sb.append(MONTH_NAMES[cal.get(Calendar.MONTH)]).append(' ');
        appendTwoDigits(sb, cal.get(Calendar.DAY_OF_MONTH)).append(' ');
        appendTwoDigits(sb, cal.get(Calendar.HOUR_OF_DAY)).append(':');
        appendTwoDigits(sb, cal.get(Calendar.MINUTE)).append(':');
        appendTwoDigits(sb, cal.get(Calendar.SECOND)).append(' ');
        sb.append(zone.getDisplayName(cal.get(Calendar.DST_OFFSET) != 0, TimeZone.SHORT)).append(' ');
        int year = cal.get(Calendar.YEAR);
        if (cal.get(Calendar.ERA) == GregorianCalendar.BC)
            year = 1 - year;
        sb.append(year);
        return sb.toString();
    }

    @Deprecated
    public int getYear() {
        GregorianCalendar cal = getCalendar();
        int year = cal.get(Calendar.YEAR);
        if (cal.get(Calendar.ERA) == GregorianCalendar.BC)
            year = 1 - year;
        return year - 1900;
    }

    @Deprecated
    public void setYear(int year) {
        setField(Calendar.YEAR, year + 1900);
    }

    @Deprecated
    public int getMonth() {
        return getField(Calendar.MONTH);
    }

    @Deprecated
    public void setMonth(int month) {
        setField(Calendar.MONTH, month);
    }

    @Deprecated
    public int getDate() {
        return getField(Calendar.DAY_OF_MONTH);
    }

    @Deprecated
    public void setDate(int date) {
        setField(Calendar.DAY_OF_MONTH, date);
    }

    @Deprecated
    public int getDay() {
        return getField(Calendar.DAY_OF_WEEK) - 1;
    }

    @Deprecated
    public int getHours() {
        return getField(Calendar.HOUR_OF_DAY);
    }

    @Deprecated
    public void setHours(int hours) {
        setField(Calendar.HOUR_OF_DAY, hours);
    }

    @Deprecated
    public int getMinutes() {
        return getField(Calendar.MINUTE);
    }

    @Deprecated
    public void setMinutes(int minutes) {
        setField(Calendar.MINUTE, minutes);
    }

    @Deprecated
    public int getSeconds() {
        return getField(Calendar.SECOND);
    }

    @Deprecated
    public void setSeconds(int seconds) {
        setField(Calendar.SECOND, seconds);
    }

    @Deprecated
    public int getTimezoneOffset() {
        GregorianCalendar cal = getCalendar();
        return -(cal.get(Calendar.ZONE_OFFSET) + cal.get(Calendar.DST_OFFSET)) / TimeZone.ONE_MINUTE;
    }

    private GregorianCalendar getCalendar() {
        return new GregorianCalendar(TimeZone.getDefaultRef(), time);
    }

    private int getField(int field) {
        return getCalendar().get(field);
    }

    private void setField(int field, int value) {
        GregorianCalendar cal = getCalendar();
        int current = cal.get(field);
        if (field == Calendar.YEAR && cal.get(Calendar.ERA) == GregorianCalendar.BC)
            current = 1 - current;
        if (current == value)
            return;
        if (field == Calendar.YEAR) {
            if (value > 0) {
                cal.set(Calendar.ERA, GregorianCalendar.AD);
                cal.set(Calendar.YEAR, value);
            }
            else {
                cal.set(Calendar.ERA, GregorianCalendar.BC);
                cal.set(Calendar.YEAR, 1 - value);
            }
        }
        else
            cal.set(field, value);
        time = cal.getTimeInMillis();
    }

    private static StringBuilder appendTwoDigits(StringBuilder sb, int value) {
        if (value < 10)
            sb.append('0');
        return sb.append(value);
    }
}
