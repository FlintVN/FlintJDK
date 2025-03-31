package java.lang.invoke;

public abstract /* sealed */ class CallSite /* permits ConstantCallSite, MutableCallSite, VolatileCallSite */ {
    final MethodHandle target;

    // TODO
    // CallSite(MethodType type) {
    //     target = makeUninitializedCallSite(type);
    // }

    CallSite(MethodHandle target) {
        target.type();
        this.target = target;
    }

    public MethodType type() {
        return target.type();
    }

    public abstract MethodHandle getTarget();

    public abstract void setTarget(MethodHandle newTarget);

    public abstract MethodHandle dynamicInvoker();
}
