package flint.io;

import java.io.IOException;
import java.io.OutputStream;

class OutputPortStream extends OutputStream {
    OutputPort outputPort;

    OutputPortStream(OutputPort outputPort) {
        this.outputPort = outputPort;
    }

    public void write(int b) throws IOException {
        outputPort.write(b);
    }

    public void write(byte[] b) throws IOException {
        outputPort.write(b, 0, b.length);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        outputPort.write(b, off, len);
    }
}
