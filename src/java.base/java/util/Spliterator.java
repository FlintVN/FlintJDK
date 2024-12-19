package java.util;

import java.util.function.Consumer;
import java.util.function.DoubleConsumer;
import java.util.function.IntConsumer;
import java.util.function.LongConsumer;

public interface Spliterator<T> {
    public static final int ORDERED = 0x00000010;
    public static final int DISTINCT = 0x00000001;
    public static final int SORTED = 0x00000004;
    public static final int SIZED = 0x00000040;
    public static final int NONNULL = 0x00000100;
    public static final int IMMUTABLE = 0x00000400;
    public static final int CONCURRENT = 0x00001000;
    public static final int SUBSIZED = 0x00004000;

    boolean tryAdvance(Consumer<? super T> action);

    default void forEachRemaining(Consumer<? super T> action) {
        do {

        } while(tryAdvance(action));
    }

    Spliterator<T> trySplit();

    long estimateSize();

    default long getExactSizeIfKnown() {
        return (characteristics() & SIZED) == 0 ? -1L : estimateSize();
    }

    int characteristics();

    default boolean hasCharacteristics(int characteristics) {
        return (characteristics() & characteristics) == characteristics;
    }

    default Comparator<? super T> getComparator() {
        throw new IllegalStateException();
    }

    public interface OfPrimitive<T, T_CONS, T_SPLITR extends Spliterator.OfPrimitive<T, T_CONS, T_SPLITR>> extends Spliterator<T> {
        @Override
        T_SPLITR trySplit();

        @SuppressWarnings("overloads")
        boolean tryAdvance(T_CONS action);

        @SuppressWarnings("overloads")
        default void forEachRemaining(T_CONS action) {
            do {

            } while(tryAdvance(action));
        }
    }

    public interface OfInt extends OfPrimitive<Integer, IntConsumer, OfInt> {
        @Override
        OfInt trySplit();

        @Override
        boolean tryAdvance(IntConsumer action);

        @Override
        default void forEachRemaining(IntConsumer action) {
            do {

            } while(tryAdvance(action));
        }

        @Override
        default boolean tryAdvance(Consumer<? super Integer> action) {
            if(action instanceof IntConsumer)
                return tryAdvance((IntConsumer)action);
            else {
                return tryAdvance(new IntConsumer() {
                    @Override
                    public void accept(int value) {
                        action.accept(value);
                    }
                });
            }
        }

        @Override
        default void forEachRemaining(Consumer<? super Integer> action) {
            if(action instanceof IntConsumer)
                forEachRemaining((IntConsumer)action);
            else {
                forEachRemaining(new IntConsumer() {
                    @Override
                    public void accept(int value) {
                        action.accept(value);
                    }
                });
            }
        }
    }

    public interface OfLong extends OfPrimitive<Long, LongConsumer, OfLong> {
        @Override
        OfLong trySplit();

        @Override
        boolean tryAdvance(LongConsumer action);

        @Override
        default void forEachRemaining(LongConsumer action) {
            do {

            } while(tryAdvance(action));
        }

        @Override
        default boolean tryAdvance(Consumer<? super Long> action) {
            if(action instanceof LongConsumer)
                return tryAdvance((LongConsumer)action);
            else {
                return tryAdvance(new LongConsumer() {
                    @Override
                    public void accept(long value) {
                        action.accept(value);
                    }
                });
            }
        }

        @Override
        default void forEachRemaining(Consumer<? super Long> action) {
            if(action instanceof LongConsumer)
                forEachRemaining((LongConsumer)action);
            else {
                forEachRemaining(new LongConsumer() {
                    @Override
                    public void accept(long value) {
                        action.accept(value);
                    }
                });
            }
        }
    }

    public interface OfDouble extends OfPrimitive<Double, DoubleConsumer, OfDouble> {
        @Override
        OfDouble trySplit();

        @Override
        boolean tryAdvance(DoubleConsumer action);

        @Override
        default void forEachRemaining(DoubleConsumer action) {
            do {

            } while(tryAdvance(action));
        }

        @Override
        default boolean tryAdvance(Consumer<? super Double> action) {
            if(action instanceof DoubleConsumer)
                return tryAdvance((DoubleConsumer)action);
            else {
                return tryAdvance(new DoubleConsumer() {
                    @Override
                    public void accept(double value) {
                        action.accept(value);
                    }
                });
            }
        }

        @Override
        default void forEachRemaining(Consumer<? super Double> action) {
            if(action instanceof DoubleConsumer)
                forEachRemaining((DoubleConsumer)action);
            else {
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
