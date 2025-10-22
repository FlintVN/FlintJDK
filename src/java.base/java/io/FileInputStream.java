package java.io;

import java.util.Arrays;
import jdk.internal.util.ArraysSupport;

public class FileInputStream extends InputStream {
    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private final FileDescriptor fd;
    private final String path;

    public FileInputStream(String name) throws FileNotFoundException {
        this(name != null ? new File(name) : null);
    }

    public FileInputStream(File file) throws FileNotFoundException {
        String name = (file != null ? file.getPath() : null);
        if(name == null)
            throw new NullPointerException();
        fd = new FileDescriptor();
        path = name;
        open(name);
    }

    public FileInputStream(FileDescriptor fdObj) {
        if(fdObj == null)
            throw new NullPointerException();
        fd = fdObj;
        path = null;
    }

    private native void open(String name) throws FileNotFoundException;

    @Override
    public native int read() throws IOException;

    private native int readBytes(byte[] b, int off, int len) throws IOException;

    @Override
    public int read(byte[] b) throws IOException {
        return readBytes(b, 0, b.length);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return readBytes(b, off, len);
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        long length = length();
        long position = position();
        long size = length - position;

        if(length <= 0 || size <= 0)
            return super.readAllBytes();

        if(size > (long)Integer.MAX_VALUE) {
            String msg = "Required array size too large for" + path + ":" + size + " = " + length + " - " + position;
            throw new OutOfMemoryError(msg);
        }

        int capacity = (int)size;
        byte[] buf = new byte[capacity];

        int nread = 0;
        int n;
        for(;;) {
            while((n = read(buf, nread, capacity - nread)) > 0)
                nread += n;

            if(n < 0 || (n = read()) < 0)
                break;

            capacity = Math.max(ArraysSupport.newLength(capacity, 1, capacity), DEFAULT_BUFFER_SIZE);
            buf = Arrays.copyOf(buf, capacity);
            buf[nread++] = (byte)n;
        }
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        if(len < 0)
            throw new IllegalArgumentException("len < 0");
        if(len == 0)
            return new byte[0];

        long length = length();
        long position = position();
        long size = length - position;

        if(length <= 0 || size <= 0)
            return super.readNBytes(len);

        int capacity = (int)Math.min(len, size);
        byte[] buf = new byte[capacity];

        int remaining = capacity;
        int nread = 0;
        int n;
        do {
            n = read(buf, nread, remaining);
            if(n > 0) {
                nread += n;
                remaining -= n;
            }
            else if(n == 0) {
                byte b = (byte)read();
                if(b == -1)
                    break;
                buf[nread++] = b;
                remaining--;
            }
        } while(n >= 0 && remaining > 0);
        return (capacity == nread) ? buf : Arrays.copyOf(buf, nread);
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        // TODO
        throw new UnsupportedOperationException();
    }

    public native long length() throws IOException;

    public native long position() throws IOException;

    @Override
    public native long skip(long n) throws IOException;

    @Override
    public native int available() throws IOException;

    @Override
    public native void close() throws IOException;

    public final FileDescriptor getFD() throws IOException {
        if(fd != null)
            return fd;
        throw new IOException();
    }
}
