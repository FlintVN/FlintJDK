package java.net;

import java.io.InputStream;
import java.io.IOException;
// import java.util.Date;

abstract public class HttpURLConnection extends URLConnection {
    protected String method = "GET";
    protected int chunkLength = -1;
    protected int fixedContentLength = -1;
    protected long fixedContentLengthLong = -1;

    public String getHeaderFieldKey(int n) {
        return null;
    }

    public void setFixedLengthStreamingMode(int contentLength) {
        if (connected)
            throw new IllegalStateException("Already connected");
        if (chunkLength != -1)
            throw new IllegalStateException("Chunked encoding streaming mode set");
        if (contentLength < 0)
            throw new IllegalArgumentException("invalid content length");
        fixedContentLength = contentLength;
    }

    public void setFixedLengthStreamingMode(long contentLength) {
        if (connected)
            throw new IllegalStateException("Already connected");
        if (chunkLength != -1)
            throw new IllegalStateException("Chunked encoding streaming mode set");
        if (contentLength < 0)
            throw new IllegalArgumentException("invalid content length");
        fixedContentLengthLong = contentLength;
    }

    private static final int DEFAULT_CHUNK_SIZE = 4096;

    public void setChunkedStreamingMode(int chunklen) {
        if (connected)
            throw new IllegalStateException("Can't set streaming mode: already connected");
        if (fixedContentLength != -1 || fixedContentLengthLong != -1)
            throw new IllegalStateException("Fixed length streaming mode set");
        chunkLength = chunklen <=0? DEFAULT_CHUNK_SIZE : chunklen;
    }

    public String getHeaderField(int n) {
        return null;
    }

    protected int responseCode = -1;
    protected String responseMessage = null;
    private static boolean followRedirects = true;
    protected boolean instanceFollowRedirects = followRedirects;

    private static final String[] methods = {
        "GET", "POST", "HEAD", "OPTIONS", "PUT", "DELETE", "TRACE"
    };

    protected HttpURLConnection(URL u) {
        super(u);
    }

    public static void setFollowRedirects(boolean set) {
        followRedirects = set;
    }

    public static boolean getFollowRedirects() {
        return followRedirects;
    }

    public void setInstanceFollowRedirects(boolean followRedirects) {
        instanceFollowRedirects = followRedirects;
    }

    public boolean getInstanceFollowRedirects() {
        return instanceFollowRedirects;
    }

    public void setRequestMethod(String method) throws ProtocolException {
        if (connected)
            throw new ProtocolException("Can't reset method: already connected");

        for (int i = 0; i < methods.length; i++) {
            if (methods[i].equals(method)) {
                this.method = method;
                return;
            }
        }
        throw new ProtocolException("Invalid HTTP method: " + method);
    }

    public String getRequestMethod() {
        return method;
    }

    public int getResponseCode() throws IOException {
        if (responseCode != -1)
            return responseCode;

        Exception exc = null;
        try {
            getInputStream();
        }
        catch (Exception e) {
            exc = e;
        }

        String statusLine = getHeaderField(0);
        if (statusLine == null) {
            if (exc != null) {
                if (exc instanceof RuntimeException)
                    throw (RuntimeException)exc;
                else
                    throw (IOException)exc;
            }
            return -1;
        }

        if (statusLine.startsWith("HTTP/1.")) {
            int codePos = statusLine.indexOf(' ');
            if (codePos > 0) {
                int phrasePos = statusLine.indexOf(' ', codePos+1);
                if (phrasePos > 0 && phrasePos < statusLine.length())
                    responseMessage = statusLine.substring(phrasePos + 1);

                if (phrasePos < 0)
                    phrasePos = statusLine.length();

                try {
                    responseCode = Integer.parseInt(statusLine.substring(codePos+1, phrasePos));
                    return responseCode;
                }
                catch (NumberFormatException e) {

                }
            }
        }
        return -1;
    }

    public String getResponseMessage() throws IOException {
        getResponseCode();
        return responseMessage;
    }

    @SuppressWarnings("deprecation")
    public long getHeaderFieldDate(String name, long Default) {
        // String dateString = getHeaderField(name);
        // try {
        //     if (dateString.indexOf("GMT") == -1)
        //         dateString = dateString + " GMT";
        //     return Date.parse(dateString);
        // }
        // catch (Exception e) {

        // }
        // return Default;

        // TODO
        throw new UnsupportedOperationException();
    }

    public abstract void disconnect();

    public abstract boolean usingProxy();

    // TODO
    // public Permission getPermission() throws IOException {
    //     int port = url.getPort();
    //     port = port < 0 ? 80 : port;
    //     String host = url.getHost() + ":" + port;
    //     Permission permission = new SocketPermission(host, "connect");
    //     return permission;
    // }

    public InputStream getErrorStream() {
        return null;
    }

    /* 2XX: generally "OK" */

    /**
     * HTTP Status-Code 200: OK.
     */
    public static final int HTTP_OK = 200;

    /**
     * HTTP Status-Code 201: Created.
     */
    public static final int HTTP_CREATED = 201;

    /**
     * HTTP Status-Code 202: Accepted.
     */
    public static final int HTTP_ACCEPTED = 202;

    /**
     * HTTP Status-Code 203: Non-Authoritative Information.
     */
    public static final int HTTP_NOT_AUTHORITATIVE = 203;

    /**
     * HTTP Status-Code 204: No Content.
     */
    public static final int HTTP_NO_CONTENT = 204;

    /**
     * HTTP Status-Code 205: Reset Content.
     */
    public static final int HTTP_RESET = 205;

    /**
     * HTTP Status-Code 206: Partial Content.
     */
    public static final int HTTP_PARTIAL = 206;

    /* 3XX: relocation/redirect */

    /**
     * HTTP Status-Code 300: Multiple Choices.
     */
    public static final int HTTP_MULT_CHOICE = 300;

    /**
     * HTTP Status-Code 301: Moved Permanently.
     */
    public static final int HTTP_MOVED_PERM = 301;

    /**
     * HTTP Status-Code 302: Temporary Redirect.
     */
    public static final int HTTP_MOVED_TEMP = 302;

    /**
     * HTTP Status-Code 303: See Other.
     */
    public static final int HTTP_SEE_OTHER = 303;

    /**
     * HTTP Status-Code 304: Not Modified.
     */
    public static final int HTTP_NOT_MODIFIED = 304;

    /**
     * HTTP Status-Code 305: Use Proxy.
     */
    public static final int HTTP_USE_PROXY = 305;

    /* 4XX: client error */

    /**
     * HTTP Status-Code 400: Bad Request.
     */
    public static final int HTTP_BAD_REQUEST = 400;

    /**
     * HTTP Status-Code 401: Unauthorized.
     */
    public static final int HTTP_UNAUTHORIZED = 401;

    /**
     * HTTP Status-Code 402: Payment Required.
     */
    public static final int HTTP_PAYMENT_REQUIRED = 402;

    /**
     * HTTP Status-Code 403: Forbidden.
     */
    public static final int HTTP_FORBIDDEN = 403;

    /**
     * HTTP Status-Code 404: Not Found.
     */
    public static final int HTTP_NOT_FOUND = 404;

    /**
     * HTTP Status-Code 405: Method Not Allowed.
     */
    public static final int HTTP_BAD_METHOD = 405;

    /**
     * HTTP Status-Code 406: Not Acceptable.
     */
    public static final int HTTP_NOT_ACCEPTABLE = 406;

    /**
     * HTTP Status-Code 407: Proxy Authentication Required.
     */
    public static final int HTTP_PROXY_AUTH = 407;

    /**
     * HTTP Status-Code 408: Request Time-Out.
     */
    public static final int HTTP_CLIENT_TIMEOUT = 408;

    /**
     * HTTP Status-Code 409: Conflict.
     */
    public static final int HTTP_CONFLICT = 409;

    /**
     * HTTP Status-Code 410: Gone.
     */
    public static final int HTTP_GONE = 410;

    /**
     * HTTP Status-Code 411: Length Required.
     */
    public static final int HTTP_LENGTH_REQUIRED = 411;

    /**
     * HTTP Status-Code 412: Precondition Failed.
     */
    public static final int HTTP_PRECON_FAILED = 412;

    /**
     * HTTP Status-Code 413: Request Entity Too Large.
     */
    public static final int HTTP_ENTITY_TOO_LARGE = 413;

    /**
     * HTTP Status-Code 414: Request-URI Too Large.
     */
    public static final int HTTP_REQ_TOO_LONG = 414;

    /**
     * HTTP Status-Code 415: Unsupported Media Type.
     */
    public static final int HTTP_UNSUPPORTED_TYPE = 415;

    /* 5XX: server error */

    /**
     * HTTP Status-Code 500: Internal Server Error.
     * @deprecated   it is misplaced and shouldn't have existed.
     */
    @Deprecated
    public static final int HTTP_SERVER_ERROR = 500;

    /**
     * HTTP Status-Code 500: Internal Server Error.
     */
    public static final int HTTP_INTERNAL_ERROR = 500;

    /**
     * HTTP Status-Code 501: Not Implemented.
     */
    public static final int HTTP_NOT_IMPLEMENTED = 501;

    /**
     * HTTP Status-Code 502: Bad Gateway.
     */
    public static final int HTTP_BAD_GATEWAY = 502;

    /**
     * HTTP Status-Code 503: Service Unavailable.
     */
    public static final int HTTP_UNAVAILABLE = 503;

    /**
     * HTTP Status-Code 504: Gateway Timeout.
     */
    public static final int HTTP_GATEWAY_TIMEOUT = 504;

    /**
     * HTTP Status-Code 505: HTTP Version Not Supported.
     */
    public static final int HTTP_VERSION = 505;
}
