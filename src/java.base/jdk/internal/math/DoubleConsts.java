package jdk.internal.math;

import static java.lang.Double.MIN_EXPONENT;
import static java.lang.Double.PRECISION;
import static java.lang.Double.SIZE;

public class DoubleConsts {
    private DoubleConsts() {

    }

    public static final int SIGNIFICAND_WIDTH = PRECISION;

    public static final int MIN_SUB_EXPONENT = MIN_EXPONENT - (SIGNIFICAND_WIDTH - 1); // -1074

    public static final int EXP_BIAS = (1 << (SIZE - SIGNIFICAND_WIDTH - 1)) - 1; // 1023

    public static final long SIGN_BIT_MASK = 1L << (SIZE - 1);

    public static final long EXP_BIT_MASK = ((1L << (SIZE - SIGNIFICAND_WIDTH)) - 1) << (SIGNIFICAND_WIDTH - 1);

    public static final long SIGNIF_BIT_MASK = (1L << (SIGNIFICAND_WIDTH - 1)) - 1;

    public static final long MAG_BIT_MASK = EXP_BIT_MASK | SIGNIF_BIT_MASK;

    static {
        assert(
            ((SIGN_BIT_MASK | EXP_BIT_MASK | SIGNIF_BIT_MASK) == ~0L) &&
            (((SIGN_BIT_MASK & EXP_BIT_MASK) == 0L) &&
            ((SIGN_BIT_MASK & SIGNIF_BIT_MASK) == 0L) &&
            ((EXP_BIT_MASK & SIGNIF_BIT_MASK) == 0L)) &&
            ((SIGN_BIT_MASK | MAG_BIT_MASK) == ~0L)
        );
    }
}
