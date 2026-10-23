package java.io;

public class BufferedOutputStream extends FilterOutputStream {
    private static final int DEFAULT_MAX_BUFFER_SIZE = 8192;

    protected byte[] buf;
    protected int count;
    private final int maxBufSize;

    public BufferedOutputStream(OutputStream out) {
        this(out, DEFAULT_MAX_BUFFER_SIZE);
    }

    public BufferedOutputStream(OutputStream out, int size) {
        super(out);
        this.buf = new byte[size];
        this.maxBufSize = size;
    }

    private void flushBuffer() throws IOException {
        if (count > 0) {
            out.write(buf, 0, count);
            count = 0;
        }
    }

    private void growIfNeeded(int len) {
        int neededSize = count + len + 1;
        if (neededSize < 0)
            neededSize = Integer.MAX_VALUE;
        int bufSize = buf.length;
        if (neededSize > bufSize && bufSize < maxBufSize) {
            int newSize = Math.min(neededSize, maxBufSize);
            byte[] newBuf = new byte[newSize];
            System.arraycopy(buf, 0, newBuf, 0, bufSize);
            buf = newBuf;
        }
    }

    @Override
    public void write(int b) throws IOException {
        synchronized (this) {
            implWrite(b);
        }
    }

    private void implWrite(int b) throws IOException {
        growIfNeeded(1);
        if (count >= buf.length)
            flushBuffer();
        buf[count++] = (byte)b;
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        synchronized (this) {
            implWrite(b, off, len);
        }
    }

    private void implWrite(byte[] b, int off, int len) throws IOException {
        if (len >= maxBufSize) {
            flushBuffer();
            out.write(b, off, len);
            return;
        }
        growIfNeeded(len);
        if (len > buf.length - count)
            flushBuffer();
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    @Override
    public void flush() throws IOException {
        synchronized (this) {
            implFlush();
        }
    }

    private void implFlush() throws IOException {
        flushBuffer();
        out.flush();
    }
}
