package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongUnaryOperator {
    long applyAsLong(long operand);

    default LongUnaryOperator compose(LongUnaryOperator before) {
        Objects.requireNonNull(before);
        return new LongUnaryOperator() {
            @Override
            public long applyAsLong(long v) {
                return applyAsLong(before.applyAsLong(v));
            }
        };
    }

    default LongUnaryOperator andThen(LongUnaryOperator after) {
        Objects.requireNonNull(after);
        return new LongUnaryOperator() {
            @Override
            public long applyAsLong(long t) {
                return after.applyAsLong(applyAsLong(t));
            }
        };
    }

    static LongUnaryOperator identity() {
        return new LongUnaryOperator() {
            @Override
            public long applyAsLong(long t) {
                return t;
            }
        };
    }
}
