package java.lang.invoke;

import java.lang.invoke.MethodHandles.Lookup;

import static java.lang.invoke.MethodHandleNatives.Constants.*;

class MethodHandleNatives {
    static class Constants {
        Constants() {

        }

        static final int MN_IS_METHOD = 0x00010000;
        static final int MN_IS_CONSTRUCTOR = 0x00020000;
        static final int MN_IS_FIELD = 0x00040000;
        static final int MN_IS_TYPE = 0x00080000;
        static final int MN_CALLER_SENSITIVE = 0x00100000;
        static final int MN_TRUSTED_FINAL = 0x00200000;
        static final int MN_REFERENCE_KIND_SHIFT = 24;
        static final int MN_REFERENCE_KIND_MASK = 0x0F000000 >> MN_REFERENCE_KIND_SHIFT;
        static final int MN_SEARCH_SUPERCLASSES = 0x00100000;
        static final int MN_SEARCH_INTERFACES = 0x00200000;

        static final byte REF_NONE = 0;
        static final byte REF_getField = 1;
        static final byte REF_getStatic = 2;
        static final byte REF_putField = 3;
        static final byte REF_putStatic = 4;
        static final byte REF_invokeVirtual = 5;
        static final byte REF_invokeStatic = 6;
        static final byte REF_invokeSpecial = 7;
        static final byte REF_newInvokeSpecial = 8;
        static final byte REF_invokeInterface = 9;
        static final byte REF_LIMIT = 10;

        static final int NESTMATE_CLASS = 0x00000001;
        static final int HIDDEN_CLASS = 0x00000002;
        static final int STRONG_LOADER_LINK = 0x00000004;
        static final int ACCESS_VM_ANNOTATIONS = 0x00000008;

        static final int LM_MODULE = Lookup.MODULE;
        static final int LM_UNCONDITIONAL = Lookup.UNCONDITIONAL;
        static final int LM_TRUSTED = -1;
    }

    static boolean refKindIsValid(int refKind) {
        return (refKind > REF_NONE && refKind < REF_LIMIT);
    }

    static boolean refKindIsField(byte refKind) {
        return (refKind <= REF_putStatic);
    }

    static boolean refKindIsGetter(byte refKind) {
        return (refKind <= REF_getStatic);
    }

    static boolean refKindIsSetter(byte refKind) {
        return refKindIsField(refKind) && !refKindIsGetter(refKind);
    }

    static boolean refKindIsMethod(byte refKind) {
        return !refKindIsField(refKind) && (refKind != REF_newInvokeSpecial);
    }

    static boolean refKindIsConstructor(byte refKind) {
        return (refKind == REF_newInvokeSpecial);
    }

    static boolean refKindHasReceiver(byte refKind) {
        return (refKind & 1) != 0;
    }

    static boolean refKindIsStatic(byte refKind) {
        return !refKindHasReceiver(refKind) && (refKind != REF_newInvokeSpecial);
    }

    static boolean refKindDoesDispatch(byte refKind) {
        return (refKind == REF_invokeVirtual || refKind == REF_invokeInterface);
    }

    static String refKindName(byte refKind) {
        return switch (refKind) {
            case REF_getField -> "getField";
            case REF_getStatic -> "getStatic";
            case REF_putField -> "putField";
            case REF_putStatic -> "putStatic";
            case REF_invokeVirtual -> "invokeVirtual";
            case REF_invokeStatic -> "invokeStatic";
            case REF_invokeSpecial -> "invokeSpecial";
            case REF_newInvokeSpecial -> "newInvokeSpecial";
            case REF_invokeInterface -> "invokeInterface";
            default -> "REF_???";
        };
    }
}
