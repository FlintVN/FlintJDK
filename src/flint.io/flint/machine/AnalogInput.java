package flint.machine;

import java.io.IOException;
import java.io.InputStream;

public interface AnalogInput {
    int read(byte[] b) throws IOException;
    int read(byte[] b, int off, int count) throws IOException;

    int read(short[] b) throws IOException;
    int read(short[] b, int off, int count) throws IOException;

    int read(int[] b) throws IOException;
    int read(int[] b, int off, int count) throws IOException;

    InputStream getInputStream();
}
