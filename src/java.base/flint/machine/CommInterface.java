package flint.machine;

public interface CommInterface {
    void open();
    void close();

    boolean isOpen();

    int read(byte[] buffer);
    int read(byte[] buffer, int offset, int count);

    void write(byte[] buffer);
    void write(byte[] buffer, int offset, int count);
}
