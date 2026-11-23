package java.util;

public class GregorianCalendar extends Calendar {
    public static final int BC = 0;
    public static final int AD = 1;

    private static final int ONE_SECOND = 1000;
    private static final int ONE_MINUTE = 60 * ONE_SECOND;
    private static final int ONE_HOUR = 60 * ONE_MINUTE;
    private static final long ONE_DAY = 24 * ONE_HOUR;

    private static final int[] MIN_VALUES = {
        BC, 1, JANUARY, 1, 0, 1, 1, SUNDAY, 1, AM, 0, 0, 0, 0, 0, -13 * ONE_HOUR, 0
    };

    private static final int[] LEAST_MAX_VALUES = {
        AD, 292269054, DECEMBER, 52, 4, 28, 365, SATURDAY, 4, PM, 11, 23, 59, 59, 999, 14 * ONE_HOUR, 20 * ONE_MINUTE
    };

    private static final int[] MAX_VALUES = {
        AD, 292278994, DECEMBER, 53, 6, 31, 366, SATURDAY, 6, PM, 11, 23, 59, 59, 999, 14 * ONE_HOUR, 2 * ONE_HOUR
    };

    private static final int[] GREATEST_MIN_VALUES = {
        BC, 1, JANUARY, 1, 0, 1, 1, SUNDAY, 1, AM, 0, 0, 0, 0, 0, -13 * ONE_HOUR, 0
    };

    public GregorianCalendar() {
        this(TimeZone.getDefaultRef());
    }

    public GregorianCalendar(TimeZone zone) {
        this(zone, System.currentTimeMillis());
    }

    @SuppressWarnings("this-escape")
    GregorianCalendar(TimeZone zone, long millis) {
        super(zone);
        setTimeInMillis(millis);
    }

    public GregorianCalendar(int year, int month, int dayOfMonth) {
        this(year, month, dayOfMonth, 0, 0, 0, 0);
    }

    public GregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute) {
        this(year, month, dayOfMonth, hourOfDay, minute, 0, 0);
    }

    public GregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second) {
        this(year, month, dayOfMonth, hourOfDay, minute, second, 0);
    }

    @SuppressWarnings("this-escape")
    GregorianCalendar(int year, int month, int dayOfMonth, int hourOfDay, int minute, int second, int millis) {
        super(TimeZone.getDefaultRef());
        set(YEAR, year);
        set(MONTH, month);
        set(DAY_OF_MONTH, dayOfMonth);
        if (hourOfDay >= 12 && hourOfDay <= 23) {
            internalSet(AM_PM, PM);
            internalSet(HOUR, hourOfDay - 12);
        }
        else
            internalSet(HOUR, hourOfDay);
        set(HOUR_OF_DAY, hourOfDay);
        set(MINUTE, minute);
        set(SECOND, second);
        set(MILLISECOND, millis);
    }

    public boolean isLeapYear(int year) {
        return TimeZone.isLeapYear(year);
    }

    public final Date getGregorianChange() {
        return new Date(Long.MIN_VALUE);
    }

    @Override
    public String getCalendarType() {
        return "gregory";
    }

    @Override
    public boolean isWeekDateSupported() {
        return false;
    }

    @Override
    public int getMinimum(int field) {
        return MIN_VALUES[field];
    }

    @Override
    public int getMaximum(int field) {
        return MAX_VALUES[field];
    }

    @Override
    public int getGreatestMinimum(int field) {
        return GREATEST_MIN_VALUES[field];
    }

    @Override
    public int getLeastMaximum(int field) {
        return LEAST_MAX_VALUES[field];
    }

    @Override
    public int getActualMaximum(int field) {
        switch (field) {
            case DAY_OF_MONTH:
                complete();
                return monthLength(getProlepticYear(), internalGet(MONTH));
            case DAY_OF_YEAR:
                complete();
                return yearLength(getProlepticYear());
            case WEEK_OF_MONTH: {
                complete();
                long fd = getFixedDate();
                long firstOfMonth = fd - internalGet(DAY_OF_MONTH) + 1;
                long lastOfMonth = firstOfMonth + monthLength(getProlepticYear(), internalGet(MONTH)) - 1;
                return getWeekNumber(firstOfMonth, lastOfMonth);
            }
            case WEEK_OF_YEAR: {
                complete();
                int year = getProlepticYear();
                long jan1 = TimeZone.daysFromCivil(year, JANUARY, 1);
                long dec31 = jan1 + yearLength(year) - 1;
                int max = 0;
                for (long d = dec31; d > dec31 - 7; d--) {
                    int woy = getWeekOfYear(year, jan1, d);
                    if (woy > max)
                        max = woy;
                }
                return max;
            }
            case DAY_OF_WEEK_IN_MONTH: {
                complete();
                int ndays = monthLength(getProlepticYear(), internalGet(MONTH));
                ndays -= (internalGet(DAY_OF_MONTH) - 1) % 7;
                return (ndays + 6) / 7;
            }
            case YEAR: {
                complete();
                boolean bc = internalGet(ERA) == BC;
                GregorianCalendar limit = (GregorianCalendar)clone();
                limit.setLenient(true);
                limit.setTimeInMillis(bc ? Long.MIN_VALUE : Long.MAX_VALUE);
                int year = limit.internalGet(YEAR);
                long pos = getYearOffsetInMillis();
                long limitPos = limit.getYearOffsetInMillis();
                if (bc ? (pos < limitPos) : (pos > limitPos))
                    year--;
                return year;
            }
            default:
                return getMaximum(field);
        }
    }

    @Override
    protected void computeFields() {
        TimeZone tz = getTimeZone();
        int rawOffset = tz.getRawOffset();
        int totalOffset = tz.getOffset(time);

        long fd = Math.floorDiv(time, ONE_DAY);
        long tod = Math.floorMod(time, ONE_DAY) + totalOffset;
        fd += Math.floorDiv(tod, ONE_DAY);
        int timeOfDay = (int)Math.floorMod(tod, ONE_DAY);

        setDateFields(fd);

        internalSet(MILLISECOND, timeOfDay % ONE_SECOND);
        timeOfDay /= ONE_SECOND;
        internalSet(SECOND, timeOfDay % 60);
        timeOfDay /= 60;
        internalSet(MINUTE, timeOfDay % 60);
        timeOfDay /= 60;
        internalSet(HOUR_OF_DAY, timeOfDay);
        internalSet(AM_PM, timeOfDay / 12);
        internalSet(HOUR, timeOfDay % 12);

        internalSet(ZONE_OFFSET, rawOffset);
        internalSet(DST_OFFSET, totalOffset - rawOffset);
    }

    private void setDateFields(long fd) {
        long ymd = TimeZone.civilFromDays(fd);
        int year = (int)(ymd >> 9);
        int month = (int)(ymd >> 5) & 0x0F;
        int day = (int)ymd & 0x1F;

        if (year > 0) {
            internalSet(ERA, AD);
            internalSet(YEAR, year);
        }
        else {
            internalSet(ERA, BC);
            internalSet(YEAR, 1 - year);
        }
        internalSet(MONTH, month);
        internalSet(DAY_OF_MONTH, day);
        internalSet(DAY_OF_WEEK, TimeZone.dayOfWeek(fd));

        long jan1 = TimeZone.daysFromCivil(year, JANUARY, 1);
        internalSet(DAY_OF_YEAR, (int)(fd - jan1) + 1);
        internalSet(DAY_OF_WEEK_IN_MONTH, (day - 1) / 7 + 1);
        internalSet(WEEK_OF_MONTH, getWeekNumber(fd - day + 1, fd));
        internalSet(WEEK_OF_YEAR, getWeekOfYear(year, jan1, fd));
    }

    @Override
    protected void computeTime() {
        if (!isLenient()) {
            for (int field = 0; field < FIELD_COUNT; field++) {
                if (!isExternallySet(field))
                    continue;
                int value = internalGet(field);
                if (value < getMinimum(field) || value > getMaximum(field))
                    throw new IllegalArgumentException(getFieldName(field));
            }
        }

        int[] originalFields = isLenient() ? null : fields.clone();

        /* Year */
        int year = isSet(YEAR) ? internalGet(YEAR) : 1970;
        if (isSet(ERA) && internalGet(ERA) == BC)
            year = 1 - year;
        else if (isSet(ERA) && internalGet(ERA) != AD)
            throw new IllegalArgumentException("Invalid era");

        /* Date */
        long fd = getFixedDateFromFields(year);

        /* Time of day */
        long timeOfDay;
        int hodStamp = getStamp(HOUR_OF_DAY);
        int hourStamp = aggregateStamp(getStamp(HOUR), getStamp(AM_PM));
        int bestStamp = (hourStamp > hodStamp) ? hourStamp : hodStamp;
        if (bestStamp == UNSET)
            bestStamp = Math.max(getStamp(HOUR), getStamp(AM_PM));
        if (bestStamp != UNSET && bestStamp == hodStamp)
            timeOfDay = internalGet(HOUR_OF_DAY);
        else {
            timeOfDay = internalGet(HOUR);
            if (isSet(AM_PM))
                timeOfDay += 12L * internalGet(AM_PM);
        }
        timeOfDay = timeOfDay * 60 + internalGet(MINUTE);
        timeOfDay = timeOfDay * 60 + internalGet(SECOND);
        timeOfDay = timeOfDay * 1000 + internalGet(MILLISECOND);

        long local = fd * ONE_DAY + timeOfDay;

        /* Zone */
        TimeZone tz = getTimeZone();
        int zoneOffset;
        if (isExternallySet(ZONE_OFFSET) && isExternallySet(DST_OFFSET))
            zoneOffset = internalGet(ZONE_OFFSET) + internalGet(DST_OFFSET);
        else {
            int raw = isExternallySet(ZONE_OFFSET) ? internalGet(ZONE_OFFSET) : tz.getRawOffset();
            int total = tz.getOffset(local - raw);
            int dst = isExternallySet(DST_OFFSET) ? internalGet(DST_OFFSET) : (total - tz.getRawOffset());
            zoneOffset = raw + dst;
            if (!isExternallySet(DST_OFFSET) && tz.getOffset(local - zoneOffset) != total)
                zoneOffset = raw;
        }
        time = local - zoneOffset;

        if (originalFields != null) {
            computeFields();
            for (int field = 0; field < FIELD_COUNT; field++) {
                if (!isExternallySet(field))
                    continue;
                if (originalFields[field] != internalGet(field)) {
                    String s = originalFields[field] + " -> " + internalGet(field);
                    System.arraycopy(originalFields, 0, fields, 0, FIELD_COUNT);
                    throw new IllegalArgumentException(getFieldName(field) + ": " + s);
                }
            }
        }
    }

    private long getFixedDateFromFields(int year) {
        int dowStamp = getStamp(DAY_OF_WEEK);
        int monthStamp = getStamp(MONTH);
        int domStamp = getStamp(DAY_OF_MONTH);
        int womStamp = aggregateStamp(getStamp(WEEK_OF_MONTH), dowStamp);
        int dowimStamp = aggregateStamp(getStamp(DAY_OF_WEEK_IN_MONTH), dowStamp);
        int doyStamp = getStamp(DAY_OF_YEAR);
        int woyStamp = aggregateStamp(getStamp(WEEK_OF_YEAR), dowStamp);

        int bestStamp = domStamp;
        if (womStamp > bestStamp)
            bestStamp = womStamp;
        if (dowimStamp > bestStamp)
            bestStamp = dowimStamp;
        if (doyStamp > bestStamp)
            bestStamp = doyStamp;
        if (woyStamp > bestStamp)
            bestStamp = woyStamp;

        if (bestStamp == UNSET) {
            womStamp = getStamp(WEEK_OF_MONTH);
            dowimStamp = Math.max(getStamp(DAY_OF_WEEK_IN_MONTH), dowStamp);
            woyStamp = getStamp(WEEK_OF_YEAR);
            bestStamp = Math.max(Math.max(womStamp, dowimStamp), woyStamp);
            if (bestStamp == UNSET)
                bestStamp = domStamp = monthStamp;
        }

        boolean useDom = false;
        boolean useWom = false;
        boolean useDowim = false;
        boolean useDoy = false;
        boolean useDow = false;
        boolean useMonth;
        if (bestStamp == domStamp ||
            (bestStamp == womStamp && getStamp(WEEK_OF_MONTH) >= getStamp(WEEK_OF_YEAR)) ||
            (bestStamp == dowimStamp && getStamp(DAY_OF_WEEK_IN_MONTH) >= getStamp(WEEK_OF_YEAR))) {
            useMonth = true;
            if (bestStamp == domStamp)
                useDom = true;
            else {
                useDow = dowStamp != UNSET;
                if (womStamp == dowimStamp) {
                    /* When they are equal, give the priority to WEEK_OF_MONTH */
                    if (getStamp(WEEK_OF_MONTH) >= getStamp(DAY_OF_WEEK_IN_MONTH))
                        useWom = true;
                    else
                        useDowim = true;
                }
                else if (bestStamp == womStamp)
                    useWom = true;
                else
                    useDowim = getStamp(DAY_OF_WEEK_IN_MONTH) != UNSET;
            }
        }
        else {
            useMonth = false;
            if (bestStamp == doyStamp)
                useDoy = true;
            else
                useDow = dowStamp != UNSET;
        }

        int month = JANUARY;
        if (useMonth) {
            month = internalGet(MONTH);
            year += Math.floorDiv(month, 12);
            month = Math.floorMod(month, 12);
        }
        long fd = TimeZone.daysFromCivil(year, month, 1);

        if (useMonth) {
            if (useDom) {
                if (isSet(DAY_OF_MONTH))
                    fd += internalGet(DAY_OF_MONTH) - 1;
            }
            else if (useWom) {
                long first = getFirstWeekStart(fd);
                if (useDow)
                    first = getDayOfWeekDateOnOrBefore(first + 6, internalGet(DAY_OF_WEEK));
                fd = first + 7L * (internalGet(WEEK_OF_MONTH) - 1);
            }
            else {
                int dayOfWeek = useDow ? internalGet(DAY_OF_WEEK) : getFirstDayOfWeek();
                int dowim = useDowim ? internalGet(DAY_OF_WEEK_IN_MONTH) : 1;
                if (dowim >= 0)
                    fd = getDayOfWeekDateOnOrBefore(fd + 7L * dowim - 1, dayOfWeek);
                else {
                    long lastDate = monthLength(year, month) + 7L * (dowim + 1);
                    fd = getDayOfWeekDateOnOrBefore(fd + lastDate - 1, dayOfWeek);
                }
            }
        }
        else if (useDoy)
            fd += internalGet(DAY_OF_YEAR) - 1;
        else {
            long first = getFirstWeekStart(fd);
            if (useDow) {
                int dayOfWeek = internalGet(DAY_OF_WEEK);
                if (dayOfWeek != getFirstDayOfWeek())
                    first = getDayOfWeekDateOnOrBefore(first + 6, dayOfWeek);
            }
            fd = first + 7L * (internalGet(WEEK_OF_YEAR) - 1);
        }
        return fd;
    }

    private static int aggregateStamp(int a, int b) {
        if (a == UNSET || b == UNSET)
            return UNSET;
        return (a > b) ? a : b;
    }

    @Override
    public void add(int field, int amount) {
        if (amount == 0)
            return;
        if (field < 0 || field >= ZONE_OFFSET)
            throw new IllegalArgumentException();

        complete();

        if (field == ERA) {
            set(ERA, (internalGet(ERA) + amount > 0) ? AD : BC);
            complete();
            return;
        }

        if (field == YEAR || field == MONTH) {
            int year = getProlepticYear();
            int month = internalGet(MONTH);
            if (field == YEAR)
                year += amount;
            else {
                long m = (long)month + amount;
                year += (int)Math.floorDiv(m, 12);
                month = Math.floorMod(m, 12);
            }
            setYearMonthPinDay(year, month);
            return;
        }

        long delta = amount;
        long timeOfDay = 0;
        switch (field) {
            case WEEK_OF_YEAR:
            case WEEK_OF_MONTH:
            case DAY_OF_WEEK_IN_MONTH:
                delta *= 7;
                break;
            case AM_PM:
                delta = amount / 2;
                timeOfDay = 12 * (amount % 2);
                break;
            case HOUR:
            case HOUR_OF_DAY:
                delta *= ONE_HOUR;
                break;
            case MINUTE:
                delta *= ONE_MINUTE;
                break;
            case SECOND:
                delta *= ONE_SECOND;
                break;
            default:
                break;
        }

        if (field >= HOUR) {
            setTimeInMillis(time + delta);
            return;
        }

        long fd = getFixedDate();
        timeOfDay += internalGet(HOUR_OF_DAY);
        timeOfDay *= 60;
        timeOfDay += internalGet(MINUTE);
        timeOfDay *= 60;
        timeOfDay += internalGet(SECOND);
        timeOfDay *= 1000;
        timeOfDay += internalGet(MILLISECOND);
        if (timeOfDay >= ONE_DAY) {
            fd++;
            timeOfDay -= ONE_DAY;
        }
        else if (timeOfDay < 0) {
            fd--;
            timeOfDay += ONE_DAY;
        }

        fd += delta;
        int zoneOffset = internalGet(ZONE_OFFSET) + internalGet(DST_OFFSET);
        setTimeInMillis(fd * ONE_DAY + timeOfDay - zoneOffset);
        zoneOffset -= internalGet(ZONE_OFFSET) + internalGet(DST_OFFSET);
        /* If the time zone offset has changed, adjust the time */
        if (zoneOffset != 0) {
            setTimeInMillis(time + zoneOffset);
            if (getFixedDate() != fd)
                setTimeInMillis(time - zoneOffset);
        }
    }

    @Override
    public void roll(int field, boolean up) {
        roll(field, up ? 1 : -1);
    }

    @Override
    public void roll(int field, int amount) {
        if (amount == 0)
            return;
        if (field < 0 || field >= ZONE_OFFSET)
            throw new IllegalArgumentException();

        complete();

        int min = getMinimum(field);
        int max = getMaximum(field);
        switch (field) {
            case HOUR:
            case HOUR_OF_DAY: {
                int rolled = getRolledValue(internalGet(field), amount, min, max);
                int pm = (field == HOUR && internalGet(AM_PM) == PM) ? 12 : 0;
                int hourOfDay = internalGet(HOUR_OF_DAY);
                if (rolled + pm == hourOfDay)
                    return;
                boolean lenient = isLenient();
                setLenient(true);
                try {
                    set(HOUR_OF_DAY, rolled + pm);
                    complete();
                    if (amount % (max + 1) != 0 && internalGet(HOUR_OF_DAY) == hourOfDay) {
                        set(HOUR_OF_DAY, getRolledValue(rolled, (amount > 0) ? 1 : -1, min, max) + pm);
                        complete();
                    }
                }
                finally {
                    setLenient(lenient);
                }
                return;
            }
            case AM_PM:
            case ERA:
            case YEAR:
            case MINUTE:
            case SECOND:
            case MILLISECOND:
                break;
            case MONTH: {
                int month = Math.floorMod(internalGet(MONTH) + amount, 12);
                set(MONTH, month);
                int monthLen = monthLength(getProlepticYear(), month);
                if (internalGet(DAY_OF_MONTH) > monthLen)
                    set(DAY_OF_MONTH, monthLen);
                complete();
                return;
            }
            case WEEK_OF_YEAR: {
                int y = getProlepticYear();
                max = getActualMaximum(WEEK_OF_YEAR);
                int woy = internalGet(WEEK_OF_YEAR);
                int weekYear = getWeekYear();
                set(DAY_OF_WEEK, internalGet(DAY_OF_WEEK));
                int value = woy + amount;
                if (weekYear == y) {
                    if (value > min && value < max) {
                        set(WEEK_OF_YEAR, value);
                        complete();
                        return;
                    }
                    long fd = getFixedDate();
                    long day1 = fd - (7L * (woy - min));
                    if (getYearFromFixedDate(day1) != y)
                        min++;
                    fd += 7L * (max - woy);
                    if (getYearFromFixedDate(fd) != y)
                        max--;
                }
                else {
                    if (weekYear > y) {
                        if (amount < 0)
                            amount++;
                        woy = max;
                    }
                    else {
                        if (amount > 0)
                            amount -= woy - max;
                        woy = min;
                    }
                    value = getRolledValue(woy, amount, min, max);
                    long first = getFirstWeekStart(TimeZone.daysFromCivil(y, JANUARY, 1));
                    int dow = internalGet(DAY_OF_WEEK);
                    if (dow != getFirstDayOfWeek())
                        first = getDayOfWeekDateOnOrBefore(first + 6, dow);
                    long fd = first + 7L * (value - 1);
                    if (getYearFromFixedDate(fd) < y)
                        value++;
                    else if (getYearFromFixedDate(fd) > y)
                        value--;
                    set(field, value);
                    complete();
                    return;
                }
                set(field, getRolledValue(woy, amount, min, max));
                complete();
                return;
            }
            case WEEK_OF_MONTH: {
                int dow = Math.floorMod(internalGet(DAY_OF_WEEK) - getFirstDayOfWeek(), 7);
                long fd = getFixedDate();
                long month1 = fd - internalGet(DAY_OF_MONTH) + 1;
                int monthLen = monthLength(getProlepticYear(), internalGet(MONTH));
                long monthDay1st = getFirstWeekStart(month1);
                max = getActualMaximum(field);
                int value = getRolledValue(internalGet(field), amount, 1, max) - 1;
                long nfd = monthDay1st + value * 7L + dow;
                if (nfd < month1)
                    nfd = month1;
                else if (nfd >= month1 + monthLen)
                    nfd = month1 + monthLen - 1;
                set(DAY_OF_MONTH, (int)(nfd - month1) + 1);
                complete();
                return;
            }
            case DAY_OF_MONTH:
                max = monthLength(getProlepticYear(), internalGet(MONTH));
                break;
            case DAY_OF_YEAR:
                max = getActualMaximum(field);
                break;
            case DAY_OF_WEEK: {
                int weekOfYear = internalGet(WEEK_OF_YEAR);
                if (weekOfYear > 1 && weekOfYear < 52) {
                    set(WEEK_OF_YEAR, weekOfYear);
                    max = SATURDAY;
                    break;
                }
                amount %= 7;
                if (amount == 0)
                    return;
                long fd = getFixedDate();
                long dowFirst = getDayOfWeekDateOnOrBefore(fd, getFirstDayOfWeek());
                fd += amount;
                if (fd < dowFirst)
                    fd += 7;
                else if (fd >= dowFirst + 7)
                    fd -= 7;
                setFixedDate(fd);
                return;
            }
            case DAY_OF_WEEK_IN_MONTH: {
                min = 1;
                int dom = internalGet(DAY_OF_MONTH);
                int monthLen = monthLength(getProlepticYear(), internalGet(MONTH));
                int lastDays = monthLen % 7;
                max = monthLen / 7;
                if ((dom - 1) % 7 < lastDays)
                    max++;
                set(DAY_OF_WEEK, internalGet(DAY_OF_WEEK));
                break;
            }
            default:
                break;
        }
        set(field, getRolledValue(internalGet(field), amount, min, max));
        complete();
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof GregorianCalendar) && super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode() ^ 0x3E6D2A1F;
    }

    @Override
    public Object clone() {
        return super.clone();
    }

    private static int getRolledValue(int value, int amount, int min, int max) {
        int range = max - min + 1;
        amount %= range;
        int n = value + amount;
        if (n > max)
            n -= range;
        else if (n < min)
            n += range;
        return n;
    }

    private int getWeekYear() {
        int year = getProlepticYear();
        int weekOfYear = internalGet(WEEK_OF_YEAR);
        if (internalGet(MONTH) == JANUARY) {
            if (weekOfYear >= 52)
                year--;
        }
        else if (weekOfYear == 1)
            year++;
        return year;
    }

    private static int getYearFromFixedDate(long fd) {
        return (int)(TimeZone.civilFromDays(fd) >> 9);
    }

    private long getYearOffsetInMillis() {
        long t = (internalGet(DAY_OF_YEAR) - 1) * 24L;
        t += internalGet(HOUR_OF_DAY);
        t *= 60;
        t += internalGet(MINUTE);
        t *= 60;
        t += internalGet(SECOND);
        t *= 1000;
        return t + internalGet(MILLISECOND) - internalGet(ZONE_OFFSET) - internalGet(DST_OFFSET);
    }

    private int getProlepticYear() {
        return (internalGet(ERA) == BC) ? 1 - internalGet(YEAR) : internalGet(YEAR);
    }

    private long getFixedDate() {
        return TimeZone.daysFromCivil(getProlepticYear(), internalGet(MONTH), internalGet(DAY_OF_MONTH));
    }

    private void setFixedDate(long fd) {
        long ymd = TimeZone.civilFromDays(fd);
        setYearMonthDay((int)(ymd >> 9), (int)(ymd >> 5) & 0x0F, (int)ymd & 0x1F);
    }

    private void setYearMonthPinDay(int year, int month) {
        int day = Math.min(internalGet(DAY_OF_MONTH), monthLength(year, month));
        setYearMonthDay(year, month, day);
    }

    /* Only the date fields are set: the time fields keep their computed state, as in the JDK */
    private void setYearMonthDay(int year, int month, int day) {
        if (year > 0) {
            set(ERA, AD);
            set(YEAR, year);
        }
        else {
            set(ERA, BC);
            set(YEAR, 1 - year);
        }
        set(MONTH, month);
        set(DAY_OF_MONTH, day);
        complete();
    }

    private static int monthLength(int year, int month) {
        return TimeZone.monthLength(year, month);
    }

    private static int yearLength(int year) {
        return TimeZone.isLeapYear(year) ? 366 : 365;
    }

    private static long getDayOfWeekDateOnOrBefore(long fd, int dayOfWeek) {
        return fd - Math.floorMod(TimeZone.dayOfWeek(fd) - dayOfWeek, 7);
    }

    private long getFirstWeekStart(long fixedDay1) {
        long first = getDayOfWeekDateOnOrBefore(fixedDay1 + 6, getFirstDayOfWeek());
        if ((first - fixedDay1) >= getMinimalDaysInFirstWeek())
            first -= 7;
        return first;
    }

    private int getWeekNumber(long fixedDay1, long fd) {
        long first = getFirstWeekStart(fixedDay1);
        return (int)Math.floorDiv(fd - first, 7) + 1;
    }

    private int getWeekOfYear(int year, long jan1, long fd) {
        int weekOfYear = getWeekNumber(jan1, fd);
        if (weekOfYear == 0) {
            long prevJan1 = TimeZone.daysFromCivil(year - 1, JANUARY, 1);
            weekOfYear = getWeekNumber(prevJan1, jan1 - 1);
        }
        else if (weekOfYear >= 52) {
            long nextJan1 = jan1 + yearLength(year);
            long nextJan1st = getDayOfWeekDateOnOrBefore(nextJan1 + 6, getFirstDayOfWeek());
            int ndays = (int)(nextJan1st - nextJan1);
            if (ndays >= getMinimalDaysInFirstWeek() && fd >= (nextJan1st - 7))
                weekOfYear = 1;
        }
        return weekOfYear;
    }
}
