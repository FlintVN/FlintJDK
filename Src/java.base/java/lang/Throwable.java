package java.lang;

public class Throwable {
    private String detailMessage;
    private Throwable cause = this;

    public Throwable() {
        detailMessage = null;
    }

    public Throwable(String message) {
        detailMessage = message;
    }

    public Throwable(String message, Throwable cause) {
        detailMessage = message;
        this.cause = cause;
    }

    public Throwable(Throwable cause) {
        detailMessage = (cause == null ? null : cause.toString());
        this.cause = cause;
    }

    public String getMessage() {
        return detailMessage;
    }

    public String getLocalizedMessage() {
        return getMessage();
    }

    public synchronized Throwable getCause() {
        return (cause == this ? null : cause);
    }

    public synchronized Throwable initCause(Throwable cause) {
        if(this.cause != this)
            throw new IllegalStateException("Can't overwrite cause with " + ((cause != null) ? cause.toString() : "a null"), this);
        if(cause == this)
            throw new IllegalArgumentException("Self-causation not permitted", this);
        this.cause = cause;
        return this;
    }

    final void setCause(Throwable t) {
        this.cause = t;
    }

    public String toString() {
        String s = getClass().getName();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
