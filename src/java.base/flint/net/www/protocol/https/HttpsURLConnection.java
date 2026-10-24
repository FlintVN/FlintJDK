package flint.net.www.protocol.https;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import flint.net.TlsSocket;
import java.net.URL;
import flint.net.www.protocol.http.HttpURLConnection;

public class HttpsURLConnection extends HttpURLConnection {
    private TlsSocket tlsSocket;

    protected HttpsURLConnection(URL u, Handler handler) {
        this(u, null, handler);
    }

    public HttpsURLConnection(URL u, String host, int port) {
        this(u, new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port)), new Handler());
    }

    protected HttpsURLConnection(URL u, Proxy p) {
        this(u, p, new Handler());
    }

    protected HttpsURLConnection(URL u, Proxy p, Handler handler) {
        super(u, p, handler);
    }

    @Override
    protected boolean useFullRequestURI() {
        return false;
    }

    @Override
    protected void openSocket() throws IOException {
        String targetHost = url.getHost();
        if (targetHost == null || targetHost.isEmpty())
            throw new ProtocolException("Invalid host in URL: " + url);
        int targetPort = url.getPort();
        if (targetPort == -1)
            targetPort = url.getDefaultPort();

        if (usingProxy())
            throw new ProtocolException("Authenticated HTTPS does not support HTTP CONNECT proxies");
        tlsSocket = new TlsSocket(targetHost, targetPort, getConnectTimeout(), getReadTimeout());
        socketIn = new BufferedInputStream(tlsSocket.getInputStream());
        socketOut = new BufferedOutputStream(tlsSocket.getOutputStream());
    }

    /** The platform currently does not expose the negotiated cipher name. */
    public String getCipherSuite() {
        return null;
    }

    @Override
    protected void closeSocket() {
        if (tlsSocket != null) {
            tlsSocket.close();
            tlsSocket = null;
        }
        super.closeSocket();
    }
}
