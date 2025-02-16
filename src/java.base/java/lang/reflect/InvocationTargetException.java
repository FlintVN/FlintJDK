package java.lang.reflect;

public class InvocationTargetException extends ReflectiveOperationException {
    private Throwable target;

    protected InvocationTargetException() {
        super((Throwable)null);
    }

    public InvocationTargetException(Throwable target) {
        super((Throwable)null);
        this.target = target;
    }

    public InvocationTargetException(Throwable target, String s) {
        super(s, null);
        this.target = target;
    }

    public Throwable getTargetException() {
        return target;
    }

    @Override
    public Throwable getCause() {
        return target;
    }
}
