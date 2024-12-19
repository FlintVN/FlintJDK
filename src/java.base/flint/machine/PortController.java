package flint.machine;

public abstract class PortController {
    public PortController() {

    }

    public abstract int read();

    public abstract void write(int value);

    public void reset() {
        write(0);
    }
}
