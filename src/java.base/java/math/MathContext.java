package java.math;
import java.io.*;

public final class MathContext {
    private static final RoundingMode DEFAULT_ROUNDINGMODE = RoundingMode.HALF_UP;

    private static final int MIN_DIGITS = 0;

    public static final MathContext UNLIMITED = new MathContext(0, RoundingMode.HALF_UP);

    public static final MathContext DECIMAL32 = new MathContext(7, RoundingMode.HALF_EVEN);

    public static final MathContext DECIMAL64 = new MathContext(16, RoundingMode.HALF_EVEN);

    public static final MathContext DECIMAL128 = new MathContext(34, RoundingMode.HALF_EVEN);

    final int precision;

    final RoundingMode roundingMode;

    public MathContext(int setPrecision) {
        this(setPrecision, DEFAULT_ROUNDINGMODE);
    }

    public MathContext(int setPrecision, RoundingMode setRoundingMode) {
        if (setPrecision < MIN_DIGITS)
            throw new IllegalArgumentException("Digits < 0");
        if (setRoundingMode == null)
            throw new NullPointerException("null RoundingMode");

        precision = setPrecision;
        roundingMode = setRoundingMode;
    }

    public MathContext(String val) {
        int setPrecision;
        if (val == null)
            throw new NullPointerException("null String");
        try {
            if (!val.startsWith("precision=")) throw new RuntimeException();
            int fence = val.indexOf(' ');    // could be -1
            int off = 10;                     // where value starts
            setPrecision = Integer.parseInt(val.substring(10, fence));

            if (!val.startsWith("roundingMode=", fence+1))
                throw new RuntimeException();
            off = fence + 1 + 13;
            String str = val.substring(off, val.length());
            roundingMode = RoundingMode.valueOf(str);
        }
        catch (RuntimeException re) {
            throw new IllegalArgumentException("bad string format");
        }

        if (setPrecision < MIN_DIGITS)
            throw new IllegalArgumentException("Digits < 0");
        precision = setPrecision;
    }

    public int getPrecision() {
        return precision;
    }

    public RoundingMode getRoundingMode() {
        return roundingMode;
    }

    public boolean equals(Object x){
        if (!(x instanceof MathContext mc))
            return false;
        return mc.precision == this.precision
            && mc.roundingMode == this.roundingMode;
    }

    public int hashCode() {
        return this.precision + roundingMode.hashCode() * 59;
    }

    public java.lang.String toString() {
        return "precision=" + precision + " " + "roundingMode=" + roundingMode.toString();
    }

    // TODO
    // @java.io.Serial
    // private void readObject(java.io.ObjectInputStream s)
    //     throws java.io.IOException, ClassNotFoundException {
    //     s.defaultReadObject();
    //     if (precision < MIN_DIGITS) {
    //         String message = "MathContext: invalid digits in stream";
    //         throw new java.io.StreamCorruptedException(message);
    //     }
    //     if (roundingMode == null) {
    //         String message = "MathContext: null roundingMode in stream";
    //         throw new java.io.StreamCorruptedException(message);
    //     }
    // }
}
