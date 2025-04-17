package java.util;

import java.util.function.DoubleConsumer;
import java.util.function.DoubleSupplier;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

@jdk.internal.ValueBased
public final class OptionalDouble {
    private static final OptionalDouble EMPTY = new OptionalDouble();

    private final boolean isPresent;
    private final double value;

    private OptionalDouble() {
        this.isPresent = false;
        this.value = Double.NaN;
    }

    public static OptionalDouble empty() {
        return EMPTY;
    }

    private OptionalDouble(double value) {
        this.isPresent = true;
        this.value = value;
    }

    public static OptionalDouble of(double value) {
        return new OptionalDouble(value);
    }

    public double getAsDouble() {
        if(!isPresent)
            throw new NoSuchElementException("No value present");
        return value;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public boolean isEmpty() {
        return !isPresent;
    }

    public void ifPresent(DoubleConsumer action) {
        if(isPresent)
            action.accept(value);
    }

    public void ifPresentOrElse(DoubleConsumer action, Runnable emptyAction) {
        if(isPresent)
            action.accept(value);
        else
            emptyAction.run();
    }

    public DoubleStream stream() {
        if(isPresent)
            return DoubleStream.of(value);
        else
            return DoubleStream.empty();
    }

    public double orElse(double other) {
        return isPresent ? value : other;
    }

    public double orElseGet(DoubleSupplier supplier) {
        return isPresent ? value : supplier.getAsDouble();
    }

    public double orElseThrow() {
        if(!isPresent)
            throw new NoSuchElementException("No value present");
        return value;
    }

    public<X extends Throwable> double orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if(isPresent)
            return value;
        else
            throw exceptionSupplier.get();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        return obj instanceof OptionalDouble other && (isPresent && other.isPresent ? Double.compare(value, other.value) == 0 : isPresent == other.isPresent);
    }

    @Override
    public int hashCode() {
        return isPresent ? Double.hashCode(value) : 0;
    }

    @Override
    public String toString() {
        return isPresent ? ("OptionalDouble[" + value + "]") : "OptionalDouble.empty";
    }
}
