package flint.net.www.protocol.http;

import java.io.IOException;
import java.io.OutputStream;
import java.io.FilterOutputStream;
import java.net.ProtocolException;

class FixedLengthOutputStream extends FilterOutputStream {
    private long remaining;
    private boolean closed;

    FixedLengthOutputStream(OutputStream out, long contentLength) {
        super(out);
        this.remaining = contentLength;
    }

    @Override
    public void write(int b) throws IOException {
        write(new byte[] { (byte) b }, 0, 1);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (off < 0 || len < 0 || off > b.length - len)
            throw new IndexOutOfBoundsException();
        if (len > remaining)
            throw new ProtocolException("cannot write more bytes than Content-Length");
        out.write(b, off, len);
        remaining -= len;
    }

    @Override
    public void close() throws IOException {
        if (closed)
            return;
        closed = true;
        out.flush();
        if (remaining > 0)
            throw new ProtocolException("insufficient data written");
    }
}
