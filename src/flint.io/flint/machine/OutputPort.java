package flint.machine;

import java.io.IOException;

public interface OutputPort extends AutoCloseable {
    OutputPort open() throws IOException;
    void close() throws IOException;

    boolean isOpen();

    void write(int b) throws IOException;
    void write(byte[] b) throws IOException;
    void write(byte[] b, int off, int count) throws IOException;
}
