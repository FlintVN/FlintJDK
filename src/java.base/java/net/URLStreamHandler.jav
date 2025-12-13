package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;

public abstract class URLStreamHandler {
    abstract protected URLConnection openConnection(URL u) throws IOException;

    protected void parseURL(URL u, String spec, int start, int limit) {
        String protocol = u.getProtocol();
        String host = u.getHost();
        int port = u.getPort();
        String file = u.getFile();
        String ref = u.getRef();

        int i;
        if((start <= limit - 2) && (spec.charAt(start) == '/') &&
            (spec.charAt(start + 1) == '/')) {
            start += 2;
            i = spec.indexOf('/', start);
            if(i < 0)
                i = limit;
            int prn = spec.indexOf(':', start);
            port = -1;
            if((prn < i) && (prn >= 0)) {
                try {
                    port = Integer.parseInt(spec.substring(prn + 1, i));
                }
                catch(Exception e) {
                    // ignore bogus port numbers
                }
                if(prn > start)
                    host = spec.substring(start, prn);
            }
            else
                host = spec.substring(start, i);
            start = i;
            file = null;
        }
        else if(host == null)
            host = "";
        if(start < limit) {
            if(file != null) {
                int questionMarkIndex = file.indexOf('?');
                if(questionMarkIndex > -1) {
                    int lastSlashIndex = file.lastIndexOf('?', questionMarkIndex);
                    file = file.substring(0, ++lastSlashIndex);
                }
            }
            if(spec.charAt(start) == '/')
                file = spec.substring(start, limit);
            else if(file != null && file.length() > 0) {
                int ind = Math.max(file.lastIndexOf('/'), file.lastIndexOf(File.separatorChar));
                file = file.substring(0, ind) + "/" + spec.substring(start, limit);
            }
            else
                file = "/" + spec.substring(start, limit);
        }
        if((file == null) || (file.length() == 0))
            file = "/";
        while((i = file.indexOf("/./")) >= 0)
            file = file.substring(0, i) + file.substring(i + 2);
        while((i = file.indexOf("/../")) >= 0) {
            if((limit = file.lastIndexOf('/', i - 1)) >= 0)
                file = file.substring(0, limit) + file.substring(i + 3);
            else
                file = file.substring(i + 3);
        }

        setURL(u, protocol, host, port, file, ref);
    }

    protected String toExternalForm(URL u) {
        String result = u.getProtocol() + ":";
        if((u.getHost() != null) && (u.getHost().length() > 0)) {
            result = result + "//" + u.getHost();
            if(u.getPort() != -1)
                result += ":" + u.getPort();
        }
        result += u.getFile();
        if(u.getRef() != null)
            result += "#" + u.getRef();
        return result;
    }

    protected void setURL(URL u, String protocol, String host, int port, String file, String ref) {
        if(this != u.handler)
            throw new SecurityException("handler for url different from " + "this handler");
        u.set(u.getProtocol(), host, port, file, ref);
    }
}
