package java.io;

public class ByteArrayInputStream extends InputStream {
    private static final int MAX_TRANSFER_SIZE = 128 * 1024;

    protected byte[] buf;
    protected int pos;
    protected int mark = 0;
    protected int count;

    public ByteArrayInputStream(byte[] buf) {
        this.buf = buf;
        this.pos = 0;
        this.count = buf.length;
    }

    public ByteArrayInputStream(byte[] buf, int offset, int length) {
        this.buf = buf;
        this.pos = offset;
        this.count = Math.min(offset + length, buf.length);
        this.mark = offset;
    }

    @Override
    public synchronized int read() {
        return (pos < count) ? (buf[pos++] & 0xFF) : -1;
    }

    @Override
    public synchronized int read(byte[] b, int off, int len) {
        if(b == null)
            throw new NullPointerException();
        if(pos >= count)
            return -1;
        int avail = count - pos;
        if(len > avail)
            len = avail;
        if(len <= 0)
            return 0;
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    @Override
    public synchronized byte[] readAllBytes() {
        if(pos == 0 || count == buf.length)
            return buf.clone();
        int newLength = count - pos;
        byte[] copy = new byte[newLength];
        System.arraycopy(buf, pos, copy, 0, newLength);
        return copy;
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) {
        int n = read(b, off, len);
        return n == -1 ? 0 : n;
    }

    @Override
    public synchronized long transferTo(OutputStream out) throws IOException {
        int len = count - pos;
        if(len > 0) {
            int nwritten = 0;
            while(nwritten < len) {
                int nbyte = Integer.min(len - nwritten, MAX_TRANSFER_SIZE);
                out.write(buf, pos, nbyte);
                pos += nbyte;
                nwritten += nbyte;
            }
            assert pos == count;
        }
        return len;
    }

    @Override
    public synchronized long skip(long n) {
        long k = count - pos;
        if(n < k)
            k = n < 0 ? 0 : n;

        pos += (int)k;
        return k;
    }

    @Override
    public synchronized int available() {
        return count - pos;
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public synchronized void mark(int readAheadLimit) {
        mark = pos;
    }

    @Override
    public synchronized void reset() {
        pos = mark;
    }

    @Override
    public void close() throws IOException {

    }
}
