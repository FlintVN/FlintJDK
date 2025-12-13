package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileDescriptor;
import java.io.ByteArrayOutputStream;

class PlainSocketImpl extends SocketImpl {
    int timeout;

    private static final int SOCKS_PROTO_VERS = 4;
    private static final int SOCKS_REPLY_VERS = 4;

    private static final int COMMAND_CONNECT = 1;
    private static final int COMMAND_BIND = 2;

    private static final int REQUEST_GRANTED = 90;
    private static final int REQUEST_REJECTED = 91;
    private static final int REQUEST_REJECTED_NO_IDENTD  = 92;
    private static final int REQUEST_REJECTED_DIFF_IDENTS = 93;

    public static final String socksServerProp = "socksProxyHost";
    public static final String socksPortProp = "socksProxyPort";

    public static final String socksDefaultPortStr = "1080";

    static {
        initProto();
    }

    protected synchronized void create(boolean stream) throws IOException {
        fd = new FileDescriptor();
        socketCreate(stream);
    }

    protected void connect(String host, int port) throws UnknownHostException, IOException {
        IOException pending = null;
        try {
            InetAddress address = InetAddress.getByName(host);

            try {
                connectToAddress(address, port);
                return;
            }
            catch(IOException e) {
                pending = e;
            }
        }
        catch(UnknownHostException e) {
            pending = e;
        }

        close();
        throw pending;
    }

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
        if(usingSocks())
            doSOCKSConnect(address, port);
        else
            doConnect(address, port);
    }

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
                int tmp = ((Integer) val).intValue();
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

    public Object getOption(int opt) throws SocketException {
        if(opt == SO_TIMEOUT)
            return new Integer(timeout);
        int ret = socketGetOption(opt);

        switch(opt) {
            case TCP_NODELAY:
                return (ret == -1) ? new Boolean(false): new Boolean(true);
            case SO_LINGER:
                return (ret == -1) ? new Boolean(false): (Object)(new Integer(ret));
            case SO_BINDADDR:
                InetAddress in = new InetAddress();
                in.address = ret;
                return in;
        }
        return null;
    }

    private void doSOCKSConnect(InetAddress address, int port) throws IOException {
        connectToSocksServer();

        sendSOCKSCommandPacket(COMMAND_CONNECT, address, port);

        int protoStatus = getSOCKSReply();

        switch(protoStatus) {
            case REQUEST_GRANTED:
            return;
            case REQUEST_REJECTED:
            case REQUEST_REJECTED_NO_IDENTD:
                throw new SocketException("SOCKS server cannot conect to identd");
            case REQUEST_REJECTED_DIFF_IDENTS:
            throw new SocketException("User name does not match identd name");
        }
    }

    private int getSOCKSReply() throws IOException {
        InputStream in = getInputStream();
        byte response[] = new byte[8];

        int code;
        if((code = in.read(response)) != response.length)
            throw new SocketException("Malformed reply from SOCKS server");

        if(response[0] != 0)
            throw new SocketException("Malformed reply from SOCKS server");

        return response[1];
    }

    private void connectToSocksServer() throws IOException {
        String socksServerString = System.getProperty(socksServerProp);
        if(socksServerString == null)
            return;

        InetAddress socksServer = InetAddress.getByName(socksServerString);

        String socksPortString = System.getProperty(socksPortProp, socksDefaultPortStr);

        int socksServerPort;
        try {
            socksServerPort = Integer.parseInt(socksPortString);
        }
        catch(Exception e) {
            throw new SocketException("Bad port number format");
        }

        doConnect(socksServer, socksServerPort);
    }

    private void doConnect(InetAddress address, int port) throws IOException {
        IOException pending = null;

        for(int i = 0 ; i < 3 ; i++) {
            try {
                socketConnect(address, port);
                return;
            }
            catch(ProtocolException e) {
                close();
                fd = new FileDescriptor();
                socketCreate(true);
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


    private void sendSOCKSCommandPacket(int command, InetAddress address, int port) throws IOException {
        byte commandPacket[] = makeCommandPacket(command, address, port);
        OutputStream out = getOutputStream();

        out.write(commandPacket);
    }

    private byte[] makeCommandPacket(int command, InetAddress address, int port) {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream(8 + 1);

        byteStream.write(SOCKS_PROTO_VERS);
        byteStream.write(command);


        byteStream.write((port >> 8) & 0xff);
        byteStream.write((port >> 0) & 0xff);

        byte addressBytes[] = address.getAddress();
        byteStream.write(addressBytes, 0, addressBytes.length);

        String userName = System.getProperty("user.name");
        byte userNameBytes[] = new byte[userName.length()];
        userName.getBytes(0, userName.length(), userNameBytes, 0);

        byteStream.write(userNameBytes, 0, userNameBytes.length);
        byteStream.write(0);

        return byteStream.toByteArray();
    }

    private boolean usingSocks() {
        return (System.getProperty(socksServerProp) != null);
    }

    protected synchronized void bind(InetAddress address, int lport) throws IOException {
        socketBind(address, lport);
    }

    protected synchronized void listen(int count) throws IOException {
        socketListen(count);
    }

    protected synchronized void accept(SocketImpl s) throws IOException {
        socketAccept(s);
    }

    protected synchronized InputStream getInputStream() throws IOException {
        return new SocketInputStream(this);
    }

    protected synchronized OutputStream getOutputStream() throws IOException {
        return new SocketOutputStream(this);
    }

    protected synchronized int available() throws IOException {
        return socketAvailable();
    }

    protected void close() throws IOException {
        if(fd != null) {
            socketClose();
            fd = null;
        }
    }

    protected void finalize() throws IOException {
        close();
    }

    private native void socketCreate(boolean isServer) throws IOException;
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
