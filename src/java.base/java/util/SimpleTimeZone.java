package java.util;

public class SimpleTimeZone extends TimeZone {
    public static final int WALL_TIME = 0;
    public static final int STANDARD_TIME = 1;
    public static final int UTC_TIME = 2;

    private static final int DOM_MODE = 1;          /* Exact day of month, "Mar 1" */
    private static final int DOW_IN_MONTH_MODE = 2; /* Day of week in month, "lastSun" */
    private static final int DOW_GE_DOM_MODE = 3;   /* Day of week after day of month, "Sun>=15" */
    private static final int DOW_LE_DOM_MODE = 4;   /* Day of week before day of month, "Sun<=21" */

    private static final int MAX_MILLIS = TimeZone.ONE_DAY;

    private int startMonth;
    private int startDay;
    private int startDayOfWeek;
    private int startTime;
    private int startTimeMode;
    private int startMode;

    private int endMonth;
    private int endDay;
    private int endDayOfWeek;
    private int endTime;
    private int endTimeMode;
    private int endMode;

    private int startYear;
    private int rawOffset;
    private boolean useDaylight;
    private int dstSavings;

    private transient boolean dirty;

    @SuppressWarnings("this-escape")
    public SimpleTimeZone(int rawOffset, String ID) {
        this.rawOffset = rawOffset;
        setID(ID);
        dstSavings = TimeZone.ONE_HOUR;
    }

    public SimpleTimeZone(int rawOffset, String ID,
                          int startMonth, int startDay, int startDayOfWeek, int startTime,
                          int endMonth, int endDay, int endDayOfWeek, int endTime) {
        this(rawOffset, ID,
             startMonth, startDay, startDayOfWeek, startTime, WALL_TIME,
             endMonth, endDay, endDayOfWeek, endTime, WALL_TIME,
             TimeZone.ONE_HOUR);
    }

    public SimpleTimeZone(int rawOffset, String ID,
                          int startMonth, int startDay, int startDayOfWeek, int startTime,
                          int endMonth, int endDay, int endDayOfWeek, int endTime,
                          int dstSavings) {
        this(rawOffset, ID,
             startMonth, startDay, startDayOfWeek, startTime, WALL_TIME,
             endMonth, endDay, endDayOfWeek, endTime, WALL_TIME,
             dstSavings);
    }

    @SuppressWarnings("this-escape")
    public SimpleTimeZone(int rawOffset, String ID,
                          int startMonth, int startDay, int startDayOfWeek, int startTime, int startTimeMode,
                          int endMonth, int endDay, int endDayOfWeek, int endTime, int endTimeMode,
                          int dstSavings) {
        setID(ID);
        this.rawOffset = rawOffset;
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startDayOfWeek = startDayOfWeek;
        this.startTime = startTime;
        this.startTimeMode = startTimeMode;
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.endDayOfWeek = endDayOfWeek;
        this.endTime = endTime;
        this.endTimeMode = endTimeMode;
        this.dstSavings = dstSavings;
        decodeRules();
        if (dstSavings <= 0)
            throw new IllegalArgumentException("Illegal daylight saving value: " + dstSavings);
    }

    public void setStartYear(int year) {
        startYear = year;
        dirty = true;
    }

    public void setStartRule(int startMonth, int startDay, int startDayOfWeek, int startTime) {
        this.startMonth = startMonth;
        this.startDay = startDay;
        this.startDayOfWeek = startDayOfWeek;
        this.startTime = startTime;
        startTimeMode = WALL_TIME;
        dirty = true;
        decodeStartRule();
    }

    public void setStartRule(int startMonth, int startDay, int startTime) {
        setStartRule(startMonth, startDay, 0, startTime);
    }

    public void setStartRule(int startMonth, int startDay, int startDayOfWeek, int startTime, boolean after) {
        if (after)
            setStartRule(startMonth, startDay, -startDayOfWeek, startTime);
        else
            setStartRule(startMonth, -startDay, -startDayOfWeek, startTime);
    }

    public void setEndRule(int endMonth, int endDay, int endDayOfWeek, int endTime) {
        this.endMonth = endMonth;
        this.endDay = endDay;
        this.endDayOfWeek = endDayOfWeek;
        this.endTime = endTime;
        endTimeMode = WALL_TIME;
        dirty = true;
        decodeEndRule();
    }

    public void setEndRule(int endMonth, int endDay, int endTime) {
        setEndRule(endMonth, endDay, 0, endTime);
    }

    public void setEndRule(int endMonth, int endDay, int endDayOfWeek, int endTime, boolean after) {
        if (after)
            setEndRule(endMonth, endDay, -endDayOfWeek, endTime);
        else
            setEndRule(endMonth, -endDay, -endDayOfWeek, endTime);
    }

    @Override
    public int getOffset(int era, int year, int month, int day, int dayOfWeek, int millis) {
        if (era != 0 && era != 1)
            throw new IllegalArgumentException("Illegal era " + era);
        if (month < 0 || month > 11)
            throw new IllegalArgumentException("Illegal month " + month);
        if (day < 1 || day > TimeZone.monthLength(era == 1 ? year : 1 - year, month))
            throw new IllegalArgumentException("Illegal day " + day);
        if (dayOfWeek < 1 || dayOfWeek > 7)
            throw new IllegalArgumentException("Illegal day of week " + dayOfWeek);
        if (millis < 0 || millis >= MAX_MILLIS)
            throw new IllegalArgumentException("Illegal millis " + millis);

        if (!useDaylight || era != 1 || year < startYear)
            return rawOffset;

        long time = TimeZone.daysFromCivil(year, month, day) * TimeZone.ONE_DAY + millis;

        long start = getStart(year);
        long end = getEnd(year);

        boolean inDst;
        if (start <= end)
            inDst = time >= start && time < end;
        else if (time < end)
            inDst = time >= getStart(year - 1);
        else if (time >= start)
            inDst = time < getEnd(year + 1);
        else
            inDst = false;
        return inDst ? (rawOffset + dstSavings) : rawOffset;
    }

    private long getStart(int year) {
        long start = getTransition(year, startMonth, startDay, startDayOfWeek, startMode) * TimeZone.ONE_DAY;
        if (startTimeMode == UTC_TIME)
            return start + startTime + rawOffset;
        return start + startTime;
    }

    private long getEnd(int year) {
        long end = getTransition(year, endMonth, endDay, endDayOfWeek, endMode) * TimeZone.ONE_DAY;
        if (endTimeMode == WALL_TIME)
            return end + endTime - dstSavings;
        if (endTimeMode == UTC_TIME)
            return end + endTime + rawOffset;
        return end + endTime;
    }

    boolean isDirty() {
        return dirty;
    }

    private static long getTransition(int year, int month, int day, int dayOfWeek, int mode) {
        switch (mode) {
            case DOM_MODE:
                return TimeZone.daysFromCivil(year, month, day);
            case DOW_IN_MONTH_MODE: {
                if (day > 0) {
                    long first = TimeZone.daysFromCivil(year, month, 1);
                    int delta = dayOfWeek - TimeZone.dayOfWeek(first);
                    if (delta < 0)
                        delta += 7;
                    return first + delta + (day - 1) * 7L;
                }
                else {
                    long last = TimeZone.daysFromCivil(year, month, TimeZone.monthLength(year, month));
                    int delta = TimeZone.dayOfWeek(last) - dayOfWeek;
                    if (delta < 0)
                        delta += 7;
                    return last - delta + (day + 1) * 7L;
                }
            }
            case DOW_GE_DOM_MODE: {
                long d = TimeZone.daysFromCivil(year, month, day);
                int delta = dayOfWeek - TimeZone.dayOfWeek(d);
                if (delta < 0)
                    delta += 7;
                return d + delta;
            }
            default: {
                /* DOW_LE_DOM_MODE */
                long d = TimeZone.daysFromCivil(year, month, day);
                int delta = TimeZone.dayOfWeek(d) - dayOfWeek;
                if (delta < 0)
                    delta += 7;
                return d - delta;
            }
        }
    }

    @Override
    public int getRawOffset() {
        return rawOffset;
    }

    @Override
    public void setRawOffset(int offsetMillis) {
        rawOffset = offsetMillis;
        dirty = true;
    }

    public void setDSTSavings(int millisSavedDuringDST) {
        if (millisSavedDuringDST <= 0)
            throw new IllegalArgumentException("Illegal daylight saving value: " + millisSavedDuringDST);
        dstSavings = millisSavedDuringDST;
        dirty = true;
    }

    @Override
    public int getDSTSavings() {
        return useDaylight ? dstSavings : 0;
    }

    @Override
    public boolean inDaylightTime(Date date) {
        return getOffset(date.getTime()) != rawOffset;
    }

    @Override
    public boolean useDaylightTime() {
        return useDaylight;
    }

    @Override
    public Object clone() {
        return super.clone();
    }

    @Override
    public int hashCode() {
        return startMonth ^ startDay ^ startDayOfWeek ^ startTime ^
               endMonth ^ endDay ^ endDayOfWeek ^ endTime ^ rawOffset;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof SimpleTimeZone))
            return false;
        SimpleTimeZone that = (SimpleTimeZone)obj;
        return getID().equals(that.getID()) && hasSameRules(that);
    }

    @Override
    public boolean hasSameRules(TimeZone other) {
        if (this == other)
            return true;
        if (!(other instanceof SimpleTimeZone))
            return false;
        SimpleTimeZone that = (SimpleTimeZone)other;
        return rawOffset == that.rawOffset &&
               useDaylight == that.useDaylight &&
               (!useDaylight ||
                (dstSavings == that.dstSavings &&
                 startMode == that.startMode &&
                 startMonth == that.startMonth &&
                 startDay == that.startDay &&
                 startDayOfWeek == that.startDayOfWeek &&
                 startTime == that.startTime &&
                 startTimeMode == that.startTimeMode &&
                 endMode == that.endMode &&
                 endMonth == that.endMonth &&
                 endDay == that.endDay &&
                 endDayOfWeek == that.endDayOfWeek &&
                 endTime == that.endTime &&
                 endTimeMode == that.endTimeMode &&
                 startYear == that.startYear));
    }

    @Override
    public String toString() {
        return getClass().getName() +
               "[id=" + getID() +
               ",offset=" + rawOffset +
               ",dstSavings=" + dstSavings +
               ",useDaylight=" + useDaylight +
               ",startYear=" + startYear +
               ",startMode=" + startMode +
               ",startMonth=" + startMonth +
               ",startDay=" + startDay +
               ",startDayOfWeek=" + startDayOfWeek +
               ",startTime=" + startTime +
               ",startTimeMode=" + startTimeMode +
               ",endMode=" + endMode +
               ",endMonth=" + endMonth +
               ",endDay=" + endDay +
               ",endDayOfWeek=" + endDayOfWeek +
               ",endTime=" + endTime +
               ",endTimeMode=" + endTimeMode + ']';
    }

    private void decodeRules() {
        decodeStartRule();
        decodeEndRule();
    }

    private void decodeStartRule() {
        useDaylight = (startDay != 0) && (endDay != 0);
        if (startDay != 0) {
            if (startMonth < 0 || startMonth > 11)
                throw new IllegalArgumentException("Illegal start month " + startMonth);
            if (startTime < 0 || startTime > MAX_MILLIS)
                throw new IllegalArgumentException("Illegal start time " + startTime);
            if (startDayOfWeek == 0)
                startMode = DOM_MODE;
            else {
                if (startDayOfWeek > 0)
                    startMode = DOW_IN_MONTH_MODE;
                else {
                    startDayOfWeek = -startDayOfWeek;
                    if (startDay > 0)
                        startMode = DOW_GE_DOM_MODE;
                    else {
                        startDay = -startDay;
                        startMode = DOW_LE_DOM_MODE;
                    }
                }
                if (startDayOfWeek > 7)
                    throw new IllegalArgumentException("Illegal start day of week " + startDayOfWeek);
            }
            if (startMode == DOW_IN_MONTH_MODE) {
                if (startDay < -5 || startDay > 5)
                    throw new IllegalArgumentException("Illegal start day of week in month " + startDay);
            }
            else if (startDay < 1 || startDay > TimeZone.monthLength(2001, startMonth))
                throw new IllegalArgumentException("Illegal start day " + startDay);
        }
    }

    private void decodeEndRule() {
        useDaylight = (startDay != 0) && (endDay != 0);
        if (endDay != 0) {
            if (endMonth < 0 || endMonth > 11)
                throw new IllegalArgumentException("Illegal end month " + endMonth);
            if (endTime < 0 || endTime > MAX_MILLIS)
                throw new IllegalArgumentException("Illegal end time " + endTime);
            if (endDayOfWeek == 0)
                endMode = DOM_MODE;
            else {
                if (endDayOfWeek > 0)
                    endMode = DOW_IN_MONTH_MODE;
                else {
                    endDayOfWeek = -endDayOfWeek;
                    if (endDay > 0)
                        endMode = DOW_GE_DOM_MODE;
                    else {
                        endDay = -endDay;
                        endMode = DOW_LE_DOM_MODE;
                    }
                }
                if (endDayOfWeek > 7)
                    throw new IllegalArgumentException("Illegal end day of week " + endDayOfWeek);
            }
            if (endMode == DOW_IN_MONTH_MODE) {
                if (endDay < -5 || endDay > 5)
                    throw new IllegalArgumentException("Illegal end day of week in month " + endDay);
            }
            else if (endDay < 1 || endDay > TimeZone.monthLength(2001, endMonth))
                throw new IllegalArgumentException("Illegal end day " + endDay);
        }
    }
}
