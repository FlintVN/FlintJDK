package java.util;
// import java.util.Date;

public class Timer {
    private static int nextSerialNumber = 0;
    private final TaskQueue queue = new TaskQueue();
    private final TimerThread thread = new TimerThread(queue);

    private static synchronized int serialNumber() {
        return nextSerialNumber++;
    }

    public Timer() {
        this("Timer-" + serialNumber());
    }

    public Timer(boolean isDaemon) {
        this("Timer-" + serialNumber(), isDaemon);
    }

    public Timer(String name) {
        thread.setName(name);
        thread.start();
    }

    public Timer(String name, boolean isDaemon) {
        thread.setName(name);
        thread.setDaemon(isDaemon);
        thread.start();
    }

    public void schedule(TimerTask task, long delay) {
        if(delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        sched(task, System.currentTimeMillis() + delay, 0);
    }

    // TODO
    // public void schedule(TimerTask task, Date time) {
    //     sched(task, time.getTime(), 0);
    // }

    public void schedule(TimerTask task, long delay, long period) {
        if(delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if(period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, System.currentTimeMillis() + delay, -period);
    }

    // TODO
    // public void schedule(TimerTask task, Date firstTime, long period) {
    //     if(period <= 0)
    //         throw new IllegalArgumentException("Non-positive period.");
    //     sched(task, firstTime.getTime(), -period);
    // }

    public void scheduleAtFixedRate(TimerTask task, long delay, long period) {
        if(delay < 0)
            throw new IllegalArgumentException("Negative delay.");
        if(period <= 0)
            throw new IllegalArgumentException("Non-positive period.");
        sched(task, System.currentTimeMillis() + delay, period);
    }

    // TODO
    // public void scheduleAtFixedRate(TimerTask task, Date firstTime, long period) {
    //     if(period <= 0)
    //         throw new IllegalArgumentException("Non-positive period.");
    //     sched(task, firstTime.getTime(), period);
    // }

    private void sched(TimerTask task, long time, long period) {
        if(time < 0)
            throw new IllegalArgumentException("Illegal execution time.");

        if(Math.abs(period) > (Long.MAX_VALUE >> 1))
            period >>= 1;

        synchronized(queue) {
            if(!thread.newTasksMayBeScheduled)
                throw new IllegalStateException("Timer already cancelled.");

            synchronized(task.lock) {
                if(task.state != TimerTask.VIRGIN)
                    throw new IllegalStateException("Task already scheduled or cancelled");
                task.nextExecutionTime = time;
                task.period = period;
                task.state = TimerTask.SCHEDULED;
            }

            queue.add(task);
            if(queue.getMin() == task)
                queue.notify();
        }
    }

    public void cancel() {
        synchronized(queue) {
            thread.newTasksMayBeScheduled = false;
            queue.clear();
            queue.notify();
        }
    }

    public int purge() {
        int result = 0;
        synchronized(queue) {
            for(int i = queue.size(); i > 0; i--) {
                if(queue.get(i).state == TimerTask.CANCELLED) {
                    queue.quickRemove(i);
                    result++;
                }
            }

            if(result != 0)
                queue.heapify();
        }

        return result;
    }
}

class TimerThread extends Thread {
    boolean newTasksMayBeScheduled = true;
    private TaskQueue queue;

    TimerThread(TaskQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            mainLoop();
        }
        finally {
            synchronized(queue) {
                newTasksMayBeScheduled = false;
                queue.clear();
            }
        }
    }

    private void mainLoop() {
        while(true) {
            try {
                TimerTask task;
                boolean taskFired;
                synchronized(queue) {
                    while(queue.isEmpty() && newTasksMayBeScheduled)
                        queue.wait();
                    if(queue.isEmpty())
                        break;

                    long currentTime, executionTime;
                    task = queue.getMin();
                    synchronized(task.lock) {
                        if(task.state == TimerTask.CANCELLED) {
                            queue.removeMin();
                            continue;
                        }
                        currentTime = System.currentTimeMillis();
                        executionTime = task.nextExecutionTime;
                        if(taskFired = (executionTime <= currentTime)) {
                            if(task.period == 0) {
                                queue.removeMin();
                                task.state = TimerTask.EXECUTED;
                            }
                            else
                                queue.rescheduleMin(task.period<0 ? currentTime   - task.period : executionTime + task.period);
                        }
                    }
                    if(!taskFired)
                        queue.wait(executionTime - currentTime);
                }
                if(taskFired)
                    task.run();
            }
            catch(InterruptedException e) {

            }
        }
    }
}

class TaskQueue {
    private TimerTask[] queue = new TimerTask[128];
    private int size = 0;

    int size() {
        return size;
    }

    void add(TimerTask task) {
        if(size + 1 == queue.length)
            queue = Arrays.copyOf(queue, 2*queue.length);

        queue[++size] = task;
        fixUp(size);
    }

    TimerTask getMin() {
        return queue[1];
    }

    TimerTask get(int i) {
        return queue[i];
    }

    void removeMin() {
        queue[1] = queue[size];
        queue[size--] = null;
        fixDown(1);
    }

    void quickRemove(int i) {
        assert i <= size;

        queue[i] = queue[size];
        queue[size--] = null;
    }

    void rescheduleMin(long newTime) {
        queue[1].nextExecutionTime = newTime;
        fixDown(1);
    }

    boolean isEmpty() {
        return size == 0;
    }

    void clear() {
        for(int i = 1; i <= size; i++)
            queue[i] = null;

        size = 0;
    }

    private void fixUp(int k) {
        while(k > 1) {
            int j = k >> 1;
            if(queue[j].nextExecutionTime <= queue[k].nextExecutionTime)
                break;
            TimerTask tmp = queue[j];  queue[j] = queue[k]; queue[k] = tmp;
            k = j;
        }
    }

    private void fixDown(int k) {
        int j;
        while((j = k << 1) <= size && j > 0) {
            if(j < size && queue[j].nextExecutionTime > queue[j + 1].nextExecutionTime)
                j++;
            if(queue[k].nextExecutionTime <= queue[j].nextExecutionTime)
                break;
            TimerTask tmp = queue[j];  queue[j] = queue[k]; queue[k] = tmp;
            k = j;
        }
    }

    void heapify() {
        for(int i = size / 2; i >= 1; i--)
            fixDown(i);
    }
}
