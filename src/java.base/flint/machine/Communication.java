package flint.machine;

public interface Communication {
    void write(byte[] buffer, int offset, int count);

    int bytesToRead();

    int read(byte[] buffer, int offset, int count);
}
