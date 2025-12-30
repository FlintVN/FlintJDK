package java.net;

import java.io.IOException;

public class MulticastSocket extends DatagramSocket {
    public MulticastSocket() throws IOException {
        super();
    }

    public MulticastSocket(int port) throws IOException {
        super(port);
    }

    void create(int port, InetAddress ignore) throws SocketException {
        try {
            this.impl = (DatagramSocketImpl)implClass.getConstructor().newInstance();
        }
        catch(Exception e) {
            throw new SocketException("can't instantiate DatagramSocketImpl" + e.toString());
        }
        impl.create();
        impl.setOption(SocketOptions.SO_REUSEADDR, new Integer(-1));
        impl.bind(port, InetAddress.anyLocalAddress());
    }

    public void setTTL(byte ttl) throws IOException {
        impl.setTTL(ttl);
    }

    public byte getTTL() throws IOException {
        return impl.getTTL();
    }

    public void joinGroup(InetAddress mcastaddr) throws IOException {
        impl.join(mcastaddr);
    }

    public void leaveGroup(InetAddress mcastaddr) throws IOException {
        impl.leave(mcastaddr);
    }

    public void setInterface(InetAddress inf) throws SocketException {
        impl.setOption(SocketOptions.IP_MULTICAST_IF, inf);
    }

    public InetAddress getInterface() throws SocketException {
        return (InetAddress) impl.getOption(SocketOptions.IP_MULTICAST_IF);
    }

    public synchronized void send(DatagramPacket p, byte ttl) throws IOException {
        byte dttl = getTTL();

        if(ttl != dttl)
            impl.setTTL(ttl);
        impl.send(p);
        if(ttl != dttl)
            impl.setTTL(dttl);
    }
}
