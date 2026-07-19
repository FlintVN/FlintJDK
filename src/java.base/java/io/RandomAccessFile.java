package java.io;

public class RandomAccessFile implements DataOutput, DataInput, Closeable {
    private static final int O_RDONLY = 1;
    private static final int O_RDWR = 2;
    private static final int O_SYNC = 4;
    private static final int O_DSYNC = 8;

    private final FileDescriptor fd;
    private final String path;
    private final int imode;

    private final byte[] buffer = new byte[Long.BYTES];

    public RandomAccessFile(String name, String mode) throws FileNotFoundException {
        this(name != null ? new File(name) : null, mode);
    }

    public RandomAccessFile(File file, String mode) throws FileNotFoundException {
        String name = (file != null ? file.getPath() : null);
        int imode = -1;

        if(mode.equals("r"))
            imode = O_RDONLY;
        else if(mode.startsWith("rw")) {
            imode = O_RDWR;
            if(mode.length() > 2) {
                if(mode.equals("rws"))
                    imode |= O_SYNC;
                else if(mode.equals("rwd"))
                    imode |= O_DSYNC;
                else
                    imode = -1;
            }
        }

        if(imode < 0)
            throw new IllegalArgumentException("Illegal mode \"" + mode + "\" must be one of " + "\"r\", \"rw\", \"rws\"," + " or \"rwd\"");
        this.imode = imode;

        if(name == null)
            throw new NullPointerException();
        this.fd = new FileDescriptor();
        this.path = name;
        open(name, imode);
    }

    public final FileDescriptor getFD() throws IOException {
        return fd;
    }

    private native void open(String name, int mode) throws FileNotFoundException;

    public native int read() throws IOException;

    private native int readBytes(byte[] b, int off, int len) throws IOException;

    public int read(byte[] b, int off, int len) throws IOException {
        return readBytes(b, off, len);
    }

    public int read(byte[] b) throws IOException {
        return readBytes(b, 0, b.length);
    }

    @Override
    public final void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    @Override
    public final void readFully(byte[] b, int off, int len) throws IOException {
        int n = 0;
        do {
            int count = this.read(b, off + n, len - n);
            if(count < 0)
                throw new EOFException();
            n += count;
        } while (n < len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        long pos;
        long len;
        long newpos;

        if(n <= 0)
            return 0;
        pos = getFilePointer();
        len = length();
        newpos = pos + n;
        if(newpos > len)
            newpos = len;
        seek(newpos);

        return (int)(newpos - pos);
    }

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

    public native long getFilePointer() throws IOException;

    public native void seek(long pos) throws IOException;

    public native long length() throws IOException;

    public native void setLength(long newLength) throws IOException;

    @Override
    public native void close() throws IOException;

    public final boolean readBoolean() throws IOException {
        return readUnsignedByte() != 0;
    }

    @Override
    public final byte readByte() throws IOException {
        return (byte)readUnsignedByte();
    }

    @Override
    public final int readUnsignedByte() throws IOException {
        int ch = this.read();
        if(ch < 0)
            throw new EOFException();
        return ch;
    }

    @Override
    public final short readShort() throws IOException {
        return (short)readUnsignedShort();
    }

    @Override
    public final int readUnsignedShort() throws IOException {
        readFully(buffer, 0, Short.BYTES);
        return ((buffer[0] & 0xFF) << 8) | (buffer[1] & 0xFF);
    }

    @Override
    public final char readChar() throws IOException {
        return (char)readUnsignedShort();
    }

    @Override
    public final int readInt() throws IOException {
        readFully(buffer, 0, Integer.BYTES);
        return ((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
    }

    @Override
    public final long readLong() throws IOException {
        readFully(buffer, 0, Long.BYTES);
        return (
            ((long)buffer[0] << 56) |
            ((long)(buffer[1] & 0xFF) << 48) |
            ((long)(buffer[2] & 0xFF) << 40) |
            ((long)(buffer[3] & 0xFF) << 32) |
            ((long)(buffer[4] & 0xFF) << 24) |
            ((buffer[5] & 0xFF) << 16) |
            ((buffer[6] & 0xFF) << 8) |
            ((buffer[7] & 0xFF) << 0)
        );
    }

    @Override
    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public final String readLine() throws IOException {
        StringBuilder input = new StringBuilder();
        int c = -1;
        boolean eol = false;

        while (!eol) {
            switch (c = read()) {
                case -1, '\n' -> eol = true;
                case '\r' -> {
                    eol = true;
                    long cur = getFilePointer();
                    if((read()) != '\n')
                        seek(cur);
                }
                default -> input.append((char) c);
            }
        }

        if((c == -1) && (input.length() == 0))
            return null;
        return input.toString();
    }

    @Override
    public final String readUTF() throws IOException {
        return DataInputStream.readUTF(this);
    }

    @Override
    public final void writeBoolean(boolean v) throws IOException {
        write(v ? 1 : 0);
    }

    @Override
    public final void writeByte(int v) throws IOException {
        write(v);
    }

    @Override
    public final void writeShort(int v) throws IOException {
        buffer[0] = (byte)(v >>> 8);
        buffer[1] = (byte)v;
        write(buffer, 0, Short.BYTES);
    }

    @Override
    public final void writeChar(int v) throws IOException {
        writeShort(v);
    }

    @Override
    public final void writeInt(int v) throws IOException {
        buffer[0] = (byte)(v >>> 24);
        buffer[1] = (byte)(v >>> 16);
        buffer[2] = (byte)(v >>> 8);
        buffer[3] = (byte)v;
        write(buffer, 0, Integer.BYTES);
    }

    @Override
    public final void writeLong(long v) throws IOException {
        buffer[0] = (byte)(v >>> 56);
        buffer[1] = (byte)(v >>> 48);
        buffer[2] = (byte)(v >>> 40);
        buffer[3] = (byte)(v >>> 32);
        buffer[4] = (byte)(v >>> 24);
        buffer[5] = (byte)(v >>> 16);
        buffer[6] = (byte)(v >>> 8);
        buffer[7] = (byte)v;
        write(buffer, 0, Long.BYTES);
    }

    @Override
    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToRawIntBits(v));
    }

    @Override
    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToRawLongBits(v));
    }

    @Override
    @SuppressWarnings("deprecation")
    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        byte[] b = new byte[len];
        char[] c = new char[len];
        s.getChars(0, len, c, 0);
        for(int i = 0; i < len; i++)
            b[i] = (byte)c[i];
        writeBytes(b, 0, len);
    }

    @Override
    public final void writeChars(String s) throws IOException {
        int clen = s.length();
        int blen = 2 * clen;
        byte[] b = new byte[blen];
        char[] c = new char[clen];
        s.getChars(0, clen, c, 0);
        for(int i = 0, j = 0; i < clen; i++) {
            b[j++] = (byte)(c[i] >>> 8);
            b[j++] = (byte)(c[i] >>> 0);
        }
        writeBytes(b, 0, blen);
    }

    @Override
    public final void writeUTF(String str) throws IOException {
        DataOutputStream.writeUTF(str, this);
    }
}
