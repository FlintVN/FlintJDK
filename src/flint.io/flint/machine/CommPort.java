package flint.machine;

import java.io.IOException;

public interface CommPort extends AutoCloseable {
    CommPort open() throws IOException;
    void close() throws IOException;

    boolean isOpen();

    int read() throws IOException;
    int read(byte[] b) throws IOException;
    int read(byte[] b, int off, int count) throws IOException;

    void write(int b) throws IOException;
    void write(byte[] b) throws IOException;
    void write(byte[] b, int off, int count) throws IOException;
}
