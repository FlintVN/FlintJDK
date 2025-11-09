package flint.machine;

import java.io.IOException;

public class SpiMaster implements CommPort {
    private static final int MOSI_PIN_INDEX = 0;
    private static final int MISO_PIN_INDEX = 1;
    private static final int CLK_PIN_INDEX  = 2;
    private static final int CS_PIN_INDEXX  = 3;

    private int spiId;
    private int mode;
    private int speed;
    private short[] pins = new short[4];

    private native int initInstance(String spi);

    public SpiMaster(String spi) {
        spiId = initInstance(spi);
    }

    @Override
    public native void open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public native int getSpeed() throws IOException;

    public void setSpeed(int speed) {
        checkStateBeforeConfig();
        this.speed = speed;
    }

    public SpiDataMode getDataMode() {
        return switch(this.mode & 0x07) {
            case 0 -> SpiDataMode.MSB_MODE0;
            case 1 -> SpiDataMode.MSB_MODE1;
            case 2 -> SpiDataMode.MSB_MODE2;
            case 3 -> SpiDataMode.MSB_MODE3;
            case 4 -> SpiDataMode.LSB_MODE0;
            case 5 -> SpiDataMode.LSB_MODE1;
            case 6 -> SpiDataMode.LSB_MODE2;
            default -> SpiDataMode.LSB_MODE3;
        };
    }

    public void setDataMode(SpiDataMode dataMode) {
        checkStateBeforeConfig();
        int m = switch(dataMode) {
            case MSB_MODE0 -> 0;
            case MSB_MODE1 -> 1;
            case MSB_MODE2 -> 2;
            case MSB_MODE3 -> 3;
            case LSB_MODE0 -> 4;
            case LSB_MODE1 -> 5;
            case LSB_MODE2 -> 6;
            default -> 7;
        };
        this.mode = (this.mode & 0xFFFFFFF8) | m;
    }

    public boolean getCsLevel() {
        return (this.mode & 0x08) != 0;
    }

    public void setCsLevel(boolean level) {
        checkStateBeforeConfig();
        if(level)
            this.mode |= 0x08;
        else
            this.mode &= ~0x08;
    }

    public int getMosiPin() {
        return pins[MOSI_PIN_INDEX];
    }

    public void setMosiPin(int pin) {
        checkStateBeforeConfig();
        pins[MOSI_PIN_INDEX] = (short)pin;
    }

    public int getMisoPin() {
        return pins[MISO_PIN_INDEX];
    }

    public void setMisoPin(int pin) {
        checkStateBeforeConfig();
        pins[MISO_PIN_INDEX] = (short)pin;
    }

    public int getClkPin() {
        return pins[CLK_PIN_INDEX];
    }

    public void setClkPin(int pin) {
        checkStateBeforeConfig();
        pins[CLK_PIN_INDEX] = (short)pin;
    }

    public int getCsPin() {
        return pins[CS_PIN_INDEXX];
    }

    public void setCsPin(int pin) {
        checkStateBeforeConfig();
        pins[CS_PIN_INDEXX] = (short)pin;
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
