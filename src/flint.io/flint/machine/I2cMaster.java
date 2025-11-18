package flint.machine;

import java.io.IOException;

public class I2cMaster implements CommPort {
    private String i2cName;
    private int i2cId = -1;
    private int speed;
    private int devAddr = -1;
    private int sda = -2;   /* Use default */
    private int scl = -2;   /* Use default */

    public I2cMaster(String i2c) {
        this(i2c, 400000);
    }

    public I2cMaster(String i2c, int speed) {
        this.i2cName = i2c;
        this.speed = speed;
    }

    @Override
    public native I2cMaster open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public native int getSpeed() throws IOException;

    public I2cMaster setSpeed(int speed) {
        checkStateBeforeConfig();
        this.speed = speed;
        return this;
    }

    public int getDevAddr() {
        return devAddr;
    }

    public I2cMaster setdevAddr(int addr) {
        devAddr = addr;
        return this;
    }

    public int getSdaPin() {
        return sda;
    }

    public I2cMaster setSdaPin(int pin) {
        checkStateBeforeConfig();
        sda = pin;
        return this;
    }

    public int getSclPin() {
        return scl;
    }

    public I2cMaster setSclPin(int pin) {
        checkStateBeforeConfig();
        scl = pin;
        return this;
    }

    @Override
    public native int read() throws IOException;

    public int read(int devAddr) throws IOException {
        this.devAddr = devAddr;
        return read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(int devAddr, byte[] b) throws IOException {
        this.devAddr = devAddr;
        return read(b, 0, b.length);
    }

    @Override
    public native int read(byte[] b, int off, int count) throws IOException;

    public int read(int devAddr, byte[] b, int off, int count) throws IOException {
        this.devAddr = devAddr;
        return read(b, off, count);
    }

    @Override
    public native void write(int b) throws IOException;

    public void write(int devAddr, int b) throws IOException {
        this.devAddr = devAddr;
        write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(int devAddr, byte[] b) throws IOException {
        this.devAddr = devAddr;
        write(b, 0, b.length);
    }

    @Override
    public native void write(byte[] b, int off, int count) throws IOException;

    public void write(int devAddr, byte[] b, int off, int count) throws IOException {
        this.devAddr = devAddr;
        write(b, off, count);
    }

    public native int readMem(int memAddr) throws IOException;

    public int readMem(int memAddr, byte[] b) throws IOException {
        return readMem(memAddr, b, 0, b.length);
    }

    public native int readMem(int memAddr, byte[] b, int off, int count) throws IOException;

    public int readMem(int devAddr, int memAddr) throws IOException {
        this.devAddr = devAddr;
        return readMem(memAddr);
    }

    public int readMem(int devAddr, int memAddr, byte[] b) throws IOException {
        this.devAddr = devAddr;
        return readMem(memAddr, b, 0, b.length);
    }

    public int readMem(int devAddr, int memAddr, byte[] b, int off, int count) throws IOException {
        this.devAddr = devAddr;
        return readMem(memAddr, b, off, count);
    }

    public native void writeMem(int memAddr, int b) throws IOException;

    public void writeMem(int memAddr, byte[] b) throws IOException {
        writeMem(memAddr, b, 0, b.length);
    }

    public native void writeMem(int memAddr, byte[] b, int off, int count) throws IOException;

    public void writeMem(int devAddr, int memAddr, int b) throws IOException {
        this.devAddr = devAddr;
        writeMem(memAddr, b);
    }

    public void writeMem(int devAddr, int memAddr, byte[] b) throws IOException {
        this.devAddr = devAddr;
        writeMem(memAddr, b, 0, b.length);
    }

    public void writeMem(int devAddr, int memAddr, byte[] b, int off, int count) throws IOException {
        this.devAddr = devAddr;
        writeMem(memAddr, b, off, count);
    }
}
