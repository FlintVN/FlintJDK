package flint.machine;

import java.io.IOException;
import java.util.Arrays;

public class Can implements InputPort, OutputPort {

    // ================= DEFAULTS & ENUMS =================
    private static final int DEFAULT_PIN = -2;

    public enum CanMode {
        NORMAL(0),
        NO_ACK(1),
        LISTEN_ONLY(2);

        private final int value;

        CanMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    // ================= USER CONFIG =================
    private final String name;
    private final int controllerId;
    private final int baudRate;

    private int rxPin = DEFAULT_PIN;
    private int txPin = DEFAULT_PIN;
    private int samplePoint = 750; // 75.0%
    private CanMode mode = CanMode.NORMAL;

    // ================= NATIVE STATE =================
    private long nativeHandle = 0;

    // ================= CONSTRUCTOR =================
    public Can(String name, int controllerId, int baudRate) {
        this.name = name;
        this.controllerId = controllerId;
        this.baudRate = baudRate;
    }

    // ================= CONFIGURATION =================
    public Can setTxPin(int pin) {
        checkConfigState();
        this.txPin = pin;
        return this;
    }

    public Can setRxPin(int pin) {
        checkConfigState();
        this.rxPin = pin;
        return this;
    }

    public Can setSamplePoint(int samplePoint) {
        checkConfigState();
        this.samplePoint = samplePoint;
        return this;
    }

    public Can setMode(CanMode mode) {
        checkConfigState();
        this.mode = mode;
        return this;
    }

    private void checkConfigState() {
        if (isOpen()) {
            throw new IllegalStateException("CAN is already open");
        }
    }

    // ================= LIFECYCLE =================
    @Override
    public synchronized native Can open() throws IOException;

    @Override
    public synchronized native void close() throws IOException;

    @Override
    public native boolean isOpen();

    public native void recover() throws IOException;

    // ================= STATUS & ALERTS =================
    public native CanStatus getStatus() throws IOException;

    public native void clearTxQueue() throws IOException;

    public native void clearRxQueue() throws IOException;

    // ================= DATA TRANSFER =================
    @Override
    public synchronized void write(int b) throws IOException {
        write(new byte[] { (byte) b }, 0, 1);
    }

    @Override
    public synchronized native void write(byte[] data, int off, int len) throws IOException;

    @Override
    public synchronized void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public synchronized int read() throws IOException {
        byte[] b = new byte[1];
        if (read(b, 0, 1) <= 0)
            return -1;
        return b[0] & 0xFF;
    }

    @Override
    public synchronized native int read(byte[] b, int off, int len) throws IOException;

    @Override
    public synchronized int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public native CanMessage readMessage(long timeoutMs) throws IOException;

    public native void writeMessage(CanMessage msg, long timeoutMs) throws IOException;

    // ================= GETTERS =================
    public String getName() {
        return name;
    }

    public int getControllerId() {
        return controllerId;
    }

    public int getBaudRate() {
        return baudRate;
    }

    public int getTxPin() {
        return txPin;
    }

    public int getRxPin() {
        return rxPin;
    }

    public int getSamplePoint() {
        return samplePoint;
    }

    public CanMode getMode() {
        return mode;
    }
}