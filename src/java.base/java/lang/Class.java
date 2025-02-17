package java.lang;

import java.lang.invoke.TypeDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.Type;

public final class Class<T> implements Type, TypeDescriptor.OfField<Class<?>> {
    private static final int ANNOTATION = 0x00002000;
    private static final int ENUM = 0x00004000;
    private static final int SYNTHETIC = 0x00001000;

    private transient String name;

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

    public native Class<?>[] getInterfaces();

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
