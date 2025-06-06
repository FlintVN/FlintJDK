package flint.machine;

public interface I2cMasterInterface extends CommInterface {
    int getSlaveAddress();
    void setSlaveAddress(int address);

    int readMemory(int memAddr, byte[] buffer, int count);
    void writeMemory(int memAddr, byte[] buffer, int count);
}
