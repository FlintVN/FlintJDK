package java.lang.reflect;

import java.lang.annotation.Annotation;
import jdk.internal.reflect.Reflection;
import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.CallerSensitiveAdapter;
import jdk.internal.vm.annotation.ForceInline;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public final class Method extends Executable {
    private int entry;  /* Used by FlintJVM */
    private final Class<?> clazz;
    private final String name;
    private final Class<?> returnType;
    private final Class<?>[] parameterTypes;
    private final Class<?>[] exceptionTypes;
    private final int modifiers;

    private Method(
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

    public boolean matches(String name, Class<?>[] ptypes) {
        Class<?>[] parameterTypes = this.parameterTypes;
        if(ptypes != parameterTypes) {
            if(ptypes == null)
                return parameterTypes.length == 0;
            int ptypesLength = ptypes.length;
            if(ptypesLength != parameterTypes.length)
                return false;
            for(int i = 0; i < ptypesLength; i++) {
                if(ptypes[i] != parameterTypes[i])
                    return false;
            }
        }
        return this.name.equals(name);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Method other) {
            if(clazz == other.clazz && name == other.name) {
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
        try {
            StringBuffer sb = new StringBuffer();
            int mod = getModifiers();
            if(mod != 0)
                sb.append(Modifier.toString(mod) + " ");
            sb.append(getReturnType().getTypeName() + " ");
            sb.append(getDeclaringClass().getTypeName() + ".");
            sb.append(getName() + "(");
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

    private native Object invoke0(Object obj, Object... args) throws InvocationTargetException;

    @CallerSensitive
    @ForceInline
    @IntrinsicCandidate
    public Object invoke(Object obj, Object... args) throws IllegalAccessException, InvocationTargetException {
        checkParameter(obj, args);
        checkAccess(Reflection.getCallerClass(), clazz, modifiers);
        return invoke0(obj, args);
    }

    @CallerSensitiveAdapter
    private Object invoke(Object obj, Object[] args, Class<?> caller) throws IllegalAccessException, InvocationTargetException {
        checkParameter(obj, args);
        checkAccess(caller, clazz, modifiers);
        return invoke0(obj, args);
    }

    private void checkParameter(Object obj, Object[] args) {
        if((modifiers & Modifier.STATIC) == 0) {
            if(!this.clazz.isAssignableFrom(obj.getClass()))
                throw new IllegalArgumentException("object is not an instance of declaring class");
        }
        int argc = args != null ? args.length : 0;
        int paramCount = parameterTypes != null ? parameterTypes.length : 0;
        if(argc != paramCount)
            throw new IllegalArgumentException("wrong number of arguments: " + argc + " expected: " + paramCount);
        Class<?>[] parameterTypes = this.parameterTypes;
        for(int i = 0; i < paramCount; i++) {
            Class<?> type = parameterTypes[i];
            Class<?> argType = args[i].getClass();
            if(type.isPrimitive()) {
                if(getWrapper(type) != argType)
                    throw new IllegalArgumentException("argument type mismatch");
            }
            else if(type != argType)
                throw new IllegalArgumentException("argument type mismatch");
        }
    }

    private static Class<?> getWrapper(Class<?> primitive) {
        if(primitive == int.class) return Integer.class;
        if(primitive == boolean.class) return Boolean.class;
        if(primitive == byte.class) return Byte.class;
        if(primitive == char.class) return Character.class;
        if(primitive == short.class) return Short.class;
        if(primitive == long.class) return Long.class;
        if(primitive == float.class) return Float.class;
        return Double.class;
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
