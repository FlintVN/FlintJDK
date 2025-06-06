package flint.machine;

public interface SpiMasterInterface extends CommInterface {
    SpiDataMode getDataMode();
    void setDataMode(SpiDataMode dataMode);

    int readWrite(byte[] txBuffer, byte[] rxBuffer, int count);
    int readWrite(byte[] txBuffer, int txOffset, byte[] rxBuffer, int rxOffset, int count);
}
