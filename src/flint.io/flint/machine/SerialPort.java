package flint.machine;

import java.io.IOException;

public class SerialPort implements InputPort, OutputPort {
    private String portName;
    private int portId = -1;
    private int baudrate;
    private int stopBits;
    private int parity;
    private int dataBits = 8;
    private int txPin = -2; /* Use default */
    private int rxPin = -2; /* Use default */

    public SerialPort(String port) {
        this(port, 9600);
    }

    public SerialPort(String port, int baudrate) {
        this.portName = port;
        this.baudrate = baudrate;
        this.stopBits = StopBits.ONE.value;
        this.parity = Parity.NONE.value;
    }

    @Override
    public native SerialPort open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public native int getBaudrate() throws IOException;

    public SerialPort setBaudrate(int baudrate) {
        checkStateBeforeConfig();
        this.baudrate = baudrate;
        return this;
    }

    public StopBits getStopBits() {
        return StopBits.fromValue(stopBits);
    }

    public SerialPort setStopBits(StopBits stopBits) {
        checkStateBeforeConfig();
        this.stopBits = stopBits.value;
        return this;
    }

    public Parity getParity() {
        return Parity.fromValue(parity);
    }

    public SerialPort setParity(Parity parity) {
        checkStateBeforeConfig();
        this.parity = parity.value;
        return this;
    }

    public int getDataBits() {
        return dataBits;
    }

    public SerialPort setDataBits(int dataBits) {
        checkStateBeforeConfig();
        if(5 <= dataBits && dataBits <= 8)
            this.dataBits = dataBits;
        else
            throw new IllegalArgumentException("Argument must be between 5 and 8");
        return this;
    }

    public int getTxPin() {
        return txPin;
    }

    public SerialPort setTxPin(int pin) {
        checkStateBeforeConfig();
        txPin = pin;
        return this;
    }

    public int getRxPin() {
        return rxPin;
    }

    public SerialPort setRxPin(int pin) {
        checkStateBeforeConfig();
        rxPin = pin;
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
    public native void write(int b) throws IOException;

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public native void write(byte[] b, int off, int count) throws IOException;
}
