package flint.io;

import java.io.IOException;
import java.io.OutputStream;

public interface OutputPort extends AutoCloseable {
    OutputPort open() throws IOException;
    void close() throws IOException;

    boolean isOpen();

    void write(int b) throws IOException;
    void write(byte[] b) throws IOException;
    void write(byte[] b, int off, int count) throws IOException;

    OutputStream getOutputStream();
}
