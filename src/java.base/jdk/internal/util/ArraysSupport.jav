package jdk.internal.util;

import jdk.internal.misc.Unsafe;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public class ArraysSupport {
    static final Unsafe U = Unsafe.getUnsafe();

    private static final boolean BIG_ENDIAN = U.isBigEndian();

    public static final int LOG2_ARRAY_BOOLEAN_INDEX_SCALE = exactLog2(Unsafe.ARRAY_BOOLEAN_INDEX_SCALE);
    public static final int LOG2_ARRAY_BYTE_INDEX_SCALE = exactLog2(Unsafe.ARRAY_BYTE_INDEX_SCALE);
    public static final int LOG2_ARRAY_CHAR_INDEX_SCALE = exactLog2(Unsafe.ARRAY_CHAR_INDEX_SCALE);
    public static final int LOG2_ARRAY_SHORT_INDEX_SCALE = exactLog2(Unsafe.ARRAY_SHORT_INDEX_SCALE);
    public static final int LOG2_ARRAY_INT_INDEX_SCALE = exactLog2(Unsafe.ARRAY_INT_INDEX_SCALE);
    public static final int LOG2_ARRAY_LONG_INDEX_SCALE = exactLog2(Unsafe.ARRAY_LONG_INDEX_SCALE);
    public static final int LOG2_ARRAY_FLOAT_INDEX_SCALE = exactLog2(Unsafe.ARRAY_FLOAT_INDEX_SCALE);
    public static final int LOG2_ARRAY_DOUBLE_INDEX_SCALE = exactLog2(Unsafe.ARRAY_DOUBLE_INDEX_SCALE);

    private static final int LOG2_BYTE_BIT_SIZE = exactLog2(Byte.SIZE);

    private static int exactLog2(int scale) {
        if((scale & (scale - 1)) != 0)
            throw new Error("data type scale not a power of two");
        return Integer.numberOfTrailingZeros(scale);
    }

    private ArraysSupport() {

    }

    @IntrinsicCandidate
    public static int vectorizedMismatch(Object a, long aOffset, Object b, long bOffset, int length, int log2ArrayIndexScale) {
        int log2ValuesPerWidth = LOG2_ARRAY_LONG_INDEX_SCALE - log2ArrayIndexScale;
        int wi = 0;
        for(; wi < length >> log2ValuesPerWidth; wi++) {
            long bi = ((long) wi) << LOG2_ARRAY_LONG_INDEX_SCALE;
            long av = U.getLongUnaligned(a, aOffset + bi);
            long bv = U.getLongUnaligned(b, bOffset + bi);
            if(av != bv) {
                long x = av ^ bv;
                int o = BIG_ENDIAN ? Long.numberOfLeadingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale) : Long.numberOfTrailingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale);
                return (wi << log2ValuesPerWidth) + o;
            }
        }

        int tail = length - (wi << log2ValuesPerWidth);

        if(log2ArrayIndexScale < LOG2_ARRAY_INT_INDEX_SCALE) {
            int wordTail = 1 << (LOG2_ARRAY_INT_INDEX_SCALE - log2ArrayIndexScale);
            if(tail >= wordTail) {
                long bi = ((long) wi) << LOG2_ARRAY_LONG_INDEX_SCALE;
                int av = U.getIntUnaligned(a, aOffset + bi);
                int bv = U.getIntUnaligned(b, bOffset + bi);
                if(av != bv) {
                    int x = av ^ bv;
                    int o = BIG_ENDIAN ? Integer.numberOfLeadingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale) : Integer.numberOfTrailingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale);
                    return (wi << log2ValuesPerWidth) + o;
                }
                tail -= wordTail;
            }
            return ~tail;
        }
        else
            return ~tail;
    }

    public static int mismatch(boolean[] a, boolean[] b, int length) {
        int i = 0;
        if(length > 7) {
            if(a[0] != b[0])
                return 0;
            i = vectorizedMismatch(a, Unsafe.ARRAY_BOOLEAN_BASE_OFFSET, b, Unsafe.ARRAY_BOOLEAN_BASE_OFFSET, length, LOG2_ARRAY_BOOLEAN_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(boolean[] a, int aFromIndex, boolean[] b, int bFromIndex, int length) {
        int i = 0;
        if(length > 7) {
            if(a[aFromIndex] != b[bFromIndex])
                return 0;
            int aOffset = Unsafe.ARRAY_BOOLEAN_BASE_OFFSET + aFromIndex;
            int bOffset = Unsafe.ARRAY_BOOLEAN_BASE_OFFSET + bFromIndex;
            i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_BOOLEAN_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(byte[] a, byte[] b, int length) {
        // ISSUE: defer to index receiving methods if performance is good
        int i = 0;
        if(length > 7) {
            if(a[0] != b[0])
                return 0;
            i = vectorizedMismatch(a, Unsafe.ARRAY_BYTE_BASE_OFFSET, b, Unsafe.ARRAY_BYTE_BASE_OFFSET, length, LOG2_ARRAY_BYTE_INDEX_SCALE);
            if(i >= 0)
                return i;
            // Align to tail
            i = length - ~i;
        }
        // Tail < 8 bytes
        for(; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(byte[] a, int aFromIndex, byte[] b, int bFromIndex, int length) {
        int i = 0;
        if(length > 7) {
            if(a[aFromIndex] != b[bFromIndex])
                return 0;
            int aOffset = Unsafe.ARRAY_BYTE_BASE_OFFSET + aFromIndex;
            int bOffset = Unsafe.ARRAY_BYTE_BASE_OFFSET + bFromIndex;
            i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_BYTE_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(char[] a, char[] b, int length) {
        int i = 0;
        if(length > 3) {
            if(a[0] != b[0])
                return 0;
            i = vectorizedMismatch(a, Unsafe.ARRAY_CHAR_BASE_OFFSET, b, Unsafe.ARRAY_CHAR_BASE_OFFSET, length, LOG2_ARRAY_CHAR_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(char[] a, int aFromIndex, char[] b, int bFromIndex, int length) {
        int i = 0;
        if(length > 3) {
            if(a[aFromIndex] != b[bFromIndex])
                return 0;
            int aOffset = Unsafe.ARRAY_CHAR_BASE_OFFSET + (aFromIndex << LOG2_ARRAY_CHAR_INDEX_SCALE);
            int bOffset = Unsafe.ARRAY_CHAR_BASE_OFFSET + (bFromIndex << LOG2_ARRAY_CHAR_INDEX_SCALE);
            i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_CHAR_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(short[] a, short[] b, int length) {
        int i = 0;
        if(length > 3) {
            if(a[0] != b[0])
                return 0;
            i = vectorizedMismatch(a, Unsafe.ARRAY_SHORT_BASE_OFFSET, b, Unsafe.ARRAY_SHORT_BASE_OFFSET, length, LOG2_ARRAY_SHORT_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(short[] a, int aFromIndex, short[] b, int bFromIndex, int length) {
        int i = 0;
        if(length > 3) {
            if(a[aFromIndex] != b[bFromIndex])
                return 0;
            int aOffset = Unsafe.ARRAY_SHORT_BASE_OFFSET + (aFromIndex << LOG2_ARRAY_SHORT_INDEX_SCALE);
            int bOffset = Unsafe.ARRAY_SHORT_BASE_OFFSET + (bFromIndex << LOG2_ARRAY_SHORT_INDEX_SCALE);
            i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_SHORT_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(int[] a, int[] b, int length) {
        int i = 0;
        if(length > 1) {
            if(a[0] != b[0])
                return 0;
            i = vectorizedMismatch( a, Unsafe.ARRAY_INT_BASE_OFFSET, b, Unsafe.ARRAY_INT_BASE_OFFSET, length, LOG2_ARRAY_INT_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(int[] a, int aFromIndex, int[] b, int bFromIndex, int length) {
        int i = 0;
        if(length > 1) {
            if(a[aFromIndex] != b[bFromIndex])
                return 0;
            int aOffset = Unsafe.ARRAY_INT_BASE_OFFSET + (aFromIndex << LOG2_ARRAY_INT_INDEX_SCALE);
            int bOffset = Unsafe.ARRAY_INT_BASE_OFFSET + (bFromIndex << LOG2_ARRAY_INT_INDEX_SCALE);
            i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_INT_INDEX_SCALE);
            if(i >= 0)
                return i;
            i = length - ~i;
        }
        for(; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(float[] a, float[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(float[] a, int aFromIndex, float[] b, int bFromIndex, int length) {
        int i = 0;
        if(length > 1) {
            if(Float.floatToRawIntBits(a[aFromIndex]) == Float.floatToRawIntBits(b[bFromIndex])) {
                int aOffset = Unsafe.ARRAY_FLOAT_BASE_OFFSET + (aFromIndex << LOG2_ARRAY_FLOAT_INDEX_SCALE);
                int bOffset = Unsafe.ARRAY_FLOAT_BASE_OFFSET + (bFromIndex << LOG2_ARRAY_FLOAT_INDEX_SCALE);
                i = vectorizedMismatch(
                        a, aOffset,
                        b, bOffset,
                        length, LOG2_ARRAY_FLOAT_INDEX_SCALE);
            }
            // Mismatched
            if(i >= 0) {
                // Check if mismatch is not associated with two NaN values
                if(!Float.isNaN(a[aFromIndex + i]) || !Float.isNaN(b[bFromIndex + i]))
                    return i;
                // Mismatch on two different NaN values that are normalized to match
                // Fall back to slow mechanism
                // ISSUE: Consider looping over vectorizedMismatch adjusting ranges
                // However, requires that returned value be relative to input ranges
                i++;
            }
            // Matched
            else
                i = length - ~i;
        }
        for(; i < length; i++) {
            if(Float.floatToIntBits(a[aFromIndex + i]) != Float.floatToIntBits(b[bFromIndex + i]))
                return i;
        }
        return -1;
    }

    public static int mismatch(long[] a, long[] b, int length) {
        if(length == 0)
            return -1;
        if(a[0] != b[0])
            return 0;
        int i = vectorizedMismatch(a, Unsafe.ARRAY_LONG_BASE_OFFSET, b, Unsafe.ARRAY_LONG_BASE_OFFSET, length, LOG2_ARRAY_LONG_INDEX_SCALE);
        return i >= 0 ? i : -1;
    }

    public static int mismatch(long[] a, int aFromIndex, long[] b, int bFromIndex, int length) {
        if(length == 0)
            return -1;
        if(a[aFromIndex] != b[bFromIndex])
            return 0;
        int aOffset = Unsafe.ARRAY_LONG_BASE_OFFSET + (aFromIndex << LOG2_ARRAY_LONG_INDEX_SCALE);
        int bOffset = Unsafe.ARRAY_LONG_BASE_OFFSET + (bFromIndex << LOG2_ARRAY_LONG_INDEX_SCALE);
        int i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_LONG_INDEX_SCALE);
        return i >= 0 ? i : -1;
    }

    public static int mismatch(double[] a, double[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(double[] a, int aFromIndex, double[] b, int bFromIndex, int length) {
        if(length == 0)
            return -1;
        int i = 0;
        if(Double.doubleToRawLongBits(a[aFromIndex]) == Double.doubleToRawLongBits(b[bFromIndex])) {
            int aOffset = Unsafe.ARRAY_DOUBLE_BASE_OFFSET + (aFromIndex << LOG2_ARRAY_DOUBLE_INDEX_SCALE);
            int bOffset = Unsafe.ARRAY_DOUBLE_BASE_OFFSET + (bFromIndex << LOG2_ARRAY_DOUBLE_INDEX_SCALE);
            i = vectorizedMismatch(a, aOffset, b, bOffset, length, LOG2_ARRAY_DOUBLE_INDEX_SCALE);
        }
        if(i >= 0) {
            // Check if mismatch is not associated with two NaN values
            if(!Double.isNaN(a[aFromIndex + i]) || !Double.isNaN(b[bFromIndex + i]))
                return i;

            // Mismatch on two different NaN values that are normalized to match
            // Fall back to slow mechanism
            // ISSUE: Consider looping over vectorizedMismatch adjusting ranges
            // However, requires that returned value be relative to input ranges
            i++;
            for(; i < length; i++) {
                if(Double.doubleToLongBits(a[aFromIndex + i]) != Double.doubleToLongBits(b[bFromIndex + i]))
                    return i;
            }
        }

        return -1;
    }

    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if(0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH)
            return prefLength;
        else {
            // put code cold in a separate method
            return hugeLength(oldLength, minGrowth);
        }
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if(minLength < 0) {
            throw new OutOfMemoryError(
                "Required array length " + oldLength + " + " + minGrowth + " is too large");
        }
        else if(minLength <= SOFT_MAX_ARRAY_LENGTH)
            return SOFT_MAX_ARRAY_LENGTH;
        else
            return minLength;
    }
}
