package java.lang;

public class Throwable {
    private String detailMessage;
    private Throwable cause = this;
    private int suppressedCount;
    private Throwable[] suppressedExceptions;

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

    protected Throwable(String message, Throwable cause, boolean enableSuppression) {
        detailMessage = message;
        this.cause = cause;
        if(!enableSuppression)
            suppressedCount = -1;
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

    private void ensureCapacity() {
        if(suppressedExceptions == null)
            suppressedExceptions = new Throwable[8];
        int oldCapacity = suppressedExceptions.length;
        if((suppressedCount + 1) > oldCapacity) {
            int newCapacity = oldCapacity + 8;
            Throwable[] buff = new Throwable[newCapacity];
            System.arraycopy(suppressedExceptions, 0, buff, 0, suppressedCount);
            suppressedExceptions = buff;
        }
    }

    public final synchronized void addSuppressed(Throwable exception) {
        if(exception == this)
            throw new IllegalArgumentException("Self-suppression not permitted", exception);
        if(exception == null)
            throw new NullPointerException("Cannot suppress a null exception");
        if(suppressedCount == -1)   /* Suppressed exceptions not recorded */
            return;
        ensureCapacity();
        suppressedExceptions[suppressedCount] = exception;
        suppressedCount++;
    }

    public final synchronized Throwable[] getSuppressed() {
        if(suppressedCount <= 0)
            return new Throwable[0];
        else {
            Throwable[] ret = new Throwable[suppressedCount];
            System.arraycopy(suppressedExceptions, 0, ret, 0, suppressedCount);
            return ret;
        }
    }
}
