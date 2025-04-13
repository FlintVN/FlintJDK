package java.io;

public abstract class ObjectStreamException extends IOException {
    protected ObjectStreamException(String message) {
        super(message);
    }

    protected ObjectStreamException(String message, Throwable cause) {
        super(message, cause);
    }

    protected ObjectStreamException() {
        super();
    }

    protected ObjectStreamException(Throwable cause) {
        super(cause);
    }
}
