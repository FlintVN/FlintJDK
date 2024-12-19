package java.lang;

public class Thread implements Runnable {
    private final Runnable task;
    private volatile int threadStatus;

    public Thread(Runnable task) {
        this.task = task;
    }

    @Override
    public void run() {
        if(task != null)
            task.run();
    }

    private native void start0();

    private static native void yield0();

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

    public void interrupt() {
        interrupt0();
    }

    private void exit() {
        // TODO
    }
}
