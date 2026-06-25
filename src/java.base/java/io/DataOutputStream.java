package java.io;

public class DataOutputStream extends FilterOutputStream implements DataOutput {
    protected int written;

    public DataOutputStream(OutputStream out) {
        super(out);
    }

    public synchronized void write(int b) throws IOException {
        out.write(b);
        written++;
    }

    public synchronized void write(byte[] b) throws IOException {
        out.write(b, 0, b.length);
        written += b.length;
    }

    public synchronized void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        written += len;
    }

    public void flush() throws IOException {
        out.flush();
    }

    public final void writeBoolean(boolean v) throws IOException {
        out.write(v ? 1 : 0);
        written++;
    }

    public final void writeByte(int v) throws IOException {
        out.write(v);
        written++;
    }

    public final void writeShort(int v) throws IOException {
        out.write((v >>> 8) & 0xFF);
        out.write(v & 0xFF);
        written += 2;
    }

    public final void writeChar(int v) throws IOException {
        writeShort(v);
    }

    public final void writeInt(int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write(v & 0xFF);
        written += 4;
    }

    public final void writeLong(long v) throws IOException {
        writeInt((int)(v >>> 32));
        writeInt((int)v);
    }

    public final void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    public final void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    public final void writeBytes(String s) throws IOException {
        int len = s.length();
        for(int i = 0; i < len; i++)
            out.write((byte)s.charAt(i));
        written += len;
    }

    public final void writeChars(String s) throws IOException {
        int n = s.length();
        for (int i = 0; i < n; i++)
            writeChar(s.charAt(i));
    }

    public final void writeUTF(String str) throws IOException {
        int strlen = str.length(), utflen = 0;
        for(int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if(c >= 0x0001 && c <= 0x007F)
                utflen++;
            else if(c > 0x07FF)
                utflen += 3;
            else
                utflen += 2;
        }
        if(utflen > 65535)
            throw new IOException("encoded string too long");
        writeShort(utflen);
        for(int i = 0; i < strlen; i++) {
            int c = str.charAt(i);
            if(c >= 0x0001 && c <= 0x007F)
                writeByte(c);
            else if(c > 0x07FF) {
                writeByte(0xE0 | ((c >> 12) & 0x0F));
                writeByte(0x80 | ((c >> 6) & 0x3F));
                writeByte(0x80 | (c & 0x3F));
            }
            else {
                writeByte(0xC0 | ((c >> 6) & 0x1F));
                writeByte(0x80 | (c & 0x3F));
            }
        }
    }

    public final int size() {
        return written;
    }
}
