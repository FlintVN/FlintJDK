package java.net;

public class Proxy {
    public enum Type {
        DIRECT,
        HTTP,
        SOCKS
    };

    private Type type;
    private SocketAddress sa;
    public final static Proxy NO_PROXY = new Proxy();

    private Proxy() {
        type = Type.DIRECT;
        sa = null;
    }

    public Proxy(Type type, SocketAddress sa) {
        if ((type == Type.DIRECT) || !(sa instanceof InetSocketAddress))
            throw new IllegalArgumentException("type " + type + " is not compatible with address " + sa);
        this.type = type;
        this.sa = sa;
    }

    public Type type() {
        return type;
    }

    public SocketAddress address() {
        return sa;
    }

    public String toString() {
        if (type() == Type.DIRECT)
            return "DIRECT";
        return type() + " @ " + address();
    }

    public final boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Proxy))
            return false;
        Proxy p = (Proxy) obj;
        if (p.type() == type()) {
            if (address() == null)
                return (p.address() == null);
            else
                return address().equals(p.address());
        }
        return false;
    }

    public final int hashCode() {
        if (address() == null)
            return type().hashCode();
        return type().hashCode() + address().hashCode();
    }
}
