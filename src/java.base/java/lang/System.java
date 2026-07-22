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

    public static String lineSeparator() {
        if(lineSeparator == null)
            lineSeparator = getProperty("line.separator");
        return lineSeparator;
    }

    private static String lineSeparator;

    public native static String getProperty(String key);

    public static String getProperty(String key, String def) {
        String v = getProperty(key);
        return (v != null) ? v : def;
    }

    public static native void exit(int status);

    public static native void gc();
}
