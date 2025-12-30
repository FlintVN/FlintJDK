package java.net;

import java.io.FileDescriptor;
import java.io.IOException;

public class DatagramSocket {
    DatagramSocketImpl impl;
    static Class<?> implClass;

    static {
        try {
            implClass = Class.forName("flint.net.FlintDatagramSocketImpl");
        }
        catch(Exception e) {
            System.out.println("Can't find class: java.net.FlintDatagramSocketImpl");
        }
    }

    @SuppressWarnings("this-escape")
    public DatagramSocket() throws SocketException {
        create(0, null);
    }

    public DatagramSocket(int port) throws SocketException {
        this(port, null);
    }

    @SuppressWarnings("this-escape")
    public DatagramSocket(int port, InetAddress laddr) throws SocketException {
        if(port < 0 || port > 0xFFFF)
            throw new IllegalArgumentException("Port out of range:"+port);

        create(port, laddr);
    }

    void create(int port, InetAddress laddr) throws SocketException {
        try {
            impl = (DatagramSocketImpl)implClass.getConstructor().newInstance();
        }
        catch(Exception e) {
            throw new SocketException("can't instantiate DatagramSocketImpl");
        }
        impl.create();
        if(laddr == null)
            laddr = InetAddress.anyLocalAddress();
        impl.bind(port, laddr);
    }

    public void send(DatagramPacket p) throws IOException  {
        synchronized(p) {
            impl.send(p);
        }
    }

    public synchronized void receive(DatagramPacket p) throws IOException {
        synchronized(p) {
            impl.receive(p);
        }
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

    public int getLocalPort() {
        return impl.getLocalPort();
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

    public void close() {
        impl.close();
    }
}
