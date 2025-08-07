package java.lang;

public final class StringBuilder extends AbstractStringBuilder implements Comparable<StringBuilder>, CharSequence {
    public StringBuilder() {
        super(16);
    }

    public StringBuilder(int capacity) {
        super(capacity);
    }

    public StringBuilder(String str) {
        super(str);
    }

    public StringBuilder(CharSequence seq) {
        super(seq);
    }

    @Override
    public StringBuilder append(Object obj) {
        super.append(String.valueOf(obj));
        return this;
    }

    @Override
    public StringBuilder append(String str) {
        super.append(str, 0, str.length());
        return this;
    }

    @Override
    public StringBuilder append(StringBuffer sb) {
        super.append(sb, 0, sb.length());
        return this;
    }

    @Override
    public StringBuilder append(CharSequence s) {
        super.append(s);
        return this;
    }

    @Override
    public StringBuilder append(CharSequence s, int start, int end) {
        super.append(s, start, end);
        return this;
    }

    @Override
    public StringBuilder append(char[] str) {
        super.append(str, 0, str.length);
        return this;
    }

    @Override
    public StringBuilder append(char[] str, int offset, int len) {
        super.append(str, offset, len);
        return this;
    }

    @Override
    public StringBuilder append(boolean b) {
        super.append(String.valueOf(b));
        return this;
    }

    @Override
    public StringBuilder append(char c) {
        super.append(c);
        return this;
    }

    @Override
    public StringBuilder append(int i) {
        super.append(String.valueOf(i));
        return this;
    }

    @Override
    public StringBuilder append(long l) {
        super.append(String.valueOf(l));
        return this;
    }

    @Override
    public StringBuilder append(float f) {
        super.append(f);
        return this;
    }

    @Override
    public StringBuilder append(double d) {
        super.append(d);
        return this;
    }

    @Override
    public StringBuilder delete(int start, int end) {
        super.delete(start, end);
        return this;
    }

    @Override
    public StringBuilder deleteCharAt(int index) {
        super.deleteCharAt(index);
        return this;
    }

    @Override
    public StringBuilder replace(int start, int end, String str) {
        super.replace(start, end, str);
        return this;
    }

    @Override
    public StringBuilder insert(int index, char[] str, int offset, int len) {
        super.insert(index, str, offset, len);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, Object obj) {
            super.insert(offset, obj);
            return this;
    }

    @Override
    public StringBuilder insert(int offset, String str) {
        super.insert(offset, str);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, char[] str) {
        super.insert(offset, str);
        return this;
    }

    @Override
    public StringBuilder insert(int dstOffset, CharSequence s) {
            super.insert(dstOffset, s);
            return this;
    }

    @Override
    public StringBuilder insert(int dstOffset, CharSequence s, int start, int end) {
        super.insert(dstOffset, s, start, end);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, boolean b) {
        super.insert(offset, b);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, char c) {
        super.insert(offset, c);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, int i) {
        super.insert(offset, i);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, long l) {
        super.insert(offset, l);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, float f) {
        super.insert(offset, f);
        return this;
    }

    @Override
    public StringBuilder insert(int offset, double d) {
        super.insert(offset, d);
        return this;
    }

    @Override
    public StringBuilder repeat(CharSequence cs, int count) {
        super.repeat(cs, count);
        return this;
    }

    @Override
    public int compareTo(StringBuilder another) {
        return super.compareTo(another);
    }

    @Override
    public int length() {
        return count;
    }

    @Override
    public int capacity() {
        return value.length >> coder;
    }

    @Override
    public String toString() {
        return substring(0, count);
    }
}
