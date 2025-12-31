package flint.machine;

import java.io.IOException;
import java.util.Arrays;

public class Can implements InputPort, OutputPort {
    private static final int UNDEFINED_ID = -1;
    private static final int DEFAULT_PIN = -2;

    private String name;
    private int id = UNDEFINED_ID;
    private int baudRate = 500_000;
    private int rxPin = DEFAULT_PIN;
    private int txPin = DEFAULT_PIN;

    private GeneralConfig generalConfig = new GeneralConfig();
    private TimingConfig timingConfig = TimingConfig.fromBaudRate(500_000);
    private FilterConfig filterConfig = FilterConfig.acceptAll();

    private static final byte[] SINGLE_BYTE_BUFFER = new byte[1];

    public Can(String name, int id, int baudRate) {
        this.name = name;
        this.id = id;
        this.baudRate = baudRate;

        this.timingConfig = TimingConfig.fromBaudRate(baudRate);
    }

    @Override
    public native Can open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    public native void start() throws IOException;
    public native void stop() throws IOException;
    public native void recover() throws IOException;

    public native void configureAlerts(long alertsMask) throws IOException;
    public native long readAlerts(long timeoutMs) throws IOException;

    public native StatusInfo getStatusInfo() throws IOException;

    public native void clearTxQueue() throws IOException;
    public native void clearRxQueue() throws IOException;

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

        if (count <= 64) {
            writeSmall(b, off, count);
        }
        else {
            byte[] temp = new byte[count];
            System.arraycopy(b, off, temp, 0, count);
            write(temp);
        }
    }

    private static void validateWriteParameters(byte[] b, int off, int count) {
        if (b == null) throw new NullPointerException("b");
        if (off < 0 || count < 0 || off + count > b.length) {
            throw new IndexOutOfBoundsException("Invalid parameters: offset=" + off + ", count=" + count + ", array length=" + b.length);
        }
    }

    private void writeSmall(byte[] b, int off, int count) throws IOException {
        if (count == 1 && off == 0) {
            write(b[0]);
        }
        else if (count <= 8) {
            byte[] smallBuffer = new byte[count];
            System.arraycopy(b, off, smallBuffer, 0, count);
            write(smallBuffer);
        }
        else {
            synchronized (SINGLE_BYTE_BUFFER) {
                if (count <= SINGLE_BYTE_BUFFER.length) {
                    System.arraycopy(b, off, SINGLE_BYTE_BUFFER, 0, count);
                    write(SINGLE_BYTE_BUFFER, 0, count);
                }
                else {
                    byte[] temp = new byte[count];
                    System.arraycopy(b, off, temp, 0, count);
                    write(temp);
                }
            }
        }
    }

    @Override
    public int read() throws IOException {
        byte[] frame = readFrame(/*timeoutMs*/ 0);
        return (frame == null || frame.length == 0) ? -1 : (frame[0] & 0xFF);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b == null ? 0 : b.length);
    }

    @Override
    public int read(byte[] b, int off, int count) throws IOException {
        if (b == null) throw new NullPointerException("b");
        if (off < 0 || count < 0 || off + count > b.length) throw new IndexOutOfBoundsException();
        byte[] frame = readFrame(0);
        if (frame == null) return -1;
        int n = Math.min(count, frame.length);
        System.arraycopy(frame, 0, b, off, n);
        return n;
    }

    public native byte[] readFrame(long timeoutMs) throws IOException;

    public native CanMessage readMessage(long timeoutMs) throws IOException;


    private void checkStateBeforeConfig() {
        if (isOpen()) throw new IllegalStateException("Cannot configure while CAN is open");
    }

    public String getName() { return name; }
    public Can setName(String name) { checkStateBeforeConfig(); this.name = name; return this; }

    public int getId() { return id; }
    public Can setId(int id) { checkStateBeforeConfig(); this.id = id; return this; }

    public int getBaudRate() { return baudRate; }
    public Can setBaudRate(int baudRate) {
        checkStateBeforeConfig();
        this.baudRate = baudRate;
        this.timingConfig = TimingConfig.fromBaudRate(baudRate);
        return this;
    }

    public int getRxPin() { return rxPin; }
    public Can setRxPin(int rxPin) {
        checkStateBeforeConfig();
        this.rxPin = rxPin;
        this.generalConfig.rxPin = rxPin;
        return this;
    }

    public int getTxPin() { return txPin; }
    public Can setTxPin(int txPin) {
        checkStateBeforeConfig();
        this.txPin = txPin;
        this.generalConfig.txPin = txPin;
        return this;
    }

    public GeneralConfig getGeneralConfig() { return generalConfig; }
    public TimingConfig getTimingConfig() { return timingConfig; }
    public FilterConfig getFilterConfig() { return filterConfig; }

    public Can setGeneralConfig(GeneralConfig cfg) {
        checkStateBeforeConfig();
        this.generalConfig = (cfg != null) ? cfg : new GeneralConfig();
        return this;
    }

    public Can setTimingConfig(TimingConfig cfg) {
        checkStateBeforeConfig();
        this.timingConfig = (cfg != null) ? cfg : TimingConfig.fromBaudRate(this.baudRate);
        return this;
    }

    public Can setFilterConfig(FilterConfig cfg) {
        checkStateBeforeConfig();
        this.filterConfig = (cfg != null) ? cfg : FilterConfig.acceptAll();
        return this;
    }

    public static class GeneralConfig {
        public int txPin = DEFAULT_PIN;
        public int rxPin = DEFAULT_PIN;
        public Mode mode = Mode.SELF_TEST;
        public int txQueueLen = 32;
        public int rxQueueLen = 32;
        public long alertsEnabled = 0L;
        public boolean autoBusOffRecovery = true;

        public enum Mode {
            NORMAL,
            NO_ACK,
            LISTEN_ONLY,
            SELF_TEST
        }

        public GeneralConfig() {}
    }

    public static class TimingConfig {
        public int brp;
        public int tseg1;
        public int tseg2;
        public int sjw;

        public TimingConfig() {}

        public static TimingConfig fromBaudRate(int baudRate) {
            TimingConfig t = new TimingConfig();
            t.brp = 10;
            t.tseg1 = 13;
            t.tseg2 = 2;
            t.sjw = 1;
            return t;
        }
    }

    public static class FilterConfig {
        public int acceptanceCode = 0x00000000;
        public int acceptanceMask = 0xFFFFFFFF;
        public boolean singleFilter = true;

        public FilterConfig() {}

        public static FilterConfig acceptAll() {
            FilterConfig f = new FilterConfig();
            f.acceptanceCode = 0;
            f.acceptanceMask = 0;
            return f;
        }


    }

    public static class StatusInfo {
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

        public StatusInfo() {}
    }

    public static class CanMessage {
        public int id;
        public boolean extended;
        public boolean rtr;
        public int dlc;
        public byte[] data;

        public CanMessage() {}

        public static CanMessage of(int id, byte[] payload) {
            return of(id, false, false, payload);
        }

        public static CanMessage of(int id, boolean extended, boolean rtr, byte[] payload) {
            CanMessage m = new CanMessage();
            m.id = id;
            m.extended = extended;
            m.rtr = rtr;
            m.dlc = payload == null ? 0 : Math.min(payload.length, 8);
            m.data = (payload == null) ? new byte[0] : Arrays.copyOf(payload, m.dlc);
            return m;
        }


    }

    public native void writeMessage(CanMessage msg, long timeoutMs) throws IOException;
    public native CanMessage receiveMessage(long timeoutMs) throws IOException;

    public void write(byte[] b, long timeoutMs) throws IOException {
        CanMessage msg = CanMessage.of(0, b);
        writeMessage(msg, timeoutMs);
    }
}
