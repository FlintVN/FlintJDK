package flint.net;

public enum WiFiAuthMode {
    OPEN(0),
    WEP(1),
    WPA_PSK(2),
    WPA2_PSK(3),
    WPA_WPA2_PSK(4),
    ENTERPRISE(5),
    WPA2_ENTERPRISE(6),
    WPA3_PSK(7),
    WPA2_WPA3_PSK(8),
    WAPI_PSK(9),
    OWE(10),
    WPA3_ENT_192(11),
    WPA3_EXT_PSK(12),
    WPA3_EXT_PSK_MIXED_MODE(13),
    DPP(14);

    final int value;

    private WiFiAuthMode(int value) {
        this.value = value;
    }

    static WiFiAuthMode fromValue(int value) {
        return switch(value) {
            case 0 -> OPEN;
            case 1 -> WEP;
            case 2 -> WPA_PSK;
            case 3 -> WPA2_PSK;
            case 4 -> WPA_WPA2_PSK;
            case 5 -> ENTERPRISE;
            case 6 -> WPA2_ENTERPRISE;
            case 7 -> WPA3_PSK;
            case 8 -> WPA2_WPA3_PSK;
            case 9 -> WAPI_PSK;
            case 10 -> OWE;
            case 11 -> WPA3_ENT_192;
            case 12 -> WPA3_EXT_PSK;
            case 13 -> WPA3_EXT_PSK_MIXED_MODE;
            default -> DPP;
        };
    }
}
