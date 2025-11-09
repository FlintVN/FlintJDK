package flint.machine;

public class Pin {
    final int pin;

    private static native void setMode(int pin, int mode);

    public native boolean read();

    public native void write(boolean level);

    public native void set();

    public native void reset();

    public native void toggle();

    public Pin(int pin) {
        this.pin = pin;
    }

    @SuppressWarnings("this-escape")
    public Pin(int pin, PinMode mode) {
        this.pin = pin;
        setMode(mode);
    }

    public Pin setMode(PinMode mode) {
        int m = switch(mode) {
            case INPUT -> 0;
            case OUTPUT -> 1;
            case INPUT_PULL_UP -> 2;
            case INPUT_PULL_DOWN -> 3;
            default -> 4;
        };
        Pin.setMode(pin, m);
        return this;
    }
}
