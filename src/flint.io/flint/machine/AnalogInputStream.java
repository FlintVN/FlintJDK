package flint.machine;

import java.io.IOException;

public interface AnalogInputStream {
    int read(byte[] b) throws IOException;
    int read(byte[] b, int off, int count) throws IOException;

    int read(short[] b) throws IOException;
    int read(short[] b, int off, int count) throws IOException;

    int read(int[] b) throws IOException;
    int read(int[] b, int off, int count) throws IOException;
}
