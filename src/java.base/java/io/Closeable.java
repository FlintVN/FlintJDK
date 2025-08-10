package java.io;

import java.io.IOException;

public interface Closeable extends AutoCloseable {
    public void close() throws IOException;
}
