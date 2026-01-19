package flint.net;

import java.net.*;
import java.io.FileDescriptor;
import java.io.IOException;

/**
 * FlintJVM can access non-public classes via Class.forName.
 * This could be because the access validation feature has not been implemented at the VM layer.
 * Which could be considered a bug, and FlintDatagramSocketImpl is exploiting this bug.
 */
/* public */ class FlintDatagramSocketImpl extends DatagramSocketImpl {
    private int timeout = 0;

    public FlintDatagramSocketImpl() {

    }

    protected synchronized void create() throws SocketException {
        fd = new FileDescriptor();
        datagramSocketCreate();
    }

    protected synchronized native void bind(int lport, InetAddress laddr) throws SocketException;

    protected native void send(DatagramPacket p) throws IOException;

    protected synchronized native int peek(InetAddress i) throws IOException;

    protected synchronized native void receive(DatagramPacket p) throws IOException;

    protected native void setTTL(byte ttl) throws IOException;

    protected native byte getTTL() throws IOException;

    protected native void join(InetAddress inetaddr) throws IOException;

    protected native void leave(InetAddress inetaddr) throws IOException;

    protected void close() {
        if(fd != null) {
            datagramSocketClose();
            fd = null;
        }
    }

    protected synchronized void finalize() {
        close();
    }

    public void setOption(int optID, Object o) throws SocketException {
        switch(optID) {
            case SO_TIMEOUT:
                if(o == null || !(o instanceof Integer))
                    throw new SocketException("bad argument for SO_TIMEOUT");
                int tmp = ((Integer) o).intValue();
                if(tmp < 0)
                    throw new IllegalArgumentException("timeout < 0");
                timeout = tmp;
                return;
            case SO_BINDADDR:
                throw new SocketException("Cannot re-bind Socket");
            case SO_REUSEADDR:
                if(o == null || !(o instanceof Integer))
                    throw new SocketException("bad argument for SO_REUSEADDR");
                break;
            case IP_MULTICAST_IF:
                if(o == null || !(o instanceof InetAddress))
                    throw new SocketException("bad argument for IP_MULTICAST_IF");
                break;
            default:
                throw new SocketException("invalid option: " + optID);
        }
        socketSetOption(optID, o);
    }

    public Object getOption(int optID) throws SocketException {
        if(optID == SO_TIMEOUT)
            return new Integer(timeout);
        return socketGetOption(optID);
    }

    private native void datagramSocketCreate() throws SocketException;
    private native void datagramSocketClose();
    private native void socketSetOption(int opt, Object val) throws SocketException;
    private native Object socketGetOption(int opt) throws SocketException;
}
