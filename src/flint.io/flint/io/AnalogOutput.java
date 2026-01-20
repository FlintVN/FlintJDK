package flint.io;

import java.io.IOException;
import java.io.OutputStream;

public interface AnalogOutput {
    void write(byte[] b) throws IOException;
    void write(byte[] b, int off, int count) throws IOException;

    void write(short[] b) throws IOException;
    void write(short[] b, int off, int count) throws IOException;

    void write(int[] b) throws IOException;
    void write(int[] b, int off, int count) throws IOException;

    OutputStream getOutputStream();
}
