package java.lang;

public class IllegalCallerException extends RuntimeException {
    public IllegalCallerException() {
        super();
    }

    public IllegalCallerException(String s) {
        super(s);
    }

    public IllegalCallerException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalCallerException(Throwable cause) {
        super(cause);
    }
}
