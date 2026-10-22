package flint.net.www.protocol.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.FilterInputStream;

class FixedLengthInputStream extends FilterInputStream {
    private long remaining;
    private boolean closed;

    FixedLengthInputStream(InputStream in, long length) {
        super(in);
        this.remaining = length;
    }

    @Override
    public int read() throws IOException {
        if (closed || remaining <= 0)
            return -1;
        int c = in.read();
        if (c != -1)
            remaining--;
        return c;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (off < 0 || len < 0 || off > b.length - len)
            throw new IndexOutOfBoundsException();
        if (len == 0)
            return 0;
        if (closed || remaining <= 0)
            return -1;
        int toRead = (int) Math.min(len, remaining);
        int n = in.read(b, off, toRead);
        if (n > 0)
            remaining -= n;
        return n;
    }

    @Override
    public long skip(long n) throws IOException {
        if (closed || remaining <= 0 || n <= 0)
            return 0;
        long toSkip = Math.min(n, remaining);
        long skipped = in.skip(toSkip);
        if (skipped > 0)
            remaining -= skipped;
        return skipped;
    }

    @Override
    public int available() throws IOException {
        if (closed || remaining <= 0)
            return 0;
        return (int) Math.min(in.available(), remaining);
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            in.close();
        }
    }
}
