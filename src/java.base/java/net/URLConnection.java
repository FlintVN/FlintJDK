package java.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;

abstract public class URLConnection {
    private static boolean defaultAllowUserInteraction = false;
    private static boolean defaultUseCaches = true;
    private static FileNameMap fileNameMap;

    protected URL url;
    protected boolean doInput = true;
    protected boolean doOutput = false;
    protected boolean allowUserInteraction = defaultAllowUserInteraction;
    protected boolean useCaches = defaultUseCaches;
    protected long ifModifiedSince = 0;
    protected boolean connected = false;

    public static FileNameMap getFileNameMap() {
        return fileNameMap;
    }

    public static void setFileNameMap(FileNameMap map) {
        fileNameMap = map;
    }

    abstract public void connect() throws IOException;

    protected URLConnection(URL url) {
        this.url = url;
    }

    public URL getURL() {
        return url;
    }

    public int getContentLength() {
        return getHeaderFieldInt("content-length", -1);
    }

    public String getContentType() {
        return getHeaderField("content-type");
    }

    public String getContentEncoding() {
        return getHeaderField("content-encoding");
    }

    public long getExpiration() {
        return getHeaderFieldDate("expires", 0);
    }

    public long getDate() {
        return getHeaderFieldDate("date", 0);
    }

    public long getLastModified() {
        return getHeaderFieldDate("last-modified", 0);
    }

    public String getHeaderField(String name) {
        return null;
    }

    public int getHeaderFieldInt(String name, int defaultValue) {
        final String value = getHeaderField(name);
        if(value != null) {
            try {
                return Integer.parseInt(value);
            }
            catch(NumberFormatException e) {

            }
        }
        return defaultValue;
    }

    public static long parseDate(String dateStr) {
        // TODO
        throw new UnsupportedOperationException();
    }

    public long getHeaderFieldDate(String name, long defaultValue) {
        final String value = getHeaderField(name);
        if(value != null) {
            try {
                return parseDate(value);
            }
            catch(Exception e) {

            }
        }
        return defaultValue;
    }

    public String getHeaderFieldKey(int n) {
        return null;
    }

    public String getHeaderField(int n) {
        return null;
    }

    public Object getContent() throws IOException {
        return getContentHandler().getContent(this);
    }

    public InputStream getInputStream() throws IOException {
        throw new UnknownServiceException("protocol doesn't support input");
    }

    public OutputStream getOutputStream() throws IOException {
        throw new UnknownServiceException("protocol doesn't support output");
    }

    public String toString() {
        return this.getClass().getName() + ":" + url;
    }

    public void setDoInput(boolean doinput) {
        if(connected)
            throw new IllegalAccessError("Already connected");
        doInput = doinput;
    }

    public boolean getDoInput() {
        return doInput;
    }

    public void setDoOutput(boolean dooutput) {
        if(connected)
            throw new IllegalAccessError("Already connected");
        doOutput = dooutput;
    }

    public boolean getDoOutput() {
        return doOutput;
    }

    public void setAllowUserInteraction(boolean allowuserinteraction) {
        if(connected)
            throw new IllegalAccessError("Already connected");
        allowUserInteraction = allowuserinteraction;
    }

    public boolean getAllowUserInteraction() {
        return allowUserInteraction;
    }

    public static void setDefaultAllowUserInteraction(boolean defaultallowuserinteraction) {
        defaultAllowUserInteraction = defaultallowuserinteraction;
    }

    public static boolean getDefaultAllowUserInteraction() {
        return defaultAllowUserInteraction;
    }

    public void setUseCaches(boolean usecaches) {
        if(connected)
            throw new IllegalAccessError("Already connected");
        useCaches = usecaches;
    }

    public boolean getUseCaches() {
        return useCaches;
    }

    public void setIfModifiedSince(long ifmodifiedsince) {
        if(connected)
            throw new IllegalAccessError("Already connected");
        ifModifiedSince = ifmodifiedsince;
    }

    public long getIfModifiedSince() {
        return ifModifiedSince;
    }

    public boolean getDefaultUseCaches() {
        return defaultUseCaches;
    }

    public void setDefaultUseCaches(boolean defaultusecaches) {
        defaultUseCaches = defaultusecaches;
    }

    public void setRequestProperty(String key, String value) {
        if(connected)
            throw new IllegalAccessError("Already connected");
    }

    public String getRequestProperty(String key) {
        if(connected)
            throw new IllegalAccessError("Already connected");
        return null;
    }

    public static void setDefaultRequestProperty(String key, String value) {

    }

    public static String getDefaultRequestProperty(String key) {
        return null;
    }

    static ContentHandlerFactory factory;

    public static synchronized void setContentHandlerFactory(ContentHandlerFactory fac) {
        if(factory != null) {
            throw new Error("factory already defined");
        }
        factory = fac;
    }

    private static Hashtable<String, ContentHandler> handlers = new Hashtable<>();
    private static ContentHandler UnknownContentHandlerP = new UnknownContentHandler();

    synchronized ContentHandler getContentHandler() throws UnknownServiceException {
        String contentType = getContentType();
        ContentHandler handler = null;
        if(contentType == null)
            throw new UnknownServiceException("no content-type");
        try {
            handler = handlers.get(contentType);
            if(handler != null)
            return handler;
        } catch(Exception e) {
        }
        if(factory != null)
            handler = factory.createContentHandler(contentType);
        if(handler == null) {
            try {
                handler = lookupContentHandlerClassFor(contentType);
            }
            catch(Exception e) {
                // e.printStackTrace();
                handler = UnknownContentHandlerP;
            }
            handlers.put(contentType, handler);
        }
        return handler;
    }

    private static final String contentClassPrefix = "sun.net.www.content";
    private static final String contentPathProp = "java.content.handler.pkgs";

    private ContentHandler lookupContentHandlerClassFor(String contentType) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        // String contentHandlerClassName = typeToPackageName(contentType);
        // String contentHandlerPkgPrefixes = getContentHandlerPkgPrefixes();
        // StringTokenizer packagePrefixIter = new StringTokenizer(contentHandlerPkgPrefixes, "|");

        // while(packagePrefixIter.hasMoreTokens()) {
        //     String packagePrefix = packagePrefixIter.nextToken().trim();
        //     try {
        //         String name = packagePrefix + "." + contentHandlerClassName;
        //         ContentHandler handler = (ContentHandler)Class.forName(name).getConstructor().newInstance();
        //         return handler;
        //     }
        //     catch(Exception e) {

        //     }
        // }

        // return UnknownContentHandlerP;

        // TODO
        throw new UnsupportedOperationException();
    }

    private String typeToPackageName(String contentType) {
        int len = contentType.length();
        char nm[] = new char[len];
        contentType.getChars(0, len, nm, 0);
        for(int i = 0; i < len; i++) {
            char c = nm[i];
            if(c == '/')
                nm[i] = '.';
            else if(!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z' || '0' <= c && c <= '9'))
                nm[i] = '_';
        }
        return new String(nm);
    }

    private String getContentHandlerPkgPrefixes() {
        // String packagePrefixList = System.getProperty(contentPathProp, "");
        // if(packagePrefixList != "") {
        //     packagePrefixList += "|";
        return contentClassPrefix;
    }

    protected static String guessContentTypeFromName(String fname) {
        String contentType = null;
        if(fileNameMap != null) {
            contentType = fileNameMap.getContentTypeFor(fname);
        }

        return contentType;
    }

    public static String guessContentTypeFromStream(InputStream is) throws IOException {
        if(!is.markSupported())
            return null;

        is.mark(16);
        int c1 = is.read();
        int c2 = is.read();
        int c3 = is.read();
        int c4 = is.read();
        int c5 = is.read();
        int c6 = is.read();
        int c7 = is.read();
        int c8 = is.read();
        int c9 = is.read();
        int c10 = is.read();
        int c11 = is.read();
        int c12 = is.read();
        int c13 = is.read();
        int c14 = is.read();
        int c15 = is.read();
        int c16 = is.read();
        is.reset();

        if(c1 == 0xCA && c2 == 0xFE && c3 == 0xBA && c4 == 0xBE)
            return "application/java-vm";
        if(c1 == 0xAC && c2 == 0xED)
            return "application/x-java-serialized-object";
        if(c1 == '<') {
            if(
                c2 == '!' ||
                ((c2 == 'h' && (c3 == 't' && c4 == 'm' && c5 == 'l' || c3 == 'e' && c4 == 'a' && c5 == 'd') || (c2 == 'b' && c3 == 'o' && c4 == 'd' && c5 == 'y'))) ||
                ((c2 == 'H' && (c3 == 'T' && c4 == 'M' && c5 == 'L' || c3 == 'E' && c4 == 'A' && c5 == 'D') || (c2 == 'B' && c3 == 'O' && c4 == 'D' && c5 == 'Y')))
            ) {
                return "text/html";
            }
            if(c2 == '?' && c3 == 'x' && c4 == 'm' && c5 == 'l' && c6 == ' ')
                return "application/xml";
        }
        if(c1 == 0xef &&  c2 == 0xbb &&  c3 == 0xbf) {
            if(c4 == '<' &&  c5 == '?' &&  c6 == 'x')
                return "application/xml";
        }
        if(c1 == 0xfe && c2 == 0xff) {
            if(c3 == 0 && c4 == '<' && c5 == 0 && c6 == '?' && c7 == 0 && c8 == 'x')
                return "application/xml";
        }
        if(c1 == 0xff && c2 == 0xfe) {
            if(c3 == '<' && c4 == 0 && c5 == '?' && c6 == 0 && c7 == 'x' && c8 == 0)
                return "application/xml";
        }
        if(c1 == 0x00 &&  c2 == 0x00 &&  c3 == 0xfe &&  c4 == 0xff) {
            if(
                c5  == 0 && c6  == 0 && c7  == 0 && c8  == '<' &&
                c9  == 0 && c10 == 0 && c11 == 0 && c12 == '?' &&
                c13 == 0 && c14 == 0 && c15 == 0 && c16 == 'x'
            ) {
                return "application/xml";
            }
        }
        if(c1 == 0xff &&  c2 == 0xfe &&  c3 == 0x00 &&  c4 == 0x00) {
            if(
                c5 == '<' && c6 == 0 && c7 == 0 && c8 == 0 &&
                c9 == '?' && c10 == 0 && c11 == 0 && c12 == 0 &&
                c13 == 'x' && c14 == 0 && c15 == 0 && c16 == 0
            ) {
                return "application/xml";
            }
        }
        if(c1 == 'G' && c2 == 'I' && c3 == 'F' && c4 == '8')
            return "image/gif";
        if(c1 == '#' && c2 == 'd' && c3 == 'e' && c4 == 'f')
            return "image/x-bitmap";
        if(c1 == '!' && c2 == ' ' && c3 == 'X' && c4 == 'P' && c5 == 'M' && c6 == '2')
            return "image/x-pixmap";
        if(c1 == 137 && c2 == 80 && c3 == 78 && c4 == 71 && c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10)
            return "image/png";
        if(c1 == 0xFF && c2 == 0xD8 && c3 == 0xFF) {
            if(c4 == 0xE0 || c4 == 0xEE)
                return "image/jpeg";
            if((c4 == 0xE1) && (c7 == 'E' && c8 == 'x' && c9 == 'i' && c10 =='f' && c11 == 0))
                return "image/jpeg";
        }
        if((c1 == 0x49 && c2 == 0x49 && c3 == 0x2a && c4 == 0x00) || (c1 == 0x4d && c2 == 0x4d && c3 == 0x00 && c4 == 0x2a))
            return "image/tiff";
        // TODO
        // if(c1 == 0xD0 && c2 == 0xCF && c3 == 0x11 && c4 == 0xE0 && c5 == 0xA1 && c6 == 0xB1 && c7 == 0x1A && c8 == 0xE1) {
        //     if(checkfpx(is))
        //         return "image/vnd.fpx";
        // }
        if(c1 == 0x2E && c2 == 0x73 && c3 == 0x6E && c4 == 0x64)
            return "audio/basic";
        if(c1 == 0x64 && c2 == 0x6E && c3 == 0x73 && c4 == 0x2E)
            return "audio/basic";
        if(c1 == 'R' && c2 == 'I' && c3 == 'F' && c4 == 'F')
            return "audio/x-wav";
        return null;
    }
}

class UnknownContentHandler extends ContentHandler {
    public Object getContent(URLConnection uc) throws IOException {
        return uc.getInputStream();
    }
}
