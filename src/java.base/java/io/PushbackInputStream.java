package java.io;

import java.util.Objects;

public class PushbackInputStream extends FilterInputStream {
    protected byte[] buf;
    protected int pos;

    private void ensureOpen() throws IOException {
        if(in == null)
            throw new IOException("Stream closed");
    }

    public PushbackInputStream(InputStream in, int size) {
        super(in);
        if(size <= 0)
            throw new IllegalArgumentException("size <= 0");
        this.buf = new byte[size];
        this.pos = size;
    }

    public PushbackInputStream(InputStream in) {
        this(in, 1);
    }

    public int read() throws IOException {
        ensureOpen();
        if(pos < buf.length)
            return buf[pos++] & 0xFF;
        return super.read();
    }

    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if(b == null)
            throw new NullPointerException();
        Objects.checkFromIndexSize(off, len, b.length);
        if(len == 0)
            return 0;

        int avail = buf.length - pos;
        if(avail > 0) {
            if(len < avail)
                avail = len;
            System.arraycopy(buf, pos, b, off, avail);
            pos += avail;
            off += avail;
            len -= avail;
        }
        if(len > 0) {
            len = super.read(b, off, len);
            if(len == -1)
                return avail == 0 ? -1 : avail;
            return avail + len;
        }
        return avail;
    }

    public void unread(int b) throws IOException {
        ensureOpen();
        if(pos == 0)
            throw new IOException("Push back buffer is full");
        buf[--pos] = (byte)b;
    }

    public void unread(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if(len > pos)
            throw new IOException("Push back buffer is full");
        pos -= len;
        System.arraycopy(b, off, buf, pos, len);
    }

    public void unread(byte[] b) throws IOException {
        unread(b, 0, b.length);
    }

    public int available() throws IOException {
        ensureOpen();
        int n = buf.length - pos;
        int avail = super.available();
        return n > (Integer.MAX_VALUE - avail) ? Integer.MAX_VALUE : n + avail;
    }

    public long skip(long n) throws IOException {
        ensureOpen();
        if(n <= 0)
            return 0;

        long pskip = buf.length - pos;
        if(pskip > 0) {
            if(n < pskip)
                pskip = n;
            pos += (int) pskip;
            n -= pskip;
        }
        if(n > 0)
            pskip += super.skip(n);
        return pskip;
    }

    public boolean markSupported() {
        return false;
    }

    public void mark(int readlimit) {

    }

    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public void close() throws IOException {
        synchronized(this) {
            implClose();
        }
    }

    private void implClose() throws IOException {
        if(in != null) {
            in.close();
            in = null;
            buf = null;
        }
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        Objects.requireNonNull(out, "out");
        ensureOpen();
        if(getClass() == PushbackInputStream.class) {
            int avail = buf.length - pos;
            if(avail > 0) {
                int length = buf.length - pos;
                byte[] buffer = new byte[length];
                System.arraycopy(buf, pos, buffer, 0, length);
                out.write(buffer);
                pos = buffer.length;
            }
            try {
                return Math.addExact(avail, in.transferTo(out));
            }
            catch(ArithmeticException ignore) {
                return Long.MAX_VALUE;
            }
        }
        else
            return super.transferTo(out);
    }
}
