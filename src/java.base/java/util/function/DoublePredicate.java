package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoublePredicate {
    boolean test(double value);

    default DoublePredicate and(DoublePredicate other) {
        Objects.requireNonNull(other);
        return new DoublePredicate() {
            @Override
            public boolean test(double value) {
                return test(value) && other.test(value);
            }
        };
    }

    default DoublePredicate negate() {
        return new DoublePredicate() {
            @Override
            public boolean test(double value) {
                return !test(value);
            }
        };
    }

    default DoublePredicate or(DoublePredicate other) {
        Objects.requireNonNull(other);
        return new DoublePredicate() {
            @Override
            public boolean test(double value) {
                return test(value) || other.test(value);
            }
        };
    }
}
