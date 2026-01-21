package flint.io;

public class TouchPad {
    private int handle;
    private int threshold = -1;

    private native int initTouchPad(int pin);

    public TouchPad(int pin) {
        this.handle = initTouchPad(pin);
    }

    public native int read();

    public native boolean isTouched();

    public int getThreshold() {
        return threshold;
    }

    public TouchPad setThreshold(int threshold) {
        this.threshold = threshold;
        return this;
    }
}
