package java.net;

public final class DatagramPacket {
    byte[] buf;
    int length;
    InetAddress address;
    int port;

    public DatagramPacket(byte ibuf[], int ilength) {
        if(ilength > ibuf.length)
            throw new IllegalArgumentException("illegal length");
        buf = ibuf;
        length = ilength;
        address = null;
        port = -1;
    }

    public DatagramPacket(byte ibuf[], int ilength, InetAddress iaddr, int iport) {
        if(ilength > ibuf.length)
            throw new IllegalArgumentException("illegal length");
        if(iport < 0 || iport > 0xFFFF)
            throw new IllegalArgumentException("Port out of range:" + iport);
        buf = ibuf;
        length = ilength;
        address = iaddr;
        port = iport;
    }

    public synchronized InetAddress getAddress() {
        return address;
    }

    public synchronized int getPort() {
        return port;
    }

    public synchronized byte[] getData() {
        return buf;
    }

    public synchronized int getLength() {
        return length;
    }

    public synchronized void setAddress(InetAddress iaddr) {
        address = iaddr;
    }

    public synchronized void setPort(int iport) {
        if(iport < 0 || iport > 0xFFFF)
            throw new IllegalArgumentException("Port out of range:" + iport);
        port = iport;
    }

    public synchronized void setData(byte[] ibuf) {
        buf = ibuf;
    }

    public synchronized void setLength(int ilength) {
        if(ilength > buf.length)
            throw new IllegalArgumentException("illegal length");
        length = ilength;
    }
}
