package java.lang.invoke;

public class StringConcatException extends Exception {
    public StringConcatException(String msg) {
        super(msg);
    }

    public StringConcatException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
