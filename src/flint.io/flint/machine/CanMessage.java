package flint.machine;

import java.util.Arrays;

public final class CanMessage {
    public int id; // 11-bit or 29-bit
    public boolean extended; // IDE
    public boolean rtr; // RTR
    public int dlc; // 0..8
    public byte[] data;

    public CanMessage() {
    }

    public CanMessage(int id, byte[] payload) {
        this.id = id;
        this.extended = id > 0x7FF;
        this.rtr = false;
        this.dlc = payload == null ? 0 : Math.min(payload.length, 8);
        this.data = payload == null
                ? new byte[0]
                : Arrays.copyOf(payload, this.dlc);
    }

    public static CanMessage of(int id, byte... payload) {
        return new CanMessage(id, payload);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: 0x").append(Integer.toHexString(id).toUpperCase());
        if (extended)
            sb.append(" (EXT)");
        if (rtr)
            sb.append(" [RTR]");
        sb.append(" DLC: ").append(dlc);

        if (!rtr && data != null && data.length > 0) {
            sb.append(" Data: ");
            for (byte b : data) {
                int val = b & 0xFF;
                if (val < 16)
                    sb.append('0');
                sb.append(Integer.toHexString(val).toUpperCase()).append(' ');
            }
        }

        return sb.toString().trim();
    }
}