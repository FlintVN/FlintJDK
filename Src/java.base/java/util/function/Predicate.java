package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);

    default Predicate<T> and(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return test(t) && other.test(t);
            }
        };
    }

    default Predicate<T> negate() {
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return !test(t);
            }
        };
    }

    default Predicate<T> or(Predicate<? super T> other) {
        Objects.requireNonNull(other);
        return new Predicate<T>() {
            @Override
            public boolean test(T t) {
                return test(t) || other.test(t);
            }
        };
    }

    static <T> Predicate<T> isEqual(Object targetRef) {
        if(null == targetRef) {
            return new Predicate<T>() {
                @Override
                public boolean test(T t) {
                    return t == null;
                }
            };
        }
        else {
            return new Predicate<T>() {
                @Override
                public boolean test(T object) {
                    return targetRef.equals(object);
                }
            };
        }
    }

    @SuppressWarnings("unchecked")
    static <T> Predicate<T> not(Predicate<? super T> target) {
        Objects.requireNonNull(target);
        return (Predicate<T>)target.negate();
    }
}
