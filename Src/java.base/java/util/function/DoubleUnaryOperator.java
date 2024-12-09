package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleUnaryOperator {
    double applyAsDouble(double operand);

    default DoubleUnaryOperator compose(DoubleUnaryOperator before) {
        Objects.requireNonNull(before);
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double v) {
                return applyAsDouble(before.applyAsDouble(v));
            }
        };
    }

    default DoubleUnaryOperator andThen(DoubleUnaryOperator after) {
        Objects.requireNonNull(after);
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                return after.applyAsDouble(applyAsDouble(t));
            }
        };
    }

    static DoubleUnaryOperator identity() {
        return new DoubleUnaryOperator() {
            @Override
            public double applyAsDouble(double t) {
                return t;
            }
        };
    }
}
