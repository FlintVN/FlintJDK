package java.lang;

import java.io.PrintStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public final class System {
    public static final PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out));

    private System() {

    }

    public static void setOut(PrintStream out) {
        setOut0(out);
    }

    private static native void setOut0(PrintStream out);

    @IntrinsicCandidate
    public static native long currentTimeMillis();

    @IntrinsicCandidate
    public static native long nanoTime();

    @IntrinsicCandidate
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
