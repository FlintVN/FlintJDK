package java.lang.invoke;

import java.io.Serializable;
import java.util.Arrays;
import java.lang.reflect.Array;
import java.util.Objects;

public final class LambdaMetafactory {
    private LambdaMetafactory() {}

    public static final int FLAG_SERIALIZABLE = 1 << 0;

    public static final int FLAG_MARKERS = 1 << 1;

    public static final int FLAG_BRIDGES = 1 << 2;

    private static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];
    private static final MethodType[] EMPTY_MT_ARRAY = new MethodType[0];

    public static CallSite metafactory(
        MethodHandles.Lookup caller,
        String interfaceMethodName,
        MethodType factoryType,
        MethodType interfaceMethodType,
        MethodHandle implementation,
        MethodType dynamicMethodType
    ) throws LambdaConversionException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
