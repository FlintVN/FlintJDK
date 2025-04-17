package java.util.stream;

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;

public interface DoubleStream extends BaseStream<Double, DoubleStream> {
    DoubleStream filter(DoublePredicate predicate);

    DoubleStream map(DoubleUnaryOperator mapper);

    <U> Stream<U> mapToObj(DoubleFunction<? extends U> mapper);

    IntStream mapToInt(DoubleToIntFunction mapper);

    LongStream mapToLong(DoubleToLongFunction mapper);

    DoubleStream flatMap(DoubleFunction<? extends DoubleStream> mapper);

    default DoubleStream mapMulti(DoubleMapMultiConsumer mapper) {
        Objects.requireNonNull(mapper);
        return flatMap(e -> {
            SpinedBuffer.OfDouble buffer = new SpinedBuffer.OfDouble();
            mapper.accept(e, buffer);
            return StreamSupport.doubleStream(buffer.spliterator(), false);
        });
    }

    DoubleStream distinct();

    DoubleStream sorted();

    DoubleStream peek(DoubleConsumer action);

    DoubleStream limit(long maxSize);

    DoubleStream skip(long n);

    default DoubleStream takeWhile(DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        return StreamSupport.doubleStream(new WhileOps.UnorderedWhileSpliterator.OfDouble.Taking(spliterator(), true, predicate), isParallel()).onClose(this::close);
    }

    default DoubleStream dropWhile(DoublePredicate predicate) {
        Objects.requireNonNull(predicate);
        return StreamSupport.doubleStream(new WhileOps.UnorderedWhileSpliterator.OfDouble.Dropping(spliterator(), true, predicate), isParallel()).onClose(this::close);
    }

    void forEach(DoubleConsumer action);

    void forEachOrdered(DoubleConsumer action);

    double[] toArray();

    double reduce(double identity, DoubleBinaryOperator op);

    OptionalDouble reduce(DoubleBinaryOperator op);

    <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> accumulator, BiConsumer<R, R> combiner);

    double sum();

    OptionalDouble min();

    OptionalDouble max();

    long count();

    OptionalDouble average();

    DoubleSummaryStatistics summaryStatistics();

    boolean anyMatch(DoublePredicate predicate);

    boolean allMatch(DoublePredicate predicate);

    boolean noneMatch(DoublePredicate predicate);

    OptionalDouble findFirst();

    OptionalDouble findAny();

    Stream<Double> boxed();

    @Override
    DoubleStream sequential();

    @Override
    DoubleStream parallel();

    @Override
    PrimitiveIterator.OfDouble iterator();

    @Override
    Spliterator.OfDouble spliterator();

    public static Builder builder() {
        return new Streams.DoubleStreamBuilderImpl();
    }

    public static DoubleStream empty() {
        return StreamSupport.doubleStream(Spliterators.emptyDoubleSpliterator(), false);
    }

    public static DoubleStream of(double t) {
        return StreamSupport.doubleStream(new Streams.DoubleStreamBuilderImpl(t), false);
    }

    public static DoubleStream of(double... values) {
        return Arrays.stream(values);
    }

    public static DoubleStream iterate(final double seed, final DoubleUnaryOperator f) {
        Objects.requireNonNull(f);
        Spliterator.OfDouble spliterator = new Spliterators.AbstractDoubleSpliterator(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL) {
            double prev;
            boolean started;

            @Override
            public boolean tryAdvance(DoubleConsumer action) {
                Objects.requireNonNull(action);
                double t;
                if (started)
                    t = f.applyAsDouble(prev);
                else {
                    t = seed;
                    started = true;
                }
                action.accept(prev = t);
                return true;
            }
        };
        return StreamSupport.doubleStream(spliterator, false);
    }

    public static DoubleStream iterate(double seed, DoublePredicate hasNext, DoubleUnaryOperator next) {
        Objects.requireNonNull(next);
        Objects.requireNonNull(hasNext);
        Spliterator.OfDouble spliterator = new Spliterators.AbstractDoubleSpliterator(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL) {
            double prev;
            boolean started, finished;

            @Override
            public boolean tryAdvance(DoubleConsumer action) {
                Objects.requireNonNull(action);
                if (finished)
                    return false;
                double t;
                if (started)
                    t = next.applyAsDouble(prev);
                else {
                    t = seed;
                    started = true;
                }
                if (!hasNext.test(t)) {
                    finished = true;
                    return false;
                }
                action.accept(prev = t);
                return true;
            }

            @Override
            public void forEachRemaining(DoubleConsumer action) {
                Objects.requireNonNull(action);
                if (finished)
                    return;
                finished = true;
                double t = started ? next.applyAsDouble(prev) : seed;
                while (hasNext.test(t)) {
                    action.accept(t);
                    t = next.applyAsDouble(t);
                }
            }
        };
        return StreamSupport.doubleStream(spliterator, false);
    }

    public static DoubleStream generate(DoubleSupplier s) {
        Objects.requireNonNull(s);
        return StreamSupport.doubleStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfDouble(Long.MAX_VALUE, s), false);
    }

    public static DoubleStream concat(DoubleStream a, DoubleStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        Spliterator.OfDouble split = new Streams.ConcatSpliterator.OfDouble(a.spliterator(), b.spliterator());
        DoubleStream stream = StreamSupport.doubleStream(split, a.isParallel() || b.isParallel());
        return stream.onClose(Streams.composedClose(a, b));
    }

    public interface Builder extends DoubleConsumer {
        @Override
        void accept(double t);

        default Builder add(double t) {
            accept(t);
            return this;
        }

        DoubleStream build();
    }

    @FunctionalInterface
    interface DoubleMapMultiConsumer {
        void accept(double value, DoubleConsumer dc);
    }
}
