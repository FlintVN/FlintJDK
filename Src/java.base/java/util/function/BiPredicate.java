package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiPredicate<T, U> {
    boolean test(T t, U u);

    default BiPredicate<T, U> and(BiPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return new BiPredicate<T, U>() {
            @Override
            public boolean test(T t, U u) {
                return test(t, u) && other.test(t, u);
            }
        };
    }

    default BiPredicate<T, U> negate() {
        return new BiPredicate<T, U>() {
            @Override
            public boolean test(T t, U u) {
                return !test(t, u);
            }
        };
    }

    default BiPredicate<T, U> or(BiPredicate<? super T, ? super U> other) {
        Objects.requireNonNull(other);
        return new BiPredicate<T, U>() {
            @Override
            public boolean test(T t, U u) {
                return test(t, u) || other.test(t, u);
            }
        };
    }
}
