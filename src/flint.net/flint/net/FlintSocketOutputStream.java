package flint.net;

import java.net.SocketImpl;
import java.io.FileOutputStream;
import java.io.IOException;

class FlintSocketOutputStream extends FileOutputStream {
    private FlintSocketImpl impl;
    private byte temp[] = new byte[1];

    FlintSocketOutputStream(FlintSocketImpl impl) throws IOException {
        super(impl.getFileDescriptor());
        this.impl = impl;
    }

    private native void socketWrite(byte b[], int off, int len) throws IOException;

    public void write(int b) throws IOException {
        temp[0] = (byte)b;
        socketWrite(temp, 0, 1);
    }

    public void write(byte b[]) throws IOException {
        socketWrite(b, 0, b.length);
    }

    public void write(byte b[], int off, int len) throws IOException {
        socketWrite(b, off, len);
    }

    public void close() throws IOException {
        impl.close();
    }

    protected void finalize() {

    }
}
