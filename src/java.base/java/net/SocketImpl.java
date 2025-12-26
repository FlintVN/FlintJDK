package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;

public abstract class SocketImpl implements SocketOptions {
    protected FileDescriptor fd;
    protected InetAddress address;
    protected int port;
    protected int localport;

    protected SocketImpl() {

    }

    protected abstract void create() throws IOException;

    protected abstract void connect(String host, int port) throws IOException;

    protected abstract void connect(InetAddress address, int port) throws IOException;

    protected abstract void bind(InetAddress host, int port) throws IOException;

    protected abstract void listen(int backlog) throws IOException;

    protected abstract void accept(SocketImpl s) throws IOException;

    protected abstract InputStream getInputStream() throws IOException;

    protected abstract OutputStream getOutputStream() throws IOException;

    protected abstract int available() throws IOException;

    protected abstract void close() throws IOException;

    protected FileDescriptor getFileDescriptor() {
        return fd;
    }

    protected InetAddress getInetAddress() {
        return address;
    }

    protected int getPort() {
        return port;
    }

    protected int getLocalPort() {
        return localport;
    }

    public String toString() {
        return "Socket[addr=" + getInetAddress() + ",port=" + getPort() + ",localport=" + getLocalPort()  + "]";
    }

    void reset() throws IOException {
        address = null;
        port = 0;
        localport = 0;
        close();
    }
}
