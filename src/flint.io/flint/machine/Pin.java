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
        Pin.setMode(pin, mode.value);
        return this;
    }
}
