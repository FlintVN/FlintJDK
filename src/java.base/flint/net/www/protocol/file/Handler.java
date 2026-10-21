package flint.net.www.protocol.file;

import java.net.URLConnection;
import java.net.URL;
import java.net.Proxy;
import java.net.URLStreamHandler;
import java.io.IOException;
import java.io.File;

public class Handler extends URLStreamHandler {
    private String getHost(URL url) {
        String host = url.getHost();
        if (host == null)
            host = "";
        return host;
    }

    protected void parseURL(URL u, String spec, int start, int limit) {
        super.parseURL(u, spec.replace(File.separatorChar, '/'), start, limit);
    }

    public synchronized URLConnection openConnection(URL u) throws IOException {
        return openConnection(u, null);
    }

    @Override
    public synchronized URLConnection openConnection(URL u, Proxy p) throws IOException {
        String host = u.getHost();
        if (host == null || host.equals("") || host.equals("~") || host.equalsIgnoreCase("localhost"))
            return new FileURLConnection(u, new File(decode(u.getPath())));
        throw new IOException("Unable to connect to: " + u.toExternalForm());
    }

    protected boolean hostsEqual(URL u1, URL u2) {
        String s1 = u1.getHost();
        String s2 = u2.getHost();
        if ("localhost".equalsIgnoreCase(s1) && (s2 == null || "".equals(s2)))
            return true;
        if ("localhost".equalsIgnoreCase(s2) && (s1 == null || "".equals(s1)))
            return true;
        return super.hostsEqual(u1, u2);
    }

    static String decode(String s) {
        if (s == null)
            return "";
        if (s.indexOf('%') < 0)
            return s;

        StringBuffer out = new StringBuffer(s.length());
        byte[] bytes = new byte[s.length() / 3 + 1];
        int n = s.length();
        int i = 0;
        while (i < n) {
            char c = s.charAt(i);
            if (c != '%') {
                out.append(c);
                i++;
                continue;
            }
            int cnt = 0;
            while (i < n && s.charAt(i) == '%') {
                if (i + 2 >= n)
                    throw new IllegalArgumentException("Incomplete % escape: " + s);
                int hi = Character.digit(s.charAt(i + 1), 16);
                int lo = Character.digit(s.charAt(i + 2), 16);
                if (hi < 0 || lo < 0)
                    throw new IllegalArgumentException("Illegal % escape: " + s);
                bytes[cnt++] = (byte) ((hi << 4) | lo);
                i += 3;
            }
            try {
                out.append(new String(bytes, 0, cnt, "UTF-8"));
            }
            catch (java.io.UnsupportedEncodingException e) {
                out.append(new String(bytes, 0, cnt));
            }
        }
        return out.toString();
    }
}
