package java.net;

interface SocketOptions {
    public void setOption(int optID, Object value) throws SocketException;

    public Object getOption(int optID) throws SocketException;

    public final static int TCP_NODELAY = 0x0001;

    public final static int SO_BINDADDR = 0x000F;

    public final static int SO_REUSEADDR = 0x04;

    public final static int IP_MULTICAST_IF = 0x10;

    public final static int SO_LINGER = 0x0080;

    public final static int SO_TIMEOUT = 0x1006;
}
