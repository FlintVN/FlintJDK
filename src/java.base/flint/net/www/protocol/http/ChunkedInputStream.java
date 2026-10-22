package flint.net.www.protocol.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.EOFException;
import java.io.FilterInputStream;

class ChunkedInputStream extends FilterInputStream {
    private int chunkRemaining = 0;
    private boolean bof = true;
    private boolean eof = false;
    private boolean closed = false;

    ChunkedInputStream(InputStream in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        byte[] b = new byte[1];
        int n = read(b, 0, 1);
        return (n == -1) ? -1 : (b[0] & 0xff);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (closed)
            throw new IOException("Stream closed");
        if (off < 0 || len < 0 || off > b.length - len)
            throw new IndexOutOfBoundsException();
        if (len == 0)
            return 0;
        if (eof)
            return -1;

        if (chunkRemaining == 0) {
            nextChunk();
            if (eof)
                return -1;
        }

        int toRead = Math.min(len, chunkRemaining);
        int n = in.read(b, off, toRead);
        if (n == -1) {
            eof = true;
            throw new EOFException("Unexpected EOF in chunked stream");
        }
        chunkRemaining -= n;
        return n;
    }

    private void nextChunk() throws IOException {
        if (!bof)
            readCRLF();
        bof = false;
        String line = readLine();
        if (line == null)
            throw new EOFException("Unexpected EOF reading chunk size");

        int semi = line.indexOf(';');
        if (semi >= 0)
            line = line.substring(0, semi);
        line = line.trim();

        try {
            chunkRemaining = Integer.parseInt(line, 16);
        }
        catch (NumberFormatException e) {
            throw new IOException("Invalid chunk size: " + line);
        }

        if (chunkRemaining == 0) {
            eof = true;
            while (true) {
                String trailer = readLine();
                if (trailer == null || trailer.isEmpty())
                    break;
            }
        }
    }

    private void readCRLF() throws IOException {
        int cr = in.read();
        int lf = in.read();
        if (cr != '\r' || lf != '\n')
            throw new IOException("Expected CRLF at end of chunk");
    }

    private String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int c;
        while ((c = in.read()) != -1) {
            if (c == '\r') {
                int next = in.read();
                if (next == '\n')
                    break;
                sb.append((char) c);
                if (next == -1)
                    break;
                sb.append((char) next);
            }
            else if (c == '\n')
                break;
            else
                sb.append((char) c);
        }
        if (c == -1 && sb.length() == 0)
            return null;
        return sb.toString();
    }

    @Override
    public void close() throws IOException {
        if (!closed) {
            closed = true;
            in.close();
        }
    }
}
