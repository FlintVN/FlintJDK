package java.io;

import java.util.Objects;

public class FilterOutputStream extends OutputStream {
    protected OutputStream out;
    private volatile boolean closed;

    public FilterOutputStream(OutputStream out) {
        this.out = out;
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void flush() throws IOException {
        out.flush();
    }

    @Override
    public void close() throws IOException {
        if(closed)
            return;
        synchronized(this) {
            if(closed)
                return;
            closed = true;
        }

        Throwable flushException = null;
        synchronized(this) {
            try {
                flush();
            }
            catch(Throwable e) {
                flushException = e;
                throw e;
            }
            finally {
                if(flushException == null)
                    out.close();
                else {
                    try {
                        out.close();
                    }
                    catch(Throwable closeException) {
                        if(flushException != closeException)
                            closeException.addSuppressed(flushException);
                        throw closeException;
                    }
                }
            }
        }
    }
}
