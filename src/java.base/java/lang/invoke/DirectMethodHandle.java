package java.lang.invoke;

final class DirectMethodHandle extends MethodHandle {
    private DirectMethodHandle(MethodType type) {
        super(type);
    }
}
