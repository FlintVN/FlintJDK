package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public interface PrimitiveIterator<T, T_CONS> extends Iterator<T> {
    @SuppressWarnings("overloads")
    void forEachRemaining(T_CONS action);

    @SuppressWarnings("overloads")
    public static interface OfInt extends PrimitiveIterator<Integer, IntConsumer> {
        int nextInt();

        default void forEachRemaining(IntConsumer action) {
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextInt());
        }

        @Override
        default Integer next() {
            return nextInt();
        }

        @Override
        default void forEachRemaining(Consumer<? super Integer> action) {
            if(action instanceof IntConsumer)
                forEachRemaining((IntConsumer)action);
            else {
                Objects.requireNonNull(action);
                forEachRemaining(new IntConsumer() {
                    @Override
                    public void accept(int value) {
                        action.accept(value);
                    }
                });
            }
        }

    }

    @SuppressWarnings("overloads")
    public static interface OfLong extends PrimitiveIterator<Long, LongConsumer> {
        long nextLong();

        default void forEachRemaining(LongConsumer action) {
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextLong());
        }

        @Override
        default Long next() {
            return nextLong();
        }

        @Override
        default void forEachRemaining(Consumer<? super Long> action) {
            if(action instanceof LongConsumer)
                forEachRemaining((LongConsumer)action);
            else {
                Objects.requireNonNull(action);
                forEachRemaining(new LongConsumer() {
                    @Override
                    public void accept(long value) {
                        action.accept(value);
                    }
                });
            }
        }
    }

    @SuppressWarnings("overloads")
    public static interface OfDouble extends PrimitiveIterator<Double, DoubleConsumer> {
        double nextDouble();

        default void forEachRemaining(DoubleConsumer action) {
            Objects.requireNonNull(action);
            while(hasNext())
                action.accept(nextDouble());
        }

        @Override
        default Double next() {
            return nextDouble();
        }

        @Override
        default void forEachRemaining(Consumer<? super Double> action) {
            if(action instanceof DoubleConsumer)
                forEachRemaining((DoubleConsumer)action);
            else {
                Objects.requireNonNull(action);
                forEachRemaining(new DoubleConsumer() {
                    @Override
                    public void accept(double value) {
                        action.accept(value);
                    }
                });
            }
        }
    }
}
