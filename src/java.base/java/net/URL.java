package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.util.Hashtable;
import java.util.StringTokenizer;

public final class URL {
    private static final long L_SLASH = 0x800000000000L;
    private static final long H_SLASH = 0x0L;
    private static final long L_NON_PRINTABLE = 0xffffffffL;
    private static final long H_NON_PRINTABLE = 0x8000000000000000L;

    private String protocol;
    private String host;
    private int port = -1;
    private String file;
    private transient String query;
    private String authority;
    private transient String path;
    private transient String userInfo;
    private String ref;
    private transient InetAddress hostAddress;
    transient URLStreamHandler handler;
    private int hashCode = -1;
    private transient UrlDeserializedState tempState;

    public URL(String protocol, String host, int port, String file) throws MalformedURLException {
        this(protocol, host, port, file, null);
    }

    public URL(String protocol, String host, String file) throws MalformedURLException {
        this(protocol, host, -1, file);
    }

    public URL(String protocol, String host, int port, String file, URLStreamHandler handler) throws MalformedURLException {
        protocol = protocol.toLowerCase();
        this.protocol = protocol;
        if (host != null) {
            if (host.indexOf(':') >= 0 && !host.startsWith("["))
                host = "["+host+"]";
            this.host = host;

            if (port < -1)
                throw new MalformedURLException("Invalid port number :" + port);
            this.port = port;
            authority = (port == -1) ? host : host + ":" + port;
        }

        Parts parts = new Parts(file);
        path = parts.getPath();
        query = parts.getQuery();

        if (query != null)
            this.file = path + "?" + query;
        else
            this.file = path;
        ref = parts.getRef();

        if (handler == null && (handler = getURLStreamHandler(protocol)) == null)
            throw new MalformedURLException("unknown protocol: " + protocol);
        this.handler = handler;
        // TODO
        // if ("jar".equalsIgnoreCase(protocol)) {
        //     if (handler instanceof sun.net.www.protocol.jar.Handler) {
        //         String s = ((sun.net.www.protocol.jar.Handler) handler).checkNestedProtocol(file);
        //         if (s != null)
        //             throw new MalformedURLException(s);
        //     }
        // }
    }

    public URL(String spec) throws MalformedURLException {
        this(null, spec);
    }

    public URL(URL context, String spec) throws MalformedURLException {
        this(context, spec, null);
    }

    public URL(URL context, String spec, URLStreamHandler handler) throws MalformedURLException {
        String original = spec;
        int i, limit, c;
        int start = 0;
        String newProtocol = null;
        boolean aRef=false;
        boolean isRelative = false;

        try {
            limit = spec.length();
            while ((limit > 0) && (spec.charAt(limit - 1) <= ' '))
                limit--;
            while ((start < limit) && (spec.charAt(start) <= ' '))
                start++;

            if (spec.regionMatches(true, start, "url:", 0, 4))
                start += 4;
            if (start < spec.length() && spec.charAt(start) == '#')
                aRef = true;
            for (i = start ; !aRef && (i < limit) && ((c = spec.charAt(i)) != '/') ; i++) {
                if (c == ':') {
                    String s = spec.substring(start, i).toLowerCase();
                    if (isValidProtocol(s)) {
                        newProtocol = s;
                        start = i + 1;
                    }
                    break;
                }
            }

            protocol = newProtocol;
            if ((context != null) && ((newProtocol == null) || newProtocol.equalsIgnoreCase(context.protocol))) {
                if (handler == null)
                    handler = context.handler;

                if (context.path != null && context.path.startsWith("/"))
                    newProtocol = null;

                if (newProtocol == null) {
                    protocol = context.protocol;
                    authority = context.authority;
                    userInfo = context.userInfo;
                    host = context.host;
                    port = context.port;
                    file = context.file;
                    path = context.path;
                    isRelative = true;
                }
            }

            if (protocol == null)
                throw new MalformedURLException("no protocol: " + original);

            if (handler == null && (handler = getURLStreamHandler(protocol)) == null)
                throw new MalformedURLException("unknown protocol: " + protocol);

            this.handler = handler;

            i = spec.indexOf('#', start);
            if (i >= 0) {
                ref = spec.substring(i + 1, limit);
                limit = i;
            }

            if (isRelative && start == limit) {
                query = context.query;
                if (ref == null)
                    ref = context.ref;
            }

            handler.parseURL(this, spec, start, limit);

        }
        catch(MalformedURLException e) {
            throw e;
        }
        catch(Exception e) {
            MalformedURLException exception = new MalformedURLException(e.getMessage());
            exception.initCause(e);
            throw exception;
        }
    }

    private boolean isValidProtocol(String protocol) {
        int len = protocol.length();
        if (len < 1)
            return false;
        char c = protocol.charAt(0);
        if (!('a' <= c && c <= 'z') && !('A' <= c && c <= 'Z'))
            return false;
        for (int i = 1; i < len; i++) {
            c = protocol.charAt(i);
            if (!('a' <= c && c <= 'z') && !('A' <= c && c <= 'Z') && !('0' <= c && c <= '9') && c != '.' && c != '+' && c != '-')
                return false;
        }
        return true;
    }

    void set(String protocol, String host, int port, String file, String ref) {
        synchronized (this) {
            this.protocol = protocol;
            this.host = host;
            authority = port == -1 ? host : host + ":" + port;
            this.port = port;
            this.file = file;
            this.ref = ref;
            hashCode = -1;
            hostAddress = null;
            int q = file.lastIndexOf('?');
            if (q != -1) {
                query = file.substring(q+1);
                path = file.substring(0, q);
            }
            else
                path = file;
        }
    }

    void set(String protocol, String host, int port, String authority, String userInfo, String path, String query, String ref) {
        synchronized (this) {
            this.protocol = protocol;
            this.host = host;
            this.port = port;
            this.file = query == null ? path : path + "?" + query;
            this.userInfo = userInfo;
            this.path = path;
            this.ref = ref;
            hashCode = -1;
            hostAddress = null;
            this.query = query;
            this.authority = authority;
        }
    }

    synchronized InetAddress getHostAddress() {
        if (hostAddress != null)
            return hostAddress;

        if (host == null || host.isEmpty())
            return null;
        try {
            hostAddress = InetAddress.getByName(host);
        }
        catch (UnknownHostException | SecurityException ex) {
            return null;
        }
        return hostAddress;
    }

    public String getQuery() {
        return query;
    }

    public String getPath() {
        return path;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public String getAuthority() {
        return authority;
    }

    public int getPort() {
        return port;
    }

    public int getDefaultPort() {
        return handler.getDefaultPort();
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public String getFile() {
        return file;
    }

    public String getRef() {
        return ref;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof URL))
            return false;
        URL u2 = (URL)obj;

        return handler.equals(this, u2);
    }

    public synchronized int hashCode() {
        if (hashCode != -1)
            return hashCode;

        hashCode = handler.hashCode(this);
        return hashCode;
    }

    public boolean sameFile(URL other) {
        return handler.sameFile(this, other);
    }

    public String toString() {
        return toExternalForm();
    }

    public String toExternalForm() {
        return handler.toExternalForm(this);
    }

    // TODO
    // public URI toURI() throws URISyntaxException {
    //     URI uri = new URI(toString());
    //     if (authority != null && isBuiltinStreamHandler(handler)) {
    //         String s = IPAddressUtil.checkAuthority(this);
    //         if (s != null) throw new URISyntaxException(authority, s);
    //     }
    //     return uri;
    // }

    public URLConnection openConnection() throws IOException {
        return handler.openConnection(this);
    }

    public URLConnection openConnection(Proxy proxy) throws IOException {
        if (proxy == null)
            throw new IllegalArgumentException("proxy can not be null");

        return handler.openConnection(this, proxy);
    }

    public final InputStream openStream() throws IOException {
        return openConnection().getInputStream();
    }

    public final Object getContent() throws IOException {
        return openConnection().getContent();
    }

    public final Object getContent(Class<?>[] classes) throws IOException {
        return openConnection().getContent(classes);
    }

    static URLStreamHandlerFactory factory;
    private static Object streamHandlerLock = new Object();

    public static void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        synchronized (streamHandlerLock) {
            if (factory != null)
                throw new Error("factory already defined");
            handlers.clear();
            factory = fac;
        }
    }

    static Hashtable<String, URLStreamHandler> handlers = new Hashtable<>();

    static URLStreamHandler getURLStreamHandler(String protocol) {
        URLStreamHandler handler = handlers.get(protocol);
        if (handler == null) {
            if (factory != null)
                handler = factory.createURLStreamHandler(protocol);

            if (handler == null) {
                try {
                    String clname = "flint.net.www.protocol." + protocol + ".Handler";
                    handler = (URLStreamHandler)Class.forName(clname).getConstructor().newInstance();
                }
                catch (Exception e) {

                }
            }
            if (handler != null)
                handlers.put(protocol, handler);
        }
        return handler;
    }

    private void resetState() {
        this.protocol = null;
        this.host = null;
        this.port = -1;
        this.file = null;
        this.authority = null;
        this.ref = null;
        this.hashCode = -1;
        this.handler = null;
        this.query = null;
        this.path = null;
        this.userInfo = null;
        this.tempState = null;
    }

    private void setSerializedHashCode(int hc) {
        this.hashCode = hc;
    }
}

class Parts {
    String path, query, ref;

    Parts(String file) {
        int ind = file.indexOf('#');
        ref = ind < 0 ? null: file.substring(ind + 1);
        file = ind < 0 ? file: file.substring(0, ind);
        int q = file.lastIndexOf('?');
        if (q != -1) {
            query = file.substring(q+1);
            path = file.substring(0, q);
        }
        else
            path = file;
    }

    String getPath() {
        return path;
    }

    String getQuery() {
        return query;
    }

    String getRef() {
        return ref;
    }
}

final class UrlDeserializedState {
    private final String protocol;
    private final String host;
    private final int port;
    private final String authority;
    private final String file;
    private final String ref;
    private final int hashCode;

    public UrlDeserializedState(String protocol, String host, int port, String authority, String file, String ref, int hashCode) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.authority = authority;
        this.file = file;
        this.ref = ref;
        this.hashCode = hashCode;
    }

    String getProtocol() {
        return protocol;
    }

    String getHost() {
        return host;
    }

    String getAuthority () {
        return authority;
    }

    int getPort() {
        return port;
    }

    String getFile () {
        return file;
    }

    String getRef () {
        return ref;
    }

    int getHashCode () {
        return hashCode;
    }

    String reconstituteUrlString() {
        int len = protocol.length() + 1;
        if (authority != null && authority.length() > 0)
            len += 2 + authority.length();
        if (file != null)
            len += file.length();
        if (ref != null)
            len += 1 + ref.length();
        StringBuilder result = new StringBuilder(len);
        result.append(protocol);
        result.append(":");
        if (authority != null && authority.length() > 0) {
            result.append("//");
            result.append(authority);
        }
        if (file != null)
            result.append(file);
        if (ref != null) {
            result.append("#");
            result.append(ref);
        }
        return result.toString();
    }
}
