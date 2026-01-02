package flint.machine;

import java.io.IOException;
import java.io.OutputStream;

public class BitStream implements OutputPort {
    private String name;
    private int id = -1;
    private int encoding;
    private int pin = -2;   /* Use default */
    private int[] timing;

    public BitStream(String name, int t0h, int t0l, int t1h, int t1l) {
        this(name, BitStreamEncoding.HIGH_LOW, t0h, t0l, t1h, t1l);
    }

    public BitStream(String name, BitStreamEncoding encoding, int t0h, int t0l, int t1h, int t1l) {
        this.name = name;
        this.encoding = encoding.value;
        this.timing = new int[] {t0h, t0l, t1h, t1l};
    }

    @Override
    public native BitStream open() throws IOException;

    @Override
    public native void close() throws IOException;

    @Override
    public native boolean isOpen();

    private void checkStateBeforeConfig() {
        if(isOpen())
            throw new IllegalStateException();
    }

    public BitStreamEncoding getEncoding() {
        return BitStreamEncoding.fromValue(encoding);
    }

    public BitStream setEncoding(BitStreamEncoding encoding) {
        checkStateBeforeConfig();
        this.encoding = encoding.value;
        return this;
    }

    public int getOutputPin() {
        return pin;
    }

    public BitStream setOutputPin(int pin) {
        checkStateBeforeConfig();
        this.pin = pin;
        return this;
    }

    public BitStream setTiming(int t0h, int t0l, int t1h, int t1l) {
        checkStateBeforeConfig();
        timing[0] = t0h;
        timing[1] = t0l;
        timing[2] = t1h;
        timing[3] = t1l;
        return this;
    }

    @Override
    public native void write(int b) throws IOException;

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public native void write(byte[] b, int off, int count) throws IOException;

    public OutputStream getOutputStream() {
        return new OutputPortStream(this);
    }
}
