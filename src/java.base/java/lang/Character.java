package java.lang;

import java.util.Objects;

public final class Character implements Comparable<Character> {
    public static final int MIN_RADIX = 2;
    public static final int MAX_RADIX = 36;
    public static final char MIN_VALUE = 0x0000;
    public static final char MAX_VALUE = 0xFFFF;
    @SuppressWarnings("unchecked")
    public static final Class<Character> TYPE = (Class<Character>)Class.getPrimitiveClass("char");

    public static final byte UNASSIGNED = 0;
    public static final byte UPPERCASE_LETTER = 1;
    public static final byte LOWERCASE_LETTER = 2;
    public static final byte TITLECASE_LETTER = 3;
    public static final byte MODIFIER_LETTER = 4;
    public static final byte OTHER_LETTER = 5;
    public static final byte NON_SPACING_MARK = 6;
    public static final byte ENCLOSING_MARK = 7;
    public static final byte COMBINING_SPACING_MARK = 8;
    public static final byte DECIMAL_DIGIT_NUMBER = 9;
    public static final byte LETTER_NUMBER = 10;
    public static final byte OTHER_NUMBER = 11;
    public static final byte SPACE_SEPARATOR = 12;
    public static final byte LINE_SEPARATOR = 13;
    public static final byte PARAGRAPH_SEPARATOR = 14;
    public static final byte CONTROL = 15;
    public static final byte FORMAT = 16;
    public static final byte PRIVATE_USE = 18;
    public static final byte SURROGATE = 19;
    public static final byte DASH_PUNCTUATION = 20;
    public static final byte START_PUNCTUATION = 21;
    public static final byte END_PUNCTUATION = 22;
    public static final byte CONNECTOR_PUNCTUATION = 23;
    public static final byte OTHER_PUNCTUATION = 24;
    public static final byte MATH_SYMBOL = 25;
    public static final byte CURRENCY_SYMBOL = 26;
    public static final byte MODIFIER_SYMBOL = 27;
    public static final byte OTHER_SYMBOL = 28;
    public static final byte INITIAL_QUOTE_PUNCTUATION = 29;
    public static final byte FINAL_QUOTE_PUNCTUATION = 30;
    static final int ERROR = 0xFFFFFFFF;
    public static final byte DIRECTIONALITY_UNDEFINED = -1;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT = 0;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT = 1;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ARABIC = 2;
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER = 3;
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_SEPARATOR = 4;
    public static final byte DIRECTIONALITY_EUROPEAN_NUMBER_TERMINATOR = 5;
    public static final byte DIRECTIONALITY_ARABIC_NUMBER = 6;
    public static final byte DIRECTIONALITY_COMMON_NUMBER_SEPARATOR = 7;
    public static final byte DIRECTIONALITY_NONSPACING_MARK = 8;
    public static final byte DIRECTIONALITY_BOUNDARY_NEUTRAL = 9;
    public static final byte DIRECTIONALITY_PARAGRAPH_SEPARATOR = 10;
    public static final byte DIRECTIONALITY_SEGMENT_SEPARATOR = 11;
    public static final byte DIRECTIONALITY_WHITESPACE = 12;
    public static final byte DIRECTIONALITY_OTHER_NEUTRALS = 13;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_EMBEDDING = 14;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_OVERRIDE = 15;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_EMBEDDING = 16;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_OVERRIDE = 17;
    public static final byte DIRECTIONALITY_POP_DIRECTIONAL_FORMAT = 18;
    public static final byte DIRECTIONALITY_LEFT_TO_RIGHT_ISOLATE = 19;
    public static final byte DIRECTIONALITY_RIGHT_TO_LEFT_ISOLATE = 20;
    public static final byte DIRECTIONALITY_FIRST_STRONG_ISOLATE = 21;
    public static final byte DIRECTIONALITY_POP_DIRECTIONAL_ISOLATE = 22;
    public static final char MIN_HIGH_SURROGATE = '\uD800';
    public static final char MAX_HIGH_SURROGATE = '\uDBFF';
    public static final char MIN_LOW_SURROGATE  = '\uDC00';
    public static final char MAX_LOW_SURROGATE  = '\uDFFF';
    public static final char MIN_SURROGATE = MIN_HIGH_SURROGATE;
    public static final char MAX_SURROGATE = MAX_LOW_SURROGATE;
    public static final int MIN_SUPPLEMENTARY_CODE_POINT = 0x010000;
    public static final int MIN_CODE_POINT = 0x000000;
    public static final int MAX_CODE_POINT = 0X10FFFF;

    private final char value;

    public static native char toLowerCase(char c);

    public static native char toUpperCase(char c);

    public static boolean isLowerCase(char ch) {
        return toUpperCase(ch) != ch;
    }

    public static boolean isUpperCase(char ch) {
        return toLowerCase(ch) != ch;
    }

    public static boolean isDigit(char ch) {
        return (('0' <= ch) && (ch <= '9'));
    }

    public static int digit(char ch, int radix) {
        if('A' <= ch && ch <= 'Z')
            ch += 32;
        int ret = -1;
        if('0' <= ch && ch <= '9')
            ret = ch - '0';
        else if('a' <= ch && ch <= 'z')
            ret = ch - 'a' + 10;
        if(ret >= radix)
            return -1;
        return ret;
    }

    public Character(char value) {
        this.value = value;
    }

    public static Character valueOf(char c) {
        return new Character(c);
    }

    public char charValue() {
        return value;
    }

    @Override
    public int hashCode() {
        return Character.hashCode(value);
    }

    public static int hashCode(char value) {
        return (int)value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Character)
            return value == ((Character)obj).charValue();
        return false;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    public static String toString(char c) {
        return String.valueOf(c);
    }

    public static boolean isValidCodePoint(int codePoint) {
        int plane = codePoint >>> 16;
        return plane < ((MAX_CODE_POINT + 1) >>> 16);
    }

    public static boolean isBmpCodePoint(int codePoint) {
        return codePoint >>> 16 == 0;
    }

    public static boolean isSupplementaryCodePoint(int codePoint) {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT && codePoint <  MAX_CODE_POINT + 1;
    }

    public static boolean isHighSurrogate(char ch) {
        return ch >= MIN_HIGH_SURROGATE && ch < (MAX_HIGH_SURROGATE + 1);
    }

    public static boolean isLowSurrogate(char ch) {
        return ch >= MIN_LOW_SURROGATE && ch < (MAX_LOW_SURROGATE + 1);
    }

    public static boolean isSurrogate(char ch) {
        return ch >= MIN_SURROGATE && ch < (MAX_SURROGATE + 1);
    }

    public static boolean isSurrogatePair(char high, char low) {
        return isHighSurrogate(high) && isLowSurrogate(low);
    }

    public static int charCount(int codePoint) {
        return codePoint >= MIN_SUPPLEMENTARY_CODE_POINT ? 2 : 1;
    }

    public static int toCodePoint(char high, char low) {
        return ((high << 10) + low) + (MIN_SUPPLEMENTARY_CODE_POINT - (MIN_HIGH_SURROGATE << 10) - MIN_LOW_SURROGATE);
    }

    public static int codePointAt(CharSequence seq, int index) {
        char c1 = seq.charAt(index);
        if(isHighSurrogate(c1) && ++index < seq.length()) {
            char c2 = seq.charAt(index);
            if(isLowSurrogate(c2))
                return toCodePoint(c1, c2);
        }
        return c1;
    }

    public static int codePointAt(char[] a, int index) {
        return codePointAtImpl(a, index, a.length);
    }

    public static int codePointAt(char[] a, int index, int limit) {
        if(index >= limit || index < 0 || limit > a.length)
            throw new IndexOutOfBoundsException();
        return codePointAtImpl(a, index, limit);
    }

    static int codePointAtImpl(char[] a, int index, int limit) {
        char c1 = a[index];
        if(isHighSurrogate(c1) && ++index < limit) {
            char c2 = a[index];
            if(isLowSurrogate(c2))
                return toCodePoint(c1, c2);
        }
        return c1;
    }

    public static int codePointBefore(CharSequence seq, int index) {
        char c2 = seq.charAt(--index);
        if(isLowSurrogate(c2) && index > 0) {
            char c1 = seq.charAt(--index);
            if(isHighSurrogate(c1))
                return toCodePoint(c1, c2);
        }
        return c2;
    }

    public static int codePointBefore(char[] a, int index) {
        return codePointBeforeImpl(a, index, 0);
    }

    public static int codePointBefore(char[] a, int index, int start) {
        if(index <= start || start < 0 || index > a.length)
            throw new IndexOutOfBoundsException();
        return codePointBeforeImpl(a, index, start);
    }

    static int codePointBeforeImpl(char[] a, int index, int start) {
        char c2 = a[--index];
        if(isLowSurrogate(c2) && index > start) {
            char c1 = a[--index];
            if(isHighSurrogate(c1))
                return toCodePoint(c1, c2);
        }
        return c2;
    }

    public static char highSurrogate(int codePoint) {
        return (char) ((codePoint >>> 10) + (MIN_HIGH_SURROGATE - (MIN_SUPPLEMENTARY_CODE_POINT >>> 10)));
    }

    public static char lowSurrogate(int codePoint) {
        return (char) ((codePoint & 0x3ff) + MIN_LOW_SURROGATE);
    }

    public static int toChars(int codePoint, char[] dst, int dstIndex) {
        if(isBmpCodePoint(codePoint)) {
            dst[dstIndex] = (char) codePoint;
            return 1;
        }
        else if(isValidCodePoint(codePoint)) {
            toSurrogates(codePoint, dst, dstIndex);
            return 2;
        }
        else
            throw new IllegalArgumentException("Not a valid Unicode code point: 0x" + Integer.toString(codePoint, 16));
    }

    public static char[] toChars(int codePoint) {
        if(isBmpCodePoint(codePoint))
            return new char[] { (char) codePoint };
        else if(isValidCodePoint(codePoint)) {
            char[] result = new char[2];
            toSurrogates(codePoint, result, 0);
            return result;
        }
        else
            throw new IllegalArgumentException("Not a valid Unicode code point: 0x" + Integer.toString(codePoint, 16));
    }

    static void toSurrogates(int codePoint, char[] dst, int index) {
        dst[index+1] = lowSurrogate(codePoint);
        dst[index] = highSurrogate(codePoint);
    }

    public static int codePointCount(CharSequence seq, int beginIndex, int endIndex) {
        Objects.checkFromToIndex(beginIndex, endIndex, seq.length());
        int n = endIndex - beginIndex;
        for(int i = beginIndex; i < endIndex; ) {
            if(isHighSurrogate(seq.charAt(i++)) && i < endIndex && isLowSurrogate(seq.charAt(i))) {
                n--;
                i++;
            }
        }
        return n;
    }

    public static int codePointCount(char[] a, int offset, int count) {
        Objects.checkFromIndexSize(offset, count, a.length);
        return codePointCountImpl(a, offset, count);
    }

    static int codePointCountImpl(char[] a, int offset, int count) {
        int endIndex = offset + count;
        int n = count;
        for(int i = offset; i < endIndex; ) {
            if(isHighSurrogate(a[i++]) && i < endIndex && isLowSurrogate(a[i])) {
                n--;
                i++;
            }
        }
        return n;
    }

    public static int offsetByCodePoints(CharSequence seq, int index, int codePointOffset) {
        int length = seq.length();
        if(index < 0 || index > length)
            throw new IndexOutOfBoundsException();

        int x = index;
        if(codePointOffset >= 0) {
            int i;
            for(i = 0; x < length && i < codePointOffset; i++) {
                if(isHighSurrogate(seq.charAt(x++)) && x < length && isLowSurrogate(seq.charAt(x)))
                    x++;
            }
            if(i < codePointOffset)
                throw new IndexOutOfBoundsException();
        }
        else {
            int i;
            for(i = codePointOffset; x > 0 && i < 0; i++) {
                if(isLowSurrogate(seq.charAt(--x)) && x > 0 &&
                    isHighSurrogate(seq.charAt(x-1))) {
                    x--;
                }
            }
            if(i < 0)
                throw new IndexOutOfBoundsException();
        }
        return x;
    }

    public static int offsetByCodePoints(char[] a, int start, int count, int index, int codePointOffset) {
        if(count > a.length-start || start < 0 || count < 0 || index < start || index > start + count)
            throw new IndexOutOfBoundsException();
        return offsetByCodePointsImpl(a, start, count, index, codePointOffset);
    }

    static int offsetByCodePointsImpl(char[]a, int start, int count, int index, int codePointOffset) {
        int x = index;
        if(codePointOffset >= 0) {
            int limit = start + count;
            int i;
            for(i = 0; x < limit && i < codePointOffset; i++) {
                if(isHighSurrogate(a[x++]) && x < limit && isLowSurrogate(a[x]))
                    x++;
            }
            if(i < codePointOffset)
                throw new IndexOutOfBoundsException();
        }
        else {
            int i;
            for(i = codePointOffset; x > start && i < 0; i++) {
                if(isLowSurrogate(a[--x]) && x > start &&
                    isHighSurrogate(a[x-1])) {
                    x--;
                }
            }
            if(i < 0)
                throw new IndexOutOfBoundsException();
        }
        return x;
    }

    public static char forDigit(int digit, int radix) {
        if((digit >= radix) || (digit < 0))
            return '\0';
        if((radix < Character.MIN_RADIX) || (radix > Character.MAX_RADIX))
            return '\0';
        if(digit < 10)
            return (char)('0' + digit);
        return (char)('a' - 10 + digit);
    }

    @Override
    public int compareTo(Character anotherCharacter) {
        return value - anotherCharacter.value;
    }

    public static int compare(char x, char y) {
        return x - y;
    }

    public static char reverseBytes(char ch) {
        return (char)(((ch & 0xFF00) >> 8) | (ch << 8));
    }
}
