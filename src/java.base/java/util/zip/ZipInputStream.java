package java.util.zip;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FilterInputStream;
import java.io.PushbackInputStream;
import java.util.Objects;

import static java.util.zip.ZipConstants64.*;

public class ZipInputStream extends FilterInputStream implements ZipConstants {
    private ZipEntry entry;
    private int flag;
    private CRC32 crc = new CRC32();
    private long remaining;
    private byte[] tmpbuf = new byte[512];

    private static final int STORED = ZipEntry.STORED;
    private static final int DEFLATED = ZipEntry.DEFLATED;

    private boolean closed = false;
    private boolean entryEOF = false;

    private void ensureOpen() throws IOException {
        if(closed)
            throw new IOException("Stream closed");
    }

    public ZipInputStream(InputStream in) {
        super(new PushbackInputStream(in, 512));
        if(in == null)
           throw new NullPointerException("in is null");
    }

    public ZipEntry getNextEntry() throws IOException {
        ensureOpen();
        if(entry != null)
            closeEntry();
        crc.reset();
        if((entry = readLOC()) == null)
            return null;
        if(entry.method == STORED)
            remaining = entry.size;
        entryEOF = false;
        return entry;
    }

    public void closeEntry() throws IOException {
        ensureOpen();
        while(read(tmpbuf, 0, tmpbuf.length) != -1);
        entryEOF = true;
    }

    public int available() throws IOException {
        ensureOpen();
        if(entryEOF)
            return 0;
        else
            return 1;
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        return read(b, 0, 1) == -1 ? -1 : b[0] & 0xFF;
    }

    @Override
    public byte[] readAllBytes() throws IOException {
        return super.readAllBytes();
    }

    @Override
    public byte[] readNBytes(int len) throws IOException {
        return super.readNBytes(len);
    }

    @Override
    public int readNBytes(byte[] b, int off, int len) throws IOException {
        return super.readNBytes(b, off, len);
    }

    @Override
    public void skipNBytes(long n) throws IOException {
        super.skipNBytes(n);
    }

    @Override
    public long transferTo(OutputStream out) throws IOException {
        return super.transferTo(out);
    }

    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        Objects.checkFromIndexSize(off, len, b.length);
        if(entry == null)
            return -1;
        switch(entry.method) {
            case DEFLATED:
                throw new ZipException("compression method not supported");
            case STORED:
                if(remaining <= 0) {
                    entryEOF = true;
                    entry = null;
                    return -1;
                }
                if(len > remaining)
                    len = (int)remaining;
                len = in.read(b, off, len);
                if(len == -1)
                    throw new ZipException("unexpected EOF");
                crc.update(b, off, len);
                remaining -= len;
                if(remaining == 0 && entry.crc != crc.getValue())
                    throw new ZipException("invalid entry CRC (expected 0x" + Long.toHexString(entry.crc) + " but got 0x" + Long.toHexString(crc.getValue()) + ")");
                return len;
            default:
                throw new ZipException("invalid compression method");
        }
    }

    public long skip(long n) throws IOException {
        if(n < 0)
            throw new IllegalArgumentException("negative skip length");
        ensureOpen();
        int max = (int)Math.min(n, Integer.MAX_VALUE);
        int total = 0;
        while(total < max) {
            int len = max - total;
            if(len > tmpbuf.length)
                len = tmpbuf.length;
            len = read(tmpbuf, 0, len);
            if(len == -1) {
                entryEOF = true;
                break;
            }
            total += len;
        }
        return total;
    }

    public void close() throws IOException {
        if(!closed) {
            super.close();
            closed = true;
        }
    }

    private byte[] b = new byte[256];

    private ZipEntry readLOC() throws IOException {
        try {
            readFully(tmpbuf, 0, LOCHDR);
        }
        catch(EOFException e) {
            return null;
        }
        if(get32(tmpbuf, 0) != LOCSIG)
            return null;
        flag = get16(tmpbuf, LOCFLG);
        int len = get16(tmpbuf, LOCNAM);
        int blen = b.length;
        if(len > blen) {
            do {
                blen = blen * 2;
            } while(len > blen);
            b = new byte[blen];
        }
        readFully(b, 0, len);
        ZipEntry e = createZipEntry(new String(b, 0, len));
        if((flag & 1) == 1)
            throw new ZipException("encrypted ZIP entry not supported");
        e.method = get16(tmpbuf, LOCHOW);
        e.xdostime = get32(tmpbuf, LOCTIM);
        if((flag & 8) == 8) {
            if(e.method != DEFLATED)
                throw new ZipException("only DEFLATED entries can have EXT descriptor");
        }
        else {
            e.crc = get32(tmpbuf, LOCCRC);
            e.csize = get32(tmpbuf, LOCSIZ);
            e.size = get32(tmpbuf, LOCLEN);
        }
        len = get16(tmpbuf, LOCEXT);
        if(len > 0) {
            byte[] extra = new byte[len];
            readFully(extra, 0, len);
            e.setExtra0(extra, e.csize == ZIP64_MAGICVAL || e.size == ZIP64_MAGICVAL, true);
        }
        return e;
    }

    protected ZipEntry createZipEntry(String name) {
        return new ZipEntry(name);
    }

    private void readFully(byte[] b, int off, int len) throws IOException {
        while(len > 0) {
            int n = in.read(b, off, len);
            if(n == -1)
                throw new EOFException();
            off += n;
            len -= n;
        }
    }

    private static final int get16(byte b[], int off) {
        return (b[off] & 0xFF) | ((b[off + 1] & 0xFF) << 8);
    }

    private static final long get32(byte b[], int off) {
        return get16(b, off) | ((long)get16(b, off + 2) << 16);
    }

    public static final long get64(byte[] b, int off) {
        return get32(b, off) | (get32(b, off + 4) << 32);
    }
}
