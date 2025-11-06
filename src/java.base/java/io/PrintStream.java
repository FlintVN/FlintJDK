package java.io;

public class PrintStream extends FilterOutputStream implements Appendable, Closeable {
    private final static int CHAR_BUFF_SIZE = 16;

    private boolean trouble = false;

    public PrintStream(OutputStream out) {
        super(out);
    }

    @Override
    public void write(int b) {
        try {
            synchronized(this) {
                out.write(b);
            }
        }
        catch(IOException x) {
            trouble = true;
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) {
        try {
            synchronized (this) {
                out.write(buf, off, len);
            }
        }
        catch(IOException x) {
            trouble = true;
        }
    }

    @Override
    public void write(byte[] buf) throws IOException {
        this.write(buf, 0, buf.length);
    }

    @Override
    public void flush() {
        try {
            synchronized(this) {
                out.flush();
            }
        }
        catch(IOException x) {
            trouble = true;
        }
    }

    @Override
    public void close() {
        try {
            synchronized(this) {
                out.close();
            }
        }
        catch(IOException x) {
            trouble = true;
        }
        out = null;
    }

    public boolean checkError() {
        if(out != null)
            flush();
        return trouble;
    }

    private static int utf8EncodeOneChar(char c, byte[] buff, int index) {
        if(c < 0x80) {
            buff[index] = (byte)c;
            return 1;
        }
        else if(c < 0x0800) {
            buff[index] = (byte)(0xC0 | (c >> 6));
            buff[index + 1] = (byte)(0x80 | (c & 0x3F));
            return 2;
        }
        else {
            buff[index] = (byte)(0xE0 | (c >> 12));
            buff[index + 1] = (byte)(0x80 | ((c >> 6) & 0x3F));
            buff[index + 2] = (byte)(0x80 | (c & 0x3F));
            return 3;
        }
    }

    private void implWrite(String str) {
        try {
            char[] chars = new char[CHAR_BUFF_SIZE];
            byte[] buff = new byte[CHAR_BUFF_SIZE * 3];
            int strlen = str.length();
            int cidx = 0;
            int bidx = 0;
            while(cidx < strlen) {
                int remaining = strlen - cidx;
                int len = remaining > CHAR_BUFF_SIZE ? CHAR_BUFF_SIZE : remaining;
                str.getChars(cidx, cidx + len, chars, 0);
                cidx += len;
                for(int i = 0; i < len; i++) {
                    if((bidx + 3) > buff.length) {
                        out.write(buff, 0, bidx);
                        bidx = 0;
                    }
                    bidx += utf8EncodeOneChar(chars[i], buff, bidx);
                }
            }
            if(bidx > 0)
                out.write(buff, 0, bidx);
        }
        catch(IOException x) {
            trouble = true;
        }
    }

    private void implNewLine() {
        try {
            out.write(0x0D);    /* "\r" */
            out.write(0x0A);    /* "\n" */
        }
        catch(IOException x) {
            trouble = true;
        }
    }

    private void newLine() {
        synchronized(this) {
            implNewLine();
        }
    }

    private void write(String str) {
        synchronized(this) {
            implWrite(str);
        }
    }

    private void writeln(String str) {
        synchronized(this) {
            implWrite(str);
            implNewLine();
        }
    }

    public final void print(String str) {
        write(str);
    }

    public final void print(boolean b) {
        write(String.valueOf(b));
    }

    public final void print(byte b) {
        write(String.valueOf(b));
    }

    public final void print(short s) {
        write(String.valueOf(s));
    }

    public final void print(char[] c) {
        write(String.valueOf(c));
    }

    public final void print(int i) {
        write(String.valueOf(i));
    }

    public final void print(long l) {
        write(String.valueOf(l));
    }

    public final void print(float f) {
        write(String.valueOf(f));
    }

    public final void print(double d) {
        write(String.valueOf(d));
    }

    public final void print(Object obj) {
        write(String.valueOf(obj));
    }

    public final void println() {
        newLine();
    }

    public final void println(String str) {
        writeln(String.valueOf(str));
    }

    public final void println(boolean b) {
        writeln(String.valueOf(b));
    }

    public final void println(byte b) {
        writeln(String.valueOf(b));
    }

    public final void println(short s) {
        writeln(String.valueOf(s));
    }

    public final void println(char[] c) {
        writeln(String.valueOf(c));
    }

    public final void println(int i) {
        writeln(String.valueOf(i));
    }

    public final void println(long l) {
        writeln(String.valueOf(l));
    }

    public final void println(float f) {
        writeln(String.valueOf(f));
    }

    public final void println(double d) {
        writeln(String.valueOf(d));
    }

    public final void println(Object obj) {
        writeln(String.valueOf(obj));
    }

    public PrintStream append(CharSequence csq) {
        print(String.valueOf(csq));
        return this;
    }

    public PrintStream append(CharSequence csq, int start, int end) {
        if(csq == null)
            csq = "null";
        return append(csq.subSequence(start, end));
    }

    public PrintStream append(char c) {
        print(c);
        return this;
    }
}
