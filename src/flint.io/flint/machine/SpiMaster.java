package flint.machine;

import java.io.IOException;

public class SpiMaster implements InputPort, OutputPort {
    private String spiName;
    private int spiId = -1;
    private int mode;
    private int speed = -2; /* Use default */
    private int mosi = -2;  /* Use default */
    private int miso = -2;  /* Use default */
    private int clk = -2;   /* Use default */
    private int cs = -2;    /* Use default */

    public SpiMaster(String spi) {
        this.spiName = spi;
        this.mode = SpiDataMode.MSB_MODE0.value;
    }

    public SpiMaster(String spi, int speed) {
        this(spi, speed, SpiDataMode.MSB_MODE0);
    }

    public SpiMaster(String spi, int speed, SpiDataMode dataMode) {
        this.spiName = spi;
        this.speed = speed;
        this.mode = dataMode.value;
    }

    @Override
    public native SpiMaster open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public native int getSpeed() throws IOException;

    public SpiMaster setSpeed(int speed) {
        checkStateBeforeConfig();
        this.speed = speed;
        return this;
    }

    public SpiDataMode getDataMode() {
        return SpiDataMode.fromValue(mode & 0x07);
    }

    public SpiMaster setDataMode(SpiDataMode dataMode) {
        checkStateBeforeConfig();
        synchronized(this) {
            mode = (mode & 0xFFFFFFF8) | dataMode.value;
        }
        return this;
    }

    public boolean getCsLevel() {
        return (mode & 0x08) != 0;
    }

    public SpiMaster setCsLevel(boolean level) {
        checkStateBeforeConfig();
        synchronized(this) {
            if(level)
                mode |= 0x08;
            else
                mode &= ~0x08;
        }
        return this;
    }

    public int getMosiPin() {
        return mosi;
    }

    public SpiMaster setMosiPin(int pin) {
        checkStateBeforeConfig();
        mosi = pin;
        return this;
    }

    public int getMisoPin() {
        return miso;
    }

    public SpiMaster setMisoPin(int pin) {
        checkStateBeforeConfig();
        miso = pin;
        return this;
    }

    public int getClkPin() {
        return clk;
    }

    public SpiMaster setClkPin(int pin) {
        checkStateBeforeConfig();
        clk = pin;
        return this;
    }

    public int getCsPin() {
        return cs;
    }

    public SpiMaster setCsPin(int pin) {
        checkStateBeforeConfig();
        cs = pin;
        return this;
    }

    @Override
    public native int read() throws IOException;

    @Override
    public int read(byte[] b) throws IOException {
        return readWrite(null, 0, b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int count) throws IOException {
        return readWrite(null, 0, b, off, count);
    }

    @Override
    public native void write(int b) throws IOException;

    @Override
    public void write(byte[] b) throws IOException {
        readWrite(b, 0, null, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int count) throws IOException {
        readWrite(b, off, null, 0, count);
    }

    public int readWrite(byte[] tx, byte[] rx, int count) throws IOException {
        return readWrite(tx, 0, rx, 0, count);
    }

    public native int readWrite(byte[] tx, int txOff, byte[] rx, int rxOff, int count) throws IOException;
}
