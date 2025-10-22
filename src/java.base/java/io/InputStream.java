
package java.io;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class InputStream implements Closeable {
    private static final int MAX_SKIP_BUFFER_SIZE = 2048;
    private static final int DEFAULT_BUFFER_SIZE = 16384;

    public InputStream() {

    }

    public static InputStream nullInputStream() {
        return new InputStream() {
            private volatile boolean closed;

            private void ensureOpen() throws IOException {
                if(closed)
                    throw new IOException("Stream closed");
            }

            @Override
            public int available () throws IOException {
                ensureOpen();
                return 0;
            }

            @Override
            public int read() throws IOException {
                ensureOpen();
                return -1;
            }

            @Override
            public int read(byte[] b, int off, int len) throws IOException {
                Objects.checkFromIndexSize(off, len, b.length);
                if(len == 0)
                    return 0;
                ensureOpen();
                return -1;
            }

            @Override
            public byte[] readAllBytes() throws IOException {
                ensureOpen();
                return new byte[0];
            }

            @Override
            public int readNBytes(byte[] b, int off, int len) throws IOException {
                Objects.checkFromIndexSize(off, len, b.length);
                ensureOpen();
                return 0;
            }

            @Override
            public byte[] readNBytes(int len) throws IOException {
                if(len < 0)
                    throw new IllegalArgumentException("len < 0");
                ensureOpen();
                return new byte[0];
            }

            @Override
            public long skip(long n) throws IOException {
                ensureOpen();
                return 0L;
            }

            @Override
            public void skipNBytes(long n) throws IOException {
                ensureOpen();
                if(n > 0)
                    throw new EOFException();
            }

            @Override
            public long transferTo(OutputStream out) throws IOException {
                Objects.requireNonNull(out);
                ensureOpen();
                return 0L;
            }

            @Override
            public void close() throws IOException {
                closed = true;
            }
        };
    }

    public abstract int read() throws IOException;

    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        if(len == 0)
            return 0;

        int c = read();
        if(c == -1)
            return -1;
        b[off] = (byte)c;

        int i = 1;
        try {
            for(; i < len ; i++) {
                c = read();
                if(c == -1)
                    break;
                b[off + i] = (byte)c;
            }
        }
        catch(IOException ee) {

        }
        return i;
    }

    private static final int MAX_BUFFER_SIZE = Integer.MAX_VALUE - 8;

    public byte[] readAllBytes() throws IOException {
        return readNBytes(Integer.MAX_VALUE);
    }

    public byte[] readNBytes(int len) throws IOException {
        if(len < 0)
            throw new IllegalArgumentException("len < 0");

        List<byte[]> bufs = null;
        byte[] result = null;
        int total = 0;
        int remaining = len;
        int n;
        do {
            byte[] buf = new byte[Math.min(remaining, DEFAULT_BUFFER_SIZE)];
            int nread = 0;

            while((n = read(buf, nread, Math.min(buf.length - nread, remaining))) > 0) {
                nread += n;
                remaining -= n;
            }

            if(nread > 0) {
                if(MAX_BUFFER_SIZE - total < nread)
                    throw new OutOfMemoryError("Required array size too large");
                if(nread < buf.length)
                    buf = Arrays.copyOfRange(buf, 0, nread);
                total += nread;
                if(result == null)
                    result = buf;
                else {
                    if(bufs == null) {
                        bufs = new ArrayList<>();
                        bufs.add(result);
                    }
                    bufs.add(buf);
                }
            }
        } while(n >= 0 && remaining > 0);

        if(bufs == null) {
            if(result == null)
                return new byte[0];
            return result.length == total ?
                result : Arrays.copyOf(result, total);
        }

        result = new byte[total];
        int offset = 0;
        remaining = total;
        for(byte[] b : bufs) {
            int count = Math.min(b.length, remaining);
            System.arraycopy(b, 0, result, offset, count);
            offset += count;
            remaining -= count;
        }

        return result;
    }

    public int readNBytes(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);

        int n = 0;
        while(n < len) {
            int count = read(b, off + n, len - n);
            if(count < 0)
                break;
            n += count;
        }
        return n;
    }

    public long skip(long n) throws IOException {
        long remaining = n;
        int nr;

        if(n <= 0)
            return 0;

        int size = (int)Math.min(MAX_SKIP_BUFFER_SIZE, remaining);
        byte[] skipBuffer = new byte[size];
        while(remaining > 0) {
            nr = read(skipBuffer, 0, (int)Math.min(size, remaining));
            if(nr < 0)
                break;
            remaining -= nr;
        }

        return n - remaining;
    }

    public void skipNBytes(long n) throws IOException {
        while(n > 0) {
            long ns = skip(n);
            if(ns > 0 && ns <= n)
                n -= ns;
            else if(ns == 0) {
                if(read() == -1)
                    throw new EOFException();
                n--;
            }
            else
                throw new IOException("Unable to skip exactly");
        }
    }

    public int available() throws IOException {
        return 0;
    }

    public void close() throws IOException {

    }

    public void mark(int readlimit) {

    }

    public void reset() throws IOException {
        throw new IOException("mark/reset not supported");
    }

    public boolean markSupported() {
        return false;
    }

    public long transferTo(OutputStream out) throws IOException {
        Objects.requireNonNull(out, "out");
        long transferred = 0;
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
        int read;
        while((read = this.read(buffer, 0, DEFAULT_BUFFER_SIZE)) >= 0) {
            out.write(buffer, 0, read);
            if(transferred < Long.MAX_VALUE) {
                try {
                    transferred = Math.addExact(transferred, read);
                }
                catch(ArithmeticException ignore) {
                    transferred = Long.MAX_VALUE;
                }
            }
        }
        return transferred;
    }
}
