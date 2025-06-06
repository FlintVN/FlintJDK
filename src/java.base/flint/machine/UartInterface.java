package flint.machine;

public interface UartInterface extends CommInterface {
    int getBaudrate();
    void setBaudrate(int baudrate);
}
