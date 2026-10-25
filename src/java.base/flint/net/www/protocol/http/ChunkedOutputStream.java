package flint.net.www.protocol.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;

class ChunkedOutputStream extends FilterOutputStream {
    private static final byte[] CRLF = { '\r', '\n' };
    private static final byte[] LAST_CHUNK = { '0', '\r', '\n', '\r', '\n' };

    private final byte[] buf;
    private int count;
    private boolean closed;

    ChunkedOutputStream(OutputStream out, int chunkSize) {
        super(out);
        this.buf = new byte[chunkSize > 0 ? chunkSize : 4096];
    }

    @Override
    public synchronized void write(int b) throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        buf[count++] = (byte) b;
        if (count == buf.length)
            flushChunk();
    }

    @Override
    public synchronized void write(byte[] b, int off, int len) throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (off < 0 || len < 0 || off > b.length - len)
            throw new IndexOutOfBoundsException();

        while (len > 0) {
            int space = buf.length - count;
            if (len >= space) {
                System.arraycopy(b, off, buf, count, space);
                count += space;
                flushChunk();
                off += space;
                len -= space;
            }
            else {
                System.arraycopy(b, off, buf, count, len);
                count += len;
                break;
            }
        }
    }

    private void flushChunk() throws IOException {
        if (count <= 0)
            return;
        byte[] header = Integer.toHexString(count).getBytes();
        out.write(header);
        out.write(CRLF);
        out.write(buf, 0, count);
        out.write(CRLF);
        count = 0;
    }

    @Override
    public synchronized void flush() throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        flushChunk();
        out.flush();
    }

    @Override
    public synchronized void close() throws IOException {
        if (closed)
            return;
        closed = true;
        flushChunk();
        out.write(LAST_CHUNK);
        out.flush();
    }
}
