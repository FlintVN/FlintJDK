package flint.machine;

import java.io.IOException;

public class OneWire implements InputPort, OutputPort {
    private String name;
    private int id = -1;
    private int speedMode;
    private int pin = -2;    /* Use default */

    public OneWire(String name) {
        this(name, OneWireSpeed.STANDARD);
    }

    public OneWire(String name, OneWireSpeed speed) {
        this.name = name;
        this.speedMode = speed.value;
        this.pin = pin;
    }

    @Override
    public native OneWire open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public OneWireSpeed getSpeedMode() {
        return OneWireSpeed.fromValue(speedMode);
    }

    public OneWire setSpeedMode(OneWireSpeed speed) {
        checkStateBeforeConfig();
        speedMode = speed.value;
        return this;
    }

    public int getPin() {
        return pin;
    }

    public OneWire setPin(int pin) {
        checkStateBeforeConfig();
        this.pin = pin;
        return this;
    }

    public native void reset();

    @Override
    public native int read() throws IOException;

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public native int read(byte[] b, int off, int count) throws IOException;

    @Override
    public native void write(int b) throws IOException;

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public native void write(byte[] b, int off, int count) throws IOException;
}
