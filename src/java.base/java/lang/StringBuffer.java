package java.lang;

public final class StringBuffer extends AbstractStringBuilder implements Comparable<StringBuilder>, CharSequence {
    private transient String toStringCache;

    public StringBuffer() {
        super(16);
    }

    public StringBuffer(int capacity) {
        super(capacity);
    }

    public StringBuffer(String str) {
        super(str);
    }

    public StringBuffer(CharSequence seq) {
        super(seq);
    }

    @Override
    public synchronized void setCharAt(int index, char ch) {
        toStringCache = null;
        super.setCharAt(index, ch);
    }

    @Override
    public synchronized StringBuffer append(Object obj) {
        toStringCache = null;
        super.append(String.valueOf(obj));
        return this;
    }

    @Override
    public synchronized StringBuffer append(String str) {
        toStringCache = null;
        super.append(str, 0, str.length());
        return this;
    }

    @Override
    public synchronized StringBuffer append(StringBuffer sb) {
        toStringCache = null;
        super.append(sb, 0, sb.length());
        return this;
    }

    @Override
    public synchronized StringBuffer append(CharSequence s) {
        toStringCache = null;
        super.append(s);
        return this;
    }

    @Override
    public synchronized StringBuffer append(CharSequence s, int start, int end) {
        toStringCache = null;
        super.append(s, start, end);
        return this;
    }

    @Override
    public synchronized StringBuffer append(char[] str) {
        toStringCache = null;
        super.append(str, 0, str.length);
        return this;
    }

    @Override
    public synchronized StringBuffer append(char[] str, int offset, int len) {
        toStringCache = null;
        super.append(str, offset, len);
        return this;
    }

    @Override
    public synchronized StringBuffer append(boolean b) {
        toStringCache = null;
        super.append(String.valueOf(b));
        return this;
    }

    @Override
    public synchronized StringBuffer append(char c) {
        toStringCache = null;
        super.append(c);
        return this;
    }

    @Override
    public synchronized StringBuffer append(int i) {
        toStringCache = null;
        super.append(String.valueOf(i));
        return this;
    }

    @Override
    public synchronized StringBuffer append(long l) {
        toStringCache = null;
        super.append(String.valueOf(l));
        return this;
    }

    @Override
    public synchronized StringBuffer append(float f) {
        toStringCache = null;
        super.append(f);
        return this;
    }

    @Override
    public synchronized StringBuffer append(double d) {
        toStringCache = null;
        super.append(d);
        return this;
    }

    @Override
    public synchronized StringBuffer delete(int start, int end) {
        toStringCache = null;
        super.delete(start, end);
        return this;
    }

    @Override
    public synchronized StringBuffer deleteCharAt(int index) {
        toStringCache = null;
        super.deleteCharAt(index);
        return this;
    }

    @Override
    public synchronized StringBuffer replace(int start, int end, String str) {
        toStringCache = null;
        super.replace(start, end, str);
        return this;
    }

    @Override
    public synchronized void trimToSize() {
        super.trimToSize();
    }

    @Override
    public synchronized int compareTo(StringBuilder another) {
        return super.compareTo(another);
    }

    @Override
    public synchronized char charAt(int index) {
        return super.charAt(index);
    }

    @Override
    public synchronized String substring(int start) {
        return super.substring(start, count);
    }

    @Override
    public synchronized CharSequence subSequence(int start, int end) {
        return super.substring(start, end);
    }

    @Override
    public synchronized String substring(int start, int end) {
        return super.substring(start, end);
    }

    @Override
    public synchronized StringBuffer insert(int index, char[] str, int offset, int len) {
        toStringCache = null;
        super.insert(index, str, offset, len);
        return this;
    }

    @Override
    public synchronized StringBuffer insert(int offset, Object obj) {
        toStringCache = null;
        super.insert(offset, String.valueOf(obj));
        return this;
    }

    @Override
    public synchronized StringBuffer insert(int offset, String str) {
        toStringCache = null;
        super.insert(offset, str);
        return this;
    }

    @Override
    public synchronized StringBuffer insert(int offset, char[] str) {
        toStringCache = null;
        super.insert(offset, str);
        return this;
    }

    @Override
    public StringBuffer insert(int dstOffset, CharSequence s) {
        super.insert(dstOffset, s);
        return this;
    }

    @Override
    public synchronized StringBuffer insert(int dstOffset, CharSequence s, int start, int end) {
        toStringCache = null;
        super.insert(dstOffset, s, start, end);
        return this;
    }

    @Override
    public StringBuffer insert(int offset, boolean b) {
        super.insert(offset, b);
        return this;
    }

    @Override
    public synchronized StringBuffer insert(int offset, char c) {
        toStringCache = null;
        super.insert(offset, c);
        return this;
    }

    @Override
    public StringBuffer insert(int offset, int i) {
        super.insert(offset, i);
        return this;
    }

    @Override
    public StringBuffer insert(int offset, long l) {
        super.insert(offset, l);
        return this;
    }

    @Override
    public StringBuffer insert(int offset, float f) {
        super.insert(offset, f);
        return this;
    }

    @Override
    public StringBuffer insert(int offset, double d) {
        super.insert(offset, d);
        return this;
    }

    @Override
    public synchronized int indexOf(String str) {
        return super.indexOf(str);
    }

    @Override
    public synchronized int lastIndexOf(String str) {
        return super.lastIndexOf(str);
    }

    @Override
    public synchronized int length() {
        return count;
    }

    @Override
    public synchronized int capacity() {
        return value.length >> coder;
    }

    @Override
    public synchronized String toString() {
        if(toStringCache == null)
            return toStringCache = super.substring(0, count);
        return toStringCache;
    }
}
