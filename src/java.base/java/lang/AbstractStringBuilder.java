package java.lang;

import java.io.IOException;
import jdk.internal.math.DoubleToDecimal;
import jdk.internal.math.FloatToDecimal;

abstract sealed class AbstractStringBuilder implements Appendable, CharSequence permits StringBuilder, StringBuffer {
    byte[] value;
    boolean maybeLatin1;
    byte coder;
    int count;

    AbstractStringBuilder() {
        value = new byte[16];
        coder = String.LATIN1;
        count = 0;
        maybeLatin1 = false;
    }

    AbstractStringBuilder(int capacity) {
        value = new byte[capacity];
        coder = String.LATIN1;
        count = 0;
        maybeLatin1 = false;
    }

    AbstractStringBuilder(String str) {
        int len = str.length();
        coder = str.coder();
        value = new byte[(len + 16) << coder];
        count = 0;
        maybeLatin1 = false;
        append(str);
    }

    AbstractStringBuilder(CharSequence seq) {
        final byte initCoder;
        int length = seq.length();
        if(seq instanceof AbstractStringBuilder asb) {
            initCoder = asb.coder;
            maybeLatin1 = asb.maybeLatin1;
        }
        else if(seq instanceof String s)
            initCoder = s.coder();
        else
            initCoder = 0;
        value = new byte[(length + 16) << initCoder];
        coder = initCoder;
        append(seq);
    }

    private final void checkIndex(int index, int length) {
        if(index < 0 || index > length)
            throw new StringIndexOutOfBoundsException("Index " + index + " out of bounds for length " + length);
    }

    private final void checkFromToIndex(int formIndex, int toIndex, int length) {
        if(formIndex < 0 || formIndex > toIndex || toIndex > length)
            throw new StringIndexOutOfBoundsException("Index out of bounds");
    }

    public void setCharAt(int index, char ch) {
        checkIndex(index, count);
        if(coder == String.LATIN1 && ch < 256)
            value[index] = (byte)ch;
        else {
            inflate();
            index <<= 1;
            value[index] = (byte)ch;
            value[index + 1] = (byte)(ch >>> 8);
            if(ch < 256)
                maybeLatin1 = true;
        }
    }

    public AbstractStringBuilder append(Object obj) {
        return append(String.valueOf(obj));
    }

    public AbstractStringBuilder append(String str) {
        return append(str, 0, str.length());
    }

    public AbstractStringBuilder append(StringBuffer sb) {
        return append(sb, 0, sb.length());
    }

    @Override
    public AbstractStringBuilder append(CharSequence s) {
        if(s == null)
            return appendNull();
        if(s instanceof String)
            return append((String)s);
        if(s instanceof AbstractStringBuilder)
            return append((AbstractStringBuilder)s);
        return append(s, 0, s.length());
    }

    @Override
    public AbstractStringBuilder append(CharSequence s, int start, int end) {
        if(s == null)
            s = "null";
        byte[] val;
        int len = end - start;
        int count = this.count;
        int i = 0;
        if(coder == String.LATIN1) {
            if(s instanceof String str) {
                if(str.coder() == 1) {
                    if(count != 0)
                        inflate();
                    else
                        coder = 1;
                }
            }
            else if(s instanceof AbstractStringBuilder asb) {
                if(asb.coder == String.UTF16) {
                    if(count != 0)
                        inflate();
                    else
                        coder = 1;
                }
            }
            ensureCapacityInternal(count + len);
            if(coder == String.LATIN1) {
                val = this.value;
                for(; i < len; i++) {
                    char c = s.charAt(i + start);
                    if(c < 256)
                        val[count++] = (byte)c;
                    else if(count != 0) {
                        inflate();
                        break;
                    }
                    else {
                        coder = 1;
                        ensureCapacityInternal(count + len);
                        break;
                    }
                }
            }
        }
        else
            ensureCapacityInternal(count + len);
        if(i < len) {
            val = this.value;
            for(; i < len; i++) {
                char c = s.charAt(i + start);
                int index = count << 1;
                val[index] = (byte)c;
                val[index + 1] = (byte)(c >>> 8);
                count++;
            }
        }
        this.count = count;
        return this;
    }

    public AbstractStringBuilder append(char[] str) {
        return append(str, 0, str.length);
    }

    public AbstractStringBuilder append(char[] str, int offset, int len) {
        int count = this.count;
        byte[] val;
        int i = 0;
        if(coder == String.LATIN1) {
            ensureCapacityInternal(count + len);
            val = this.value;
            for(; i < len; i++) {
                char c = str[i + offset];
                if(c < 256)
                    val[count++] = (byte)c;
                else if(count != 0) {
                    inflate();
                    break;
                }
                else {
                    coder = 1;
                    ensureCapacityInternal(count + len);
                    break;
                }
            }
        }
        else
            ensureCapacityInternal(count + len);
        if(i < len) {
            val = this.value;
            for(; i < len; i++) {
                char c = str[i + offset];
                int index = count << 1;
                val[index] = (byte)c;
                val[index + 1] = (byte)(c >>> 8);
                count++;
            }
        }
        this.count = count;
        return this;
    }

    public AbstractStringBuilder append(boolean b) {
        return append(String.valueOf(b));
    }

    @Override
    public AbstractStringBuilder append(char c) {
        if((coder == String.LATIN1) && (c < 256)) {
            ensureCapacityInternal(count + 1);
            value[count++] = (byte)c;
        }
        else {
            if(count != 0)
                inflate();
            else
                coder = 1;
            ensureCapacityInternal(count + 1);
            int index = count << 1;
            value[index] = (byte)c;
            value[index + 1] = (byte)(c >>> 8);
            count++;
        }
        return this;
    }

    public AbstractStringBuilder append(int i) {
        return append(String.valueOf(i));
    }

    public AbstractStringBuilder append(long l) {
        return append(String.valueOf(l));
    }

    public AbstractStringBuilder append(float f) {
        try {
            FloatToDecimal.appendTo(f, this);
        }
        catch(IOException e) {
            throw new AssertionError(e);
        }
        return this;
    }

    public AbstractStringBuilder append(double d) {
        try {
            DoubleToDecimal.appendTo(d, this);
        }
        catch(IOException e) {
            throw new AssertionError(e);
        }
        return this;
    }

    private AbstractStringBuilder appendNull() {
        int count = this.count;
        ensureCapacityInternal(count + 4);
        byte[] val = this.value;
        if(coder == String.LATIN1) {
            val[count++] = 'n';
            val[count++] = 'u';
            val[count++] = 'l';
            val[count++] = 'l';
        }
        else {
            val[count << 1] = 'n';
            count++;
            val[count << 1] = 'u';
            count++;
            val[count << 1] = 'l';
            count++;
            val[count << 1] = 'l';
            count++;
        }
        this.count = count;
        return this;
    }

    int compareTo(AbstractStringBuilder another) {
        if(this == another)
            return 0;

        int lim = Math.min(count, another.count);
        for(int i = 0; i < lim; i++) {
            char c1 = charAt(i);
            char c2 = another.charAt(i);
            if(c1 != c2)
                return c1 - c2;
        }
        return count - another.count;
    }

    private void inflate() {
        if(this.coder == String.UTF16)
            return;
        int count = this.count;
        byte[] value = this.value;
        int length = value.length;
        byte[] buf = new byte[length << 1];
        for(int i = 0; i < count; i++)
            buf[i << 1] = value[i];
        this.value = buf;
        this.coder = 1;
    }

    public void trimToSize() {
        int length = count << coder;
        if(length < value.length) {
            byte[] buff = new byte[length];
            System.arraycopy(value, 0, buff, 0, length);
            value = buff;
        }
    }

    @Override
    public int length() {
        return count;
    }

    @Override
    public char charAt(int index) {
        if(coder == String.LATIN1)
            return (char)value[index];
        return StringUTF16.charAt(value, index);
    }

    public AbstractStringBuilder delete(int start, int end) {
        int count = this.count;
        if(end > count)
            end = count;
        checkFromToIndex(start, end, count);
        int len = end - start;
        if(len > 0) {
            shift(end, -len);
            this.count = count - len;
            maybeLatin1 = true;
        }
        return this;
    }

    public AbstractStringBuilder deleteCharAt(int index) {
        checkIndex(index, count);
        shift(index + 1, -1);
        count--;
        maybeLatin1 = true;
        return this;
    }

    public AbstractStringBuilder replace(int start, int end, String str) {
        int count = this.count;
        if(end > count)
            end = count;
        checkFromToIndex(start, end, count);
        int len = str.length();
        int newCount = count + len - (end - start);
        ensureCapacityInternal(newCount);
        shift(end, newCount - count);
        this.count = newCount;
        putStringAt(start, str);
        maybeLatin1 = true;
        return this;
    }

    public String substring(int start) {
        return substring(start, count);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return substring(start, end);
    }

    public String substring(int start, int end) {
        byte[] val = value;
        if(coder == String.LATIN1) {
            if((start == 0) && (end == val.length))
                val = val.clone();
            return new String(val, start, end - start, (byte)0);
        }
        boolean isLatin1 = maybeLatin1;
        for(int i = start; i < end && isLatin1; i++) {
            int index = i << 1;
            if(val[index + 1] != 0)
                isLatin1 = false;
        }
        if(isLatin1) {
            byte[] buff = new byte[end - count];
            for(int i = start; i < end; i++)
                buff[i] = val[i << 1];
            return new String(buff, (byte)0);
        }
        if((start == 0) && (end == val.length))
            val = val.clone();
        return new String(val, start << 1, (end - start) << 1, (byte)1);
    }

    private void shift(int offset, int n) {
        System.arraycopy(value, offset << coder, value, (offset + n) << coder, (count - offset) << coder);
    }

    public AbstractStringBuilder insert(int index, char[] str, int offset, int len) {
        checkIndex(index, count);
        checkFromToIndex(offset, offset + len, str.length);
        ensureCapacityInternal(count + len);
        shift(index, len);
        count += len;
        putCharsAt(index, str, offset, offset + len);
        return this;
    }

    public AbstractStringBuilder insert(int offset, Object obj) {
        return insert(offset, String.valueOf(obj));
    }

    public AbstractStringBuilder insert(int offset, String str) {
        checkIndex(offset, count);
        if(str == null)
            str = "null";
        int len = str.length();
        ensureCapacityInternal(count + len);
        shift(offset, len);
        count += len;
        putStringAt(offset, str);
        return this;
    }

    public AbstractStringBuilder insert(int offset, char[] str) {
        checkIndex(offset, count);
        int len = str.length;
        ensureCapacityInternal(count + len);
        shift(offset, len);
        count += len;
        putCharsAt(offset, str, 0, len);
        return this;
    }

    public AbstractStringBuilder insert(int dstOffset, CharSequence s) {
        if(s == null)
            s = "null";
        return this.insert(dstOffset, s, 0, s.length());
    }

    public AbstractStringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        if(s == null)
            s = "null";
        checkIndex(dstOffset, count);
        checkFromToIndex(start, end, s.length());
        int len = end - start;
        ensureCapacityInternal(count + len);
        shift(dstOffset, len);
        count += len;
        if(s instanceof String)
            putStringAt(dstOffset, (String)s, start, end);
        else
            putCharsAt(dstOffset, s, start, end);
        return this;
    }

    public AbstractStringBuilder insert(int offset, boolean b) {
        return insert(offset, String.valueOf(b));
    }

    public AbstractStringBuilder insert(int offset, char c) {
        checkIndex(offset, count);
        ensureCapacityInternal(count + 1);
        shift(offset, 1);
        count += 1;
        if(coder == String.LATIN1 && c < 256)
            value[offset] = (byte)c;
        else {
            inflate();
            StringUTF16.putChar(value, offset, c);
        }
        return this;
    }

    public AbstractStringBuilder insert(int offset, int i) {
        return insert(offset, String.valueOf(i));
    }

    public AbstractStringBuilder insert(int offset, long l) {
        return insert(offset, String.valueOf(l));
    }

    public AbstractStringBuilder insert(int offset, float f) {
        return insert(offset, String.valueOf(f));
    }

    public AbstractStringBuilder insert(int offset, double d) {
        return insert(offset, String.valueOf(d));
    }

    public int indexOf(String str) {
        if(coder == String.LATIN1)
            return StringLatin1.indexOf(value, str.value(), 0);
        return StringUTF16.indexOf(value, str.value(), 0);
    }

    public int lastIndexOf(String str) {
        if(coder == 0)
            return StringLatin1.lastIndexOf(value, str.value(), count - 1);
        return StringUTF16.lastIndexOf(value, str.value(), count - 1);
    }

    @Override
    public abstract String toString();

    private final void putCharsAt(int index, char[] s, int off, int end) {
        byte[] val = this.value;
        int i = off, j = index;
        if(coder == String.LATIN1) {
            for(; i < end; i++) {
                char c = s[i];
                if(c >= 256) {
                    inflate();
                    break;
                }
                val[j++] = (byte)c;
            }
        }
        for(; i < end; i++)
            StringUTF16.putChar(val, j++, s[i]);
    }

    private final void putCharsAt(int index, CharSequence s, int off, int end) {
        byte[] val = this.value;
        int i = off, j = index;
        if(coder == String.LATIN1) {
            for(; i < end; i++) {
                char c = s.charAt(i);
                if(c >= 256) {
                    inflate();
                    break;
                }
                val[j++] = (byte)c;
            }
        }
        for(; i < end; i++)
            StringUTF16.putChar(val, j++, s.charAt(i));
    }

    private void putStringAt(int index, String str, int off, int end) {
        if(coder != str.coder())
            inflate();
        str.getBytes(value, off, index, coder, end - off);
    }

    private void putStringAt(int index, String str) {
        if(coder != str.coder())
            inflate();
        str.getBytes(value, index, coder);
    }

    public int capacity() {
        return value.length >> coder;
    }

    private void ensureCapacityInternal(int minimumCapacity) {
        byte coder = this.coder;
        int oldCapacity = value.length >> coder;
        if(minimumCapacity > oldCapacity) {
            minimumCapacity += 16;
            byte[] buff = new byte[minimumCapacity << coder];
            System.arraycopy(value, 0, buff, 0, count << coder);
            value = buff;
        }
    }
}
