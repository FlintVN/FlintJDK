package java.lang.reflect;

import java.lang.annotation.Annotation;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.vm.annotation.ForceInline;

public final class Constructor<T> extends Executable {
    private final Class<T> clazz;
    private final Class<?>[] parameterTypes;
    private final Class<?>[] exceptionTypes;
    private final int modifiers;

    Constructor(
        Class<T> declaringClass,
        Class<?>[] parameterTypes,
        Class<?>[] checkedExceptions,
        int modifiers
    ) {
        this.clazz = declaringClass;
        this.parameterTypes = parameterTypes;
        this.exceptionTypes = checkedExceptions;
        this.modifiers = modifiers;
    }

    @Override
    public Class<T> getDeclaringClass() {
        return clazz;
    }

    @Override
    public String getName() {
        return clazz.getName();
    }

    @Override
    public int getModifiers() {
        return modifiers;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        if(parameterTypes.length > 0)
            return parameterTypes.clone();
        return parameterTypes;
    }

    @Override
    public Type[] getGenericParameterTypes() {
        Type[] types = new Type[parameterTypes.length];
        for(int i = 0; i < types.length; i++)
            types[i] = parameterTypes[i];
        return types;
    }

    @Override
    public int getParameterCount() {
        return parameterTypes.length;
    }

    @Override
    public Class<?>[] getExceptionTypes() {
        if(exceptionTypes.length > 0)
            return exceptionTypes.clone();
        return exceptionTypes;
    }

    public boolean matches(Class<?>[] ptypes) {
        Class<?>[] parameterTypes = this.parameterTypes;
        if(ptypes == parameterTypes)
            return true;
        int ptypesLength = ptypes.length;
        if(ptypesLength != parameterTypes.length)
            return false;
        for(int i = 0; i < ptypesLength; i++) {
            if(ptypes[i] == parameterTypes[i])
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Constructor<?> other) {
            if(getDeclaringClass() == other.getDeclaringClass())
                return equalParamTypes(parameterTypes, other.parameterTypes);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clazz.getName().hashCode();
    }

    @Override
    public String toString() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public String toGenericString() {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitive
    @ForceInline
    public T newInstance(Object... initargs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isVarArgs() {
        return super.isVarArgs();
    }

    @Override
    public boolean isSynthetic() {
        return super.isSynthetic();
    }

    @Override
    public Annotation[][] getParameterAnnotations() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
