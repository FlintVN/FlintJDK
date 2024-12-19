package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongPredicate {
    boolean test(long value);

    default LongPredicate and(LongPredicate other) {
        Objects.requireNonNull(other);
        return new LongPredicate() {
            @Override
            public boolean test(long value) {
               return test(value) && other.test(value);
            }
        };
    }

    default LongPredicate negate() {
        return new LongPredicate() {
            @Override
            public boolean test(long value) {
                return !test(value);
            }
        };
    }

    default LongPredicate or(LongPredicate other) {
        Objects.requireNonNull(other);
        return new LongPredicate() {
            @Override
            public boolean test(long value) {
                return test(value) || other.test(value);
            }
        };
    }
}
