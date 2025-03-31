package java.lang.invoke;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Function;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public abstract sealed class MethodHandle permits /* NativeMethodHandle, */ DirectMethodHandle, /* DelegatingMethodHandle, */ BoundMethodHandle {
    @java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD})
    @java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
    @interface PolymorphicSignature {

    }

    private final MethodType type;

    public MethodType type() {
        return type;
    }

    MethodHandle(MethodType type) {
        this.type = Objects.requireNonNull(type);
    }

    @IntrinsicCandidate
    public final native @PolymorphicSignature Object invokeExact(Object... args) throws Throwable;

    @IntrinsicCandidate
    public final native @PolymorphicSignature Object invoke(Object... args) throws Throwable;

    boolean isInvokeSpecial() {
        return false;
    }

    @Override
    public String toString() {
        return standardString();
    }

    String standardString() {
        return "MethodHandle" + type;
    }
}
