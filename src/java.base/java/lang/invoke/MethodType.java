package java.lang.invoke;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import jdk.internal.vm.annotation.Stable;

public final class MethodType implements TypeDescriptor.OfMethod<Class<?>, MethodType> {
    private final @Stable Class<?> rtype;
    private final @Stable Class<?>[] ptypes;
    private @Stable String methodDescriptor;

    static final int MAX_JVM_ARITY = 255;

    static final Class<?>[] NO_PTYPES = {};

    private MethodType(Class<?> rtype, Class<?>[] ptypes) {
        this.rtype = rtype;
        this.ptypes = ptypes;
    }

    Class<?> rtype() {
        return rtype;
    }

    Class<?>[] ptypes() {
        return ptypes;
    }

    static void checkSlotCount(int count) {
        if((count & MAX_JVM_ARITY) != count)
            throw new IllegalArgumentException("bad parameter count " + count);
    }

    private static int checkPtypes(Class<?>[] ptypes) {
        int slots = 0;
        for(Class<?> ptype : ptypes) {
            Objects.requireNonNull(ptype);
            if(ptype == void.class)
                throw new IllegalArgumentException("parameter type cannot be void");
            if(ptype == double.class || ptype == long.class)
                slots++;
        }
        checkSlotCount(ptypes.length + slots);
        return slots;
    }

    public static MethodType methodType(Class<?> rtype, Class<?>[] ptypes) {
        return methodType(rtype, ptypes, false);
    }

    private static Class<?>[] listToArray(List<Class<?>> ptypes) {
        checkSlotCount(ptypes.size());
        return (Class<?>[])ptypes.toArray();
    }

    public static MethodType methodType(Class<?> rtype, List<Class<?>> ptypes) {
        boolean notrust = false;
        return methodType(rtype, listToArray(ptypes), notrust);
    }

    public static MethodType methodType(Class<?> rtype) {
        return makeImpl(rtype, NO_PTYPES, true);
    }

    public static MethodType methodType(Class<?> rtype, MethodType ptypes) {
        return methodType(rtype, ptypes.ptypes, true);
    }

    public static MethodType methodType(Class<?> rtype, Class<?> ptype0) {
        return makeImpl(rtype, new Class<?>[]{ ptype0 }, true);
    }

    public static MethodType methodType(Class<?> rtype, Class<?> ptype0, Class<?>... ptypes) {
        int len = ptypes.length;
        Class<?>[] ptypes1 = new Class<?>[1 + len];
        ptypes1[0] = ptype0;
        System.arraycopy(ptypes, 0, ptypes1, 1, len);
        return makeImpl(rtype, ptypes1, true);
    }

    @Override
    public MethodType changeParameterType(int num, Class<?> nptype) {
        if(parameterType(num) == nptype)
            return this;
        Class<?>[] nptypes = ptypes.clone();
        nptypes[num] = nptype;
        return makeImpl(rtype, nptypes, true);
    }

    @Override
    public MethodType insertParameterTypes(int num, Class<?>... ptypesToInsert) {
        int len = ptypes.length;
        if(num < 0 || num > len)
            throw new IndexOutOfBoundsException();
        checkPtypes(ptypesToInsert);
        int ilen = ptypesToInsert.length;
        if(ilen == 0)
            return this;
        Class<?>[] nptypes = new Class<?>[len + ilen];
        if(num > 0)
            System.arraycopy(ptypes, 0, nptypes, 0, num);
        System.arraycopy(ptypesToInsert, 0, nptypes, num, ilen);
        if(num < len)
            System.arraycopy(ptypes, num, nptypes, num + ilen, len - num);
        return makeImpl(rtype, nptypes, true);
    }

    static MethodType methodType(Class<?> rtype, Class<?>[] ptypes, boolean trusted) {
        return makeImpl(rtype, ptypes, trusted);
    }

    private static MethodType makeImpl(Class<?> rtype, Class<?>[] ptypes, boolean trusted) {
        checkPtypes(ptypes);
        if(trusted)
            return new MethodType(rtype, ptypes);
        else {
            ptypes = Arrays.copyOf(ptypes, ptypes.length);
            return new MethodType(rtype, ptypes);
        }
    }

    public MethodType appendParameterTypes(Class<?>... ptypesToInsert) {
        return insertParameterTypes(parameterCount(), ptypesToInsert);
    }

    public MethodType insertParameterTypes(int num, List<Class<?>> ptypesToInsert) {
        return insertParameterTypes(num, listToArray(ptypesToInsert));
    }

    public MethodType appendParameterTypes(List<Class<?>> ptypesToInsert) {
        return insertParameterTypes(parameterCount(), ptypesToInsert);
    }

    @Override
    public MethodType dropParameterTypes(int start, int end) {
        int len = ptypes.length;
        if(!(0 <= start && start <= end && end <= len))
            throw new IndexOutOfBoundsException("start=" + start + " end=" + end);
        if(start == end)  return this;
        Class<?>[] nptypes;
        if(start == 0)
            nptypes = (end == len) ? NO_PTYPES : Arrays.copyOfRange(ptypes, end, len);
        else {
            if(end == len)
                nptypes = Arrays.copyOfRange(ptypes, 0, start);
            else {
                int tail = len - end;
                nptypes = Arrays.copyOfRange(ptypes, 0, start + tail);
                System.arraycopy(ptypes, end, nptypes, start, tail);
            }
        }
        return methodType(rtype, nptypes, true);
    }

    @Override
    public MethodType changeReturnType(Class<?> nrtype) {
        if(returnType() == nrtype)
            return this;
        return methodType(nrtype, ptypes, true);
    }

    @Override
    public Class<?> parameterType(int num) {
        return ptypes[num];
    }

    @Override
    public int parameterCount() {
        return ptypes.length;
    }

    @Override
    public Class<?> returnType() {
        return rtype;
    }

    @Override
    public List<Class<?>> parameterList() {
        return List.of(ptypes);
    }

    public Class<?> lastParameterType() {
        int len = ptypes.length;
        return len == 0 ? void.class : ptypes[len-1];
    }

    @Override
    public Class<?>[] parameterArray() {
        return ptypes.clone();
    }

    @Override
    public boolean equals(Object x) {
        if(this == x)
            return true;
        if(x instanceof MethodType)
            return equals((MethodType)x);
        return false;
    }

    private boolean equals(MethodType that) {
        return this.rtype == that.rtype && Arrays.equals(this.ptypes, that.ptypes);
    }

    @Override
    public int hashCode() {
        int hashCode = 31 + rtype.hashCode();
        for(Class<?> ptype : ptypes)
            hashCode = 31 * hashCode + ptype.hashCode();
        return hashCode;
    }

    @Override
    public String toString() {
        StringJoiner sj = new StringJoiner(",", "(", ")" + rtype.getSimpleName());
        for(int i = 0; i < ptypes.length; i++)
            sj.add(ptypes[i].getSimpleName());
        return sj.toString();
    }

    public String toMethodDescriptorString() {
        String desc = methodDescriptor;
        if(desc == null) {
            Class<?>[] ptypes = this.ptypes;
            StringBuilder sb = new StringBuilder();
            sb.append("(");
            for(int i = 0; i < ptypes.length; i++)
                sb.append(ptypes[i].descriptorString());
            sb.append(")");
            sb.append(rtype.descriptorString());
            desc = sb.toString();
            methodDescriptor = desc;
        }
        return desc;
    }

    @Override
    public String descriptorString() {
        return toMethodDescriptorString();
    }

    static String toFieldDescriptorString(Class<?> cls) {
        if(cls == Object.class)
            return "Ljava/lang/Object;";
        else if(cls == int.class)
            return "I";
        return cls.descriptorString();
    }
}
