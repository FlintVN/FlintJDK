package flint.net.www.protocol.http;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.Map;
import flint.net.www.MessageHeader;

public class HttpURLConnection extends java.net.HttpURLConnection {
    private static final int MAX_REDIRECTS = 5;
    protected static final String USER_AGENT = "FlintJDK/1.0";
    private static final int MAX_ERROR_BODY = 4096;

    protected final Handler handler;
    protected Proxy instProxy;
    protected Socket socket;
    protected InputStream socketIn;
    protected OutputStream socketOut;

    private MessageHeader responses;

    private InputStream inputStream;
    private InputStream errorStream;
    private ByteArrayOutputStream poster;
    private OutputStream streamingOut;
    private boolean sentRequests = false;
    private boolean parsedHeader = false;
    private boolean inGetInputStream = false;

    protected HttpURLConnection(URL u, Handler handler) {
        this(u, null, handler);
    }

    public HttpURLConnection(URL u, String host, int port) {
        this(u, new Proxy(Proxy.Type.HTTP, InetSocketAddress.createUnresolved(host, port)), new Handler());
    }

    protected HttpURLConnection(URL u, Proxy p) {
        this(u, p, new Handler());
    }

    protected HttpURLConnection(URL u, Proxy p, Handler handler) {
        super(u);
        this.instProxy = p;
        this.handler = handler;
        this.requests = new MessageHeader();
        this.responses = new MessageHeader();
    }

    @Override
    public synchronized void connect() throws IOException {
        if (connected)
            return;
        try {
            openSocket();
        }
        catch (IOException | RuntimeException e) {
            closeSocket();
            throw e;
        }
        connected = true;
    }

    protected void openSocket() throws IOException {
        InetSocketAddress endpoint = resolveConnectEndpoint();

        socket = new Socket();
        int connectTimeout = getConnectTimeout();
        if (connectTimeout > 0)
            socket.setSoTimeout(connectTimeout);
        socket.connect(endpoint);
        int readTimeout = getReadTimeout();
        if (readTimeout > 0 || connectTimeout > 0)
            socket.setSoTimeout(readTimeout);

        socketIn = new BufferedInputStream(socket.getInputStream());
        socketOut = new BufferedOutputStream(socket.getOutputStream());
    }

    protected InetSocketAddress resolveConnectEndpoint() throws ProtocolException {
        if (usingProxy()) {
            if (instProxy != null && instProxy.type() == Proxy.Type.HTTP)
                return (InetSocketAddress)instProxy.address();
            return new InetSocketAddress(handler.proxy, handler.proxyPort);
        }
        String connectHost = url.getHost();
        if (connectHost == null || connectHost.isEmpty())
            throw new ProtocolException("Invalid host in URL: " + url);
        int connectPort = url.getPort();
        if (connectPort == -1)
            connectPort = handler.getDefaultPort();
        return new InetSocketAddress(connectHost, connectPort);
    }

    @Override
    public boolean usingProxy() {
        if (instProxy != null)
            return instProxy.type() == Proxy.Type.HTTP;
        return handler != null && handler.proxy != null && handler.proxyPort > 0;
    }

    @Override
    public synchronized OutputStream getOutputStream() throws IOException {
        if (!doOutput)
            throw new ProtocolException("cannot write to a URLConnection if doOutput=false - call setDoOutput(true)");

        if ("GET".equals(method))
            method = "POST";

        if ("HEAD".equals(method) || "OPTIONS".equals(method) || "TRACE".equals(method))
            throw new ProtocolException("HTTP method " + method + " doesn't support output");

        if (inputStream != null)
            throw new ProtocolException("Cannot write output after reading input.");

        connect();

        if (streamingOut != null)
            return streamingOut;
        if (poster != null)
            return poster;

        long fixedLen = fixedContentLengthLong != -1 ? fixedContentLengthLong : fixedContentLength;
        if (fixedLen != -1) {
            writeRequestHeaders(fixedLen, false);
            streamingOut = new FixedLengthOutputStream(socketOut, fixedLen);
            return streamingOut;
        }
        else if (chunkLength != -1) {
            writeRequestHeaders(-1, true);
            streamingOut = new ChunkedOutputStream(socketOut, chunkLength);
            return streamingOut;
        }
        else {
            poster = new ByteArrayOutputStream();
            return poster;
        }
    }

    protected boolean useFullRequestURI() {
        return usingProxy();
    }

    private void writeRequestHeaders(long contentLength, boolean chunked) throws IOException {
        if (sentRequests)
            return;

        String requestURI;
        if (useFullRequestURI())
            requestURI = url.toExternalForm();
        else {
            requestURI = url.getFile();
            if (requestURI == null || requestURI.isEmpty())
                requestURI = "/";
        }

        String requestLine = method + " " + requestURI + " HTTP/1.1";
        MessageHeader reqHeader = new MessageHeader();
        reqHeader.prepend(requestLine, null);

        if (requests != null) {
            for (int i = 0; ; i++) {
                String k = requests.getKey(i);
                String v = requests.getValue(i);
                if (k == null && v == null)
                    break;
                if (k != null)
                    reqHeader.add(k, v);
            }
        }

        String hostHeader = url.getHost();
        int port = url.getPort();
        if (port != -1 && port != handler.getDefaultPort())
            hostHeader = hostHeader + ":" + port;

        reqHeader.setIfNotSet("Host", hostHeader);
        reqHeader.setIfNotSet("User-Agent", USER_AGENT);
        reqHeader.setIfNotSet("Accept", "*/*");
        reqHeader.setIfNotSet("Connection", "close");

        if (doOutput) {
            reqHeader.setIfNotSet("Content-Type", "application/x-www-form-urlencoded");
            if (chunked)
                reqHeader.set("Transfer-Encoding", "chunked");
            else if (contentLength >= 0)
                reqHeader.set("Content-Length", Long.toString(contentLength));
        }

        ByteArrayOutputStream headBuf = new ByteArrayOutputStream(256);
        PrintStream ps = new PrintStream(headBuf);
        reqHeader.print(ps);
        headBuf.writeTo(socketOut);
        socketOut.flush();
        sentRequests = true;
    }

    @Override
    public synchronized InputStream getInputStream() throws IOException {
        if (!doInput)
            throw new ProtocolException("Cannot read from URLConnection if doInput=false (call setDoInput(true))");

        if (inGetInputStream)
            return null;

        if (responseCode >= HTTP_BAD_REQUEST)
            throwError(responseCode);

        if (inputStream != null)
            return inputStream;

        inGetInputStream = true;
        boolean success = false;
        try {
            int redirects = 0;
            while (true) {
                connect();

                if (streamingOut != null)
                    streamingOut.close();
                else if (poster != null) {
                    writeRequestHeaders(poster.size(), false);
                    if (poster.size() > 0) {
                        poster.writeTo(socketOut);
                        socketOut.flush();
                    }
                }
                else if (!sentRequests) {
                    writeRequestHeaders(-1, false);
                }

                responses.parseHeader(socketIn);
                parsedHeader = true;
                int code = getResponseCode();
                if (code == -1)
                    throw new ProtocolException("Invalid HTTP response: " + responses.getValue(0));

                while (code == 100) {
                    responses.reset();
                    parsedHeader = false;
                    responseCode = -1;
                    responseMessage = null;
                    responses.parseHeader(socketIn);
                    parsedHeader = true;
                    code = getResponseCode();
                    if (code == -1)
                        throw new ProtocolException("Invalid HTTP response: " + responses.getValue(0));
                }

                if (shouldRedirect(code) && redirects < MAX_REDIRECTS) {
                    String location = responses.findValue("Location");
                    if (location != null) {
                        URL newUrl = new URL(url, location);
                        if (newUrl.getProtocol().equalsIgnoreCase(url.getProtocol())) {
                            closeSocket();
                            connected = false;
                            sentRequests = false;
                            parsedHeader = false;
                            responses.reset();
                            responseCode = -1;
                            responseMessage = null;
                            url = newUrl;
                            if (code == HTTP_SEE_OTHER || ((code == HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP) && "POST".equals(method))) {
                                method = "GET";
                                doOutput = false;
                                poster = null;
                                streamingOut = null;
                            }
                            redirects++;
                            continue;
                        }
                    }
                }
                break;
            }

            inputStream = createBodyStream();

            if (responseCode >= HTTP_BAD_REQUEST) {
                errorStream = bufferErrorBody(inputStream);
                inputStream = null;
                throwError(responseCode);
            }

            success = true;
            return inputStream;
        }
        finally {
            inGetInputStream = false;
            if (!success)
                closeSocket();
        }
    }

    private boolean shouldRedirect(int code) {
        if (!getInstanceFollowRedirects())
            return false;
        if (streamingOut != null)
            return false;
        return code == HTTP_MOVED_PERM || code == HTTP_MOVED_TEMP ||
               code == HTTP_SEE_OTHER || code == 307 || code == 308;
    }

    private InputStream createBodyStream() throws IOException {
        if ("HEAD".equals(method) || responseCode == HTTP_NO_CONTENT || responseCode == HTTP_NOT_MODIFIED)
            return new FixedLengthInputStream(socketIn, 0);

        String transferEncoding = responses.findValue("Transfer-Encoding");
        if (transferEncoding != null && transferEncoding.toLowerCase().indexOf("chunked") >= 0)
            return new ChunkedInputStream(socketIn);

        String contentLenStr = responses.findValue("Content-Length");
        if (contentLenStr != null) {
            try {
                long contentLen = Long.parseLong(contentLenStr.trim());
                if (contentLen >= 0)
                    return new FixedLengthInputStream(socketIn, contentLen);
            }
            catch (NumberFormatException e) {

            }
        }
        return socketIn;
    }

    private static InputStream bufferErrorBody(InputStream body) {
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        byte[] tmp = new byte[512];
        try {
            int n;
            while (buf.size() < MAX_ERROR_BODY && (n = body.read(tmp, 0, Math.min(tmp.length, MAX_ERROR_BODY - buf.size()))) > 0)
                buf.write(tmp, 0, n);
        }
        catch (IOException e) {

        }
        return new ByteArrayInputStream(buf.toByteArray());
    }

    private void throwError(int code) throws IOException {
        if (code == HTTP_NOT_FOUND || code == HTTP_GONE)
            throw new FileNotFoundException(url.toString());
        throw new IOException("Server returned HTTP response code: " + code + " for URL: " + url.toString());
    }

    @Override
    public InputStream getErrorStream() {
        if (connected && responseCode >= HTTP_BAD_REQUEST)
            return errorStream;
        return null;
    }

    private void ensureResponseHeaders() {
        if (!parsedHeader) {
            try {
                getInputStream();
            }
            catch (IOException e) {

            }
        }
    }

    @Override
    public String getHeaderField(String name) {
        ensureResponseHeaders();
        if (name == null)
            return responses.getValue(0);
        return responses.findValue(name);
    }

    @Override
    public String getHeaderField(int n) {
        ensureResponseHeaders();
        return responses.getValue(n);
    }

    @Override
    public String getHeaderFieldKey(int n) {
        ensureResponseHeaders();
        return responses.getKey(n);
    }

    @Override
    public Map<String, List<String>> getHeaderFields() {
        ensureResponseHeaders();
        return responses.getHeaders();
    }

    @Override
    public synchronized void disconnect() {
        closeSocket();
        connected = false;
    }

    protected void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            }
            catch (IOException e) {

            }
            socket = null;
            socketIn = null;
            socketOut = null;
        }
    }
}
