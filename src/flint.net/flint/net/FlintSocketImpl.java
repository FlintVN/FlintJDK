package flint.net;

import java.net.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;
import java.io.ByteArrayOutputStream;

class FlintSocketImpl extends SocketImpl {
    private int timeout;

    static {
        initProto();
    }

    public FlintSocketImpl() {

    }

    @Override
    protected synchronized void create() throws IOException {
        fd = new FileDescriptor();
        socketCreate();
    }

    @Override
    protected void connect(String host, int port) throws UnknownHostException, IOException {
        try {
            InetAddress address = InetAddress.getByName(host);
            connectToAddress(address, port);
            return;
        }
        catch(UnknownHostException e) {
            close();
            throw e;
        }
        catch(IOException e) {
            close();
            throw e;
        }
    }

    @Override
    protected void connect(InetAddress address, int port) throws IOException {
        this.port = port;
        this.address = address;

        try {
            connectToAddress(address, port);
            return;
        }
        catch(IOException e) {
            close();
            throw e;
        }
    }

    private void connectToAddress(InetAddress address, int port) throws IOException {
        IOException pending = null;

        for(int i = 0; i < 3; i++) {
            try {
                socketConnect(address, port);
                return;
            }
            catch(ProtocolException e) {
                close();
                fd = new FileDescriptor();
                socketCreate();
                pending = e;
            }
            catch(IOException e) {
                close();
                throw e;
            }
        }

        close();
        throw pending;
    }

    @Override
    public void setOption(int opt, Object val) throws SocketException {
        boolean on = true;
        switch(opt) {
            case SO_LINGER:
                if(val == null || (!(val instanceof Integer) && !(val instanceof Boolean)))
                    throw new SocketException("Bad parameter for option");
                if(val instanceof Boolean)
                    on = false;
                break;
            case SO_TIMEOUT:
                if(val == null || (!(val instanceof Integer)))
                    throw new SocketException("Bad parameter for SO_TIMEOUT");
                int tmp = ((Integer)val).intValue();
                if(tmp < 0)
                    throw new IllegalArgumentException("timeout < 0");
                timeout = tmp;
                return;
            case SO_BINDADDR:
                throw new SocketException("Cannot re-bind socket");
            case TCP_NODELAY:
                if(val == null || !(val instanceof Boolean))
                    throw new SocketException("bad parameter for TCP_NODELAY");
                on = ((Boolean)val).booleanValue();
                break;
            default:
                throw new SocketException("unrecognized TCP option: " + opt);
        }
        socketSetOption(opt, on, val);
    }

    @Override
    public Object getOption(int opt) throws SocketException {
        if(opt == SO_TIMEOUT)
            return new Integer(timeout);
        int ret = socketGetOption(opt);

        switch(opt) {
            case TCP_NODELAY:
                return (ret == -1) ? new Boolean(false) : new Boolean(true);
            case SO_LINGER:
                return (ret == -1) ? new Boolean(false) : new Integer(ret);
            case SO_BINDADDR: {
                byte[] ipBytes = new byte[4];
                ipBytes[0] = (byte)((ret >> 24) & 0xFF);
                ipBytes[0] = (byte)((ret >> 16) & 0xFF);
                ipBytes[0] = (byte)((ret >> 8) & 0xFF);
                ipBytes[0] = (byte)(ret & 0xFF);
                try {
                    return InetAddress.getByAddress(ipBytes);
                }
                catch(UnknownHostException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    @Override
    protected synchronized void bind(InetAddress address, int lport) throws IOException {
        socketBind(address, lport);
    }

    @Override
    protected synchronized void listen(int count) throws IOException {
        socketListen(count);
    }

    @Override
    protected synchronized void accept(SocketImpl s) throws IOException {
        socketAccept(s);
    }

    @Override
    protected synchronized InputStream getInputStream() throws IOException {
        return new FlintSocketInputStream(this);
    }

    @Override
    protected synchronized OutputStream getOutputStream() throws IOException {
        return new FlintSocketOutputStream(this);
    }

    @Override
    protected synchronized int available() throws IOException {
        return socketAvailable();
    }

    @Override
    protected void close() throws IOException {
        if(fd != null) {
            socketClose();
            fd = null;
        }
    }

    @Override
    protected FileDescriptor getFileDescriptor() {
        return fd;
    }

    @Override
    protected InetAddress getInetAddress() {
        return address;
    }

    @Override
    protected int getPort() {
        return port;
    }

    @Override
    protected int getLocalPort() {
        return localport;
    }

    protected void finalize() throws IOException {
        close();
    }

    private native void socketCreate() throws IOException;
    private native void socketConnect(InetAddress address, int port) throws IOException;
    private native void socketBind(InetAddress address, int port) throws IOException;
    private native void socketListen(int count) throws IOException;
    private native void socketAccept(SocketImpl s) throws IOException;
    private native int socketAvailable() throws IOException;
    private native void socketClose() throws IOException;
    private native static void initProto();
    private native void socketSetOption(int cmd, boolean on, Object value) throws SocketException;
    private native int socketGetOption(int opt) throws SocketException;
}
