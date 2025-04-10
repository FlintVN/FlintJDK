package jdk.internal.util;

import java.util.Arrays;
import java.util.Collection;
// import jdk.internal.access.JavaLangAccess;
// import jdk.internal.access.SharedSecrets;
// import jdk.internal.misc.Unsafe;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public class ArraysSupport {
    // TODO
    // static final Unsafe U = Unsafe.getUnsafe();

    // TODO
    // private static final boolean BIG_ENDIAN = U.isBigEndian();

    // TODO
    // public static final int LOG2_ARRAY_BOOLEAN_INDEX_SCALE = exactLog2(Unsafe.ARRAY_BOOLEAN_INDEX_SCALE);
    // public static final int LOG2_ARRAY_BYTE_INDEX_SCALE = exactLog2(Unsafe.ARRAY_BYTE_INDEX_SCALE);
    // public static final int LOG2_ARRAY_CHAR_INDEX_SCALE = exactLog2(Unsafe.ARRAY_CHAR_INDEX_SCALE);
    // public static final int LOG2_ARRAY_SHORT_INDEX_SCALE = exactLog2(Unsafe.ARRAY_SHORT_INDEX_SCALE);
    // public static final int LOG2_ARRAY_INT_INDEX_SCALE = exactLog2(Unsafe.ARRAY_INT_INDEX_SCALE);
    // public static final int LOG2_ARRAY_LONG_INDEX_SCALE = exactLog2(Unsafe.ARRAY_LONG_INDEX_SCALE);
    // public static final int LOG2_ARRAY_FLOAT_INDEX_SCALE = exactLog2(Unsafe.ARRAY_FLOAT_INDEX_SCALE);
    // public static final int LOG2_ARRAY_DOUBLE_INDEX_SCALE = exactLog2(Unsafe.ARRAY_DOUBLE_INDEX_SCALE);

    // TODO
    // private static final int LOG2_BYTE_BIT_SIZE = exactLog2(Byte.SIZE);

    // TODO
    // public static final int T_BOOLEAN = 4;
    // public static final int T_CHAR = 5;
    // public static final int T_FLOAT = 6;
    // public static final int T_DOUBLE = 7;
    // public static final int T_BYTE = 8;
    // public static final int T_SHORT = 9;
    // public static final int T_INT = 10;
    // public static final int T_LONG = 11;

    // TODO
    // private static int exactLog2(int scale) {
    //     if((scale & (scale - 1)) != 0)
    //         throw new Error("data type scale not a power of two");
    //     return Integer.numberOfTrailingZeros(scale);
    // }

    private ArraysSupport() {

    }

    // TODO
    // @IntrinsicCandidate
    // public static int vectorizedMismatch(Object a, long aOffset, Object b, long bOffset, int length, int log2ArrayIndexScale) {
    //     int log2ValuesPerWidth = LOG2_ARRAY_LONG_INDEX_SCALE - log2ArrayIndexScale;
    //     int wi = 0;
    //     for(; wi < length >> log2ValuesPerWidth; wi++) {
    //         long bi = ((long) wi) << LOG2_ARRAY_LONG_INDEX_SCALE;
    //         long av = U.getLongUnaligned(a, aOffset + bi);
    //         long bv = U.getLongUnaligned(b, bOffset + bi);
    //         if(av != bv) {
    //             long x = av ^ bv;
    //             int o = BIG_ENDIAN
    //                     ? Long.numberOfLeadingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale)
    //                     : Long.numberOfTrailingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale);
    //             return (wi << log2ValuesPerWidth) + o;
    //         }
    //     }

    //     int tail = length - (wi << log2ValuesPerWidth);

    //     if(log2ArrayIndexScale < LOG2_ARRAY_INT_INDEX_SCALE) {
    //         int wordTail = 1 << (LOG2_ARRAY_INT_INDEX_SCALE - log2ArrayIndexScale);
    //         if(tail >= wordTail) {
    //             long bi = ((long) wi) << LOG2_ARRAY_LONG_INDEX_SCALE;
    //             int av = U.getIntUnaligned(a, aOffset + bi);
    //             int bv = U.getIntUnaligned(b, bOffset + bi);
    //             if(av != bv) {
    //                 int x = av ^ bv;
    //                 int o = BIG_ENDIAN
    //                         ? Integer.numberOfLeadingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale)
    //                         : Integer.numberOfTrailingZeros(x) >> (LOG2_BYTE_BIT_SIZE + log2ArrayIndexScale);
    //                 return (wi << log2ValuesPerWidth) + o;
    //             }
    //             tail -= wordTail;
    //         }
    //         return ~tail;
    //     }
    //     else
    //         return ~tail;
    // }

    // TODO
    // @IntrinsicCandidate
    // public static int vectorizedHashCode(Object array, int fromIndex, int length, int initialValue, int basicType) {
    //     return switch (basicType) {
    //         case T_BOOLEAN -> signedHashCode(initialValue, (byte[]) array, fromIndex, length);
    //         case T_CHAR -> array instanceof byte[]
    //                 ? utf16hashCode(initialValue, (byte[]) array, fromIndex, length)
    //                 : hashCode(initialValue, (char[]) array, fromIndex, length);
    //         case T_BYTE -> hashCode(initialValue, (byte[]) array, fromIndex, length);
    //         case T_SHORT -> hashCode(initialValue, (short[]) array, fromIndex, length);
    //         case T_INT -> hashCode(initialValue, (int[]) array, fromIndex, length);
    //             default -> throw new IllegalArgumentException("unrecognized basic type: " + basicType);
    //     };
    // }

    // TODO
    // private static int signedHashCode(int result, byte[] a, int fromIndex, int length) {
    //     int end = fromIndex + length;
    //     for(int i = fromIndex; i < end; i++)
    //         result = 31 * result + (a[i] & 0xff);
    //     return result;
    // }

    // TODO
    // private static int hashCode(int result, byte[] a, int fromIndex, int length) {
    //     int end = fromIndex + length;
    //     for(int i = fromIndex; i < end; i++)
    //         result = 31 * result + a[i];
    //     return result;
    // }

    // TODO
    // private static int hashCode(int result, char[] a, int fromIndex, int length) {
    //     int end = fromIndex + length;
    //     for(int i = fromIndex; i < end; i++)
    //         result = 31 * result + a[i];
    //     return result;
    // }

    // TODO
    // private static int hashCode(int result, short[] a, int fromIndex, int length) {
    //     int end = fromIndex + length;
    //     for(int i = fromIndex; i < end; i++)
    //         result = 31 * result + a[i];
    //     return result;
    // }

    // TODO
    // private static int hashCode(int result, int[] a, int fromIndex, int length) {
    //     int end = fromIndex + length;
    //     for(int i = fromIndex; i < end; i++)
    //         result = 31 * result + a[i];
    //     return result;
    // }

    // TODO
    // private static final JavaLangAccess JLA = SharedSecrets.getJavaLangAccess();

    // TODO
    // public static int utf16hashCode(int result, byte[] value, int fromIndex, int length) {
    //     int end = fromIndex + length;
    //     for(int i = fromIndex; i < end; i++)
    //         result = 31 * result + JLA.getUTF16Char(value, i);
    //     return result;
    // }

    public static int mismatch(boolean[] a, boolean[] b, int length) {
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(boolean[] a, int aFromIndex, boolean[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(byte[] a, byte[] b, int length) {
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(byte[] a, int aFromIndex, byte[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(char[] a, char[] b, int length) {
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(char[] a, int aFromIndex, char[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(short[] a, short[] b, int length) {
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(short[] a, int aFromIndex, short[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(int[] a, int[] b, int length) {
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(int[] a, int aFromIndex, int[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(float[] a, float[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(float[] a, int aFromIndex, float[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(long[] a, long[] b, int length) {
        for(int i = 0; i < length; i++) {
            if(a[i] != b[i])
                return i;
        }
        return -1;
    }

    public static int mismatch(long[] a, int aFromIndex, long[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static int mismatch(double[] a, double[] b, int length) {
        return mismatch(a, 0, b, 0, length);
    }

    public static int mismatch(double[] a, int aFromIndex, double[] b, int bFromIndex, int length) {
        for(int i = 0; i < length; i++) {
            if(a[aFromIndex + i] != b[bFromIndex + i])
                return i;
        }
        return -1;
    }

    public static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    public static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        int prefLength = oldLength + Math.max(minGrowth, prefGrowth);
        if(0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH)
            return prefLength;
        else
            return hugeLength(oldLength, minGrowth);
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        int minLength = oldLength + minGrowth;
        if(minLength < 0)
            throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
        else if(minLength <= SOFT_MAX_ARRAY_LENGTH)
            return SOFT_MAX_ARRAY_LENGTH;
        else
            return minLength;
    }

    public static <T> T[] reverse(T[] a) {
        int limit = a.length / 2;
        for(int i = 0, j = a.length - 1; i < limit; i++, j--) {
            T t = a[i];
            a[i] = a[j];
            a[j] = t;
        }
        return a;
    }

    public static <T> T[] toArrayReversed(Collection<?> coll, T[] array) {
        T[] newArray = reverse(coll.toArray(Arrays.copyOfRange(array, 0, 0)));
        if(newArray.length > array.length) {
            return newArray;
        } else {
            System.arraycopy(newArray, 0, array, 0, newArray.length);
            if(array.length > newArray.length) {
                array[newArray.length] = null;
            }
            return array;
        }
    }
}
