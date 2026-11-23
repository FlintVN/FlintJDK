package java.util;

public abstract class Calendar implements Cloneable, Comparable<Calendar> {
    public static final int ERA = 0;
    public static final int YEAR = 1;
    public static final int MONTH = 2;
    public static final int WEEK_OF_YEAR = 3;
    public static final int WEEK_OF_MONTH = 4;
    public static final int DATE = 5;
    public static final int DAY_OF_MONTH = 5;
    public static final int DAY_OF_YEAR = 6;
    public static final int DAY_OF_WEEK = 7;
    public static final int DAY_OF_WEEK_IN_MONTH = 8;
    public static final int AM_PM = 9;
    public static final int HOUR = 10;
    public static final int HOUR_OF_DAY = 11;
    public static final int MINUTE = 12;
    public static final int SECOND = 13;
    public static final int MILLISECOND = 14;
    public static final int ZONE_OFFSET = 15;
    public static final int DST_OFFSET = 16;
    public static final int FIELD_COUNT = 17;

    public static final int SUNDAY = 1;
    public static final int MONDAY = 2;
    public static final int TUESDAY = 3;
    public static final int WEDNESDAY = 4;
    public static final int THURSDAY = 5;
    public static final int FRIDAY = 6;
    public static final int SATURDAY = 7;

    public static final int JANUARY = 0;
    public static final int FEBRUARY = 1;
    public static final int MARCH = 2;
    public static final int APRIL = 3;
    public static final int MAY = 4;
    public static final int JUNE = 5;
    public static final int JULY = 6;
    public static final int AUGUST = 7;
    public static final int SEPTEMBER = 8;
    public static final int OCTOBER = 9;
    public static final int NOVEMBER = 10;
    public static final int DECEMBER = 11;
    public static final int UNDECIMBER = 12;

    public static final int AM = 0;
    public static final int PM = 1;

    public static final int ALL_STYLES = 0;
    public static final int SHORT = 1;
    public static final int LONG = 2;
    public static final int NARROW_FORMAT = 4;
    public static final int NARROW_STANDALONE = NARROW_FORMAT | 0x8000;
    public static final int SHORT_FORMAT = 1;
    public static final int LONG_FORMAT = 2;
    public static final int SHORT_STANDALONE = SHORT | 0x8000;
    public static final int LONG_STANDALONE = LONG | 0x8000;

    static final int UNSET = 0;
    static final int COMPUTED = 1;
    static final int MINIMUM_USER_STAMP = 2;

    private static final String[] FIELD_NAMES = {
        "ERA", "YEAR", "MONTH", "WEEK_OF_YEAR", "WEEK_OF_MONTH", "DAY_OF_MONTH",
        "DAY_OF_YEAR", "DAY_OF_WEEK", "DAY_OF_WEEK_IN_MONTH", "AM_PM", "HOUR",
        "HOUR_OF_DAY", "MINUTE", "SECOND", "MILLISECOND", "ZONE_OFFSET", "DST_OFFSET"
    };

    protected int[] fields;
    protected boolean[] isSet;
    protected long time;
    protected boolean isTimeSet;
    protected boolean areFieldsSet;

    transient int[] stamp;
    private transient int nextStamp;

    private boolean lenient;
    private TimeZone zone;
    private int firstDayOfWeek;
    private int minimalDaysInFirstWeek;

    protected Calendar() {
        this(TimeZone.getDefaultRef());
    }

    protected Calendar(TimeZone zone) {
        fields = new int[FIELD_COUNT];
        isSet = new boolean[FIELD_COUNT];
        stamp = new int[FIELD_COUNT];
        nextStamp = MINIMUM_USER_STAMP;
        lenient = true;
        this.zone = (TimeZone)zone.clone();
        firstDayOfWeek = SUNDAY;
        minimalDaysInFirstWeek = 1;
    }

    public static Calendar getInstance() {
        return new GregorianCalendar();
    }

    public static Calendar getInstance(TimeZone zone) {
        return new GregorianCalendar(zone);
    }

    public static Set<String> getAvailableCalendarTypes() {
        return Set.of("gregory");
    }

    public String getCalendarType() {
        return getClass().getName();
    }

    protected abstract void computeTime();

    protected abstract void computeFields();

    public final Date getTime() {
        return new Date(getTimeInMillis());
    }

    public final void setTime(Date date) {
        setTimeInMillis(date.getTime());
    }

    public long getTimeInMillis() {
        if (!isTimeSet)
            updateTime();
        return time;
    }

    public void setTimeInMillis(long millis) {
        if (time == millis && isTimeSet && areFieldsSet && (zone instanceof SimpleTimeZone) && !((SimpleTimeZone)zone).isDirty())
            return;
        time = millis;
        isTimeSet = true;
        areFieldsSet = false;
        computeFieldsAll();
    }

    public int get(int field) {
        complete();
        return internalGet(field);
    }

    protected final int internalGet(int field) {
        return fields[field];
    }

    final void internalSet(int field, int value) {
        fields[field] = value;
    }

    public void set(int field, int value) {
        if (isTimeSet && !areFieldsSet)
            computeFieldsAll();
        fields[field] = value;
        isSet[field] = true;
        stamp[field] = nextStamp++;
        if (nextStamp == Integer.MAX_VALUE)
            adjustStamp();
        isTimeSet = false;
        areFieldsSet = false;
    }

    public final void set(int year, int month, int date) {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
    }

    public final void set(int year, int month, int date, int hourOfDay, int minute) {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
    }

    public final void set(int year, int month, int date, int hourOfDay, int minute, int second) {
        set(YEAR, year);
        set(MONTH, month);
        set(DATE, date);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
    }

    public final void clear() {
        for (int i = 0; i < FIELD_COUNT; i++) {
            fields[i] = 0;
            stamp[i] = UNSET;
            isSet[i] = false;
        }
        isTimeSet = false;
        areFieldsSet = false;
    }

    public final void clear(int field) {
        if (isTimeSet && !areFieldsSet)
            computeFieldsAll();
        fields[field] = 0;
        stamp[field] = UNSET;
        isSet[field] = false;
        isTimeSet = false;
        areFieldsSet = false;
    }

    public final boolean isSet(int field) {
        return stamp[field] != UNSET;
    }

    protected void complete() {
        if (!isTimeSet)
            updateTime();
        if (!areFieldsSet)
            computeFieldsAll();
    }

    public abstract void add(int field, int amount);

    public abstract void roll(int field, boolean up);

    public void roll(int field, int amount) {
        while (amount > 0) {
            roll(field, true);
            amount--;
        }
        while (amount < 0) {
            roll(field, false);
            amount++;
        }
    }

    public void setTimeZone(TimeZone value) {
        zone = value;
        if (isTimeSet)
            computeFieldsAll();
    }

    public TimeZone getTimeZone() {
        return zone;
    }

    public void setLenient(boolean lenient) {
        this.lenient = lenient;
    }

    public boolean isLenient() {
        return lenient;
    }

    public void setFirstDayOfWeek(int value) {
        if (firstDayOfWeek == value)
            return;
        firstDayOfWeek = value;
        invalidateWeekFields();
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setMinimalDaysInFirstWeek(int value) {
        if (minimalDaysInFirstWeek == value)
            return;
        minimalDaysInFirstWeek = value;
        invalidateWeekFields();
    }

    public int getMinimalDaysInFirstWeek() {
        return minimalDaysInFirstWeek;
    }

    public boolean isWeekDateSupported() {
        return false;
    }

    public abstract int getMinimum(int field);

    public abstract int getMaximum(int field);

    public abstract int getGreatestMinimum(int field);

    public abstract int getLeastMaximum(int field);

    public int getActualMinimum(int field) {
        return getMinimum(field);
    }

    public int getActualMaximum(int field) {
        return getMaximum(field);
    }

    public boolean before(Object when) {
        return (when instanceof Calendar) && compareTo((Calendar)when) < 0;
    }

    public boolean after(Object when) {
        return (when instanceof Calendar) && compareTo((Calendar)when) > 0;
    }

    @Override
    public int compareTo(Calendar anotherCalendar) {
        return Long.compare(getTimeInMillis(), anotherCalendar.getTimeInMillis());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof Calendar))
            return false;
        Calendar that = (Calendar)obj;
        return getTimeInMillis() == that.getTimeInMillis() &&
               lenient == that.lenient &&
               firstDayOfWeek == that.firstDayOfWeek &&
               minimalDaysInFirstWeek == that.minimalDaysInFirstWeek &&
               zone.equals(that.zone);
    }

    @Override
    public int hashCode() {
        int otheritems = (lenient ? 1 : 0) |
                         (firstDayOfWeek << 1) |
                         (minimalDaysInFirstWeek << 4) |
                         (zone.hashCode() << 7);
        long t = getTimeInMillis();
        return (int)t ^ (int)(t >> 32) ^ otheritems;
    }

    @Override
    public Object clone() {
        try {
            Calendar other = (Calendar)super.clone();
            other.fields = fields.clone();
            other.isSet = isSet.clone();
            other.stamp = stamp.clone();
            other.zone = (TimeZone)zone.clone();
            return other;
        }
        catch (CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(256);
        sb.append(getClass().getName()).append('[');
        sb.append("time=");
        if (isTimeSet)
            sb.append(time);
        else
            sb.append('?');
        sb.append(",areFieldsSet=").append(areFieldsSet);
        sb.append(",lenient=").append(lenient);
        sb.append(",zone=").append(zone);
        sb.append(",firstDayOfWeek=").append(firstDayOfWeek);
        sb.append(",minimalDaysInFirstWeek=").append(minimalDaysInFirstWeek);
        for (int i = 0; i < FIELD_COUNT; i++) {
            sb.append(',').append(FIELD_NAMES[i]).append('=');
            if (isSet(i))
                sb.append(fields[i]);
            else
                sb.append('?');
        }
        return sb.append(']').toString();
    }

    static String getFieldName(int field) {
        return FIELD_NAMES[field];
    }

    final int getStamp(int field) {
        return stamp[field];
    }

    final boolean isExternallySet(int field) {
        return stamp[field] >= MINIMUM_USER_STAMP;
    }

    private void updateTime() {
        computeTime();
        isTimeSet = true;
    }

    private void computeFieldsAll() {
        computeFields();
        for (int i = 0; i < FIELD_COUNT; i++) {
            stamp[i] = COMPUTED;
            isSet[i] = true;
        }
        areFieldsSet = true;
    }

    private void invalidateWeekFields() {
        if (isTimeSet && areFieldsSet)
            computeFieldsAll();
    }

    private void adjustStamp() {
        int max = MINIMUM_USER_STAMP;
        int newStamp = MINIMUM_USER_STAMP;
        for (;;) {
            int min = Integer.MAX_VALUE;
            for (int v : stamp) {
                if (v >= newStamp && min > v)
                    min = v;
                if (max < v)
                    max = v;
            }
            if (max != min && min == Integer.MAX_VALUE)
                break;
            for (int i = 0; i < stamp.length; i++) {
                if (stamp[i] == min)
                    stamp[i] = newStamp;
            }
            newStamp++;
            if (min == max)
                break;
        }
        nextStamp = newStamp;
    }
}
