package java.util.function;

import java.util.Objects;
import java.util.Comparator;

@FunctionalInterface
public interface BinaryOperator<T> extends BiFunction<T, T, T> {
    public static <T> BinaryOperator<T> minBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return new BinaryOperator<T>() {
            @Override
            public T apply(T a, T b) {
                return comparator.compare(a, b) <= 0 ? a : b;
            }
        };
    }

    public static <T> BinaryOperator<T> maxBy(Comparator<? super T> comparator) {
        Objects.requireNonNull(comparator);
        return new BinaryOperator<T>() {
            @Override
            public T apply(T a, T b) {
                return comparator.compare(a, b) >= 0 ? a : b;
            }
        };
    }
}
