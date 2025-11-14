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
        if(authModeInEnum == null) {
            synchronized(this) {
                authModeInEnum = switch(authMode) {
                    case 0 -> WiFiAuthMode.OPEN;
                    case 1 -> WiFiAuthMode.WEP;
                    case 2 -> WiFiAuthMode.WPA_PSK;
                    case 3 -> WiFiAuthMode.WPA2_PSK;
                    case 4 -> WiFiAuthMode.WPA_WPA2_PSK;
                    case 5 -> WiFiAuthMode.ENTERPRISE;
                    case 6 -> WiFiAuthMode.WPA2_ENTERPRISE;
                    case 7 -> WiFiAuthMode.WPA3_PSK;
                    case 8 -> WiFiAuthMode.WPA2_WPA3_PSK;
                    case 9 -> WiFiAuthMode.WAPI_PSK;
                    case 10 -> WiFiAuthMode.OWE;
                    case 11 -> WiFiAuthMode.WPA3_ENT_192;
                    case 12 -> WiFiAuthMode.WPA3_EXT_PSK;
                    case 13 -> WiFiAuthMode.WPA3_EXT_PSK_MIXED_MODE;
                    default -> WiFiAuthMode.DPP;
                };
            }
        }
        return authModeInEnum;
    }
}
