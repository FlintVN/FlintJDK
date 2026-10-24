package flint.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;

/** Internal bridge to the platform's authenticated TLS implementation.
 * The platform must have a trusted CA bundle and a synchronized wall clock.
 * Missing native support is an error; there is no unverified fallback.
 */
public final class TlsSocket {
    private volatile int handle;
    private final int readTimeout;
    private static final int MAX_PACKET = 16384;

    public TlsSocket(String host, int port, int connectTimeout, int readTimeout) throws IOException {
        this.readTimeout = readTimeout;
        try {
            handle = open(host, port, connectTimeout == 0 ? 20000 : Math.max(250, Math.min(20000, connectTimeout)));
        } catch (LinkageError unavailable) {
            throw new IOException("Authenticated TLS is unavailable in this firmware");
        }
        if (handle == 0)
            throw new IOException("Authenticated TLS connection was not created");
    }

    private int active() throws IOException {
        int value = handle;
        if (value == 0) throw new IOException("TLS connection is closed");
        return value;
    }

    public InputStream getInputStream() {
        return new InputStream() {
            @Override public int read() throws IOException {
                byte[] one = new byte[1];
                return read(one, 0, 1) < 0 ? -1 : one[0] & 255;
            }
            @Override public int read(byte[] bytes, int offset, int length) throws IOException {
                checkRange(bytes, offset, length);
                if (length == 0) return 0;
                long started = System.nanoTime();
                while (true) {
                    int count = TlsSocket.read(active(), bytes, offset, Math.min(length, MAX_PACKET));
                    if (count != 0) return count;
                    if (readTimeout > 0 && (System.nanoTime() - started) / 1000000L >= readTimeout)
                        throw new SocketTimeoutException("TLS read timed out");
                    if (Thread.currentThread().isInterrupted())
                        throw new IOException("TLS read interrupted");
                    Thread.yield();
                }
            }
            @Override public void close() { TlsSocket.this.close(); }
        };
    }

    public OutputStream getOutputStream() {
        return new OutputStream() {
            @Override public void write(int value) throws IOException {
                write(new byte[] { (byte)value }, 0, 1);
            }
            @Override public void write(byte[] bytes, int offset, int length) throws IOException {
                checkRange(bytes, offset, length);
                while (length > 0) {
                    int count = Math.min(length, MAX_PACKET);
                    TlsSocket.write(active(), bytes, offset, count);
                    offset += count;
                    length -= count;
                }
            }
            @Override public void close() { TlsSocket.this.close(); }
        };
    }

    public void close() {
        int value;
        synchronized (this) { value = handle; handle = 0; }
        if (value != 0) close(value);
    }

    private static void checkRange(byte[] bytes, int offset, int length) {
        if (bytes == null) throw new NullPointerException();
        if (offset < 0 || length < 0 || offset > bytes.length - length)
            throw new IndexOutOfBoundsException();
    }

    private static native int open(String host, int port, int timeout) throws IOException;
    private static native int read(int handle, byte[] bytes, int offset, int length) throws IOException;
    private static native void write(int handle, byte[] bytes, int offset, int length) throws IOException;
    private static native void close(int handle);
}
