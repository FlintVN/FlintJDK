package flint.io;

import java.io.IOException;
import java.io.InputStream;

class InputPortStream extends InputStream {
    InputPort inputPort;

    InputPortStream(InputPort inputPort) {
        this.inputPort = inputPort;
    }

    public int read() throws IOException {
        return inputPort.read();
    }

    public int read(byte[] b) throws IOException {
        return inputPort.read(b, 0, b.length);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        return inputPort.read(b, off, len);
    }
}
