package flint.machine;

public abstract class Communication {
    public Communication() {

    }

    public void write(byte[] buffer) {
        write(buffer, 0, buffer.length);
    }

    public abstract void write(byte[] buffer, int offset, int count);

    public abstract int bytesToRead();

    public int read(byte[] buffer) {
        return read(buffer, 0, buffer.length);
    }

    public abstract int read(byte[] buffer, int offset, int count);
}
