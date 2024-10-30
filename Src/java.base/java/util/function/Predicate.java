package java.util.function;

@FunctionalInterface
public interface Predicate<T> {
    boolean test(T t);

    default Predicate<T> and(Predicate<? super T> other) {
        if(other == null)
            throw new NullPointerException();
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
        if(other == null)
            throw new NullPointerException();
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
        if(target == null)
            throw new NullPointerException();
        return (Predicate<T>)target.negate();
    }
}
