package flint.machine;

public final class CanStatus {
    public int txErrorCounter;
    public int rxErrorCounter;
    public int txQueueFree;
    public int rxQueueFill;
    public State state;

    public enum State {
        STOPPED,
        RUNNING,
        BUS_OFF,
        RECOVERING
    }

    public CanStatus() {
    }

    @Override
    public String toString() {
        return "Status{state=" + state +
                ", txErr=" + txErrorCounter +
                ", rxErr=" + rxErrorCounter +
                ", txFree=" + txQueueFree +
                ", rxFill=" + rxQueueFill + "}";
    }
}