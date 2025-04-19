package java.lang;

import java.util.Objects;
import java.util.Arrays;
import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.lang.reflect.Modifier;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import jdk.internal.reflect.CallerSensitive;

public final class Class<T> implements Type, TypeDescriptor.OfField<Class<?>> {
    private static final int ANNOTATION = 0x00002000;
    private static final int ENUM = 0x00004000;
    private static final int SYNTHETIC = 0x00001000;

    private transient String name;

    private Class<?>[] interfaces;

    private Field[] declaredFields;
    private Field[] declaredPublicFields;
    private Method[] declaredMethods;
    private Method[] declaredPublicMethods;
    private Constructor<T>[] declaredConstructors;
    private Constructor<T>[] declaredPublicConstructors;

    private Class() {

    }

    @Override
    public String toString() {
        String kind = isInterface() ? "interface " : isPrimitive() ? "" : "class ";
        return kind.concat(name);
    }

    static native Class<?> getPrimitiveClass(String name);

    public native static Class<?> forName(String className) throws ClassNotFoundException;

    public native boolean isInstance(Object obj);
    public native boolean isAssignableFrom(Class<?> cls);
    public native boolean isInterface();
    public native boolean isArray();
    public native boolean isPrimitive();

    @Override
    public Class<?> componentType() {
        return isArray() ? getComponentType() : null;
    }

    @Override
    public Class<?> arrayType() {
        try {
            return Array.newInstance(this, 0).getClass();
        }
        catch(IllegalArgumentException iae) {
            throw new UnsupportedOperationException(iae);
        }
    }

    public String getName() {
        return name;
    }

    public native Class<? super T> getSuperclass();

    public String getPackageName() {
        Class<?> c = isArray() ? elementType() : this;
        if(c.isPrimitive())
            return "java.lang";
        else {
            String cn = c.getName();
            int dot = cn.lastIndexOf('.');
            return (dot != -1) ? cn.substring(0, dot).intern() : "";
        }
    }

    public Class<?>[] getInterfaces() {
        Class<?>[] interfaces = this.interfaces;
        if(interfaces == null)
            interfaces = getInterfaces0();
        if(interfaces.length > 0)
            return interfaces.clone();
        return interfaces;
    }

    private native Class<?>[] getInterfaces0();

    public Type[] getGenericInterfaces() {
        // TODO
        return getInterfaces();
    }

    public native Class<?> getComponentType();

    private Class<?> elementType() {
        if(!isArray())
            return null;

        Class<?> c = this;
        while(c.isArray())
            c = c.getComponentType();
        return c;
    }

    public native int getModifiers();

    @CallerSensitive
    public Class<?> getDeclaringClass() {
        return getDeclaringClass0();
    }

    private native Class<?> getDeclaringClass0();

    public String getSimpleName() {
        String simpleName = name;
        int arrayCount = 0;
        int startIndex = simpleName.lastIndexOf('.');
        int endIndex = simpleName.length();
        while(simpleName.charAt(arrayCount) == '[')
            arrayCount++;
        startIndex = (startIndex < 0) ? arrayCount : (startIndex + 1);
        if((endIndex - startIndex) == 1) {
            char ch = simpleName.charAt(startIndex);
            simpleName = switch(ch) {
                case 'Z' -> "boolean";
                case 'C' -> "char";
                case 'F' -> "float";
                case 'D' -> "double";
                case 'B' -> "byte";
                case 'S' -> "short";
                case 'I' -> "int";
                case 'J' -> "long";
                default -> String.valueOf(ch);
            };
        }
        else {
            if(arrayCount > 0 && startIndex == arrayCount && simpleName.charAt(arrayCount) == 'L')
                startIndex++;
            if(simpleName.charAt(endIndex - 1) == ';')
                endIndex--;
            simpleName = simpleName.substring(startIndex, endIndex);
        }
        if(arrayCount > 0)
            simpleName = simpleName.concat("[]".repeat(arrayCount));
        return simpleName;
    }

    @Override
    public String getTypeName() {
        if(isArray()) {
            try {
                Class<?> cl = this;
                int dimensions = 0;
                do {
                    dimensions++;
                    cl = cl.getComponentType();
                } while(cl.isArray());
                return cl.getName().concat("[]".repeat(dimensions));
            }
            catch(Throwable e) {

            }
        }
        return name;
    }

    private static boolean arrayContentsEq(Object[] a1, Object[] a2) {
        if(a1 == null)
            return a2 == null || a2.length == 0;
        if(a2 == null)
            return a1.length == 0;
        if(a1.length != a2.length)
            return false;
        for(int i = 0; i < a1.length; i++) {
            if(a1[i] != a2[i])
                return false;
        }
        return true;
    }

    public boolean desiredAssertionStatus() {
        return false;
    }

    public boolean isEnum() {
        return (this.getModifiers() & ENUM) != 0;
    }

    public boolean isRecord() {
        return (this.getModifiers() & 0x00000010) != 0;
    }

    @SuppressWarnings("unchecked")
    public T cast(Object obj) {
        if(obj != null && !isInstance(obj))
            throw new ClassCastException("Cannot cast " + obj.getClass().getName() + " to " + name);
        return (T)obj;
    }

    public native boolean isHidden();

    private native Field[] getDeclaredFields0();
    private native Method[] getDeclaredMethods0();
    private native Constructor<T>[] getDeclaredConstructors0();

    @CallerSensitive
    public Field[] getFields() {
        Field[] publicFields = privateGetDeclaredFields(true);
        if(publicFields.length > 0)
            return publicFields.clone();
        return publicFields;
    }

    @CallerSensitive
    public Field getField(String name) throws NoSuchFieldException {
        Objects.requireNonNull(name);
        Field field = getFields0(name);
        if(field == null)
            throw new NoSuchFieldException(name);
        return field;
    }

    private Field getFields0(String name) {
        Field[] publicFields = privateGetDeclaredFields(true);
        for(int i = 0; i < publicFields.length; i++) {
            Field field = publicFields[i];
            if(name == field.getName())
                return field;
        }
        Class<?> superClass = getSuperclass();
        if(superClass == null)
            return null;
        return superClass.getFields0(name);
    }

    private Field[] privateGetDeclaredFields(boolean publicOnly) {
        if(publicOnly) {
            if(declaredPublicFields == null) {
                if(declaredFields == null)
                    declaredFields = getDeclaredFields0();
                int count = 0;
                Field[] fields = declaredFields;
                for(int i = 0; i < fields.length; i++) {
                    if(Modifier.isPublic(fields[i].getModifiers()))
                        count++;
                }
                if(count == declaredFields.length)
                    declaredPublicFields = declaredFields;
                else {
                    Field[] publicFields = new Field[count];
                    if(count > 0) {
                        int index = 0;
                        for(int i = 0; i < fields.length; i++) {
                            if(Modifier.isPublic(fields[i].getModifiers()))
                                publicFields[index++] = fields[i];
                        }
                    }
                    declaredPublicFields = publicFields;
                }
            }
            return declaredPublicFields;
        }
        else {
            if(declaredFields == null)
                declaredFields = getDeclaredFields0();
            return declaredFields;
        }
    }

    @CallerSensitive
    public Method[] getMethods() {
        Method[] publicMethods = privateGetDeclaredMethods(true);
        if(publicMethods.length > 0)
            return publicMethods.clone();
        return publicMethods;
    }

    @CallerSensitive
    public Method getMethod(String name, Class<?>... parameterTypes) throws NoSuchMethodException {
        Objects.requireNonNull(name);
        Method method = getMethod0(name, parameterTypes);
        if(method == null)
            throw new NoSuchMethodException(methodToString(name, parameterTypes));
        return method;
    }

    private Method getMethod0(String name, Class<?>... parameterTypes) {
        Method[] publicMethods = privateGetDeclaredMethods(true);
        for(int i = 0; i < publicMethods.length; i++) {
            Method method = publicMethods[i];
            if(method.matches(name, parameterTypes))
                return method;
        }
        Class<?> superClass = getSuperclass();
        if(superClass == null)
            return null;
        return superClass.getMethod0(name, parameterTypes);
    }

    private Method[] privateGetDeclaredMethods(boolean publicOnly) {
        if(publicOnly) {
            if(declaredPublicMethods == null) {
                if(declaredMethods == null)
                    declaredMethods = getDeclaredMethods0();
                int count = 0;
                Method[] methods = declaredMethods;
                for(int i = 0; i < methods.length; i++) {
                    if(Modifier.isPublic(methods[i].getModifiers()))
                        count++;
                }
                if(count == declaredMethods.length)
                    declaredPublicMethods = declaredMethods;
                else {
                    Method[] publicMethods = new Method[count];
                    if(count > 0) {
                        int index = 0;
                        for(int i = 0; i < methods.length; i++) {
                            if(Modifier.isPublic(methods[i].getModifiers()))
                                publicMethods[index++] = methods[i];
                        }
                    }
                    declaredPublicMethods = publicMethods;
                }
            }
            return declaredPublicMethods;
        }
        else {
            if(declaredMethods == null)
                declaredMethods = getDeclaredMethods0();
            return declaredMethods;
        }
    }

    @CallerSensitive
    public Constructor<?>[] getConstructors() {
        Constructor<T>[] publicConstructor = privateGetDeclaredConstructors(true);
        if(publicConstructor.length > 0)
            return publicConstructor.clone();
        return publicConstructor;
    }

    @CallerSensitive
    public Constructor<T> getConstructor(Class<?>... parameterTypes) throws NoSuchMethodException {
        Constructor<T> constructor = getConstructor0(parameterTypes, true);
        if(constructor == null)
            throw new NoSuchMethodException(methodToString("<init>", parameterTypes));
        return constructor;
    }

    private Constructor<T> getConstructor0(Class<?>[] parameterTypes, boolean isPublic) {
        Constructor<T>[] publicConstructor = privateGetDeclaredConstructors(isPublic);
        for(int i = 0; i < publicConstructor.length; i++) {
            Constructor<T> constructor = publicConstructor[i];
            if(constructor.matches(parameterTypes))
                return constructor;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private Constructor<T>[] privateGetDeclaredConstructors(boolean publicOnly) {
        if(publicOnly) {
            if(declaredPublicConstructors == null) {
                if(declaredConstructors == null)
                    declaredConstructors = getDeclaredConstructors0();
                int count = 0;
                Constructor<T>[] constructors = declaredConstructors;
                for(int i = 0; i < constructors.length; i++) {
                    if(Modifier.isPublic(constructors[i].getModifiers()))
                        count++;
                }
                if(count == declaredConstructors.length)
                    declaredPublicConstructors = declaredConstructors;
                else {
                    Constructor<T>[] publicConstructors = (Constructor<T>[])new Constructor<?>[count];
                    if(count > 0) {
                        int index = 0;
                        for(int i = 0; i < constructors.length; i++) {
                            if(Modifier.isPublic(constructors[i].getModifiers()))
                                publicConstructors[index++] = constructors[i];
                        }
                    }
                    declaredPublicConstructors = publicConstructors;
                }
            }
            return declaredPublicConstructors;
        }
        else {
            if(declaredConstructors == null)
                declaredConstructors = getDeclaredConstructors0();
            return declaredConstructors;
        }
    }

    private String methodToString(String name, Class<?>[] argTypes) {
        int length = this.name.length() + 3 + name.length();
        if(argTypes != null && argTypes.length > 0) {
            for(int i = 0; i < argTypes.length; i++)
                length += argTypes[i].getName().length();
            length += argTypes.length - 1;
        }
        int index = 0;
        byte[] buff = new byte[length];

        byte[] tmp = this.name.value();
        System.arraycopy(tmp, 0, buff, index, tmp.length);
        index += tmp.length;

        buff[index++] = '.';

        tmp = name.value();
        System.arraycopy(tmp, 0, buff, index, tmp.length);
        index += tmp.length;

        buff[index++] = '(';
        if(argTypes != null && argTypes.length > 0) {
            for(int i = 0; i < argTypes.length - 1; i++) {
                tmp = argTypes[i].getName().value();
                System.arraycopy(tmp, 0, buff, index, tmp.length);
                index += tmp.length;
                buff[index++] = ',';
            }
            tmp = argTypes[argTypes.length - 1].getName().value();
            System.arraycopy(tmp, 0, buff, index, tmp.length);
            index += tmp.length;
        }
        buff[index] = ')';

        return new String(buff, String.LATIN1);
    }

    @Override
    public String descriptorString() {
        if(isPrimitive()) {
            String name = this.name;
            switch(name.length()) {
                case 3:
                    return "I";
                case 4:
                    if(name.equals("char"))
                        return "C";
                    else if(name.equals("byte"))
                        return "B";
                    else
                        return "J";
                case 5:
                    if(name.equals("float"))
                        return "F";
                    else
                        return "S";
                case 6:
                    return "D";
                default:
                    return "Z";
            }
        }
        else if(isArray())
            return "[" + getComponentType().descriptorString();
        else if(isHidden()) {
            String name = this.name;
            int index = name.indexOf('/');
            StringBuilder sb = new StringBuilder(name.length() + 2);
            sb.append('L');
            sb.append(name.substring(0, index).replace('.', '/'));
            sb.append('.');
            sb.append(name, index + 1, name.length());
            sb.append(';');
            return sb.toString();
        }
        else {
            String name = this.name.replace('.', '/');
            return new StringBuilder(name.length() + 2).append('L').append(name).append(';').toString();
        }
    }
}
