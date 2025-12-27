package java.net;

import java.lang.reflect.*;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

public class Socket {
    private static SocketImplFactory factory;

    SocketImpl impl;

    public static synchronized void setSocketImplFactory(SocketImplFactory fac) throws IOException {
        if(factory != null)
            throw new SocketException("factory already defined");
        factory = fac;
    }

    protected Socket() {
        if(factory != null)
            impl = factory.createSocketImpl();
        else {
            try {
                impl = (SocketImpl)Class.forName("flint.net.FlintSocketImpl").getConstructor().newInstance();
            }
            catch(ClassNotFoundException e) {
                System.out.println("Class not found: flint.net.FlintInetAddressImpl");
            }
            catch(NoSuchMethodException e) {
                System.out.println("Method not found: flint.net.FlintInetAddressImpl.<init>()");
            }
            catch(InstantiationException | IllegalArgumentException | InvocationTargetException e) {
                System.out.println("Could not instantiate: flint.net.FlintInetAddressImpl");
            }
            catch(IllegalAccessException e) {
                System.out.println("Cannot access class: flint.net.FlintInetAddressImpl");
            }
        }
    }

    protected Socket(SocketImpl impl) throws SocketException {
        this.impl = impl;
    }

    public Socket(String host, int port) throws UnknownHostException, IOException {
        this(InetAddress.getByName(host), port, null, 0);
    }

    public Socket(InetAddress address, int port) throws IOException {
        this(address, port, null, 0);
    }

    public Socket(String host, int port, InetAddress localAddr, int localPort) throws IOException {
        this(InetAddress.getByName(host), port, localAddr, localPort);
    }

    public Socket(InetAddress address, int port, InetAddress localAddr, int localPort) throws IOException {
        this();

        if(port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("port out range:"+port);

        if(localPort < 0 || localPort > 0xFFFF)
            throw new IllegalArgumentException("port out range:"+localPort);

        try {
            impl.create();
            if(localAddr != null || localPort > 0) {
                if(localAddr == null)
                    localAddr = InetAddress.anyLocalAddress();
                impl.bind(localAddr, localPort);
            }
            impl.connect(address, port);
        }
        catch(SocketException e) {
            impl.close();
            throw e;
        }
    }

    public InetAddress getInetAddress() {
        return impl.getInetAddress();
    }

    public InetAddress getLocalAddress() {
        InetAddress in = null;
        try {
            in = (InetAddress) impl.getOption(SocketOptions.SO_BINDADDR);
        }
        catch(Exception e) {
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
        return ((Boolean) impl.getOption(SocketOptions.TCP_NODELAY)).booleanValue();
    }

    public void setSoLinger(boolean on, int val) throws SocketException {
        if(!on)
            impl.setOption(SocketOptions.SO_LINGER, new Boolean(on));
        else
            impl.setOption(SocketOptions.SO_LINGER, new Integer(val));
    }

    public int getSoLinger() throws SocketException {
        Object o = impl.getOption(SocketOptions.SO_LINGER);
        if(o instanceof Integer)
            return ((Integer)o).intValue();
        else
            return -1;
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    public synchronized int getSoTimeout() throws SocketException {
        Object o = impl.getOption(SocketOptions.SO_TIMEOUT);
        if(o instanceof Integer)
            return ((Integer)o).intValue();
        else
            return 0;
    }

    public synchronized void close() throws IOException {
        impl.close();
    }

    public String toString() {
        return "Socket[addr=" + impl.getInetAddress() + ",port=" + impl.getPort() + ",localport=" + impl.getLocalPort() + "]";
    }
}
