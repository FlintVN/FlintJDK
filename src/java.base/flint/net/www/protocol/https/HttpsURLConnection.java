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
    private boolean captureSessionInfo;
    private SessionInfo sessionInfo;

    public static final class SessionInfo {
        public final String protocolVersion;
        public final String cipherSuite;
        public final String subject;
        public final String issuer;
        public final String certificateVersion;
        public final String signatureAlgorithm;
        public final String serialNumber;
        public final long notBefore;
        public final long notAfter;

        private SessionInfo(String[] fields) {
            protocolVersion = fields[0];
            cipherSuite = fields[1];
            subject = fields[2];
            issuer = fields[3];
            certificateVersion = fields[4];
            signatureAlgorithm = fields[5];
            serialNumber = fields[6];
            notBefore = Long.parseLong(fields[7]);
            notAfter = Long.parseLong(fields[8]);
        }
    }

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
        sessionInfo = null;
        tlsSocket = new TlsSocket(targetHost, targetPort, getConnectTimeout(), getReadTimeout());
        if (captureSessionInfo)
            sessionInfo = new SessionInfo(tlsSocket.getSessionInfo());
        socketIn = new BufferedInputStream(tlsSocket.getInputStream());
        socketOut = new BufferedOutputStream(tlsSocket.getOutputStream());
    }

    public synchronized void enableSessionInfo() {
        captureSessionInfo = true;
    }

    public synchronized SessionInfo getSessionInfo() throws IOException {
        if (sessionInfo == null) {
            connect();
            if (sessionInfo == null && tlsSocket != null)
                sessionInfo = new SessionInfo(tlsSocket.getSessionInfo());
            if (sessionInfo == null)
                throw new IOException("TLS session information is unavailable");
        }
        return sessionInfo;
    }

    public synchronized String getCipherSuite() {
        return sessionInfo == null ? null : sessionInfo.cipherSuite;
    }

    @Override
    public synchronized void disconnect() {
        super.disconnect();
        sessionInfo = null;
    }

    @Override
    protected void closeSocket() {
        if (tlsSocket != null) {
            tlsSocket.close();
            tlsSocket = null;
        }
        super.closeSocket();
        socketIn = null;
        socketOut = null;
    }
}
