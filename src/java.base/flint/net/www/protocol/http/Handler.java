package flint.net.www.protocol.http;

import java.io.IOException;
import java.net.URL;
import java.net.Proxy;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Handler extends URLStreamHandler {
    protected String proxy;
    protected int proxyPort;

    public Handler() {
        proxy = null;
        proxyPort = -1;
    }

    public Handler(String proxy, int port) {
        this.proxy = proxy;
        this.proxyPort = port;
    }

    @Override
    protected int getDefaultPort() {
        return 80;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return openConnection(u, (Proxy) null);
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        return new HttpURLConnection(u, p, this);
    }
}
