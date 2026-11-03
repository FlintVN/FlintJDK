package java.util.zip;

import java.io.RandomAccessFile;
import java.io.IOException;
import java.io.File;
import java.io.Closeable;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Enumeration;

public class ZipFile implements ZipConstants, Closeable {
    RandomAccessFile raf;
    private String name;
    private Hashtable<String, ZipEntry> entries;
    long cenpos;
    private long endpos;
    long pos;

    private static final int STORED = ZipEntry.STORED;
    private static final int DEFLATED = ZipEntry.DEFLATED;

    public ZipFile(String name) throws IOException {
        raf = new RandomAccessFile(name, "r");
        this.name = name;
        readCEN();
    }

    public ZipFile(File file) throws ZipException, IOException {
        this(file.getPath());
    }

    public ZipEntry getEntry(String name) {
            if (name == null)
               throw new NullPointerException("name");
            if (raf == null)
               throw new IllegalStateException("zip file closed");
        return entries.get(name);
    }

    public InputStream getInputStream(ZipEntry ze) throws IOException {        
        InputStream in = new ZipFileInputStream(this, ze);
        switch (ze.method) {
            case STORED:
                return in;
            case DEFLATED:
                return new InflaterInputStream(in, new Inflater(true));
            default:
                throw new ZipException("invalid compression method");
        }
    }

    public String getName() {
        return name;
    }

    public Enumeration<? extends ZipEntry> entries() {
        if (raf == null)
            throw new IllegalStateException("zip file closed");
        return entries.elements();
    }

    public void close() throws IOException {
        if (raf != null) {
            raf.close();
            raf = null;
        }
    }

    synchronized int read(long pos, byte b[], int off, int len) throws IOException {
        if (pos != this.pos)
            raf.seek(pos);
        int n = raf.read(b, off, len);
        if (n > 0)
            this.pos = pos + n;
        return n;
    }

    synchronized int read(long pos) throws IOException {
        if (pos != this.pos)
            raf.seek(pos);
        int n = raf.read();
        if (n > 0)
            this.pos = pos + 1;
        return n;
    }

    private void readCEN() throws IOException {
        findEND();
        byte[] endbuf = new byte[ENDHDR];
        raf.readFully(endbuf);
        if (get32(endbuf, 0) != ENDSIG)
            throw new ZipException("invalid END header signature");
        cenpos = get32(endbuf, ENDOFF);
        int cenlen = (int)get32(endbuf, ENDSIZ);
        if (cenpos + cenlen != endpos)
            throw new ZipException("invalid END header format");
        int nent = get16(endbuf, ENDTOT);
        if (nent * CENHDR > cenlen)
            throw new ZipException("invalid END header format");
        if (get16(endbuf, ENDSUB) != nent)
            throw new ZipException("cannot have more than one drive");
        raf.seek(cenpos);
        byte cenbuf[] = new byte[cenlen];
        raf.readFully(cenbuf);
        entries = new Hashtable<>(nent);
        for (int off = 0; off < cenlen; ) {
            if (get32(cenbuf, off) != CENSIG)
                throw new ZipException("invalid CEN header signature");
            int baseoff = off;
            off += CENHDR;
            int len = get16(cenbuf, baseoff + CENNAM);
            if (len == 0 || off + len > cenlen)
                throw new ZipException("invalid CEN entry name");
            ZipEntry e = new ZipEntry(new String(cenbuf, off, len));
            e.flag = get16(cenbuf, baseoff + CENFLG);
            e.method = get16(cenbuf, baseoff + CENHOW);
            e.xdostime = get32(cenbuf, baseoff + CENTIM);
            e.crc = get32(cenbuf, baseoff + CENCRC);
            e.size = get32(cenbuf, baseoff + CENLEN);
            e.csize = get32(cenbuf, baseoff + CENSIZ);
            e.offset = get32(cenbuf, baseoff + CENOFF);
            if (e.offset + e.csize > cenpos)
                throw new ZipException("invalid CEN entry size");
            off += len;
            len = get16(cenbuf, baseoff + CENEXT);
            if (len > 0) {
                if (off + len > cenlen)
                    throw new ZipException("invalid CEN entry extra data");
                e.extra = new byte[len];
                System.arraycopy(cenbuf, off, e.extra, 0, len);
                off += len;
            }
            len = get16(cenbuf, baseoff + CENCOM);
            if (len > 0) {
                if (off + len > cenlen)
                    throw new ZipException("invalid CEN entry comment");
                e.comment = new String(cenbuf, off, len);
                off += len;
            }
            entries.put(e.name, e);
        }
        if (entries.size() != nent)
            throw new ZipException("invalid CEN header format");
    }

    private static final int INBUFSIZ = 64;

    private void findEND() throws IOException {
        long len = raf.length();
        raf.seek(len);
        long markpos = Math.max(0, len - 0xffff);
        byte buf[] = new byte[INBUFSIZ + 4];
        for (pos = len; pos > markpos; ) {
            int n = Math.min((int)(pos - markpos), INBUFSIZ);
            pos -= n;
            raf.seek(pos);
            raf.readFully(buf, 0, n);
            while (--n > 0) {
                if (get32(buf, n) == ENDSIG) {
                    endpos = pos + n;
                    if (len - endpos < ENDHDR)
                        continue;
                    raf.seek(endpos);
                    byte endbuf[] = new byte[ENDHDR];
                    raf.readFully(endbuf);
                    int comlen = get16(endbuf, ENDCOM);
                    if (endpos + ENDHDR + comlen != len)
                        continue;
                    raf.seek(endpos);
                    pos = endpos;
                    return;
                }
            }
        }
        throw new ZipException("not a ZIP file (END header not found)");
    }

    static final int get16(byte b[], int off) {
        return (b[off] & 0xff) | ((b[off+1] & 0xff) << 8);
    }

    static final long get32(byte b[], int off) {
        return get16(b, off) | ((long)get16(b, off+2) << 16);
    }
}

class ZipFileInputStream extends InputStream implements ZipConstants {
    private ZipFile zf;
    private ZipEntry ze;
    private long pos;
    private long count;

    ZipFileInputStream(ZipFile zf, ZipEntry ze) throws IOException {
        this.zf = zf;
        this.ze = ze;
        readLOC();
    }

    public int available() {
        return (int)Math.min(count, Integer.MAX_VALUE);
    }

    public int read(byte b[], int off, int len) throws IOException {
        if (count == 0)
            return -1;
        if (len > count)
            len = (int)Math.min(count, Integer.MAX_VALUE);
        len = zf.read(pos, b, off, len);
        if (len == -1)
            throw new ZipException("premature EOF");
        pos += len;
        count -= len;
        return len;
    }

    public int read() throws IOException {
        if (count == 0)
            return -1;
        int n = zf.read(pos);
        if (n == -1)
            throw new ZipException("premature EOF");
        pos += 1;
        count -= 1;
        return n;
    }

    public long skip(long n) {
        if (n > count)
            n = count;
        pos += n;
        count -= n;
        return n;
    }

    private void readLOC() throws IOException {
        byte locbuf[] = new byte[LOCHDR];
        zf.read(ze.offset, locbuf, 0, LOCHDR);
        if (ZipFile.get32(locbuf, 0) != LOCSIG)
            throw new ZipException("invalid LOC header signature");
        count = ze.csize;
        pos = ze.offset + LOCHDR + ZipFile.get16(locbuf, LOCNAM) + ZipFile.get16(locbuf, LOCEXT);
        if (pos + count > zf.cenpos)
            throw new ZipException("invalid LOC header format");
    }
}
