package java.util.function;

import java.util.Objects;

@FunctionalInterface
public interface LongConsumer {
    void accept(long value);

    default LongConsumer andThen(LongConsumer after) {
        Objects.requireNonNull(after);
        return new LongConsumer() {
            @Override
            public void accept(long t) {
                accept(t);
                after.accept(t);
            }
        };
    }
}
