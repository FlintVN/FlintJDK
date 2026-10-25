package flint.net.www.protocol.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class FileURLConnection extends URLConnection {
    private final File file;
    private InputStream is;
    private boolean isDirectory;
    private boolean exists;
    private long length = -1;
    private long lastModified = 0;
    private String contentType;

    protected FileURLConnection(URL u, File file) {
        super(u);
        this.file = file;
    }

    @Override
    public void connect() throws IOException {
        if (connected)
            return;

        if (!file.exists())
            throw new FileNotFoundException(file.getPath());
        exists = true;

        if (file.isDirectory()) {
            isDirectory = true;
            String[] names = file.list();
            if (names == null)
                names = new String[0];
            else
                sort(names);

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < names.length; i++)
                sb.append(names[i]).append('\n');
            byte[] data = sb.toString().getBytes();
            length = data.length;
            contentType = "text/plain";
            is = new ByteArrayInputStream(data);
        }
        else {
            length = file.length();
            contentType = getFileNameMap().getContentTypeFor(file.getName());
            is = new BufferedInputStream(new FileInputStream(file));
        }
        lastModified = file.lastModified();
        connected = true;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        connect();
        return is;
    }

    @Override
    public String getHeaderField(String name) {
        try {
            connect();
        }
        catch (IOException e) {
            return null;
        }
        if (name == null)
            return null;
        if (name.equalsIgnoreCase("content-type"))
            return contentType;
        if (name.equalsIgnoreCase("content-length"))
            return length >= 0 ? Long.toString(length) : null;
        if (name.equalsIgnoreCase("last-modified"))
            return lastModified != 0 ? Long.toString(lastModified) : null;
        return null;
    }

    @Override
    public String getContentType() {
        return getHeaderField("content-type");
    }

    @Override
    public int getContentLength() {
        try {
            connect();
        }
        catch (IOException e) {
            return -1;
        }
        return length > Integer.MAX_VALUE ? -1 : (int) length;
    }

    @Override
    public long getLastModified() {
        try {
            connect();
        }
        catch (IOException e) {
            return 0;
        }
        return lastModified;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    private static void sort(String[] a) {
        for (int i = 1; i < a.length; i++) {
            String key = a[i];
            int j = i - 1;
            while (j >= 0 && a[j].compareTo(key) > 0) {
                a[j + 1] = a[j];
                j--;
            }
            a[j + 1] = key;
        }
    }
}
