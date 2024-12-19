package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);

    default Consumer<T> andThen(Consumer<? super T> after) {
        Objects.requireNonNull(after);
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                accept(t);
                after.accept(t);
            }
        };
    }
}
