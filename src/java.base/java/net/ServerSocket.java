package java.net;

import java.lang.reflect.*;
import java.io.IOException;
import java.io.FileDescriptor;

public class ServerSocket {
    private static SocketImplFactory factory;

    private SocketImpl impl;

    public static synchronized void setSocketFactory(SocketImplFactory fac) throws IOException {
        if(factory != null)
            throw new SocketException("factory already defined");
        factory = fac;
    }

    private ServerSocket() {
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

    public ServerSocket(int port) throws IOException {
        this(port, 50, null);
    }

    public ServerSocket(int port, int backlog) throws IOException {
        this(port, backlog, null);
    }

    public ServerSocket(int port, int backlog, InetAddress bindAddr) throws IOException {
        this();

        if(port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("Port value out of range: " + port);
        try {
            impl.create();
            if(bindAddr == null)
                bindAddr = InetAddress.anyLocalAddress();

            impl.bind(bindAddr, port);
            impl.listen(backlog);
        }
        catch(IOException e) {
            impl.close();
            throw e;
        }
    }

    public InetAddress getInetAddress() {
        return impl.getInetAddress();
    }

    public int getLocalPort() {
        return impl.getLocalPort();
    }

    public Socket accept() throws IOException {
        Socket s = new Socket();
        implAccept(s);
        return s;
    }

    protected final void implAccept(Socket s) throws IOException {
        SocketImpl si = s.impl;
        try {
            s.impl = null;
            si.address = null;
            si.fd = new FileDescriptor();
            impl.accept(si);
        }
        catch(IOException e) {
            si.reset();
            s.impl = si;
            throw e;
        }
        s.impl = si;
    }

    public void close() throws IOException {
        impl.close();
    }

    public synchronized void setSoTimeout(int timeout) throws SocketException {
        impl.setOption(SocketOptions.SO_TIMEOUT, new Integer(timeout));
    }

    public synchronized int getSoTimeout() throws IOException {
        Object o = impl.getOption(SocketOptions.SO_TIMEOUT);
        if(o instanceof Integer)
            return ((Integer)o).intValue();
        else
            return 0;
    }

    public String toString() {
        return "ServerSocket[addr=" + impl.getInetAddress() + ",port=" + impl.getPort() + ",localport=" + impl.getLocalPort()  + "]";
    }
}
