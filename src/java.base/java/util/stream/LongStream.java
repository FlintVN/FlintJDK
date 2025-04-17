package java.util.stream;

import java.util.Arrays;
import java.util.LongSummaryStatistics;
import java.util.Objects;
import java.util.OptionalDouble;
import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.LongBinaryOperator;
import java.util.function.LongConsumer;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ObjLongConsumer;
import java.util.function.Supplier;

public interface LongStream extends BaseStream<Long, LongStream> {
    LongStream filter(LongPredicate predicate);

    LongStream map(LongUnaryOperator mapper);

    <U> Stream<U> mapToObj(LongFunction<? extends U> mapper);

    IntStream mapToInt(LongToIntFunction mapper);

    DoubleStream mapToDouble(LongToDoubleFunction mapper);

    LongStream flatMap(LongFunction<? extends LongStream> mapper);

    default LongStream mapMulti(LongMapMultiConsumer mapper) {
        Objects.requireNonNull(mapper);
        return flatMap(e -> {
            SpinedBuffer.OfLong buffer = new SpinedBuffer.OfLong();
            mapper.accept(e, buffer);
            return StreamSupport.longStream(buffer.spliterator(), false);
        });
    }

    LongStream distinct();

    LongStream sorted();

    LongStream peek(LongConsumer action);

    LongStream limit(long maxSize);

    LongStream skip(long n);

    default LongStream takeWhile(LongPredicate predicate) {
        Objects.requireNonNull(predicate);
        return StreamSupport.longStream(new WhileOps.UnorderedWhileSpliterator.OfLong.Taking(spliterator(), true, predicate), isParallel()).onClose(this::close);
    }

    default LongStream dropWhile(LongPredicate predicate) {
        Objects.requireNonNull(predicate);
        return StreamSupport.longStream(new WhileOps.UnorderedWhileSpliterator.OfLong.Dropping(spliterator(), true, predicate), isParallel()).onClose(this::close);
    }

    void forEach(LongConsumer action);

    void forEachOrdered(LongConsumer action);

    long[] toArray();

    long reduce(long identity, LongBinaryOperator op);

    OptionalLong reduce(LongBinaryOperator op);

    <R> R collect(Supplier<R> supplier, ObjLongConsumer<R> accumulator, BiConsumer<R, R> combiner);

    long sum();

    OptionalLong min();

    OptionalLong max();

    long count();

    OptionalDouble average();

    LongSummaryStatistics summaryStatistics();

    boolean anyMatch(LongPredicate predicate);

    boolean allMatch(LongPredicate predicate);

    boolean noneMatch(LongPredicate predicate);

    OptionalLong findFirst();

    OptionalLong findAny();

    DoubleStream asDoubleStream();

    Stream<Long> boxed();

    @Override
    LongStream sequential();

    @Override
    LongStream parallel();

    @Override
    PrimitiveIterator.OfLong iterator();

    @Override
    Spliterator.OfLong spliterator();

    public static Builder builder() {
        return new Streams.LongStreamBuilderImpl();
    }

    public static LongStream empty() {
        return StreamSupport.longStream(Spliterators.emptyLongSpliterator(), false);
    }

    public static LongStream of(long t) {
        return StreamSupport.longStream(new Streams.LongStreamBuilderImpl(t), false);
    }

    public static LongStream of(long... values) {
        return Arrays.stream(values);
    }

    public static LongStream iterate(final long seed, final LongUnaryOperator f) {
        Objects.requireNonNull(f);
        Spliterator.OfLong spliterator = new Spliterators.AbstractLongSpliterator(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL) {
            long prev;
            boolean started;

            @Override
            public boolean tryAdvance(LongConsumer action) {
                Objects.requireNonNull(action);
                long t;
                if(started)
                    t = f.applyAsLong(prev);
                else {
                    t = seed;
                    started = true;
                }
                action.accept(prev = t);
                return true;
            }
        };
        return StreamSupport.longStream(spliterator, false);
    }

    public static LongStream iterate(long seed, LongPredicate hasNext, LongUnaryOperator next) {
        Objects.requireNonNull(next);
        Objects.requireNonNull(hasNext);
        Spliterator.OfLong spliterator = new Spliterators.AbstractLongSpliterator(Long.MAX_VALUE, Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL) {
            long prev;
            boolean started, finished;

            @Override
            public boolean tryAdvance(LongConsumer action) {
                Objects.requireNonNull(action);
                if(finished)
                    return false;
                long t;
                if(started)
                    t = next.applyAsLong(prev);
                else {
                    t = seed;
                    started = true;
                }
                if(!hasNext.test(t)) {
                    finished = true;
                    return false;
                }
                action.accept(prev = t);
                return true;
            }

            @Override
            public void forEachRemaining(LongConsumer action) {
                Objects.requireNonNull(action);
                if(finished)
                    return;
                finished = true;
                long t = started ? next.applyAsLong(prev) : seed;
                while(hasNext.test(t)) {
                    action.accept(t);
                    t = next.applyAsLong(t);
                }
            }
        };
        return StreamSupport.longStream(spliterator, false);
    }

    public static LongStream generate(LongSupplier s) {
        Objects.requireNonNull(s);
        return StreamSupport.longStream(new StreamSpliterators.InfiniteSupplyingSpliterator.OfLong(Long.MAX_VALUE, s), false);
    }

    public static LongStream range(long startInclusive, final long endExclusive) {
        if(startInclusive >= endExclusive)
            return empty();
        else if(endExclusive - startInclusive < 0) {
            long m = startInclusive + Long.divideUnsigned(endExclusive - startInclusive, 2) + 1;
            return concat(range(startInclusive, m), range(m, endExclusive));
        }
        else
            return StreamSupport.longStream(new Streams.RangeLongSpliterator(startInclusive, endExclusive, false), false);
    }

    public static LongStream rangeClosed(long startInclusive, final long endInclusive) {
        if(startInclusive > endInclusive)
            return empty();
        else if(endInclusive - startInclusive + 1 <= 0) {
            long m = startInclusive + Long.divideUnsigned(endInclusive - startInclusive, 2) + 1;
            return concat(range(startInclusive, m), rangeClosed(m, endInclusive));
        }
        else
            return StreamSupport.longStream(new Streams.RangeLongSpliterator(startInclusive, endInclusive, true), false);
    }

    public static LongStream concat(LongStream a, LongStream b) {
        Objects.requireNonNull(a);
        Objects.requireNonNull(b);

        Spliterator.OfLong split = new Streams.ConcatSpliterator.OfLong(a.spliterator(), b.spliterator());
        LongStream stream = StreamSupport.longStream(split, a.isParallel() || b.isParallel());
        return stream.onClose(Streams.composedClose(a, b));
    }

    public interface Builder extends LongConsumer {
        @Override
        void accept(long t);

        default Builder add(long t) {
            accept(t);
            return this;
        }

        LongStream build();
    }

    @FunctionalInterface
    interface LongMapMultiConsumer {
        void accept(long value, LongConsumer lc);
    }
}
