package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.StringTokenizer;

public final class URL {
    private static final String protocolPathProp = "java.protocol.handler.pkgs";

    private String protocol;
    private String host;
    private int port = -1;
    private String file;
    private String ref;
    transient URLStreamHandler handler;
    private int hashCode = -1;

    public URL(String protocol, String host, int port, String file)
        throws MalformedURLException {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        int ind = file.indexOf('#');
        this.file = ind < 0 ? file: file.substring(0, ind);
        this.ref = ind < 0 ? null: file.substring(ind + 1);
        if((handler = getURLStreamHandler(protocol)) == null)
            throw new MalformedURLException("unknown protocol: " + protocol);
    }

    public URL(String protocol, String host, String file) throws MalformedURLException {
        this(protocol, host, -1, file);
    }

    public URL(String spec) throws MalformedURLException {
        this(null, spec);
    }

    public URL(URL context, String spec) throws MalformedURLException {
        String original = spec;
        int i, limit, c;
        int start = 0;
        String newProtocol = null;
        boolean aRef=false;

        try {
            limit = spec.length();
            while((limit > 0) && (spec.charAt(limit - 1) <= ' '))
                limit--;
            while((start < limit) && (spec.charAt(start) <= ' '))
                start++;

            if(spec.regionMatches(true, start, "url:", 0, 4))
                start += 4;
            if(start < spec.length() && spec.charAt(start) == '#')
                aRef=true;
            for(i = start ; !aRef && (i < limit) && ((c = spec.charAt(i)) != '/') ; i++) {
                if(c == ':') {
                    newProtocol = spec.substring(start, i).toLowerCase();
                    start = i + 1;
                    break;
                }
            }
            if((context != null) && ((newProtocol == null) || newProtocol.equals(context.protocol))) {
                protocol = context.protocol;
                host = context.host;
                port = context.port;
                file = context.file;
            }
            else
                protocol = newProtocol;

            if(protocol == null)
                throw new MalformedURLException("no protocol: "+original);

            if((handler = getURLStreamHandler(protocol)) == null)
                throw new MalformedURLException("unknown protocol: "+protocol);

            i = spec.indexOf('#', start);
            if(i >= 0) {
                ref = spec.substring(i + 1, limit);
                limit = i;
            }
            handler.parseURL(this, spec, start, limit);
        }
        catch(MalformedURLException e) {
            throw e;
        }
        catch(Exception e) {
            throw new MalformedURLException(original + ": " + e);
        }
    }

    protected void set(String protocol, String host, int port, String file, String ref) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.file = file;
        this.ref = ref;
    }

    public int getPort() {
        return port;
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
        return (ref == null) ? (obj instanceof URL) && sameFile((URL)obj) : (obj instanceof URL) && sameFile((URL)obj) && ref.equals(((URL)obj).ref);
    }

    public int hashCode() {
        if(hashCode == -1)
            hashCode = host.toLowerCase().hashCode() ^ file.hashCode() ^ protocol.hashCode();
        return hashCode;
    }

    boolean hostsEqual(String h1, String h2) {
        if(h1.equals(h2))
            return true;
        try {
            InetAddress a1 = InetAddress.getByName(h1);
            InetAddress a2 = InetAddress.getByName(h2);
            return a1.equals(a2);
        }
        catch(UnknownHostException e) {

        }
        return false;
    }

    public boolean sameFile(URL other) {
        return protocol.equals(other.protocol) && hostsEqual(host, other.host) && (port == other.port) && file.equals(other.file);
    }

    public String toString() {
        return toExternalForm();
    }

    public String toExternalForm() {
        return handler.toExternalForm(this);
    }

    public URLConnection openConnection() throws java.io.IOException {
        return handler.openConnection(this);
    }

    public final InputStream openStream() throws java.io.IOException {
        return openConnection().getInputStream();
    }

    public final Object getContent() throws java.io.IOException {
        return openConnection().getContent();
    }

    static URLStreamHandlerFactory factory;

    public static synchronized void setURLStreamHandlerFactory(URLStreamHandlerFactory fac) {
        if(factory != null)
            throw new Error("factory already defined");
        handlers.clear();
        factory = fac;
    }

    static Hashtable<String, URLStreamHandler> handlers = new Hashtable<>();

    static synchronized URLStreamHandler getURLStreamHandler(String protocol) {
        URLStreamHandler handler = handlers.get(protocol);
        if(handler == null) {
            if(factory != null)
                handler = factory.createURLStreamHandler(protocol);

            if(handler == null) {
                String packagePrefixList = System.getProperty(protocolPathProp, "");
                if(packagePrefixList != "")
                    packagePrefixList += "|";

                packagePrefixList += "sun.net.www.protocol";

                StringTokenizer packagePrefixIter = new StringTokenizer(packagePrefixList, "|");

                while(handler == null && packagePrefixIter.hasMoreTokens()) {
                    String packagePrefix = packagePrefixIter.nextToken().trim();
                    try {
                        String clname = packagePrefix + "." + protocol + ".Handler";
                        handler = (URLStreamHandler)Class.forName(clname).getConstructor().newInstance();
                    }
                    catch(Exception e) {

                    }
                }
            }
            if(handler != null)
                handlers.put(protocol, handler);
        }
        return handler;
    }

    // TODO
    // private synchronized void writeObject(java.io.ObjectOutputStream s) throws IOException {
    //     s.defaultWriteObject();
    // }

    // TODO
    // private synchronized void readObject(java.io.ObjectInputStream s) throws IOException, ClassNotFoundException {
    //     s.defaultReadObject();
    //     if((handler = getURLStreamHandler(protocol)) == null) {
    //         throw new IOException("unknown protocol: " + protocol);
    //     }
    // }
}
