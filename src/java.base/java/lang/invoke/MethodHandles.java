package java.lang.invoke;

import jdk.internal.reflect.CallerSensitive;
import jdk.internal.reflect.CallerSensitiveAdapter;
import jdk.internal.vm.annotation.ForceInline;

import java.lang.reflect.Modifier;

public class MethodHandles {
    private MethodHandles() {

    }

    @CallerSensitive
    @ForceInline
    public static Lookup lookup() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static Lookup publicLookup() {
        // TODO
        throw new UnsupportedOperationException();
    }

    public static final class Lookup {
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

        @Override
        public String toString() {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findStatic(Class<?> refc, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findVirtual(Class<?> refc, String name, MethodType type) throws NoSuchMethodException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public MethodHandle findConstructor(Class<?> refc, MethodType type) throws NoSuchMethodException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
        }

        public Class<?> findClass(String targetName) throws ClassNotFoundException, IllegalAccessException {
            // TODO
            throw new UnsupportedOperationException();
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

        @Deprecated(since="14")
        public boolean hasPrivateAccess() {
            // TODO
            throw new UnsupportedOperationException();
        }

        public boolean hasFullPrivilegeAccess() {
            // TODO
            throw new UnsupportedOperationException();
        }
    }
}
