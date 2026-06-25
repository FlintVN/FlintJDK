package java.lang;

import jdk.internal.vm.annotation.IntrinsicCandidate;

public final class Object {
    public Object() {

    }

    public final native Class<?> getClass();

    public native int hashCode();

    protected native Object clone() throws CloneNotSupportedException;

    public boolean equals(Object obj) {
        return (this == obj);
    }

    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }

    @IntrinsicCandidate
    public final native void notify();

    @IntrinsicCandidate
    public final native void notifyAll();

    private final native void wait0(long timeoutMillis) throws InterruptedException;

    public final void wait(long timeoutMillis) throws InterruptedException {
        wait0(timeoutMillis);
    }

    public final void wait() throws InterruptedException {
        wait(0);
    }

    public final void wait(long timeoutMillis, int nanos) throws InterruptedException {
        if(timeoutMillis < 0)
            throw new IllegalArgumentException("timeoutMillis value is negative");
        if(nanos < 0 || nanos > 999999)
            throw new IllegalArgumentException("nanosecond timeout value out of range");
        if(nanos > 0 && timeoutMillis < Long.MAX_VALUE)
            timeoutMillis++;
        wait(timeoutMillis);
    }
}
