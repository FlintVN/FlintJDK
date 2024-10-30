package java.util.function;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);

    default Consumer<T> andThen(Consumer<? super T> after) {
        if(after == null)
            throw new NullPointerException();
        return new Consumer<T>() {
            @Override
            public void accept(T t) {
                accept(t);
                after.accept(t);
            }
        };
    }
}
