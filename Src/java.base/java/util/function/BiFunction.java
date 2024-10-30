package java.util.function;

@FunctionalInterface
public interface BiFunction<T, U, R> {
    R apply(T t, U u);

    default <V> BiFunction<T, U, V> andThen(Function<? super R, ? extends V> after) {
        if(after == null)
            throw new NullPointerException();
        return new BiFunction<T, U, V>() {
            @Override
            public V apply(T t, U u) {
                return after.apply(BiFunction.this.apply(t, u));
            }
        };
    }
}
