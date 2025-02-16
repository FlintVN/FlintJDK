package java.lang.reflect;

import java.util.Set;

public interface Member {
    public static final int PUBLIC = 0;
    public static final int DECLARED = 1;

    public Class<?> getDeclaringClass();

    public String getName();

    public int getModifiers();

    public default Set<AccessFlag> accessFlags() {
        throw new UnsupportedOperationException();
    }

    public boolean isSynthetic();
}
