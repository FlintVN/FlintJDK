package java.lang.reflect;

import java.util.Set;
import java.util.Objects;
import java.lang.annotation.Annotation;

public abstract sealed class Executable implements Member permits Constructor, Method {
    @SuppressWarnings("deprecation")
    Executable() {

    }

    boolean equalParamTypes(Class<?>[] params1, Class<?>[] params2) {
        if(params1.length == params2.length) {
            for(int i = 0; i < params1.length; i++) {
                if(params1[i] != params2[i])
                    return false;
            }
            return true;
        }
        return false;
    }

    public abstract Class<?> getDeclaringClass();

    public abstract String getName();

    public abstract int getModifiers();

    @Override
    public Set<AccessFlag> accessFlags() {
        // return AccessFlag.maskToAccessFlags(getModifiers(), AccessFlag.Location.METHOD);
        // TODO
        throw new UnsupportedOperationException();
    }

    public abstract Class<?>[] getParameterTypes();

    public Type[] getGenericParameterTypes() {
        // TODO
        return getParameterTypes();
    }

    public abstract int getParameterCount();

    public abstract Class<?>[] getExceptionTypes();

    public abstract String toGenericString();

    public boolean isVarArgs() {
        return (getModifiers() & Modifier.VARARGS) != 0;
    }

    public boolean isSynthetic() {
        return Modifier.isSynthetic(getModifiers());
    }

    public abstract Annotation[][] getParameterAnnotations();
}
