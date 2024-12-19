package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface BiConsumer<T, U> {
    void accept(T t, U u);

    default BiConsumer<T, U> andThen(BiConsumer<? super T, ? super U> after) {
        Objects.requireNonNull(after);

        return new BiConsumer<T, U>() {
            @Override
            public void accept(T l, U r) {
                accept(l, r);
                after.accept(l, r);
            }
        };
    }
}
