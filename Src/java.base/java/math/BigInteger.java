package java.math;

public class BigInteger extends Number implements Comparable<BigInteger> {
    private final int[] mag;
    private final int signum;

    public static final BigInteger ZERO = new BigInteger((int[])null, 0);
    public static final BigInteger ONE = new BigInteger(new int[]{1}, 1);
    private static final BigInteger NEGATIVE_ONE = new BigInteger(new int[]{1}, -1);

    private static native int[] makeMagnitude(long val);
    private static native int[] makeMagnitude(byte[] val, int off, int len);
    private static native int[] makeMagnitude(int signum, byte[] val, int off, int len);
    private static native int[] makeMagnitude(int[] val, int off, int len);

    private static native int bitLength(int[] mag, int signum);
    private static native int getInt(int[] mag, int signum, int n);
    private static native int compareMagnitude(int[] x, int[] y);
    private static native int[] add(int[] x, int[] y);
    private static native int[] subtract(int[] big, int[] little);
    private static native int[] multiply(int[] x, int[] y);
    private static native int[] divide(int[] x, int[] y);
    private static native int[] remainder(int[] x, int[] y);
    private static native int[] shiftLeft(int[] x, int n);
    private static native int[] shiftRight(int[] x, int n);
    private static native int[] square(int[] x);
    private static native int[] pow(int[] x, int exponent);
    private static native int[] sqrt(int[] x);
    private static native int[] getIntArray(int[] mag, int signum, int length);

    public static BigInteger valueOf(long val) {
        if(val == 0)
            return ZERO;
        else if(val == -1)
            return NEGATIVE_ONE;
        return new BigInteger(val);
    }

    public BigInteger(byte[] val, int off, int len) {
        if(val != null && val.length == 0)
            throw new NumberFormatException("Zero length BigInteger");
        int[] mag = makeMagnitude(val, off, len);
        this.mag = mag;
        this.signum = (mag == null) ? 0 : ((val[off] >= 0) ? 1 : -1);
    }

    public BigInteger(byte[] val) {
        this(val, 0, val.length);
    }

    public BigInteger(int signum, byte[] magnitude, int off, int len) {
        if(signum != 0 && magnitude != null && magnitude.length == 0)
            throw new NumberFormatException("Zero length BigInteger");
        int[] mag = makeMagnitude(signum, magnitude, off, len);
        this.mag = mag;
        this.signum = (mag == null) ? 0 : signum;
    }

    public BigInteger(int signum, byte[] magnitude) {
        this(signum, magnitude, 0, magnitude.length);
    }

    private BigInteger(int[] magnitude, int signum) {
        this.signum = (magnitude == null) ? 0 : signum;
        this.mag = (signum == 0) ? null : magnitude;
    }

    private BigInteger(int[] val) {
        if(val != null && val.length == 0)
            throw new NumberFormatException("Zero length BigInteger");
        int[] mag = makeMagnitude(val, 0, val.length);
        this.mag = mag;
        this.signum = (mag == null) ? 0 : ((val[0] >= 0) ? 1 : -1);
    }

    public BigInteger(String val, int radix) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger(String val) {
        this(val, 10);
    }

    private BigInteger(long val) {
        if(val == 0) {
            mag = null;
            this.signum = 0;
        }
        else {
            mag = makeMagnitude(val);
            this.signum = (val > 0) ? 1 : -1;
        }
    }

    public BigInteger add(BigInteger val) {
        if(val.signum == 0)
            return this;
        if(signum == 0)
            return val;
        if(val.signum == signum)
            return new BigInteger(add(mag, val.mag), signum);
        int cmp = compareMagnitude(mag, val.mag);
        if(cmp == 0)
            return ZERO;
        int[] resultMag = (cmp > 0) ? subtract(mag, val.mag) : subtract(val.mag, mag);
        return new BigInteger(resultMag, cmp == signum ? 1 : -1);
    }

    public BigInteger subtract(BigInteger val) {
        if(val.signum == 0)
            return this;
        if(signum == 0)
            return val.negate();
        if(val.signum != signum)
            return new BigInteger(add(mag, val.mag), signum);
        int cmp = compareMagnitude(mag, val.mag);
        if(cmp == 0)
            return ZERO;
        int[] resultMag = (cmp > 0) ? subtract(mag, val.mag) : subtract(val.mag, mag);
        return new BigInteger(resultMag, cmp == signum ? 1 : -1);
    }

    public BigInteger multiply(BigInteger val) {
        if(signum == 0 || val.signum == 0)
            return ZERO;
        if(val.compareTo(ONE) == 0)
            return this;
        else if(compareTo(ONE) == 0)
            return val;
        int[] resultMag = multiply(mag, val.mag);
        return new BigInteger(resultMag, signum == val.signum ? 1 : -1);
    }

    public BigInteger divide(BigInteger val) {
        if(val.signum == 0)
            throw new ArithmeticException("Divided by zero");
        else if(signum == 0)
            return ZERO;
        else if(val.compareTo(ONE) == 0)
            return this;
        int[] resultMag = divide(mag, val.mag);
        if(resultMag == null)
            return ZERO;
        return new BigInteger(resultMag, signum == val.signum ? 1 : -1);
    }

    public BigInteger remainder(BigInteger val) {
        if(val.signum == 0)
            throw new ArithmeticException("Divided by zero");
        else if(signum == 0 || compareTo(ONE) == 0)
            return ZERO;
        int[] resultMag = remainder(mag, val.mag);
        if(resultMag == null)
            return ZERO;
        return new BigInteger(resultMag, signum);
    }

    private BigInteger square() {
        if(signum == 0)
            return ZERO;
        else if(compareMagnitude(mag, ONE.mag) == 0)
            return ONE;
        int[] resultMag = square(mag);
        return new BigInteger(resultMag, 1);
    }

    public BigInteger pow(int exponent) {
        if(exponent < 0)
            throw new ArithmeticException("Negative exponent");
        if(signum == 0)
            return (exponent == 0) ? ONE : this;
        int[] resultMag = pow(mag, exponent);
        if(signum > 0)
            return new BigInteger(resultMag, 1);
        else
            return new BigInteger(resultMag, ((exponent & 0x01) == 0x01) ? -1 : 1);
    }

    public BigInteger sqrt() {
        if(signum < 0)
            throw new ArithmeticException("Negative BigInteger");
        else if(signum == 0)
            return ZERO;
        int[] resultMag = sqrt(mag);
        return new BigInteger(resultMag, 1);
    }

    public BigInteger gcd(BigInteger val) {
        if(val.signum == 0)
            return this.abs();
        else if (this.signum == 0)
            return val.abs();
        BigInteger a = this;
        do {
            BigInteger temp = val;
            val = a.remainder(val);
            a = temp;
        } while(val.signum != 0);
        return a;
    }

    public BigInteger abs() {
        if(signum < 0)
            return new BigInteger(mag, 1);
        return this;
    }

    public BigInteger negate() {
        if(signum == 0)
            return this;
        return new BigInteger(mag, -signum);
    }

    public BigInteger mod(BigInteger m) {
        if(m.signum <= 0)
            throw new ArithmeticException("Modulus not positive");
        BigInteger result = this.remainder(m);
        return (result.signum >= 0 ? result : result.add(m));
    }

    public BigInteger modPow(BigInteger exponent, BigInteger m) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger modInverse(BigInteger m) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger shiftLeft(int n) {
        if(signum == 0 || n == 0)
            return this;
        else if(n > 0) {
            int[] resultMag = shiftLeft(mag, n);
            return new BigInteger(resultMag, signum);
        }
        else {
            int[] resultMag = shiftRight(mag, n);
            if(resultMag == null)
                return (signum > 0) ? ZERO : NEGATIVE_ONE;
            return new BigInteger(resultMag, signum);
        }
    }

    public BigInteger shiftRight(int n) {
        if(signum == 0 || n == 0)
            return this;
        else if(n > 0) {
            int[] resultMag = shiftRight(mag, n);
            if(resultMag == null)
                return (signum > 0) ? ZERO : NEGATIVE_ONE;
            return new BigInteger(resultMag, signum);
        }
        else {
            int[] resultMag = shiftLeft(mag, n);
            return new BigInteger(resultMag, signum);
        }
    }

    public BigInteger and(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger or(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger xor(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger not() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger andNot(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger setBit(int n) {
        if(n < 0)
            throw new ArithmeticException("Negative bit address");
        int intNum = n >>> 5;
        int length = (signum == 0) ? 1 : (mag.length + 1);
        int tmp = ((n + 1) >>> 5) + 1;
        length = (length > tmp) ? length : tmp;
        int[] result = getIntArray(mag, signum, length);
        result[result.length - intNum - 1] |= (1 << (n & 31));
        return new BigInteger(result);
    }

    public BigInteger clearBit(int n) {
        if(n < 0)
            throw new ArithmeticException("Negative bit address");
        int intNum = n >>> 5;
        int length = (signum == 0) ? 1 : (mag.length + 1);
        int tmp = ((n + 1) >>> 5) + 1;
        length = (length > tmp) ? length : tmp;
        int[] result = getIntArray(mag, signum, length);
        result[result.length - intNum - 1] &= ~(1 << (n & 31));
        return new BigInteger(result);
    }

    public BigInteger flipBit(int n) {
        if(n < 0)
            throw new ArithmeticException("Negative bit address");
        int intNum = n >>> 5;
        int length = (signum == 0) ? 1 : (mag.length + 1);
        int tmp = ((n + 1) >>> 5) + 1;
        length = (length > tmp) ? length : tmp;
        int[] result = getIntArray(mag, signum, length);
        result[result.length - intNum - 1] ^= (1 << (n & 31));
        return new BigInteger(result);
    }

    public int bitLength() {
        return bitLength(mag, signum);
    }

    public BigInteger min(BigInteger val) {
        return (compareTo(val) < 0 ? this : val);
    }

    public BigInteger max(BigInteger val) {
        return (compareTo(val) > 0 ? this : val);
    }

    public BigInteger nextProbablePrime() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int intValue() {
        return getInt(mag, signum, 0);
    }

    public long longValue() {
        return ((long)getInt(mag, signum, 1) << 32) | getInt(mag, signum, 0);
    }

    public float floatValue() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public double doubleValue() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public byte byteValue() {
        return (byte)intValue();
    }

    public short shortValue() {
        return (short)intValue();
    }

    public int compareTo(BigInteger val) {
        if(val == this)
            return 0;
        if(signum == val.signum) {
            return switch (signum) {
                case 1  -> compareMagnitude(mag, val.mag);
                case -1 -> compareMagnitude(val.mag, mag);
                default -> 0;
            };
        }
        return signum > val.signum ? 1 : -1;
    }

    public boolean equals(Object x) {
        if(x == this)
            return true;
        if(!(x instanceof BigInteger xInt))
            return false;
        if(xInt.signum != signum)
            return false;
        return (compareMagnitude(mag, xInt.mag) == 0);
    }

    public int hashCode() {
        int hashCode = 0;
        for(int i=0; i < mag.length; i++)
            hashCode = (int)(31 * hashCode + (mag[i] & 0xFFFFFFFFL));
        return hashCode * signum;
    }

    public String toString() {
        return toString(10);
    }

    public String toString(int radix) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
