package java.io;

import java.util.Objects;

public class DataInputStream extends FilterInputStream implements DataInput {

    public DataInputStream(InputStream in) {
        super(in);
    }

    public final int read(byte[] b) throws IOException {
        return in.read(b, 0, b.length);
    }

    public final int read(byte[] b, int off, int len) throws IOException {
        return in.read(b, off, len);
    }

    public final void readFully(byte[] b) throws IOException {
        readFully(b, 0, b.length);
    }

    public final void readFully(byte[] b, int off, int len) throws IOException {
        Objects.checkFromIndexSize(off, len, b.length);
        int n = 0;
        while(n < len) {
            int count = in.read(b, off + n, len - n);
            if(count < 0)
                throw new EOFException();
            n += count;
        }
    }

    public final int skipBytes(int n) throws IOException {
        int total = 0;
        int cur = 0;

        while((total < n) && ((cur = (int)in.skip(n - total)) > 0))
            total += cur;

        return total;
    }

    public final boolean readBoolean() throws IOException {
        return readUnsignedByte() != 0;
    }

    public final byte readByte() throws IOException {
        return (byte)readUnsignedByte();
    }

    public final int readUnsignedByte() throws IOException {
        int ch = in.read();
        if(ch < 0)
            throw new EOFException();
        return ch;
    }

    public final short readShort() throws IOException {
        return (short)((readUnsignedByte() << 8) | readUnsignedByte());
    }

    public final int readUnsignedShort() throws IOException {
        return (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public final char readChar() throws IOException {
        return (char)((readUnsignedByte() << 8) | readUnsignedByte());
    }

    public final int readInt() throws IOException {
        return (readUnsignedByte() << 24) | (readUnsignedByte() << 16) | (readUnsignedByte() << 8) | readUnsignedByte();
    }

    public final long readLong() throws IOException {
        return ((long)readInt() << 32) | (readInt() & 0xFFFFFFFFL);
    }

    public final float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    public final double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    public final String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        boolean any = false;
        while((c = in.read()) >= 0) {
            any = true;
            if(c == '\n' || c == '\r')
                break;
            sb.append((char) c);
        }
        return any ? sb.toString() : null;
    }

    public final String readUTF() throws IOException {
        return readUTF(this);
    }

    public static final String readUTF(DataInput in) throws IOException {
        int utflen = in.readUnsignedShort();
        byte[] bytearr = new byte[utflen];
        char[] chararr = new char[utflen];
        in.readFully(bytearr, 0, utflen);
        int count = 0, cc = 0;
        while(count < utflen) {
            int c = bytearr[count] & 0xFF;
            if(c > 127)
                break;
            count++;
            chararr[cc++] = (char) c;
        }
        while(count < utflen) {
            int c = bytearr[count] & 0xFF;
            int x = c >> 4;
            if(x <= 7) {
                count++;
                chararr[cc++] = (char) c;
            }
            else if(x == 12 || x == 13) {
                count += 2;
                if(count > utflen)
                    throw new IOException();
                int c2 = bytearr[count - 1];
                chararr[cc++] = (char) (((c & 0x1F) << 6) | (c2 & 0x3F));
            }
            else if(x == 14) {
                count += 3;
                if(count > utflen)
                    throw new IOException();
                int c2 = bytearr[count - 2];
                int c3 = bytearr[count - 1];
                chararr[cc++] = (char) (((c & 0x0F) << 12) | ((c2 & 0x3F) << 6) | (c3 & 0x3F));
            }
            else
                throw new IOException();
        }
        return new String(chararr, 0, cc);
    }
}
