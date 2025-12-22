package flint.net;

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

    public static void connect(String ssid) {
        WiFi.connect(ssid, null, WiFiAuthMode.OPEN.value);
    }

    public static void connect(String ssid, String password) {
        WiFi.connect(ssid, password, WiFiAuthMode.WPA2_PSK.value);
    }

    public static void connect(String ssid, String password, WiFiAuthMode authMode) {
        WiFi.connect(ssid, password, authMode.value);
    }

    public static void softAP(String ssid) {
        WiFi.softAP(ssid, null, WiFiAuthMode.OPEN.value, 1, 5);
    }

    public static void softAP(String ssid, String password) {
        WiFi.softAP(ssid, password, WiFiAuthMode.WPA2_PSK.value, 1, 5);
    }

    public static void softAP(String ssid, String password, WiFiAuthMode authMode) {
        WiFi.softAP(ssid, password, authMode.value, 0, 5);
    }

    public static synchronized AccessPointRecord[] scanNetworks() {
        WiFi.startScan(true);
        return WiFi.getScanResults();
    }
}
