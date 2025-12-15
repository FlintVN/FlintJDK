package java.lang.reflect;

import java.lang.annotation.Annotation;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.vm.annotation.ForceInline;

public final class Constructor<T> extends Executable {
    private final Class<T> clazz;
    private final Class<?>[] parameterTypes;
    private final Class<?>[] exceptionTypes;
    private final int modifiers;

    private Constructor(
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
        return super.getGenericParameterTypes();
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
        if(ptypes == null)
            return parameterTypes.length == 0;
        int ptypesLength = ptypes.length;
        if(ptypesLength != parameterTypes.length)
            return false;
        for(int i = 0; i < ptypesLength; i++) {
            if(ptypes[i] != parameterTypes[i])
                return false;
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Constructor<?> other) {
            if(clazz == other.clazz)
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
        try {
            StringBuffer sb = new StringBuffer();
            int mod = getModifiers();
            if(mod != 0)
                sb.append(Modifier.toString(mod) + " ");
            sb.append(getDeclaringClass().getTypeName());
            sb.append("(");
            Class<?>[] params = parameterTypes;
            for(int j = 0; j < params.length; j++) {
                sb.append(params[j].getTypeName());
                if(j < (params.length - 1))
                    sb.append(",");
            }
            sb.append(")");
            Class<?>[] exceptions = exceptionTypes;
            if(exceptions.length > 0) {
                sb.append(" throws ");
                for(int k = 0; k < exceptions.length; k++) {
                    sb.append(exceptions[k].getName());
                    if(k < (exceptions.length - 1))
                        sb.append(",");
                }
            }
            return sb.toString();
        }
        catch(Exception e) {
            return "<" + e + ">";
        }
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
