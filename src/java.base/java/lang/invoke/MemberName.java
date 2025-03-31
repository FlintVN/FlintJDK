package java.lang.invoke;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;

import static java.lang.invoke.MethodHandleNatives.Constants.*;

final class MemberName implements Member, Cloneable {
    private Class<?> clazz;
    private String name;
    private Object type;
    private int flags;

    private static final int MH_INVOKE_MODS = Modifier.NATIVE | Modifier.FINAL | Modifier.PUBLIC;

    static final int BRIDGE = 0x00000040;
    static final int VARARGS = 0x00000080;
    static final int SYNTHETIC = 0x00001000;
    static final int ANNOTATION = 0x00002000;
    static final int ENUM = 0x00004000;

    static final String CONSTRUCTOR_NAME = "<init>";

    static final int RECOGNIZED_MODIFIERS = 0xFFFF;

    static final int IS_METHOD = MN_IS_METHOD;
    static final int IS_CONSTRUCTOR = MN_IS_CONSTRUCTOR;
    static final int IS_FIELD = MN_IS_FIELD;
    static final int IS_TYPE = MN_IS_TYPE;
    static final int CALLER_SENSITIVE = MN_CALLER_SENSITIVE;
    static final int TRUSTED_FINAL = MN_TRUSTED_FINAL;

    static final int ALL_ACCESS = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
    static final int ALL_KINDS = IS_METHOD | IS_CONSTRUCTOR | IS_FIELD | IS_TYPE;
    static final int IS_INVOCABLE = IS_METHOD | IS_CONSTRUCTOR;

    public Class<?> getDeclaringClass() {
        return clazz;
    }

    public String getName() {
        if(name == null)
            return null;
        return name;
    }

    public MethodType getMethodOrFieldType() {
        if(isInvocable())
            return getMethodType();
        if(isGetter())
            return MethodType.methodType(getFieldType());
        if(isSetter())
            return MethodType.methodType(void.class, getFieldType());
        throw new InternalError("not a method or field: "+this);
    }

    public MethodType getMethodType() {
        if(type == null)
            return null;
        if(!isInvocable())
            throw new IllegalArgumentException("not invocable, no method type");
        if(type instanceof MethodType)
            return (MethodType)type;
        throw new IllegalArgumentException("bad method type " + type);
    }

    String getMethodDescriptor() {
        if(type == null)
            return null;
        if(!isInvocable())
            throw new IllegalArgumentException("not invocable, no method type");
        if(type instanceof String)
            return (String)type;
        return getMethodType().toMethodDescriptorString();
    }

    public MethodType getInvocationType() {
        MethodType itype = getMethodOrFieldType();
        if(isConstructor() && getReferenceKind() == REF_newInvokeSpecial)
            return itype.changeReturnType(clazz);
        if(!isStatic())
            return itype.insertParameterTypes(0, clazz);
        return itype;
    }

    public Class<?> getFieldType() {
        if(type == null)
            return null;
        if(isInvocable())
            throw new IllegalArgumentException("not a field or nested class, no simple type");
        if(type instanceof Class<?>)
            return (Class<?>)type;
        throw new IllegalArgumentException("bad field type " + type);
    }

    public Object getType() {
        return (isInvocable() ? getMethodType() : getFieldType());
    }

    public int getModifiers() {
        return (flags & RECOGNIZED_MODIFIERS);
    }

    public byte getReferenceKind() {
        return (byte)((flags >>> MN_REFERENCE_KIND_SHIFT) & MN_REFERENCE_KIND_MASK);
    }

    private boolean staticIsConsistent() {
        byte refKind = getReferenceKind();
        return MethodHandleNatives.refKindIsStatic(refKind) == isStatic() || getModifiers() == 0;
    }

    private MemberName changeReferenceKind(byte refKind, byte oldKind) {
        flags += (((int)refKind - oldKind) << MN_REFERENCE_KIND_SHIFT);
        return this;
    }

    private boolean matchingFlagsSet(int mask, int flags) {
        return (this.flags & mask) == flags;
    }

    private boolean allFlagsSet(int flags) {
        return (this.flags & flags) == flags;
    }

    private boolean anyFlagSet(int flags) {
        return (this.flags & flags) != 0;
    }

    public boolean isMethodHandleInvoke() {
        final int bits = MH_INVOKE_MODS &~ Modifier.PUBLIC;
        final int negs = Modifier.STATIC;
        if(matchingFlagsSet(bits | negs, bits) && clazz == MethodHandle.class)
            return isMethodHandleInvokeName(name);
        return false;
    }

    public static boolean isMethodHandleInvokeName(String name) {
        return switch (name) {
            case "invoke", "invokeExact" -> true;
            default -> false;
        };
    }

    public boolean isStatic() {
        return Modifier.isStatic(flags);
    }

    public boolean isPublic() {
        return Modifier.isPublic(flags);
    }

    public boolean isPrivate() {
        return Modifier.isPrivate(flags);
    }

    public boolean isProtected() {
        return Modifier.isProtected(flags);
    }

    public boolean isFinal() {
        return Modifier.isFinal(flags);
    }

    public boolean canBeStaticallyBound() {
        return Modifier.isFinal(flags | clazz.getModifiers());
    }

    public boolean isVolatile() {
        return Modifier.isVolatile(flags);
    }

    public boolean isAbstract() {
        return Modifier.isAbstract(flags);
    }

    public boolean isNative() {
        return Modifier.isNative(flags);
    }

    public boolean isBridge() {
        return allFlagsSet(IS_METHOD | BRIDGE);
    }

    public boolean isVarargs() {
        return allFlagsSet(VARARGS) && isInvocable();
    }

    public boolean isSynthetic() {
        return allFlagsSet(SYNTHETIC);
    }

    public boolean isInvocable() {
        return anyFlagSet(IS_INVOCABLE);
    }

    public boolean isMethod() {
        return allFlagsSet(IS_METHOD);
    }

    public boolean isConstructor() {
        return allFlagsSet(IS_CONSTRUCTOR);
    }

    public boolean isField() {
        return allFlagsSet(IS_FIELD);
    }

    public boolean isType() {
        return allFlagsSet(IS_TYPE);
    }

    public boolean isPackage() {
        return !anyFlagSet(ALL_ACCESS);
    }

    public boolean isCallerSensitive() {
        return allFlagsSet(CALLER_SENSITIVE);
    }

    public boolean isTrustedFinalField() {
        return allFlagsSet(TRUSTED_FINAL | IS_FIELD);
    }

    public boolean refersTo(Class<?> declc, String n) {
        return clazz == declc && getName().equals(n);
    }

    private void init(Class<?> defClass, String name, Object type, int flags) {
        this.clazz = defClass;
        this.name = name;
        this.type = type;
        this.flags = flags;
    }

    private static int flagsMods(int flags, int mods, byte refKind) {
        return flags | mods | (refKind << MN_REFERENCE_KIND_SHIFT);
    }

    public MemberName(Method m) {
        this(m, false);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Method m, boolean wantSpecial) {
        Objects.requireNonNull(m);
        if (clazz == null) {
            if(m.getDeclaringClass() == MethodHandle.class &&
                isMethodHandleInvokeName(m.getName())) {
                MethodType type = MethodType.methodType(m.getReturnType(), m.getParameterTypes());
                int flags = flagsMods(IS_METHOD, m.getModifiers(), REF_invokeVirtual);
                init(MethodHandle.class, m.getName(), type, flags);
                if(isMethodHandleInvoke())
                    return;
            }
            throw new LinkageError(m.toString());
        }
        this.name = m.getName();
        if(this.type == null)
            this.type = new Object[] { m.getReturnType(), m.getParameterTypes() };
        if(wantSpecial) {
            if(isAbstract())
                throw new AbstractMethodError(this.toString());
            if(getReferenceKind() == REF_invokeVirtual)
                changeReferenceKind(REF_invokeSpecial, REF_invokeVirtual);
            else if (getReferenceKind() == REF_invokeInterface)
                changeReferenceKind(REF_invokeSpecial, REF_invokeInterface);
        }
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Constructor<?> ctor) {
        Objects.requireNonNull(ctor);
        this.name = CONSTRUCTOR_NAME;
        if(this.type == null)
            this.type = new Object[] { void.class, ctor.getParameterTypes() };
    }

    public MemberName(Field fld) {
        this(fld, false);
    }

    @SuppressWarnings("LeakingThisInConstructor")
    public MemberName(Field fld, boolean makeSetter) {
        Objects.requireNonNull(fld);
        this.name = fld.getName();
        this.type = fld.getType();
        byte refKind = this.getReferenceKind();
        assert(refKind == (isStatic() ? REF_getStatic : REF_getField));
        if(makeSetter)
            changeReferenceKind((byte)(refKind + (REF_putStatic - REF_getStatic)), refKind);
    }

    public MemberName(Class<?> type) {
        init(type.getDeclaringClass(), type.getSimpleName(), type, flagsMods(IS_TYPE, type.getModifiers(), REF_NONE));
    }

    public MemberName(Class<?> defClass, String name, Class<?> type, byte refKind) {
        init(defClass, name, type, flagsMods(IS_FIELD, 0, refKind));
    }

    public MemberName(Class<?> defClass, String name, MethodType type, byte refKind) {
        int initFlags = (name != null && name.equals(CONSTRUCTOR_NAME) ? IS_CONSTRUCTOR : IS_METHOD);
        init(defClass, name, type, flagsMods(initFlags, 0, refKind));
    }

    public MemberName(byte refKind, Class<?> defClass, String name, Object type) {
        int kindFlags;
        if(MethodHandleNatives.refKindIsField(refKind)) {
            kindFlags = IS_FIELD;
            if(!(type instanceof Class))
                throw new IllegalArgumentException("not a field type");
        }
        else if(MethodHandleNatives.refKindIsMethod(refKind)) {
            kindFlags = IS_METHOD;
            if(!(type instanceof MethodType))
                throw new IllegalArgumentException("not a method type");
        }
        else if(refKind == REF_newInvokeSpecial) {
            kindFlags = IS_CONSTRUCTOR;
            if(!(type instanceof MethodType) ||
                !CONSTRUCTOR_NAME.equals(name))
                throw new IllegalArgumentException("not a constructor type or name");
        }
        else {
            throw new IllegalArgumentException("bad reference kind "+refKind);
        }
        init(defClass, name, type, flagsMods(kindFlags, 0, refKind));
    }

    public MemberName asSpecial() {
        switch (getReferenceKind()) {
            case REF_invokeSpecial: return this;
            case REF_invokeVirtual: return clone().changeReferenceKind(REF_invokeSpecial, REF_invokeVirtual);
            case REF_invokeInterface: return clone().changeReferenceKind(REF_invokeSpecial, REF_invokeInterface);
            case REF_newInvokeSpecial: return clone().changeReferenceKind(REF_invokeSpecial, REF_newInvokeSpecial);
        }
        throw new IllegalArgumentException(this.toString());
    }

    public MemberName asConstructor() {
        switch (getReferenceKind()) {
            case REF_invokeSpecial: return clone().changeReferenceKind(REF_newInvokeSpecial, REF_invokeSpecial);
            case REF_newInvokeSpecial:  return this;
        }
        throw new IllegalArgumentException(this.toString());
    }

    public MemberName asNormalOriginal() {
        byte refKind = getReferenceKind();
        byte newRefKind = switch (refKind) {
            case REF_invokeInterface,
                 REF_invokeVirtual,
                 REF_invokeSpecial -> clazz.isInterface() ? REF_invokeInterface : REF_invokeVirtual;
            default -> refKind;
        };
        if(newRefKind == refKind)
            return this;
        MemberName result = clone().changeReferenceKind(newRefKind, refKind);
        return result;
    }

    public boolean isGetter() {
        return MethodHandleNatives.refKindIsGetter(getReferenceKind());
    }

    public boolean isSetter() {
        return MethodHandleNatives.refKindIsSetter(getReferenceKind());
    }

    static MemberName makeMethodHandleInvoke(String name, MethodType type) {
        return makeMethodHandleInvoke(name, type, MH_INVOKE_MODS | SYNTHETIC);
    }

    static MemberName makeMethodHandleInvoke(String name, MethodType type, int mods) {
        MemberName mem = new MemberName(MethodHandle.class, name, type, REF_invokeVirtual);
        mem.flags |= mods;
        return mem;
    }

    @Override protected MemberName clone() {
        try {
            return (MemberName)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }

    @Override
    @SuppressWarnings({"deprecation", "removal"})
    public int hashCode() {
        return Objects.hash(clazz, new Byte(getReferenceKind()), name, getType());
    }

    @Override
    public boolean equals(Object that) {
        return (that instanceof MemberName && this.equals((MemberName)that));
    }

    public boolean equals(MemberName that) {
        if(this == that)
            return true;
        if(that == null)
            return false;
        return (
            this.clazz == that.clazz &&
            this.getReferenceKind() == that.getReferenceKind() &&
            Objects.equals(this.name, that.name) &&
            Objects.equals(this.getType(), that.getType())
        );
    }

    @SuppressWarnings("LocalVariableHidesMemberVariable")
    @Override
    public String toString() {
        if(isType())
            return type.toString();
        StringBuilder buf = new StringBuilder();
        if(getDeclaringClass() != null) {
            buf.append(getName(clazz));
            buf.append('.');
        }
        String name = this.name;
        buf.append(name == null ? "*" : name);
        Object type = this.type;
        if(!isInvocable()) {
            buf.append('/');
            buf.append(type == null ? "*" : getName(type));
        }
        else
            buf.append(type == null ? "(*)*" : getName(type));
        byte refKind = getReferenceKind();
        if(refKind != REF_NONE) {
            buf.append('/');
            buf.append(MethodHandleNatives.refKindName(refKind));
        }
        return buf.toString();
    }

    private static String getName(Object obj) {
        if(obj instanceof Class<?>)
            return ((Class<?>)obj).getName();
        return String.valueOf(obj);
    }
}
