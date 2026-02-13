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
            throw new IllegalStateException("Cannot change configuration while CAN is open");
        }
    }

    // ================= LIFECYCLE =================
    @Override
    public synchronized native Can open() throws IOException;

    @Override
    public synchronized native void close() throws IOException;

    @Override
    public native boolean isOpen();

    public native void start() throws IOException;

    public native void stop() throws IOException;

    public native void recover() throws IOException;

    // ================= STATUS & ALERTS =================
    public native CanStatus getStatus() throws IOException;

    public native void configureAlerts(long alertsMask) throws IOException;

    public native long readAlerts(long timeoutMs) throws IOException;

    public native void clearTxQueue() throws IOException;

    public native void clearRxQueue() throws IOException;

    // ================= DATA TRANSFER =================
    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte) b });
    }

    @Override
    public native void write(byte[] data) throws IOException;

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (b == null)
            throw new NullPointerException();
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }
        if (len == 0)
            return;

        byte[] tmp = Arrays.copyOfRange(b, off, off + Math.min(len, 8));
        write(tmp);
    }

    @Override
    public int read() throws IOException {
        byte[] frame = readFrame(0);
        return (frame == null || frame.length == 0) ? -1 : (frame[0] & 0xFF);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (b == null)
            throw new NullPointerException();
        if (off < 0 || len < 0 || off + len > b.length) {
            throw new IndexOutOfBoundsException();
        }

        byte[] frame = readFrame(0);
        if (frame == null)
            return -1;

        int n = Math.min(len, frame.length);
        System.arraycopy(frame, 0, b, off, n);
        return n;
    }

    public native byte[] readFrame(long timeoutMs) throws IOException;

    public native CanMessage readMessage(long timeoutMs) throws IOException;

    public native void writeMessage(CanMessage msg, long timeoutMs) throws IOException;

    public native CanMessage receiveMessage(long timeoutMs) throws IOException;

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