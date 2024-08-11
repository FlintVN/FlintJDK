package java.math;

public class BigInteger extends Number implements Comparable<BigInteger> {
    public static BigInteger valueOf(long val) {
        return new BigInteger(val);
    }

    public BigInteger(byte[] val, int off, int len) {
        // TODO
    }

    public BigInteger(byte[] val) {
        this(val, 0, val.length);
    }

    public BigInteger(int signum, byte[] magnitude, int off, int len) {
        // TODO
    }

    public BigInteger(int signum, byte[] magnitude) {
        this(signum, magnitude, 0, magnitude.length);
    }

    public BigInteger(String val, int radix) {
        // TODO
    }

    public BigInteger(String val) {
        this(val, 10);
    }

    private BigInteger(long val) {
        // TODO
    }

    public BigInteger add(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger subtract(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger multiply(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger divide(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger remainder(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }
    public BigInteger pow(int exponent) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger sqrt() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger gcd(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger abs() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger negate() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger mod(BigInteger m) {
        // TODO
        throw new UnsupportedOperationException();
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
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger shiftRight(int n) {
        // TODO
        throw new UnsupportedOperationException();
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
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger clearBit(int n) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger flipBit(int n) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger min(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger max(BigInteger val) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public BigInteger nextProbablePrime() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public int intValue() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public long longValue() {
        // TODO
        throw new UnsupportedOperationException();
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
        // TODO
        throw new UnsupportedOperationException();
    }

    public String toString() {
        return toString(10);
    }

    public String toString(int radix) {
        // TODO
        throw new UnsupportedOperationException();
    }
}
