package java.util;

import jdk.internal.vm.annotation.ForceInline;

public final class StringJoiner {
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final String prefix;
    private final String delimiter;
    private final String suffix;

    private String[] elts;

    private int size;

    private int len;

    private String emptyValue;

    public StringJoiner(CharSequence delimiter) {
        this(delimiter, "", "");
    }

    public StringJoiner(CharSequence delimiter, CharSequence prefix, CharSequence suffix) {
        Objects.requireNonNull(prefix, "The prefix must not be null");
        Objects.requireNonNull(delimiter, "The delimiter must not be null");
        Objects.requireNonNull(suffix, "The suffix must not be null");
        this.prefix = prefix.toString();
        this.delimiter = delimiter.toString();
        this.suffix = suffix.toString();
        checkAddLength(0, 0);
    }

    public StringJoiner setEmptyValue(CharSequence emptyValue) {
        this.emptyValue = Objects.requireNonNull(emptyValue, "The empty value must not be null").toString();
        return this;
    }

    @Override
    public String toString() {
        final int size = this.size;
        var elts = this.elts;
        if(size == 0) {
            if(emptyValue != null)
                return emptyValue;
            elts = EMPTY_STRING_ARRAY;
        }
        return String.join(prefix, suffix, delimiter, elts, size);
    }

    public StringJoiner add(CharSequence newElement) {
        final String elt = String.valueOf(newElement);
        if(elts == null)
            elts = new String[8];
        else {
            if(size == elts.length) {
                String[] tmp = new String[2 * size];
                System.arraycopy(elts, 0, tmp, 0, size);
                elts = tmp;
            }
            len = checkAddLength(len, delimiter.length());
        }
        len = checkAddLength(len, elt.length());
        elts[size++] = elt;
        return this;
    }

    private int checkAddLength(int oldLen, int inc) {
        long newLen = (long)oldLen + (long)inc;
        long tmpLen = newLen + (long)prefix.length() + (long)suffix.length();
        if(tmpLen != (int)tmpLen)
            throw new OutOfMemoryError("Requested array size exceeds VM limit");
        return (int)newLen;
    }

    public StringJoiner merge(StringJoiner other) {
        Objects.requireNonNull(other);
        if(other.size == 0)
            return this;
        other.compactElts();
        return add(other.elts[0]);
    }

    private void compactElts() {
        int sz = size;
        if(sz > 1) {
            elts[0] = String.join("", "", delimiter, elts, sz);
            for(int i = 1; i < sz; i++)
                elts[i] = null;
            size = 1;
        }
    }

    public int length() {
        return (size == 0 && emptyValue != null) ? emptyValue.length() : len + prefix.length() + suffix.length();
    }
}
