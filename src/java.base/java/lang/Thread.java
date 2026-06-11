package java.lang;

public class Thread implements Runnable {
    public static final int MIN_PRIORITY = 1;
    public static final int NORM_PRIORITY = 5;
    public static final int MAX_PRIORITY = 10;

    private volatile int handle;
    private volatile String name;
    volatile boolean interrupted;
    private final Runnable task;
    final int stackSize;
    volatile int priority;
    volatile boolean daemon;
    volatile int threadStatus;

    public Thread() {
        this(null, null, 0);
    }

    public Thread(Runnable task) {
        this(task, null, 0);
    }

    public Thread(Runnable task, String name, int stackSize) {
        this.name = name;
        this.task = task;
        this.stackSize = stackSize;
        this.priority = currentThread().priority;
        this.daemon = currentThread().daemon;
    }

    @Override
    public void run() {
        if(task != null)
            task.run();
    }

    private native void start0();

    private static native void yield0();

    private native void setPriority0(int newPriority);

    private native void interrupt0();

    public static native Thread currentThread();

    private static native void sleep0(long millis) throws InterruptedException;

    public void start() {
        synchronized(this) {
            if(threadStatus != 0)
                throw new IllegalThreadStateException();
            start0();
        }
    }

    public static void yield() {
        yield0();
    }

    public static void sleep(long millis) throws InterruptedException {
        if(millis < 0)
            throw new IllegalArgumentException("timeout value is negative");
        sleep0(millis);
    }

    private void exit() {
        // TODO
    }

    public void interrupt() {
        interrupted = true;
        interrupt0();
    }

    public static boolean interrupted() {
        return currentThread().getAndClearInterrupt();
    }

    public boolean isInterrupted() {
        return interrupted;
    }

    boolean getAndClearInterrupt() {
        boolean oldValue = interrupted;
        if(oldValue)
            interrupted = false;
        return oldValue;
    }

    public final boolean isAlive() {
        return handle != 0;
    }

    public final int threadId() {
        return handle;
    }

    public final void setPriority(int newPriority) {
        if(newPriority > MAX_PRIORITY || newPriority < MIN_PRIORITY)
            throw new IllegalArgumentException();
        setPriority0(priority = newPriority);
    }

    public final int getPriority() {
        return priority;
    }

    public final synchronized void setName(String name) {
        if(name == null)
            throw new NullPointerException("name cannot be null");
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    public final void setDaemon(boolean on) {
        if(isAlive())
            throw new IllegalThreadStateException();
        daemon = on;
    }

    public final boolean isDaemon() {
        return daemon;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Thread[#");
        sb.append(threadId());
        sb.append(",");
        sb.append(getName());
        sb.append(",");
        sb.append(getPriority());
        sb.append(",");
        sb.append("]");
        return sb.toString();
    }
}
