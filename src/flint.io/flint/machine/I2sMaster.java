package flint.machine;

import java.io.IOException;

public class I2sMaster implements InputPort, OutputPort, AnalogInputStream, AnalogOutputStream {
    private String i2sName;
    private int i2sId = -1;
    private int mode = 0;
    private int sampleRate;
    private int bufferSize = -1;
    private int sck = -2;   /* Use default */
    private int ws = -2;    /* Use default */
    private int sd = -2;    /* Use default */
    private int mclk = -2;  /* Use default */

    public I2sMaster(String i2s) {
        this(i2s, 44100);
    }

    @SuppressWarnings("this-escape")
    public I2sMaster(String i2s, int sampleRate) {
        this.i2sName = i2s;
        this.sampleRate = sampleRate;
        setDataMode(I2sDataMode.STEREO);
        setDirection(I2sDirection.TX);
        setDataBits(16);
    }

    @Override
    public native I2sMaster open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public I2sDataMode getDataMode() {
        return I2sDataMode.fromValue(mode & 0x01);
    }

    public I2sMaster setDataMode(I2sDataMode mode) {
        checkStateBeforeConfig();
        synchronized(this) {
            this.mode = (this.mode & 0xFFFFFFFE) | mode.value;
        }
        return this;
    }

    public I2sDirection getDirection() {
        return I2sDirection.fromValue((mode >>> 1) & 0x01);
    }

    public I2sMaster setDirection(I2sDirection dir) {
        checkStateBeforeConfig();
        synchronized(this) {
            this.mode = (this.mode & 0xFFFFFFFD) | dir.value;
        }
        return this;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public I2sMaster setSampleRate(int sampleRate) {
        checkStateBeforeConfig();
        this.sampleRate = sampleRate;
        return this;
    }

    public int getDataBits() {
        return (mode >>> 2) & 0x3F;
    }

    public I2sMaster setDataBits(int dataBits) {
        checkStateBeforeConfig();
        if(dataBits == 8 || dataBits == 16 || dataBits == 24 || dataBits == 32) {
            synchronized(this) {
                mode = (mode & 0xFFFFFF03) | (dataBits << 2);
            }
        }
        else
            throw new IllegalArgumentException("Argument must be one in the value 8, 16, 24 or 32");
        return this;
    }

    public int getSckPin() {
        return sck;
    }

    public I2sMaster setSckPin(int pin) {
        checkStateBeforeConfig();
        this.sck = pin;
        return this;
    }

    public int getWsPin() {
        return ws;
    }

    public I2sMaster setWsPin(int pin) {
        checkStateBeforeConfig();
        this.ws = pin;
        return this;
    }

    public int getSdPin() {
        return sd;
    }

    public I2sMaster setSdPin(int pin) {
        checkStateBeforeConfig();
        this.sd = pin;
        return this;
    }

    public int getMclkPin() {
        return mclk;
    }

    public I2sMaster setMclkPin(int pin) {
        checkStateBeforeConfig();
        this.mclk = pin;
        return this;
    }

    @Override
    public native int read() throws IOException;
    
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public native int read(byte[] b, int off, int count) throws IOException;

    @Override
    public int read(short[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public native int read(short[] b, int off, int count) throws IOException;

    @Override
    public int read(int[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public native int read(int[] b, int off, int count) throws IOException;

    @Override
    public native void write(int b) throws IOException;

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public native void write(byte[] b, int off, int count) throws IOException;

    @Override
    public void write(short[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public native void write(short[] b, int off, int count) throws IOException;

    @Override
    public void write(int[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public native void write(int[] b, int off, int count) throws IOException;
}
