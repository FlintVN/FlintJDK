package java.lang.invoke;

import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.CallerSensitiveAdapter;
import jdk.internal.reflect.Reflection;
import jdk.internal.vm.annotation.ForceInline;

import java.lang.reflect.Modifier;

public class MethodHandles {
    private MethodHandles() {

    }

    @CallerSensitive
    @ForceInline
    public static Lookup lookup() {
        final Class<?> c = Reflection.getCallerClass();
        if(c == null)
            throw new IllegalCallerException("no caller frame");
        return new Lookup(c);
    }

    public static Lookup publicLookup() {
        return Lookup.PUBLIC_LOOKUP;
    }

    public static final class Lookup {
        private final Class<?> lookupClass;
        private final int allowedModes;

        public static final int PUBLIC = Modifier.PUBLIC;
        public static final int PRIVATE = Modifier.PRIVATE;
        public static final int PROTECTED = Modifier.PROTECTED;
        public static final int PACKAGE = Modifier.STATIC;
        public static final int MODULE = PACKAGE << 1;
        public static final int UNCONDITIONAL = PACKAGE << 2;
        public static final int ORIGINAL = PACKAGE << 3;

        private static final int ALL_MODES = (PUBLIC | PRIVATE | PROTECTED | PACKAGE | MODULE | UNCONDITIONAL | ORIGINAL);
        private static final int FULL_POWER_MODES = (ALL_MODES & ~UNCONDITIONAL);
        private static final int TRUSTED = -1;

        static final Lookup PUBLIC_LOOKUP = new Lookup(Object.class, UNCONDITIONAL);

        public Class<?> lookupClass() {
            return lookupClass;
        }

        public int lookupModes() {
            return allowedModes & ALL_MODES;
        }

        Lookup(Class<?> lookupClass) {
            this(lookupClass, FULL_POWER_MODES);
        }

        private Lookup(Class<?> lookupClass, int allowedModes) {
            assert !lookupClass.isArray() && !lookupClass.isPrimitive();
            this.lookupClass = lookupClass;
            this.allowedModes = allowedModes;
        }

        @Override
        public String toString() {
            String cname = lookupClass.getName();
            // if(prevLookupClass != null)
            //     cname += "/" + prevLookupClass.getName();
            switch(allowedModes) {
            case 0:
                return cname + "/noaccess";
            case UNCONDITIONAL:
                return cname + "/publicLookup";
            case PUBLIC:
                return cname + "/public";
            case PUBLIC | MODULE:
                return cname + "/module";
            case PUBLIC | PACKAGE:
            case PUBLIC | MODULE | PACKAGE:
                return cname + "/package";
            case PUBLIC | PACKAGE | PRIVATE:
            case PUBLIC | MODULE | PACKAGE | PRIVATE:
                    return cname + "/private";
            case PUBLIC | PACKAGE | PRIVATE | PROTECTED:
            case PUBLIC | MODULE | PACKAGE | PRIVATE | PROTECTED:
            case FULL_POWER_MODES:
                    return cname;
            case TRUSTED:
                return "/trusted";
            default:
                cname = cname + "/" + Integer.toHexString(allowedModes);
                return cname;
            }
        }

        private native MethodHandle findStatic(Class<?> refc, String name, String desc);

        public MethodHandle findStatic(Class<?> refc, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
            try {
                return findStatic(refc, name, type.toMethodDescriptorString());
            }
            catch(NoSuchMethodError ex) {
                throw new NoSuchMethodException(refc.getName() + "." + name + type.toString());
            }
        }

        private native MethodHandle findVirtual(Class<?> refc, String name, String desc);

        public MethodHandle findVirtual(Class<?> refc, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
            try {
                return findVirtual(refc, name, type.toMethodDescriptorString());
            }
            catch(NoSuchMethodError ex) {
                throw new NoSuchMethodException(refc.getName() + "." + name + type.toString());
            }
        }

        private native MethodHandle findConstructor(Class<?> refc, String desc);

        public MethodHandle findConstructor(Class<?> refc, MethodType type) throws NoSuchMethodException, IllegalAccessException {
            try {
                return findConstructor(refc, type.toMethodDescriptorString());
            }
            catch(NoSuchMethodError ex) {
                throw new NoSuchMethodException(refc.getName() + ".<init>" + type.toString());
            }
        }

        public Class<?> findClass(String targetName) throws ClassNotFoundException, IllegalAccessException {
            return Class.forName(targetName);
        }

        public Class<?> accessClass(Class<?> targetClass) throws IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findSpecial(Class<?> refc, String name, MethodType type, Class<?> specialCaller) throws NoSuchMethodException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findGetter(Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findSetter(Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        // TODO
        // public VarHandle findVarHandle(Class<?> recv, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        //     // TODO
        //     throw new UnsupportedOperationException();
        // }

        public MethodHandle findStaticGetter(Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findStaticSetter(Class<?> refc, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        // TODO
        // public VarHandle findStaticVarHandle(Class<?> decl, String name, Class<?> type) throws NoSuchFieldException, IllegalAccessException {
        //     // TODO
        //     throw new UnsupportedOperationException();
        // }

        public boolean hasFullPrivilegeAccess() {
            return (allowedModes & (PRIVATE | MODULE)) == (PRIVATE | MODULE);
        }
    }

    private static native BoundMethodHandle rebindArguments(MethodHandle target, byte[] indexes, Object[] values);

    public static MethodHandle insertArguments(MethodHandle target, int pos, Object... values) {
        int insCount = values.length;
        Class<?>[] ptypes = insertArgumentsChecks(target, insCount, pos);
        if(insCount == 0) return target;
        byte[] indexes = new byte[insCount];
        for(int i = 0; i < insCount; i++) {
            indexes[i] = (byte)(pos + i);
            if(ptypes[i].isPrimitive())
                indexes[i] |= (byte)0x80;
        }
        return rebindArguments(target, indexes, values);
    }

    private static Class<?>[] insertArgumentsChecks(MethodHandle target, int insCount, int pos) throws RuntimeException {
        MethodType oldType = target.type();
        int outargs = oldType.parameterCount();
        int inargs = outargs - insCount;
        if((pos + insCount) > 127 || inargs < 0)
            throw new IllegalArgumentException("too many values to insert");
        if(pos < 0 || pos > inargs)
            throw new IllegalArgumentException("no argument type to append");
        return oldType.ptypes();
    }
}
