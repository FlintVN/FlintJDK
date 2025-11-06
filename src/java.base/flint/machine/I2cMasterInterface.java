package flint.machine;

import java.io.IOException;

public interface I2cMasterInterface extends CommInterface {
    int getSlaveAddress();
    void setSlaveAddress(int addr);

    int readMemory(int memAddr, byte[] b, int count) throws IOException;
    void writeMemory(int memAddr, byte[] b, int count) throws IOException;
}
