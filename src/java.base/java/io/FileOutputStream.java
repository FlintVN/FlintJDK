package java.io;

public class FileOutputStream extends OutputStream {
    private final FileDescriptor fd;
    private final String path;

    public FileOutputStream(String name) throws FileNotFoundException {
        this(name, false);
    }

    public FileOutputStream(String name, boolean append) throws FileNotFoundException {
        if(name == null)
            throw new NullPointerException();
        this.fd = new FileDescriptor();
        this.path = name;
        open(name, append);
    }

    public FileOutputStream(File file) throws FileNotFoundException {
        this(file != null ? file.getPath() : null, false);
    }

    public FileOutputStream(File file, boolean append) throws FileNotFoundException {
        this(file != null ? file.getPath() : null, append);
    }

    public FileOutputStream(FileDescriptor fdObj) {
        if(fdObj == null)
            throw new NullPointerException();
        this.fd = fdObj;
        this.path = null;
    }

    private native void open(String name, boolean append) throws FileNotFoundException;

    @Override
    public native void write(int b) throws IOException;

    private native void writeBytes(byte[] b, int off, int len) throws IOException;

    @Override
    public void write(byte[] b) throws IOException {
        writeBytes(b, 0, b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeBytes(b, off, len);
    }

    @Override
    public native void close() throws IOException;

    public final FileDescriptor getFD()  throws IOException {
        if(fd != null)
            return fd;
        throw new IOException();
    }
}
