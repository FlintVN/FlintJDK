package java.util.zip;

// import java.util.Date;
import java.util.Objects;

import static java.util.zip.ZipConstants64.*;

public class ZipEntry implements ZipConstants, Cloneable {
    String name;        // entry name
    long xdostime = -1; // modification time (in DOS time)
    long crc = -1;      // crc-32 of entry data
    long size = -1;     // uncompressed size of entry data
    long csize = -1;    // compressed size of entry data
    int method = -1;    // compression method
    int flag = 0;       // general purpose flag
    byte[] extra;       // optional extra field data for entry
    String comment;     // optional comment string for entry

    public static final int STORED = 0;
    public static final int DEFLATED = 8;

    public ZipEntry(String name) {
        Objects.requireNonNull(name, "name");
        if(name.length() > 0xFFFF)
            throw new IllegalArgumentException("entry name too long");
        this.name = name;
    }

    public ZipEntry(ZipEntry e) {
        Objects.requireNonNull(e, "entry");
        name = e.name;
        xdostime = e.xdostime;
        crc = e.crc;
        size = e.size;
        csize = e.csize;
        method = e.method;
        flag = e.flag;
        extra = e.extra;
        comment = e.comment;
    }

    public String getName() {
        return name;
    }

    public void setTime(long time) {
        this.xdostime = javaToDosTime(time);
    }

    public long getTime() {
        return xdostime != -1 ? dosToJavaTime(xdostime) : -1;
    }

    public void setSize(long size) {
        if(size < 0)
            throw new IllegalArgumentException("invalid entry size");
        this.size = size;
    }

    public long getSize() {
        return size;
    }

    public long getCompressedSize() {
        return csize;
    }

    public void setCrc(long crc) {
        if(crc < 0 || crc > 0xFFFFFFFFL)
            throw new IllegalArgumentException("invalid entry crc-32");
        this.crc = crc;
    }

    public long getCrc() {
        return crc;
    }

    public void setMethod(int method) {
        if(method != STORED && method != DEFLATED)
            throw new IllegalArgumentException("invalid compression method");
        this.method = method;
    }

    public int getMethod() {
        return method;
    }

    public void setExtra(byte[] extra) {
        setExtra0(extra, false, true);
    }

    void setExtra0(byte[] extra, boolean doZIP64, boolean isLOC) {
        if(extra != null) {
            if(extra.length > 0xFFFF)
                throw new IllegalArgumentException("invalid extra field length");
            int off = 0;
            int len = extra.length;
            while(off + 4 < len) {
                int tag = get16(extra, off);
                int sz = get16(extra, off + 2);
                off += 4;
                if(off + sz > len)
                    break;
                switch(tag) {
                    case EXTID_ZIP64:
                        if(doZIP64) {
                            if(isLOC) {
                                if(sz >= 16) {
                                    size = get64(extra, off);
                                    csize = get64(extra, off + 8);
                                }
                            }
                            else {
                                if(size == ZIP64_MAGICVAL) {
                                    if(off + 8 > len)
                                        break;
                                    size = get64(extra, off);
                                }
                                if(csize == ZIP64_MAGICVAL) {
                                    if(off + 16 > len)
                                        break;
                                    csize = get64(extra, off + 8);
                                }
                            }
                        }
                        break;
                    case EXTID_NTFS:
                        if(sz < 32)
                            break;
                        int pos = off + 4;
                        if(get16(extra, pos) !=  0x0001 || get16(extra, pos + 2) != 24)
                            break;
                        long wtime = get64(extra, pos + 4);
                        // TODO
                        // if(wtime != WINDOWS_TIME_NOT_AVAILABLE)
                        //     mtime = winTimeToFileTime(wtime);
                        // wtime = get64(extra, pos + 12);
                        // if(wtime != WINDOWS_TIME_NOT_AVAILABLE)
                        //     atime = winTimeToFileTime(wtime);
                        // wtime = get64(extra, pos + 20);
                        // if(wtime != WINDOWS_TIME_NOT_AVAILABLE)
                        //     ctime = winTimeToFileTime(wtime);
                        break;
                    case EXTID_EXTT:
                        // TODO
                        // int flag = Byte.toUnsignedInt(extra[off]);
                        // int sz0 = 1;
                        // if((flag & 0x1) != 0 && (sz0 + 4) <= sz) {
                        //     mtime = unixTimeToFileTime(get32S(extra, off + sz0));
                        //     sz0 += 4;
                        // }
                        // if((flag & 0x2) != 0 && (sz0 + 4) <= sz) {
                        //     atime = unixTimeToFileTime(get32S(extra, off + sz0));
                        //     sz0 += 4;
                        // }
                        // if((flag & 0x4) != 0 && (sz0 + 4) <= sz) {
                        //     ctime = unixTimeToFileTime(get32S(extra, off + sz0));
                        //     sz0 += 4;
                        // }
                        break;
                    default:
                }
                off += sz;
            }
        }
        this.extra = extra;
    }

    public byte[] getExtra() {
        return extra;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    public boolean isDirectory() {
        return name.endsWith("/");
    }

    public String toString() {
        return getName();
    }

    public int hashCode() {
        return name.hashCode();
    }

    public Object clone() {
        try {
            ZipEntry e = (ZipEntry)super.clone();
            e.extra = (extra == null) ? null : extra.clone();
            return e;
        }
        catch(CloneNotSupportedException e) {
            throw new InternalError(e);
        }
    }

    private static long dosToJavaTime(long dtime) {
        // Date d = new Date(
        //     (int)(((dtime >> 25) & 0x7f) + 80),
        //     (int)(((dtime >> 21) & 0x0f) - 1),
        //     (int)((dtime >> 16) & 0x1f),
        //     (int)((dtime >> 11) & 0x1f),
        //     (int)((dtime >> 5) & 0x3f),
        //     (int)((dtime << 1) & 0x3e)
        // );
        // return d.getTime();

        // TODO
        throw new UnsupportedOperationException();
    }

    private static long javaToDosTime(long time) {
        // Date d = new Date(time);
        // int year = d.getYear() + 1900;
        // if(year < 1980)
        //     return (1 << 21) | (1 << 16);
        // return (year - 1980) << 25 | (d.getMonth() + 1) << 21 | d.getDate() << 16 | d.getHours() << 11 | d.getMinutes() << 5 | d.getSeconds() >> 1;

        // TODO
        throw new UnsupportedOperationException();
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

    public static final int get32S(byte[] b, int off) {
        return (get16(b, off) | (get16(b, off+2) << 16));
    }
}
