package java.util.zip;

import java.util.Objects;
import jdk.internal.vm.annotation.IntrinsicCandidate;

public class CRC32 implements Checksum {
    private int crc;

    public CRC32() {

    }

    @Override
    public void update(int b) {
        crc = update(crc, b);
    }

    @Override
    public void update(byte[] b) {
        update(b, 0, b.length);
    }

    @Override
    public void update(byte[] b, int off, int len) {
        if(b == null)
            throw new NullPointerException();
        Objects.checkFromIndexSize(off, len, b.length);
        crc = updateBytes(crc, b, off, len);
    }

    @Override
    public void reset() {
        crc = 0;
    }

    @Override
    public long getValue() {
        return (long)crc & 0xFFFFFFFFL;
    }

    @IntrinsicCandidate
    private static native int update(int crc, int b);

    private static int updateBytes(int crc, byte[] b, int off, int len) {
        if(b == null)
            throw new NullPointerException();
        Objects.checkFromIndexSize(off, len, b.length);
        return updateBytes0(crc, b, off, len);
    }

    @IntrinsicCandidate
    private static native int updateBytes0(int crc, byte[] b, int off, int len);
}
