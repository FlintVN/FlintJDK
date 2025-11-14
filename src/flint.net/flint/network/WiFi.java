package flint.network;

public class WiFi {
    private WiFi() {

    }

    public static native boolean isSupported();

    private static native void connect(String ssid, String password, int authMode);
    public static native boolean isConnected();
    public static native AccessPointRecord getAPinfo();
    public static native void disconnect();

    private static native void softAP(String ssid, String password, int authMode, int channel, int maxConnection);
    public static native void softAPdisconnect();

    public static native void startScan(boolean blocked);
    public static native AccessPointRecord[] getScanResults();
    public static native void stopScan();

    private static int getAuthModeValue(WiFiAuthMode authMode) {
        return switch(authMode) {
            case OPEN -> 0;
            case WEP -> 1;
            case WPA_PSK -> 2;
            case WPA2_PSK -> 3;
            case WPA_WPA2_PSK -> 4;
            case ENTERPRISE -> 5;
            case WPA2_ENTERPRISE -> 6;
            case WPA3_PSK -> 7;
            case WPA2_WPA3_PSK -> 8;
            case WAPI_PSK -> 9;
            case OWE -> 10;
            case WPA3_ENT_192 -> 11;
            case WPA3_EXT_PSK -> 12;
            case WPA3_EXT_PSK_MIXED_MODE -> 13;
            default -> 14;
        };
    }

    public static void connect(String ssid) {
        WiFi.connect(ssid, null, getAuthModeValue(WiFiAuthMode.OPEN));
    }

    public static void connect(String ssid, String password) {
        WiFi.connect(ssid, password, getAuthModeValue(WiFiAuthMode.WPA2_PSK));
    }

    public static void connect(String ssid, String password, WiFiAuthMode authMode) {
        WiFi.connect(ssid, password, getAuthModeValue(authMode));
    }

    public static void softAP(String ssid) {
        WiFi.softAP(ssid, null, getAuthModeValue(WiFiAuthMode.OPEN), 1, 5);
    }

    public static void softAP(String ssid, String password) {
        WiFi.softAP(ssid, password, getAuthModeValue(WiFiAuthMode.WPA2_PSK), 1, 5);
    }

    public static void softAP(String ssid, String password, WiFiAuthMode authMode) {
        WiFi.softAP(ssid, password, getAuthModeValue(authMode), 0, 5);
    }

    public static synchronized AccessPointRecord[] scanNetworks() {
        WiFi.startScan(true);
        return WiFi.getScanResults();
    }
}
