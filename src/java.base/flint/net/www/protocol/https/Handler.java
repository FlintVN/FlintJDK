package flint.net.www.protocol.https;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class Handler extends flint.net.www.protocol.http.Handler {
    public Handler() {
        super();
    }

    public Handler(String proxy, int port) {
        super(proxy, port);
    }

    @Override
    protected int getDefaultPort() {
        return 443;
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        return openConnection(u, (Proxy) null);
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        return new HttpsURLConnection(u, p, this);
    }
}
