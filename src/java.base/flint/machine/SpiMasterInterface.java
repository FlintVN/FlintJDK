package flint.machine;

import java.io.IOException;

public interface SpiMasterInterface extends CommInterface {
    SpiDataMode getDataMode();
    void setDataMode(SpiDataMode dataMode);

    int readWrite(byte[] tx, byte[] rx, int count) throws IOException;
    int readWrite(byte[] tx, int txOff, byte[] rx, int rxOff, int count) throws IOException;
}
