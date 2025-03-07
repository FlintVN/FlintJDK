package java.lang.reflect;

import jdk.internal.reflect.CallerSensitive;
import jdk.internal.vm.annotation.ForceInline;

public final class Field implements Member {
    private final Class<?> clazz;
    private final String name;
    private final Class<?> type;
    private final int modifiers;

    @SuppressWarnings("deprecation")
    Field(
        Class<?> declaringClass,
        String name,
        Class<?> type,
        int modifiers
    ) {
        this.clazz = declaringClass;
        this.name = name;
        this.type = type;
        this.modifiers = modifiers;
    }

    @Override
    public Class<?> getDeclaringClass() {
        return clazz;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public boolean isSynthetic() {
        return Modifier.isSynthetic(getModifiers());
    }

    public Class<?> getType() {
        return type;
    }

    public Type getGenericType() {
        return type;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Field other)
            return (clazz == other.clazz) && (name.equals(other.name)) && (type == other.type);
        return false;
    }

    @Override
    public int hashCode() {
        return clazz.getName().hashCode() ^ name.hashCode();
    }

    @Override
    public String toString() {
        int mod = this.modifiers;
        return (((mod == 0) ? "" : (Modifier.toString(mod) + " ")) + type.getTypeName() + " " + clazz.getTypeName() + "." + name);
    }

    public String toGenericString() {
        int mod = this.modifiers;
        Type fieldType = getGenericType();
        return (((mod == 0) ? "" : (Modifier.toString(mod) + " ")) + fieldType.getTypeName() + " " + clazz.getTypeName() + "." + name);
    }

    @CallerSensitive
    @ForceInline
    public Object get(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public boolean getBoolean(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public byte getByte(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public char getChar(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public short getShort(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public int getInt(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public long getLong(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public float getFloat(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public double getDouble(Object obj) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void set(Object obj, Object value) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setBoolean(Object obj, boolean z) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setByte(Object obj, byte b) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setChar(Object obj, char c) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setShort(Object obj, short s) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setInt(Object obj, int i) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setLong(Object obj, long l) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setFloat(Object obj, float f) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public void setDouble(Object obj, double d) throws IllegalArgumentException, IllegalAccessException {
        // TODO
        throw new UnsupportedOperationException();
    }
}
