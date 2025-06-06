package flint.machine;

public interface CommInterface {
    void open();
    void close();

    CommStatus getStatus();

    int read(byte[] buffer);
    int read(byte[] buffer, int offset, int count);

    int readAsync(byte[] buffer);
    int readAsync(byte[] buffer, int offset, int count);

    void write(byte[] buffer);
    void write(byte[] buffer, int offset, int count);

    void writeAsync(byte[] buffer);
    void writeAsync(byte[] buffer, int offset, int count);
}
