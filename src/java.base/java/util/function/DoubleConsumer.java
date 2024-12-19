package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface DoubleConsumer {
    void accept(double value);

    default DoubleConsumer andThen(DoubleConsumer after) {
        Objects.requireNonNull(after);
        return new DoubleConsumer() {
            @Override
            public void accept(double t) {
                accept(t);
                after.accept(t);
            }
        };
    }
}
