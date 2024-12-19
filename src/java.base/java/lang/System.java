package java.lang;

import java.io.PrintStream;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public final class System {
    public static final PrintStream out = new PrintStream();

    private System() {

    }

    public static native long currentTimeMillis();

    public static native long nanoTime();

    public static native final void arraycopy(Object src, int srcPos, Object dest, int destPos, int length);

    @IntrinsicCandidate
    public static native int identityHashCode(Object x);

    public static void exit(int status) {
        // TODO
    }

    public static void gc() {
        // TODO
    }
}
