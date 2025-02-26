package java.lang.reflect;

import java.lang.annotation.Annotation;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.CallerSensitiveAdapter;
import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public final class Method extends Executable {
    private final Class<?> clazz;
    private final String name;
    private final Class<?> returnType;
    private final Class<?>[] parameterTypes;
    private final Class<?>[] exceptionTypes;
    private final int modifiers;

    Method(
        Class<?> declaringClass,
        String name,
        Class<?>[] parameterTypes,
        Class<?> returnType,
        Class<?>[] checkedExceptions,
        int modifiers
    ) {
        this.clazz = declaringClass;
        this.name = name;
        this.parameterTypes = parameterTypes;
        this.returnType = returnType;
        this.exceptionTypes = checkedExceptions;
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

    public Class<?> getReturnType() {
        return returnType;
    }

    public Type getGenericReturnType() {
        return returnType;
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

    public boolean matches(String name, Class<?>[] ptypes) {
        Class<?>[] parameterTypes = this.parameterTypes;
        if(ptypes != parameterTypes) {
            if(ptypes == null)
                return parameterTypes.length == 0;
            int ptypesLength = ptypes.length;
            if(ptypesLength != parameterTypes.length)
                return false;
            for(int i = 0; i < ptypesLength; i++) {
                if(ptypes[i] == parameterTypes[i])
                    return false;
            }
        }
        return this.name.equals(name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Method other) {
            if((getDeclaringClass() == other.getDeclaringClass()) && (getName() == other.getName())) {
                if(!returnType.equals(other.getReturnType()))
                    return false;
                return equalParamTypes(parameterTypes, other.parameterTypes);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return clazz.getName().hashCode() ^ name.hashCode();
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
    @IntrinsicCandidate
    public Object invoke(Object obj, Object... args) throws IllegalAccessException, InvocationTargetException {
        // TODO
        throw new UnsupportedOperationException();
    }

    @CallerSensitiveAdapter
    private Object invoke(Object obj, Object[] args, Class<?> caller) throws IllegalAccessException, InvocationTargetException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public boolean isBridge() {
        return (getModifiers() & Modifier.BRIDGE) != 0;
    }

    @Override
    public boolean isVarArgs() {
        return super.isVarArgs();
    }

    @Override
    public boolean isSynthetic() {
        return super.isSynthetic();
    }

    public boolean isDefault() {
        return ((getModifiers() & (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC)) == Modifier.PUBLIC) && getDeclaringClass().isInterface();
    }

    @Override
    public Annotation[][] getParameterAnnotations() {
        // TODO
        throw new UnsupportedOperationException();
    }
}
