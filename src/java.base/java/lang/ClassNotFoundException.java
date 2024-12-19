package java.lang;

public class ClassNotFoundException extends ReflectiveOperationException {
    public ClassNotFoundException() {
        super();
    }

    public ClassNotFoundException(String s) {
        super(s, null);
    }

    public ClassNotFoundException(String s, Throwable ex) {
        super(s, ex);
    }

    public Throwable getException() {
        return super.getCause();
    }
}
