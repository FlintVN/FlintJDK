package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface IntUnaryOperator {
    int applyAsInt(int operand);

    default IntUnaryOperator compose(IntUnaryOperator before) {
        Objects.requireNonNull(before);
        return new IntUnaryOperator() {
            @Override
            public int applyAsInt(int v) {
                return applyAsInt(before.applyAsInt(v));
            }
        };
    }

    default IntUnaryOperator andThen(IntUnaryOperator after) {
        Objects.requireNonNull(after);
        return new IntUnaryOperator() {
            @Override
            public int applyAsInt(int t) {
                return after.applyAsInt(applyAsInt(t));
            }
        };
    }

    static IntUnaryOperator identity() {
        return new IntUnaryOperator() {
            @Override
            public int applyAsInt(int t) {
                return t;
            }
        };
    }
}
