package java.io;

import java.util.Objects;

public abstract class OutputStream implements Closeable, Flushable {
    public OutputStream() {

    }

    public static OutputStream nullOutputStream() {
        return new OutputStream() {
            private volatile boolean closed;

            private void ensureOpen() throws IOException {
                if(closed)
                    throw new IOException("Stream closed");
            }

            @Override
            public void write(int b) throws IOException {
                ensureOpen();
            }

            @Override
            public void write(byte[] b, int off, int len) throws IOException {
                Objects.checkFromIndexSize(off, len, b.length);
                ensureOpen();
            }

            @Override
            public void close() {
                closed = true;
            }
        };
    }

    public abstract void write(int b) throws IOException;

    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        for(int i = 0 ; i < len ; i++)
            write(b[off + i]);
    }

    public void flush() throws IOException {

    }

    public void close() throws IOException {

    }
}
