package java.util;

public abstract class TimeZone implements Cloneable {
    public static final int SHORT = 0;
    public static final int LONG = 1;

    static final int ONE_MINUTE = 60 * 1000;
    static final int ONE_HOUR = 60 * ONE_MINUTE;
    static final int ONE_DAY = 24 * ONE_HOUR;

    static final String GMT_ID = "GMT";

    private static final int RULE_NONE = 0;
    private static final int RULE_US = 1;
    private static final int RULE_EU = 2;
    private static final int RULE_AU = 3;
    private static final int RULE_NZ = 4;

    private static final String[] ZONE_IDS = {
        "Pacific/Midway",
        "Pacific/Honolulu",
        "America/Anchorage",
        "America/Los_Angeles",
        "America/Phoenix",
        "America/Denver",
        "America/Chicago",
        "America/Mexico_City",
        "America/Bogota",
        "America/New_York",
        "America/Caracas",
        "America/Halifax",
        "America/St_Johns",
        "America/Argentina/Buenos_Aires",
        "America/Sao_Paulo",
        "Atlantic/South_Georgia",
        "Atlantic/Azores",
        "Africa/Abidjan",
        "Europe/Lisbon",
        "Europe/London",
        "GMT",
        "UTC",
        "Etc/UTC",
        "Africa/Lagos",
        "Europe/Berlin",
        "Europe/Madrid",
        "Europe/Paris",
        "Europe/Rome",
        "Africa/Johannesburg",
        "Europe/Athens",
        "Europe/Kyiv",
        "Europe/Kiev",
        "Asia/Riyadh",
        "Europe/Istanbul",
        "Europe/Moscow",
        "Asia/Tehran",
        "Asia/Dubai",
        "Asia/Kabul",
        "Asia/Karachi",
        "Asia/Kolkata",
        "Asia/Kathmandu",
        "Asia/Dhaka",
        "Asia/Yangon",
        "Asia/Bangkok",
        "Asia/Ho_Chi_Minh",
        "Asia/Saigon",
        "Asia/Jakarta",
        "Asia/Hong_Kong",
        "Asia/Shanghai",
        "Asia/Singapore",
        "Asia/Taipei",
        "Australia/Perth",
        "Asia/Seoul",
        "Asia/Tokyo",
        "Australia/Adelaide",
        "Australia/Darwin",
        "Australia/Brisbane",
        "Australia/Sydney",
        "Pacific/Noumea",
        "Pacific/Auckland",
        "Pacific/Fiji",
        "Pacific/Tongatapu",
        "Pacific/Kiritimati",
    };

    private static final short[] ZONE_OFFSETS = {
        -660,
        -600,
        -540,
        -480,
        -420,
        -420,
        -360,
        -360,
        -300,
        -300,
        -240,
        -240,
        -210,
        -180,
        -180,
        -120,
        -60,
        0,
        0,
        0,
        0,
        0,
        0,
        60,
        60,
        60,
        60,
        60,
        120,
        120,
        120,
        120,
        180,
        180,
        180,
        210,
        240,
        270,
        300,
        330,
        345,
        360,
        390,
        420,
        420,
        420,
        420,
        480,
        480,
        480,
        480,
        480,
        540,
        540,
        570,
        570,
        600,
        600,
        660,
        720,
        720,
        780,
        840,
    };

    private static final byte[] ZONE_RULES = {
        RULE_NONE,  /* Pacific/Midway */
        RULE_NONE,  /* Pacific/Honolulu */
        RULE_US,    /* America/Anchorage */
        RULE_US,    /* America/Los_Angeles */
        RULE_NONE,  /* America/Phoenix */
        RULE_US,    /* America/Denver */
        RULE_US,    /* America/Chicago */
        RULE_NONE,  /* America/Mexico_City */
        RULE_NONE,  /* America/Bogota */
        RULE_US,    /* America/New_York */
        RULE_NONE,  /* America/Caracas */
        RULE_US,    /* America/Halifax */
        RULE_US,    /* America/St_Johns */
        RULE_NONE,  /* America/Argentina/Buenos_Aires */
        RULE_NONE,  /* America/Sao_Paulo */
        RULE_NONE,  /* Atlantic/South_Georgia */
        RULE_EU,    /* Atlantic/Azores */
        RULE_NONE,  /* Africa/Abidjan */
        RULE_EU,    /* Europe/Lisbon */
        RULE_EU,    /* Europe/London */
        RULE_NONE,  /* GMT */
        RULE_NONE,  /* UTC */
        RULE_NONE,  /* Etc/UTC */
        RULE_NONE,  /* Africa/Lagos */
        RULE_EU,    /* Europe/Berlin */
        RULE_EU,    /* Europe/Madrid */
        RULE_EU,    /* Europe/Paris */
        RULE_EU,    /* Europe/Rome */
        RULE_NONE,  /* Africa/Johannesburg */
        RULE_EU,    /* Europe/Athens */
        RULE_EU,    /* Europe/Kyiv */
        RULE_EU,    /* Europe/Kiev */
        RULE_NONE,  /* Asia/Riyadh */
        RULE_NONE,  /* Europe/Istanbul */
        RULE_NONE,  /* Europe/Moscow */
        RULE_NONE,  /* Asia/Tehran */
        RULE_NONE,  /* Asia/Dubai */
        RULE_NONE,  /* Asia/Kabul */
        RULE_NONE,  /* Asia/Karachi */
        RULE_NONE,  /* Asia/Kolkata */
        RULE_NONE,  /* Asia/Kathmandu */
        RULE_NONE,  /* Asia/Dhaka */
        RULE_NONE,  /* Asia/Yangon */
        RULE_NONE,  /* Asia/Bangkok */
        RULE_NONE,  /* Asia/Ho_Chi_Minh */
        RULE_NONE,  /* Asia/Saigon */
        RULE_NONE,  /* Asia/Jakarta */
        RULE_NONE,  /* Asia/Hong_Kong */
        RULE_NONE,  /* Asia/Shanghai */
        RULE_NONE,  /* Asia/Singapore */
        RULE_NONE,  /* Asia/Taipei */
        RULE_NONE,  /* Australia/Perth */
        RULE_NONE,  /* Asia/Seoul */
        RULE_NONE,  /* Asia/Tokyo */
        RULE_AU,    /* Australia/Adelaide */
        RULE_NONE,  /* Australia/Darwin */
        RULE_NONE,  /* Australia/Brisbane */
        RULE_AU,    /* Australia/Sydney */
        RULE_NONE,  /* Pacific/Noumea */
        RULE_NZ,    /* Pacific/Auckland */
        RULE_NONE,  /* Pacific/Fiji */
        RULE_NONE,  /* Pacific/Tongatapu */
        RULE_NONE,  /* Pacific/Kiritimati */
    };

    private static volatile TimeZone defaultTimeZone;

    private String ID;

    public TimeZone() {

    }

    public abstract int getOffset(int era, int year, int month, int day, int dayOfWeek, int milliseconds);

    public int getOffset(long date) {
        long days = Math.floorDiv(date, ONE_DAY);
        long tod = Math.floorMod(date, ONE_DAY) + (long)getRawOffset();
        days += Math.floorDiv(tod, ONE_DAY);
        int millis = Math.floorMod(tod, ONE_DAY);
        long ymd = civilFromDays(days);
        int year = (int)(ymd >> 9);
        int month = (int)(ymd >> 5) & 0x0F;
        int day = (int)ymd & 0x1F;
        int era = 1;
        if (year <= 0) {
            era = 0;
            year = 1 - year;
        }
        return getOffset(era, year, month, day, dayOfWeek(days), millis);
    }

    public abstract void setRawOffset(int offsetMillis);

    public abstract int getRawOffset();

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        if (ID == null)
            throw new NullPointerException();
        this.ID = ID;
    }

    public final String getDisplayName() {
        return getDisplayName(false, LONG);
    }

    public String getDisplayName(boolean daylight, int style) {
        if (style != SHORT && style != LONG)
            throw new IllegalArgumentException("Illegal style: " + style);
        int offset = getRawOffset();
        if (daylight && useDaylightTime())
            offset += getDSTSavings();
        return toCustomID(offset / ONE_MINUTE);
    }

    public int getDSTSavings() {
        return useDaylightTime() ? ONE_HOUR : 0;
    }

    public abstract boolean useDaylightTime();

    public boolean observesDaylightTime() {
        return useDaylightTime();
    }

    public abstract boolean inDaylightTime(Date date);

    public static synchronized TimeZone getTimeZone(String ID) {
        if (ID == null)
            throw new NullPointerException();
        TimeZone tz = getBuiltinTimeZone(ID);
        if (tz == null) {
            tz = parseCustomTimeZone(ID);
            if (tz == null)
                tz = new SimpleTimeZone(0, GMT_ID);
        }
        return tz;
    }

    public static synchronized String[] getAvailableIDs(int rawOffset) {
        if (rawOffset % ONE_MINUTE != 0)
            return new String[0];
        int offset = rawOffset / ONE_MINUTE;
        int count = 0;
        for (int i = 0; i < ZONE_OFFSETS.length; i++) {
            if (ZONE_OFFSETS[i] == offset)
                count++;
        }
        String[] ret = new String[count];
        for (int i = 0, j = 0; i < ZONE_OFFSETS.length; i++) {
            if (ZONE_OFFSETS[i] == offset)
                ret[j++] = ZONE_IDS[i];
        }
        return ret;
    }

    public static synchronized String[] getAvailableIDs() {
        return ZONE_IDS.clone();
    }

    public static TimeZone getDefault() {
        return (TimeZone)getDefaultRef().clone();
    }

    static TimeZone getDefaultRef() {
        TimeZone tz = defaultTimeZone;
        if (tz == null) {
            synchronized (TimeZone.class) {
                tz = defaultTimeZone;
                if (tz == null) {
                    String id = System.getProperty("user.timezone");
                    if (id == null || id.isEmpty())
                        id = GMT_ID;
                    tz = getTimeZone(id);
                    defaultTimeZone = tz;
                }
            }
        }
        return tz;
    }

    public static void setDefault(TimeZone zone) {
        defaultTimeZone = (zone == null) ? null : (TimeZone)zone.clone();
    }

    public boolean hasSameRules(TimeZone other) {
        return other != null &&
               getRawOffset() == other.getRawOffset() &&
               useDaylightTime() == other.useDaylightTime();
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

    private static TimeZone getBuiltinTimeZone(String ID) {
        for (int i = 0; i < ZONE_IDS.length; i++) {
            if (ZONE_IDS[i].equals(ID))
                return createZone(ID, ZONE_OFFSETS[i] * ONE_MINUTE, ZONE_RULES[i]);
        }
        return null;
    }

    private static TimeZone createZone(String ID, int rawOffset, int rule) {
        switch (rule) {
            case RULE_US:
                return new SimpleTimeZone(rawOffset, ID,
                    2, 8, -1, 2 * ONE_HOUR, SimpleTimeZone.WALL_TIME,
                    10, 1, -1, 2 * ONE_HOUR, SimpleTimeZone.WALL_TIME,
                    ONE_HOUR);
            case RULE_EU:
                return new SimpleTimeZone(rawOffset, ID,
                    2, -1, 1, ONE_HOUR, SimpleTimeZone.UTC_TIME,
                    9, -1, 1, ONE_HOUR, SimpleTimeZone.UTC_TIME,
                    ONE_HOUR);
            case RULE_AU:
                return new SimpleTimeZone(rawOffset, ID,
                    9, 1, -1, 2 * ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
                    3, 1, -1, 2 * ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
                    ONE_HOUR);
            case RULE_NZ:
                return new SimpleTimeZone(rawOffset, ID,
                    8, -1, 1, 2 * ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
                    3, 1, -1, 2 * ONE_HOUR, SimpleTimeZone.STANDARD_TIME,
                    ONE_HOUR);
            default:
                return new SimpleTimeZone(rawOffset, ID);
        }
    }

    private static TimeZone parseCustomTimeZone(String ID) {
        int length = ID.length();
        if (length < 5 || !ID.startsWith(GMT_ID))
            return null;

        int index = GMT_ID.length();
        char c = ID.charAt(index++);
        boolean negative;
        if (c == '-')
            negative = true;
        else if (c == '+')
            negative = false;
        else
            return null;

        int hours = 0;
        int num = 0;
        int countDelim = 0;
        int len = 0;
        while (index < length) {
            c = ID.charAt(index++);
            if (c == ':') {
                if (countDelim > 0 || len < 1 || len > 2)
                    return null;
                hours = num;
                countDelim++;
                num = 0;
                len = 0;
                continue;
            }
            if (c < '0' || c > '9')
                return null;
            num = num * 10 + (c - '0');
            len++;
        }

        int minutes;
        if (countDelim == 0) {
            if (len <= 2) {
                hours = num;
                minutes = 0;
            }
            else if (len <= 4) {
                hours = num / 100;
                minutes = num % 100;
            }
            else
                return null;
        }
        else {
            if (len != 2)
                return null;
            minutes = num;
        }
        if (hours > 23 || minutes > 59)
            return null;

        int offset = hours * 60 + minutes;
        if (negative)
            offset = -offset;
        return new SimpleTimeZone(offset * ONE_MINUTE, toCustomID(offset));
    }

    static String toCustomID(int offsetMinutes) {
        if (offsetMinutes == 0)
            return GMT_ID;
        char[] buf = new char[9];
        buf[0] = 'G';
        buf[1] = 'M';
        buf[2] = 'T';
        if (offsetMinutes < 0) {
            buf[3] = '-';
            offsetMinutes = -offsetMinutes;
        }
        else
            buf[3] = '+';
        int hh = offsetMinutes / 60;
        int mm = offsetMinutes % 60;
        buf[4] = (char)('0' + hh / 10);
        buf[5] = (char)('0' + hh % 10);
        buf[6] = ':';
        buf[7] = (char)('0' + mm / 10);
        buf[8] = (char)('0' + mm % 10);
        return new String(buf);
    }

    static boolean isLeapYear(int year) {
        return (year & 3) == 0 && ((year % 100) != 0 || (year % 400) == 0);
    }

    static int monthLength(int year, int month) {
        switch (month) {
            case 1:
                return isLeapYear(year) ? 29 : 28;
            case 3:
            case 5:
            case 8:
            case 10:
                return 30;
            default:
                return 31;
        }
    }

    static long daysFromCivil(int year, int month, int day) {
        long y = year;
        int m = month + 1;
        if (m <= 2)
            y--;
        long era = Math.floorDiv(y, 400);
        long yoe = y - era * 400;
        long doy = (153 * (m + (m > 2 ? -3 : 9)) + 2) / 5 + day - 1;
        long doe = yoe * 365 + yoe / 4 - yoe / 100 + doy;
        return era * 146097 + doe - 719468;
    }

    static long civilFromDays(long days) {
        long z = days + 719468;
        long era = Math.floorDiv(z, 146097);
        long doe = z - era * 146097;
        long yoe = (doe - doe / 1460 + doe / 36524 - doe / 146096) / 365;
        long y = yoe + era * 400;
        long doy = doe - (365 * yoe + yoe / 4 - yoe / 100);
        long mp = (5 * doy + 2) / 153;
        int d = (int)(doy - (153 * mp + 2) / 5 + 1);
        int m = (int)(mp < 10 ? mp + 3 : mp - 9);
        if (m <= 2)
            y++;
        return (y << 9) | ((m - 1) << 5) | d;
    }

    static int dayOfWeek(long days) {
        return Math.floorMod(days + 4, 7) + 1;
    }
}
