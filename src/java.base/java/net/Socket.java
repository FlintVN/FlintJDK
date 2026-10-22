package java.net;

import java.io.Closeable;
import java.lang.reflect.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import flint.net.FlintSocketImpl;

public class Socket implements Closeable {
    private static SocketImplFactory factory;

    private boolean created = false;
    private boolean bound = false;
    private boolean connected = false;
    private boolean closed = false;
    private Object closeLock = new Object();

    SocketImpl impl;

    public static synchronized void setSocketImplFactory(SocketImplFactory fac) throws IOException {
        if (factory != null)
            throw new SocketException("factory already defined");
        factory = fac;
    }

    @SuppressWarnings("this-escape")
    public Socket() {
        setImpl();
    }

    protected Socket(SocketImpl impl) throws SocketException {
        this.impl = impl;
    }

    public Socket(String host, int port) throws UnknownHostException, IOException {
        this(host != null ? new InetSocketAddress(host, port) : new InetSocketAddress(InetAddress.getByName(null), port), (SocketAddress) null);
    }

    public Socket(InetAddress address, int port) throws IOException {
        this(address != null ? new InetSocketAddress(address, port) : null, (SocketAddress) null);
    }

    public Socket(String host, int port, InetAddress localAddr, int localPort) throws IOException {
        this(host != null ? new InetSocketAddress(host, port) : new InetSocketAddress(InetAddress.getByName(null), port), new InetSocketAddress(localAddr, localPort));
    }

    public Socket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
        this(address != null ? new InetSocketAddress(address, port) : null, new InetSocketAddress(localAddr, localPort));
    }

    @SuppressWarnings("this-escape")
    private Socket(SocketAddress address, SocketAddress localAddr) throws IOException {
        if (address == null)
            throw new NullPointerException();

        try {
            createImpl();
            if (localAddr != null)
                bind(localAddr);
            connect(address);
        }
        catch (IOException | IllegalArgumentException | SecurityException e) {
            try {
                close();
            }
            catch (IOException ce) {
                e.addSuppressed(ce);
            }
            throw e;
        }
    }

    void createImpl() throws SocketException {
        if (impl == null)
            setImpl();
        try {
            impl.create();
            created = true;
        }
        catch (IOException e) {
            throw new SocketException(e.getMessage());
        }
    }

    void setImpl() {
        if (factory != null)
            impl = factory.createSocketImpl();
        else
            impl = new FlintSocketImpl();
    }

    public void connect(SocketAddress endpoint) throws IOException {
        if (endpoint == null)
            throw new IllegalArgumentException("connect: The address can't be null");
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (isConnected())
            throw new SocketException("already connected");
        if (!(endpoint instanceof InetSocketAddress))
            throw new IllegalArgumentException("Unsupported address type");

        InetSocketAddress epoint = (InetSocketAddress)endpoint;
        InetAddress addr = epoint.getAddress();
        int port = epoint.getPort();
        checkAddress(addr, "connect");

        if (!created)
            createImpl();
        if (epoint.isUnresolved())
            impl.connect(addr.getHostName(), port);
        else
            impl.connect(addr, port);

        connected = true;
        bound = true;
    }

    public void bind(SocketAddress bindpoint) throws IOException {
        if (isClosed())
            throw new SocketException("Socket is closed");
        if (isBound())
            throw new SocketException("Already bound");
        if (bindpoint != null && (!(bindpoint instanceof InetSocketAddress)))
            throw new IllegalArgumentException("Unsupported address type");

        InetSocketAddress epoint = (InetSocketAddress) bindpoint;
        if (epoint != null && epoint.isUnresolved())
            throw new SocketException("Unresolved address");
        if (epoint == null)
            epoint = new InetSocketAddress(0);

        InetAddress addr = epoint.getAddress();
        int port = epoint.getPort();
        checkAddress(addr, "bind");

        if (!created)
            createImpl();
        impl.bind(addr, port);
        bound = true;
    }

    private void checkAddress(InetAddress addr, String op) {
        if (addr == null)
            return;
        if (!(addr instanceof Inet4Address || addr instanceof Inet6Address))
            throw new IllegalArgumentException(op + ": invalid address type");
    }

    public InetAddress getInetAddress() {
        return impl.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        InetAddress in = null;
        try {
            in = (InetAddress)impl.getOption(SocketOptions.SO_BINDADDR);
        }
        catch (Exception e) {
            in = InetAddress.anyLocalAddress();
        }
        return in;
    }

    public int getPort() {
        return impl.getPort();
    }

    public int getLocalPort() {
        return impl.getLocalPort();
    }

    public InputStream getInputStream() throws IOException {
        return impl.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return impl.getOutputStream();
    }

    public void setTcpNoDelay(boolean on) throws SocketException {
        impl.setOption(SocketOptions.TCP_NODELAY, new Boolean(on));
    }

    public boolean getTcpNoDelay() throws SocketException {
        return ((Boolean)impl.getOption(SocketOptions.TCP_NODELAY)).booleanValue();
    }

    public void setSoLinger(boolean on, int val) throws SocketException {
        if (!on)
            impl.setOption(SocketOptions.SO_LINGER, new Boolean(on));
        else
            impl.setOption(SocketOptions.SO_LINGER, new Integer(val));
    }

    public int getSoLinger() throws SocketException {
        Object o = impl.getOption(SocketOptions.SO_LINGER);
        if (o instanceof Integer)
            return ((Integer)o).intValue();
        else
            return -1;
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    public synchronized int getSoTimeout() throws SocketException {
        Object o = impl.getOption(SocketOptions.SO_TIMEOUT);
        if (o instanceof Integer)
            return ((Integer)o).intValue();
        else
            return 0;
    }

    public synchronized void close() throws IOException {
        synchronized(closeLock) {
            if (closed)
                return;
            if (created)
                impl.close();
            closed = true;
        }
    }

    public String toString() {
        if (isConnected())
            return "Socket[addr=" + impl.getInetAddress() + ",port=" + impl.getPort() + ",localport=" + impl.getLocalPort() + "]";
        return "Socket[unconnected]";
    }

    public boolean isConnected() {
        return connected;
    }

    public boolean isBound() {
        return bound;
    }

    public boolean isClosed() {
        synchronized(closeLock) {
            return closed;
        }
    }
}
