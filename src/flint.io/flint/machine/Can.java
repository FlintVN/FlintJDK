package flint.machine;

import java.io.IOException;
import java.util.Arrays;

public class Can implements InputPort, OutputPort {

    private static final int UNDEFINED_ID = -1;
    private static final int DEFAULT_PIN = -2;

    /* ================= USER CONFIG ================= */

    private String name;
    private int id = UNDEFINED_ID;
    private int baudRate = 500_000;
    private int rxPin = DEFAULT_PIN;
    private int txPin = DEFAULT_PIN;
    private int sjw = 0;
    private int samplePoint = 0;

    /* ================= NATIVE STATE ================= */

    /**
     * Native TWAI node handle (C pointer cast to long).
     * 0 = closed
     */
    private long nativeHandle = 0;

    private static final byte[] SINGLE_BYTE_BUFFER = new byte[1];

    /* ================= CONSTRUCTOR ================= */

    public Can(String name, int id, int baudRate) {
        this.name = name;
        this.id = id;
        this.baudRate = baudRate;
    }

    /* ================= NATIVE API ================= */

    @Override
    public native Can open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public boolean isOpen() {
        return nativeHandle != 0;
    }

    public native void start() throws IOException;
    public native void stop() throws IOException;
    public native void recover() throws IOException;

    public native void configureAlerts(long alertsMask) throws IOException;
    public native long readAlerts(long timeoutMs) throws IOException;

    public native StatusInfo getStatusInfo() throws IOException;

    public native void clearTxQueue() throws IOException;
    public native void clearRxQueue() throws IOException;

    /* ================= WRITE ================= */

    @Override
    public void write(int b) throws IOException {
        synchronized (SINGLE_BYTE_BUFFER) {
            SINGLE_BYTE_BUFFER[0] = (byte) (b & 0xFF);
            write(SINGLE_BYTE_BUFFER);
        }
    }

    @Override
    public native void write(byte[] b) throws IOException;

    @Override
    public void write(byte[] b, int off, int count) throws IOException {
        validateWriteParameters(b, off, count);
        if (count == 0) return;

        if (count <= 8) {
            byte[] tmp = new byte[count];
            System.arraycopy(b, off, tmp, 0, count);
            write(tmp);
        } else {
            byte[] tmp = new byte[count];
            System.arraycopy(b, off, tmp, 0, count);
            write(tmp);
        }
    }

    private static void validateWriteParameters(byte[] b, int off, int count) {
        if (b == null) throw new NullPointerException("b");
        if (off < 0 || count < 0 || off + count > b.length) {
            throw new IndexOutOfBoundsException();
        }
    }

    /* ================= READ ================= */

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
    public int read(byte[] b, int off, int count) throws IOException {
        if (b == null) throw new NullPointerException("b");
        if (off < 0 || count < 0 || off + count > b.length)
            throw new IndexOutOfBoundsException();

        byte[] frame = readFrame(0);
        if (frame == null) return -1;

        int n = Math.min(count, frame.length);
        System.arraycopy(frame, 0, b, off, n);
        return n;
    }

    public native byte[] readFrame(long timeoutMs) throws IOException;

    public native CanMessage readMessage(long timeoutMs) throws IOException;

    /* ================= CONFIG SAFETY ================= */

    private void checkStateBeforeConfig() {
        if (isOpen())
            throw new IllegalStateException("Cannot configure while CAN is open");
    }

    /* ================= GET / SET ================= */

    public String getName() { return name; }
    public Can setName(String name) {
        checkStateBeforeConfig();
        this.name = name;
        return this;
    }

    public int getId() { return id; }
    public Can setId(int id) {
        checkStateBeforeConfig();
        this.id = id;
        return this;
    }

    public int getBaudRate() { return baudRate; }
    public Can setBaudRate(int baudRate) {
        checkStateBeforeConfig();
        this.baudRate = baudRate;
        return this;
    }

    public int getRxPin() { return rxPin; }
    public Can setRxPin(int rxPin) {
        checkStateBeforeConfig();
        this.rxPin = rxPin;
        return this;
    }

    public int getTxPin() { return txPin; }
    public Can setTxPin(int txPin) {
        checkStateBeforeConfig();
        this.txPin = txPin;
        return this;
    }

    public int getSjw() { return sjw; }
    public Can setSjw(int sjw) {
        checkStateBeforeConfig();
        this.sjw = sjw;
        return this;
    }

    public int getSamplePoint() { return samplePoint; }
    public Can setSamplePoint(int samplePoint) {
        checkStateBeforeConfig();
        this.samplePoint = samplePoint;
        return this;
    }

    /* ================= STATUS ================= */

    public static class StatusInfo {
        public StatusInfo() {}   // explicit public ctor
        public int txErrorCounter;
        public int rxErrorCounter;
        public int txQueueFree;
        public int rxQueueFill;
        public State state;

        public enum State {
            STOPPED,
            RUNNING,
            BUS_OFF,
            RECOVERING
        }
    }

    /* ================= MESSAGE ================= */

    public static class CanMessage {
        public CanMessage() {}   // explicit public ctor
        public int id;
        public boolean extended;
        public boolean rtr;
        public int dlc;
        public byte[] data;

        public static CanMessage of(int id, byte[] payload) {
            CanMessage m = new CanMessage();
            m.id = id;
            m.extended = false;
            m.rtr = false;
            m.dlc = payload == null ? 0 : Math.min(payload.length, 8);
            m.data = payload == null ? new byte[0] : Arrays.copyOf(payload, m.dlc);
            return m;
        }
    }

    public native void writeMessage(CanMessage msg, long timeoutMs) throws IOException;
    public native CanMessage receiveMessage(long timeoutMs) throws IOException;
}
