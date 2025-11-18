package flint.network;

public class AccessPointRecord {
    private final byte[] mac;
    private final String ssid;
    private final byte rssi;
    private final byte authMode;
    private WiFiAuthMode authModeInEnum = null;

    public AccessPointRecord(byte[] mac, String ssid, byte rssi, byte authMode) {
        this.mac = mac;
        this.ssid = ssid;
        this.rssi = rssi;
        this.authMode = authMode;
    }

    public byte[] getMac() {
        return mac.clone();
    }

    public String getSsid() {
        return ssid;
    }

    public byte getRssi() {
        return rssi;
    }

    public WiFiAuthMode getAuthMode() {
        if(authModeInEnum == null)
            authModeInEnum = WiFiAuthMode.fromValue(authMode);
        return authModeInEnum;
    }
}
