package java.lang;

import jdk.internal.vm.annotation.ForceInline;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

final class StringConcatHelper {
    private static final long LATIN1 = (long)String.LATIN1 << 32;
    private static final long UTF16 = (long)String.UTF16 << 32;

    private StringConcatHelper() {

    }

    private static long checkOverflow(long lengthCoder) {
        if((int)lengthCoder >= 0)
            return lengthCoder;
        throw new OutOfMemoryError("Overflow: String length out of range");
    }

    static long mix(long lengthCoder, boolean value) {
        return checkOverflow(lengthCoder + (value ? 4 : 5));
    }

    static long mix(long lengthCoder, char value) {
        return checkOverflow(lengthCoder + 1) | ((((int)value >>> 8) == 0) ? 0 : UTF16);
    }

    static long mix(long lengthCoder, int value) {
        return checkOverflow(lengthCoder + Integer.stringSize(value));
    }

    static long mix(long lengthCoder, long value) {
        return checkOverflow(lengthCoder + Long.stringSize(value));
    }

    static long mix(long lengthCoder, String value) {
        lengthCoder += value.length();
        if(value.coder() == String.UTF16)
            lengthCoder |= UTF16;
        return checkOverflow(lengthCoder);
    }

    private static long prepend(long indexCoder, byte[] buf, boolean value) {
        int index = (int)indexCoder;
        if(indexCoder < UTF16) {
            if(value) {
                buf[--index] = 'e';
                buf[--index] = 'u';
                buf[--index] = 'r';
                buf[--index] = 't';
            }
            else {
                buf[--index] = 'e';
                buf[--index] = 's';
                buf[--index] = 'l';
                buf[--index] = 'a';
                buf[--index] = 'f';
            }
            return index;
        }
        else {
            if(value) {
                StringUTF16.putChar(buf, --index, 'e');
                StringUTF16.putChar(buf, --index, 'u');
                StringUTF16.putChar(buf, --index, 'r');
                StringUTF16.putChar(buf, --index, 't');
            }
            else {
                StringUTF16.putChar(buf, --index, 'e');
                StringUTF16.putChar(buf, --index, 's');
                StringUTF16.putChar(buf, --index, 'l');
                StringUTF16.putChar(buf, --index, 'a');
                StringUTF16.putChar(buf, --index, 'f');
            }
            return index | UTF16;
        }
    }

    static long prepend(long indexCoder, byte[] buf, boolean value, String prefix) {
        indexCoder = prepend(indexCoder, buf, value);
        if(prefix != null) indexCoder = prepend(indexCoder, buf, prefix);
        return indexCoder;
    }

    private static long prepend(long indexCoder, byte[] buf, char value) {
        if(indexCoder < UTF16)
            buf[(int)(--indexCoder)] = (byte) (value & 0xFF);
        else
            StringUTF16.putChar(buf, (int)(--indexCoder), value);
        return indexCoder;
    }

    static long prepend(long indexCoder, byte[] buf, char value, String prefix) {
        indexCoder = prepend(indexCoder, buf, value);
        if(prefix != null) indexCoder = prepend(indexCoder, buf, prefix);
        return indexCoder;
    }

    private static long prepend(long indexCoder, byte[] buf, int value) {
        if(indexCoder < UTF16)
            return Integer.getChars(value, (int)indexCoder, buf);
        else
            return StringUTF16.getChars(value, (int)indexCoder, buf) | UTF16;
    }

    static long prepend(long indexCoder, byte[] buf, int value, String prefix) {
        indexCoder = prepend(indexCoder, buf, value);
        if(prefix != null) indexCoder = prepend(indexCoder, buf, prefix);
        return indexCoder;
    }

    private static long prepend(long indexCoder, byte[] buf, long value) {
        if(indexCoder < UTF16)
            return Long.getChars(value, (int)indexCoder, buf);
        else
            return StringUTF16.getChars(value, (int)indexCoder, buf) | UTF16;
    }

    static long prepend(long indexCoder, byte[] buf, long value, String prefix) {
        indexCoder = prepend(indexCoder, buf, value);
        if(prefix != null) indexCoder = prepend(indexCoder, buf, prefix);
        return indexCoder;
    }

    private static long prepend(long indexCoder, byte[] buf, String value) {
        indexCoder -= value.length();
        if(indexCoder < UTF16)
            value.getBytes(buf, (int)indexCoder, String.LATIN1);
        else
            value.getBytes(buf, (int)indexCoder, String.UTF16);
        return indexCoder;
    }

    static long prepend(long indexCoder, byte[] buf, String value, String prefix) {
        indexCoder = prepend(indexCoder, buf, value);
        if(prefix != null) indexCoder = prepend(indexCoder, buf, prefix);
        return indexCoder;
    }

    static String newString(byte[] buf, long indexCoder) {
        if(indexCoder == LATIN1)
            return new String(buf, String.LATIN1);
        else if(indexCoder == UTF16)
            return new String(buf, String.UTF16);
        else
            throw new InternalError("Storage is not completely initialized, " + (int)indexCoder + " bytes left");
    }

    @ForceInline
    static String simpleConcat(Object first, Object second) {
        String s1 = stringOf(first);
        String s2 = stringOf(second);
        if(s1.isEmpty())
            return new String(s2);
        if(s2.isEmpty())
            return new String(s1);
        long indexCoder = mix(LATIN1, s1);
        indexCoder = mix(indexCoder, s2);
        byte[] buf = newArray(indexCoder);
        indexCoder = prepend(indexCoder, buf, s2);
        indexCoder = prepend(indexCoder, buf, s1);
        return newString(buf, indexCoder);
    }

    @ForceInline
    static String newStringOf(Object arg) {
        return new String(stringOf(arg));
    }

    static String stringOf(Object value) {
        String s;
        return (value == null || (s = value.toString()) == null) ? "null" : s;
    }

    @ForceInline
    static byte[] newArrayWithSuffix(String suffix, long indexCoder) {
        byte[] buf = newArray(indexCoder + suffix.length());
        if(indexCoder < UTF16)
            suffix.getBytes(buf, (int)indexCoder, String.LATIN1);
        else
            suffix.getBytes(buf, (int)indexCoder, String.UTF16);
        return buf;
    }

    @ForceInline
    static byte[] newArray(long indexCoder) {
        byte coder = (byte)(indexCoder >> 32);
        int index = ((int)indexCoder) << coder;
        if(index < 0)
            throw new OutOfMemoryError("Overflow: String length out of range");
        return new byte[index];
    }

    static MethodHandle lookupStatic(String name, MethodType methodType) {
        try {
            return MethodHandles.lookup().findStatic(StringConcatHelper.class, name, methodType);
        }
        catch (NoSuchMethodException|IllegalAccessException e) {
            throw new AssertionError(e);
        }
    }
}
