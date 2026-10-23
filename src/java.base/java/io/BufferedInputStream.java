package java.io;

import jdk.internal.util.ArraysSupport;

public class BufferedInputStream extends FilterInputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;
    private static final byte[] EMPTY = new byte[0];

    private final int initialSize;

    protected volatile byte[] buf;
    protected int count;
    protected int pos;
    protected int markpos = -1;
    protected int marklimit;

    private InputStream getInIfOpen() throws IOException {
        InputStream input = in;
        if (input == null)
            throw new IOException("Stream closed");
        return input;
    }

    private byte[] getBufIfOpen(boolean allocateIfEmpty) throws IOException {
        byte[] buffer = buf;
        if (allocateIfEmpty && buffer == EMPTY) {
            synchronized(this) {
                if (buf == EMPTY) {
                    buffer = new byte[initialSize];
                    buf = buffer;
                }
            }
        }
        if (buffer == null)
            throw new IOException("Stream closed");
        return buffer;
    }

    private byte[] getBufIfOpen() throws IOException {
        return getBufIfOpen(true);
    }

    private void ensureOpen() throws IOException {
        if (buf == null) {
            throw new IOException("Stream closed");
        }
    }

    public BufferedInputStream(InputStream in) {
        this(in, DEFAULT_BUFFER_SIZE);
    }

    public BufferedInputStream(InputStream in, int size) {
        super(in);
        if (size <= 0)
            throw new IllegalArgumentException("Buffer size <= 0");
        initialSize = size;
        if (getClass() == BufferedInputStream.class)
            buf = EMPTY;
        else
            buf = new byte[size];
    }

    private void fill() throws IOException {
        byte[] buffer = getBufIfOpen();
        if (markpos == -1)
            pos = 0;
        else if (pos >= buffer.length) {
            if (markpos > 0) {
                int sz = pos - markpos;
                System.arraycopy(buffer, markpos, buffer, 0, sz);
                pos = sz;
                markpos = 0;
            }
            else if (buffer.length >= marklimit) {
                markpos = -1;
                pos = 0;
            }
            else {
                int nsz = ArraysSupport.newLength(pos, 1, pos);
                if (nsz > marklimit)
                    nsz = marklimit;
                byte[] nbuf = new byte[nsz];
                System.arraycopy(buffer, 0, nbuf, 0, pos);
                synchronized(this) {
                    if (buf != buffer)
                        throw new IOException("Stream closed");
                    buf = nbuf;
                }
                buffer = nbuf;
            }
        }
        count = pos;
        int n = getInIfOpen().read(buffer, pos, buffer.length - pos);
        if (n > 0)
            count = n + pos;
    }

    public int read() throws IOException {
        synchronized(this) {
            return implRead();
        }
    }

    private int implRead() throws IOException {
        if (pos >= count) {
            fill();
            if (pos >= count)
                return -1;
        }
        return getBufIfOpen()[pos++] & 0xff;
    }

    private int read1(byte[] b, int off, int len) throws IOException {
        int avail = count - pos;
        if (avail <= 0) {
            int size = Math.max(getBufIfOpen(false).length, initialSize);
            if (len >= size && markpos == -1)
                return getInIfOpen().read(b, off, len);
            fill();
            avail = count - pos;
            if (avail <= 0) return -1;
        }
        int cnt = (avail < len) ? avail : len;
        System.arraycopy(getBufIfOpen(), pos, b, off, cnt);
        pos += cnt;
        return cnt;
    }

    public int read(byte[] b, int off, int len) throws IOException {
        synchronized(this) {
            return implRead(b, off, len);
        }
    }

    private int implRead(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        if ((off | len | (off + len) | (b.length - (off + len))) < 0)
            throw new IndexOutOfBoundsException();
        else if (len == 0)
            return 0;

        int n = 0;
        for (;;) {
            int nread = read1(b, off + n, len - n);
            if (nread <= 0)
                return (n == 0) ? nread : n;
            n += nread;
            if (n >= len)
                return n;
            InputStream input = in;
            if (input != null && input.available() <= 0)
                return n;
        }
    }

    public long skip(long n) throws IOException {
        synchronized(this) {
            return implSkip(n);
        }
    }

    private long implSkip(long n) throws IOException {
        ensureOpen();
        if (n <= 0)
            return 0;
        long avail = count - pos;

        if (avail <= 0) {
            if (markpos == -1)
                return getInIfOpen().skip(n);

            fill();
            avail = count - pos;
            if (avail <= 0)
                return 0;
        }

        long skipped = (avail < n) ? avail : n;
        pos += (int)skipped;
        return skipped;
    }

    public int available() throws IOException {
        synchronized(this) {
            return implAvailable();
        }
    }

    private int implAvailable() throws IOException {
        int n = count - pos;
        int avail = getInIfOpen().available();
        return n > (Integer.MAX_VALUE - avail) ? Integer.MAX_VALUE : n + avail;
    }

    public void mark(int readlimit) {
        synchronized(this) {
            implMark(readlimit);
        }
    }

    private void implMark(int readlimit) {
        marklimit = readlimit;
        markpos = pos;
    }

    public void reset() throws IOException {
        synchronized(this) {
            implReset();
        }
    }

    private void implReset() throws IOException {
        ensureOpen();
        if (markpos < 0)
            throw new IOException("Resetting to invalid mark");
        pos = markpos;
    }

    public boolean markSupported() {
        return true;
    }

    public void close() throws IOException {
        InputStream input;
        synchronized(this) {
            if (buf == null)
                return;
            buf = null;
            input = in;
            in = null;
        }
        if (input != null)
            input.close();
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        if (out == null)
            throw new NullPointerException("out");
        synchronized(this) {
            return implTransferTo(out);
        }
    }

    private long implTransferTo(OutputStream out) throws IOException {
        if (getClass() == BufferedInputStream.class && markpos == -1) {
            int avail = count - pos;
            if (avail > 0) {
                byte[] buffer = new byte[avail];
                System.arraycopy(getBufIfOpen(), pos, buffer, 0, avail);
                out.write(buffer);
                pos = count;
            }
            try {
                return Math.addExact(avail, getInIfOpen().transferTo(out));
            }
            catch (ArithmeticException ignore) {
                return Long.MAX_VALUE;
            }
        }
        else
            return super.transferTo(out);
    }
}
