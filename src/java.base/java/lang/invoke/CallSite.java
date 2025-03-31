package java.lang.invoke;

public abstract /* sealed */ class CallSite /* permits ConstantCallSite, MutableCallSite, VolatileCallSite */ {
    final MethodHandle target;

    CallSite(MethodHandle target) {
        this.target = target;
    }

    public MethodType type() {
        return target.type();
    }

    public abstract MethodHandle getTarget();

    public abstract void setTarget(MethodHandle newTarget);

    public abstract MethodHandle dynamicInvoker();
}
