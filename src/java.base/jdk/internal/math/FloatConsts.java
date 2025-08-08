package jdk.internal.math;

import static java.lang.Float.MIN_EXPONENT;
import static java.lang.Float.PRECISION;
import static java.lang.Float.SIZE;

public class FloatConsts {
    private FloatConsts() {}

    public static final int SIGNIFICAND_WIDTH = PRECISION;

    public static final int MIN_SUB_EXPONENT = MIN_EXPONENT - (SIGNIFICAND_WIDTH - 1); // -149

    public static final int EXP_BIAS = (1 << (SIZE - SIGNIFICAND_WIDTH - 1)) - 1; // 127

    public static final int SIGN_BIT_MASK = 1 << (SIZE - 1);

    public static final int EXP_BIT_MASK = ((1 << (SIZE - SIGNIFICAND_WIDTH)) - 1) << (SIGNIFICAND_WIDTH - 1);

    public static final int SIGNIF_BIT_MASK = (1 << (SIGNIFICAND_WIDTH - 1)) - 1;

    public static final int MAG_BIT_MASK = EXP_BIT_MASK | SIGNIF_BIT_MASK;

    static {
        assert(
            ((SIGN_BIT_MASK | EXP_BIT_MASK | SIGNIF_BIT_MASK) == ~0) &&
            (((SIGN_BIT_MASK & EXP_BIT_MASK) == 0) &&
            ((SIGN_BIT_MASK & SIGNIF_BIT_MASK) == 0) &&
            ((EXP_BIT_MASK & SIGNIF_BIT_MASK) == 0)) &&
            ((SIGN_BIT_MASK | MAG_BIT_MASK) == ~0)
        );
    }
}
