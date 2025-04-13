package java.io;

public class InvalidObjectException extends ObjectStreamException {
    public InvalidObjectException(String reason) {
        super(reason);
    }

    public InvalidObjectException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
