package java.lang;

import java.io.PrintStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public final class System {
    public static final PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out));

    private static String[] propertyKeys = new String[8];
    private static String[] propertyValues = new String[8];
    private static int propertyCount;

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

    public static native void exit(int status);

    public static native void gc();

    static native byte[] getResourceBytes0(String name);

    public static synchronized String getProperty(String key) {
        checkPropertyKey(key);
        int index = findProperty(key);
        return index < 0 ? null : propertyValues[index];
    }

    public static String getProperty(String key, String defaultValue) {
        String value = getProperty(key);
        return value == null ? defaultValue : value;
    }

    public static synchronized String setProperty(String key, String value) {
        checkPropertyKey(key);
        if(value == null)
            throw new NullPointerException("value");

        int index = findProperty(key);
        if(index >= 0) {
            String previous = propertyValues[index];
            propertyValues[index] = value;
            return previous;
        }

        ensurePropertyCapacity();
        propertyKeys[propertyCount] = key;
        propertyValues[propertyCount] = value;
        propertyCount++;
        return null;
    }

    public static synchronized String clearProperty(String key) {
        checkPropertyKey(key);
        int index = findProperty(key);
        if(index < 0)
            return null;

        String previous = propertyValues[index];
        int moved = propertyCount - index - 1;
        if(moved > 0) {
            arraycopy(propertyKeys, index + 1, propertyKeys, index, moved);
            arraycopy(propertyValues, index + 1, propertyValues, index, moved);
        }
        propertyCount--;
        propertyKeys[propertyCount] = null;
        propertyValues[propertyCount] = null;
        return previous;
    }

    private static void checkPropertyKey(String key) {
        if(key == null)
            throw new NullPointerException("key");
        if(key.length() == 0)
            throw new IllegalArgumentException("key is empty");
    }

    private static int findProperty(String key) {
        for(int i = 0; i < propertyCount; i++) {
            if(propertyKeys[i].equals(key))
                return i;
        }
        return -1;
    }

    private static void ensurePropertyCapacity() {
        if(propertyCount < propertyKeys.length)
            return;
        int newLength = propertyKeys.length * 2;
        String[] newKeys = new String[newLength];
        String[] newValues = new String[newLength];
        arraycopy(propertyKeys, 0, newKeys, 0, propertyCount);
        arraycopy(propertyValues, 0, newValues, 0, propertyCount);
        propertyKeys = newKeys;
        propertyValues = newValues;
    }
}
