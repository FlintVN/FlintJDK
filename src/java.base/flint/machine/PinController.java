package flint.machine;

public abstract class PinController {
    public PinController() {

    }

    public abstract boolean read();

    public abstract void write(boolean level);

    public void set() {
        write(true);
    }

    public void reset() {
        write(false);
    }
}
