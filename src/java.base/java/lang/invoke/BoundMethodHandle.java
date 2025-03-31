package java.lang.invoke;

final class BoundMethodHandle extends MethodHandle {
    byte[] posArray;
    Object[] values;

    private BoundMethodHandle(MethodType type) {
        super(type);
    }
}
