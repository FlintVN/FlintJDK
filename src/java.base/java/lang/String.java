package java.lang;

import java.util.Objects;
import java.lang.annotation.Native;
import jdk.internal.vm.annotation.ForceInline;

public final class String implements Comparable<String>, CharSequence {
    private final byte[] value;
    private final byte coder;
    private int hash;
    private boolean hashIsZero;

    @Native static final byte LATIN1 = 0;
    @Native static final byte UTF16  = 1;

    private static boolean isLatin1(byte[] utf8Value, int offset, int count) {
        count += offset;
        while(offset < count) {
            byte b = utf8Value[offset];
            if(b < 0) {
                int byteCount = getUtf8ByteCount(b);
                int code = utf8Decode(utf8Value, offset, byteCount);
                if(code > 255)
                    return false;
                offset += byteCount;
            }
            else
                offset++;
        }
        return true;
    }

    private static boolean isLatin1(char[] utf8Value, int offset, int count) {
        count += offset;
        for(; offset < count; offset++)
            if(utf8Value[offset] > 255)
                return false;
        return true;
    }

    private static int getUtf8ByteCount(byte b) {
        if(b > 0)
            return 1;
        if((b & 0xE0) == 0xC0)
            return 2;
        else if((b & 0xF0) == 0xE0)
            return 3;
        else if((b & 0xF8) == 0xF0)
            return 4;
        else if((b & 0xFC) == 0xF8)
            return 5;
        else
            return 6;
    }

    private static int utf8Decode(byte[] utf8Value, int offset, int byteCount) {
        byte b = utf8Value[offset];
        int code = b & (0xFF >> (byteCount + 1));
        while(--byteCount > 0) {
            offset++;
            code <<= 6;
            code |= utf8Value[offset] & 0x3F;
        }
        return code;
    }

    private static int getUtf8StrLength(byte[] utf8, int offset, int count) {
        int len = 0;
        count += offset;
        while(offset < count) {
            offset += getUtf8ByteCount(utf8[offset]);
            len++;
        }
        return len;
    }

    public String() {
        value = "".value;
        coder = "".coder;
    }

    public String(String original) {
        value = original.value;
        coder = original.coder;
    }

    public String(char[] value) {
        this(value, 0, value.length);
    }

    public String(char[] value, int offset, int count) {
        boolean isLatin1 = isLatin1(value, offset, count);
        byte[] buff = isLatin1 ? new byte[count] : new byte[count << 1];
        if(isLatin1) for(int i = 0; i < count; i++)
            buff[i] = (byte)value[i + offset];
        else for(int i = 0; i < count; i++)
            StringUTF16.putChar(buff, i, value[i + offset]);
        this.value = buff;
        this.coder = isLatin1 ? (byte)0 : (byte)1;
    }

    String(byte[] value, byte coder) {
        this(value, 0, value.length, coder);
    }

    String(byte[] value, int offset, int count, byte coder) {
        if(offset == 0 && count == value.length)
            this.value = value;
        else {
            this.value = new byte[count];
            System.arraycopy(value, offset, this.value, 0, count);
        }
        this.coder = coder;
    }

    public String(byte[] utf8Value) {
        this(utf8Value, 0, utf8Value.length);
    }

    public String(byte[] utf8Value, int offset, int count) {
        boolean isLatin1 = isLatin1(utf8Value, offset, count);
        int length = getUtf8StrLength(utf8Value, offset, count);
        byte[] buff = isLatin1 ? new byte[length] : new byte[length << 1];
        if(isLatin1) {
            for(int i = 0; i < length; i++) {
                byte b = utf8Value[offset];
                int byteCount = getUtf8ByteCount(b);
                int code = utf8Decode(utf8Value, offset, byteCount);
                buff[i] = (byte)code;
                offset += byteCount;
            }
        }
        else for(int i = 0; i < length; i++) {
            byte b = utf8Value[offset];
            int byteCount = getUtf8ByteCount(b);
            int code = utf8Decode(utf8Value, offset, byteCount);
            StringUTF16.putChar(buff, i, (char)code);
            offset += byteCount;
        }
        this.value = buff;
        this.coder = isLatin1 ? (byte)0 : (byte)1;
    }

    public String(StringBuffer buffer) {
        this(buffer.toString());
    }

    public String(StringBuilder builder) {
        this(builder.toString());
    }

    byte coder() {
        return coder;
    }

    byte[] value() {
        return value;
    }

    @Override
    public int length() {
        return value.length >>> coder;
    }

    @Override
    public char charAt(int index) {
        if(coder == LATIN1)
            return (char)value[index];
        return StringUTF16.charAt(value, index);
    }

    @Override
    public boolean isEmpty() {
        return value.length == 0;
    }

    public boolean startsWith(String prefix, int toffset) {
        if(toffset < 0 || toffset > length() - prefix.length())
            return false;
        byte ta[] = value;
        byte pa[] = prefix.value;
        int po = 0;
        int pc = pa.length;
        byte coder = this.coder;
        if(coder == prefix.coder) {
            int to = (coder == LATIN1) ? toffset : toffset << 1;
            while(po < pc) {
                if(ta[to++] != pa[po++])
                    return false;
            }
        }
        else {
            if(coder == LATIN1)
                return false;
            while(po < pc) {
                if(StringUTF16.charAt(ta, toffset++) != (pa[po++] & 0xff))
                    return false;
            }
        }
        return true;
    }

    public boolean startsWith(String prefix) {
        return startsWith(prefix, 0);
    }

    public boolean endsWith(String suffix) {
        return startsWith(suffix, length() - suffix.length());
    }

    public boolean contains(CharSequence s) {
        return indexOf(s.toString()) >= 0;
    }

    public int indexOf(int ch) {
        return indexOf(ch, 0);
    }

    public int indexOf(int ch, int fromIndex) {
        return (coder == LATIN1) ? StringLatin1.indexOf(value, ch, fromIndex) : StringUTF16.indexOf(value, ch, fromIndex);
    }

    public int indexOf(String str) {
        return indexOf(str, 0);
    }

    public int indexOf(String str, int fromIndex) {
        byte coder = this.coder;
        if(coder == str.coder)
            return (coder == LATIN1) ? StringLatin1.indexOf(value, str.value, fromIndex) : StringUTF16.indexOf(value, str.value, fromIndex);
        if(coder == LATIN1)
            return -1;
        return StringUTF16.indexOfLatin1(value, str.value, fromIndex);
    }

    public int lastIndexOf(int ch) {
        return lastIndexOf(ch, length() - 1);
    }

    public int lastIndexOf(int ch, int fromIndex) {
        if(coder == LATIN1)
            return StringLatin1.lastIndexOf(value, ch, fromIndex);
        return StringUTF16.lastIndexOf(value, ch, fromIndex);
    }

    public int lastIndexOf(String str) {
        return lastIndexOf(str, length() - 1);
    }

    public int lastIndexOf(String str, int fromIndex) {
        byte coder = this.coder;
        if(coder == str.coder) {
            if(coder == LATIN1)
                return StringLatin1.lastIndexOf(value, str.value, fromIndex);
            return StringUTF16.lastIndexOf(value, str.value, fromIndex);
        }
        if(coder == LATIN1)
            return -1;
        return StringUTF16.lastIndexOfLatin1(value, str.value, fromIndex);
    }

    public String concat(String str) {
        if(str.isEmpty())
            return this;
        byte coder = this.coder;
        int len1 = length();
        int len2 = str.length();
        byte[] value1 = this.value;
        byte[] value2 = str.value;
        if((coder == UTF16) || (str.coder == UTF16)) {
            byte[] buff = new byte[(len1 + len2) << 1];
            if(coder == LATIN1) {
                for(int i = 0; i < len1; i++)
                    StringUTF16.putChar(buff, i, (char)(value1[i] & 0xFF));
            }
            else for(int i = 0; i < len1; i++)
                StringUTF16.putChar(buff, i, StringUTF16.charAt(value1, i));
            if(str.coder == LATIN1) {
                for(int i = 0; i < len2; i++)
                    StringUTF16.putChar(buff, i + len1, (char)(value2[i] & 0xFF));
            }
            else for(int i = 0; i < len2; i++)
                StringUTF16.putChar(buff, i + len1, StringUTF16.charAt(value2, i));
            return new String(buff, 0, buff.length, (byte)1);
        }
        else {
            byte[] buff = new byte[len1 + len2];
            for(int i = 0; i < len1; i++)
                buff[i] = value1[i];
            for(int i = 0; i < len2; i++)
                buff[i + len1] = value2[i];
            return new String(buff, 0, buff.length, (byte)0);
        }
    }

    public String replace(char oldChar, char newChar) {
        if(oldChar != newChar) {
            String ret = (coder == LATIN1) ? StringLatin1.replace(value, oldChar, newChar) : StringUTF16.replace(value, oldChar, newChar);
            if(ret != null)
                return ret;
        }
        return this;
    }

    public String replace(CharSequence target, CharSequence replacement) {
        String targetStr = target.toString();
        int index = indexOf(targetStr);
        if(index >= 0) {
            int targetLength = target.length();
            StringBuilder sb = new StringBuilder();
            if(index != 0)
                sb = new StringBuilder(substring(0, index));
            sb.append(replacement);

            int startIndex = index + targetLength;
            index = indexOf(targetStr, index + targetLength);
            while(index >= 0) {
                if(index != startIndex)
                    sb.append(substring(startIndex, index));
                sb.append(replacement);
                startIndex = index + targetLength;
                index = indexOf(targetStr, startIndex);
            }
            if(index >= 0)
                sb.append(substring(startIndex, index));
            return sb.toString();
        }
        return this;
    }

    public void getChars(int srcBegin, int srcEnd, char[] dst, int dstBegin) {
        if(coder == LATIN1)
            StringLatin1.getChars(value, srcBegin, srcEnd, dst, dstBegin);
        else
            StringUTF16.getChars(value, srcBegin, srcEnd, dst, dstBegin);
    }

    void getBytes(byte[] dst, int dstBegin, byte coder) {
        if(this.coder == coder)
            System.arraycopy(value, 0, dst, dstBegin << coder, value.length);
        else {
            int len = value.length;
            if(dstBegin < 0 || ((dstBegin + len) << coder) > dst.length)
                throw new StringIndexOutOfBoundsException("Index out of bounds");
            dstBegin <<= coder;
            byte[] val = value;
            for(int i = 0; i < len; i++) {
                dst[dstBegin++] = val[i];
                dst[dstBegin++] = 0;
            }
        }
    }

    void getBytes(byte[] dst, int srcPos, int dstBegin, byte coder, int length) {
        if(this.coder == coder)
            System.arraycopy(value, srcPos << coder, dst, dstBegin << coder, length << coder);
        else {
            if(srcPos < 0 || srcPos >= length)
                throw new StringIndexOutOfBoundsException("Index " + srcPos + " out of bounds for length " + length);
            if((dstBegin < 0) || ((dstBegin + (length << coder)) > dst.length))
                throw new StringIndexOutOfBoundsException("Index out of bounds");
            dstBegin <<= coder;
            byte[] val = value;
            for(int i = 0; i < length; i++) {
                dst[dstBegin++] = val[i + srcPos];
                dst[dstBegin++] = 0;
            }
        }
    }

    public byte[] getBytes() {
        int len = length();
        int coder = this.coder;
        int retLen = 0, idx = 0;
        byte[] value = this.value;
        if(coder == LATIN1) {
            for(int i = 0; i < len; i++)
                retLen += value[i] > 0 ? 1 : 2;
            byte[] ret = new byte[retLen];
            for(int i = 0; i < len; i++)
                idx += utf8EncodeOneChar((char)value[i], ret, idx);
            return ret;
        }
        else {
            for(int i = 0; i < len; i++) {
                char c = StringUTF16.charAt(value, i);
                retLen += c < 0x80 ? 1 : (c < 0x0800 ? 2 : 3);
            }
            byte[] ret = new byte[retLen];
            for(int i = 0; i < len; i++)
                idx += utf8EncodeOneChar(StringUTF16.charAt(value, i), ret, idx);
            return ret;
        }
    }

    private static int utf8EncodeOneChar(char c, byte[] buff, int index) {
        if(c < 0x80) {
            buff[index] = (byte)c;
            return 1;
        }
        else if(c < 0x0800) {
            buff[index] = (byte)(0xC0 | (c >> 6));
            buff[index + 1] = (byte)(0x80 | (c & 0x3F));
            return 2;
        }
        else {
            buff[index] = (byte)(0xE0 | (c >> 12));
            buff[index + 1] = (byte)(0x80 | ((c >> 6) & 0x3F));
            buff[index + 2] = (byte)(0x80 | (c & 0x3F));
            return 3;
        }
    }

    public String substring(int beginIndex) {
        return substring(beginIndex, length());
    }

    public String substring(int beginIndex, int endIndex) {
        byte[] val = value;
        int length = val.length >>> coder;
        if(beginIndex == 0 && endIndex == length)
            return this;
        int subLen = endIndex - beginIndex;
        if(coder == LATIN1)
            return new String(val, beginIndex, subLen, (byte)0);
        boolean isLatin1 = true;
        for(int i = beginIndex; i < endIndex; i++) {
            if(StringUTF16.charAt(val, i) > 255) {
                isLatin1 = false;
                break;
            }
        }
        if(isLatin1) {
            byte[] buff = new byte[subLen];
            for(int i = beginIndex; i < endIndex; i++)
                buff[i] = val[i << 1];
            return new String(buff, (byte)0);
        }
        return new String(val, beginIndex << 1, subLen << 1, (byte)1);
    }

    @Override
    public CharSequence subSequence(int beginIndex, int endIndex) {
        return this.substring(beginIndex, endIndex);
    }

    public String[] split() {
        return coder == LATIN1 ? StringLatin1.split(value) : StringUTF16.split(value);
    }

    public String[] split(char ch) {
        String[] ret = coder == LATIN1 ? StringLatin1.split(value, ch) : StringUTF16.split(value, ch);
        if(ret == null)
            ret = new String[] {this};
        return ret;
    }

    public static String join(CharSequence delimiter, CharSequence... elements) {
        var delim = delimiter.toString();
        var elems = new String[elements.length];
        for(int i = 0; i < elements.length; i++)
            elems[i] = String.valueOf(elements[i]);
        return join("", "", delim, elems, elems.length);
    }

    public static String join(String prefix, String suffix, String delimiter, String[] elements, int size) {
        int icoder = prefix.coder() | suffix.coder();
        long len = (long)prefix.length() + suffix.length();
        if(size > 1) {
            len += (long)(size - 1) * delimiter.length();
            icoder |= delimiter.coder();
        }
        for(int i = 0; i < size; i++) {
            var el = elements[i];
            len += el.length();
            icoder |= el.coder();
        }
        byte coder = (byte)icoder;
        if(len < 0L || (len <<= coder) != (int)len)
            throw new OutOfMemoryError("Requested string length exceeds VM limit");
        byte[] value = new byte[(int)len];
        int off = 0;
        prefix.getBytes(value, off, coder);
        off += prefix.length();
        if(size > 0) {
            var el = elements[0];
            el.getBytes(value, off, coder);
            off += el.length();
            for(int i = 1; i < size; i++) {
                delimiter.getBytes(value, off, coder);
                off += delimiter.length();
                el = elements[i];
                el.getBytes(value, off, coder); off += el.length();
            }
        }
        suffix.getBytes(value, off, coder);
        return new String(value, 0, value.length, coder);
    }

    public static String join(CharSequence delimiter, Iterable<? extends CharSequence> elements) {
        Objects.requireNonNull(delimiter);
        Objects.requireNonNull(elements);
        var delim = delimiter.toString();
        var elems = new String[8];
        int size = 0;
        for(CharSequence cs: elements) {
            if(size >= elems.length) {
                String[] tmp = new String[elems.length << 1];
                System.arraycopy(elems, 0, tmp, 0, elems.length);
                elems = tmp;
            }
            elems[size++] = String.valueOf(cs);
        }
        return join("", "", delim, elems, size);
    }

    public String toLowerCase() {
        String ret = (coder == LATIN1) ? StringLatin1.toLowerCase(value) : StringUTF16.toLowerCase(value);
        if(ret == null)
            return this;
        return ret;
    }

    public String toUpperCase() {
        String ret = (coder == LATIN1) ? StringLatin1.toUpperCase(value) : StringUTF16.toUpperCase(value);
        if(ret == null)
            return this;
        return ret;
    }

    public String trim() {
        String ret = (coder == LATIN1) ? StringLatin1.trim(value) : StringUTF16.trim(value);
        if(ret == null)
            return this;
        return ret;
    }

    public String translateEscapes() {
        if(isEmpty())
            return "";
        char[] chars = toCharArray();
        int length = chars.length;
        int from = 0;
        int to = 0;
        while(from < length) {
            char ch = chars[from++];
            if(ch == '\\') {
                ch = from < length ? chars[from++] : '\0';
                switch(ch) {
                    case 'b':
                        ch = '\b';
                        break;
                    case 'f':
                        ch = '\f';
                        break;
                    case 'n':
                        ch = '\n';
                        break;
                    case 'r':
                        ch = '\r';
                        break;
                    case 's':
                        ch = ' ';
                        break;
                    case 't':
                        ch = '\t';
                        break;
                    case '\'':
                    case '\"':
                    case '\\':
                        break;
                    case '0': case '1': case '2': case '3':
                    case '4': case '5': case '6': case '7':
                        int limit = Integer.min(from + (ch <= '3' ? 2 : 1), length);
                        int code = ch - '0';
                        while(from < limit) {
                            ch = chars[from];
                            if(ch < '0' || '7' < ch)
                                break;
                            from++;
                            code = (code << 3) | (ch - '0');
                        }
                        ch = (char)code;
                        break;
                    case '\n':
                        continue;
                    case '\r':
                        if(from < length && chars[from] == '\n')
                            from++;
                        continue;
                    default: {
                        String msg = "Invalid escape sequence: " + ch + " " + Integer.toHexString((int)ch);
                        throw new IllegalArgumentException(msg);
                    }
                }
            }
            chars[to++] = ch;
        }
        return new String(chars, 0, to);
    }

    public char[] toCharArray() {
        return (coder == LATIN1) ? StringLatin1.toChars(value) : StringUTF16.toChars(value);
    }

    public static String valueOf(Object obj) {
        return (obj == null) ? "null" : obj.toString();
    }

    public static String valueOf(char[] data) {
        return new String(data);
    }

    public static String valueOf(char[] data, int offset, int count) {
        return new String(data, offset, count);
    }

    public static String copyValueOf(char[] data, int offset, int count) {
        return new String(data, offset, count);
    }

    public static String copyValueOf(char[] data) {
        return new String(data);
    }


    public static String valueOf(boolean b) {
        return b ? "true" : "false";
    }

    public static String valueOf(char c) {
        if(c < 256) {
            byte[] buff = new byte[1];
            buff[0] = (byte)c;
            return new String(buff, (byte)0);
        }
        else {
            byte[] buff = new byte[2];
            buff[0] = (byte)c;
            buff[1] = (byte)(c >>> 8);
            return new String(buff, (byte)0);
        }
    }

    public static String valueOf(int i) {
        return Integer.toString(i);
    }

    public static String valueOf(long l) {
        return Long.toString(l);
    }

    public static String valueOf(float f) {
        return Float.toString(f);
    }

    public static String valueOf(double d) {
        return Double.toString(d);
    }

    public native String intern();

    public String repeat(int count) {
        if(count < 0)
            throw new IllegalArgumentException("count is negative: " + count);
        if(count == 1)
            return this;
        byte[] value = this.value;
        int len = value.length;
        if(len == 0 || count == 0)
            return "";
        byte[] buff = new byte[len * count];
        for(int i = 0; i < count; i++)
            System.arraycopy(value, 0, buff, i * len, len);
        return new String(buff, 0, buff.length, this.coder);
    }

    @Override
    public String toString() {
        return this;
    }

    public boolean equals(Object anObject) {
        if(this == anObject)
            return true;
        if((anObject instanceof String aString) && (this.coder == aString.coder))
            return StringLatin1.equals(value, aString.value);
        return false;
    }

    public boolean equalsIgnoreCase(String anotherString) {
        if(this == anotherString)
            return true;
        byte coder = this.coder;
        if(coder == anotherString.coder) {
            if(coder == LATIN1)
                return StringLatin1.equalsIgnoreCase(value, anotherString.value);
            return StringUTF16.equalsIgnoreCase(value, anotherString.value);
        }
        return false;
    }

    @Override
    public int compareTo(String anotherString) {
        byte[] v1 = value;
        byte[] v2 = anotherString.value;
        byte coder = this.coder;
        if(coder == anotherString.coder)
            return coder == LATIN1 ? StringLatin1.compareTo(v1, v2) : StringUTF16.compareTo(v1, v2);
        return coder == LATIN1 ? StringLatin1.compareToUTF16(v1, v2) : StringUTF16.compareToLatin1(v1, v2);
    }

    public int compareToIgnoreCase(String str) {
        byte[] v1 = this.value;
        byte[] v2 = str.value;
        byte coder = this.coder();
        if(coder == str.coder())
            return coder == LATIN1 ? StringLatin1.compareToCI(v1, v2) : StringUTF16.compareToCI(v1, v2);
        return coder == LATIN1 ? StringLatin1.compareToCI_UTF16(v1, v2) : StringUTF16.compareToCI_Latin1(v1, v2);
    }

    @Override
    public int hashCode() {
        int h = hash;
        if(h == 0 && !hashIsZero) {
            h = (coder == LATIN1) ? StringLatin1.hashCode(value) : StringUTF16.hashCode(value);
            if(h == 0)
                hashIsZero = true;
            else
                hash = h;
        }
        return h;
    }
}
