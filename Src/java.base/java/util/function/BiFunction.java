package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    R apply(T t, U u);

    default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        Objects.requireNonNull(after);
        return new BiFunction<T, U, V>() {
            @Override
            public V apply(T t, U u) {
                return after.apply(BiFunction.this.apply(t, u));
            }
        };
    }
}
