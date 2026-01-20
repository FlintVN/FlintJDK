package flint.machine;

import java.io.IOException;
import java.io.InputStream;

public interface InputPort extends AutoCloseable {
    InputPort open() throws IOException;
    void close() throws IOException;

    boolean isOpen();

    int read() throws IOException;
    int read(byte[] b) throws IOException;
    int read(byte[] b, int off, int count) throws IOException;

    InputStream getInputStream();
}
