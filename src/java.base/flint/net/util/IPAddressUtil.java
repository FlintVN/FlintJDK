package flint.net.util;

public class IPAddressUtil {
    private IPAddressUtil() {

    }

    public static boolean isIPv6LiteralAddress(String src) {
        if (src == null)
            return false;
        int n = src.length();
        if (n < 2)
            return false;
 
        int i = 0;
        int groups = 0;
        boolean compressed = false;
 
        if (src.charAt(0) == ':') {
            if (src.charAt(1) != ':')
                return false;
            compressed = true;
            i = 2;
            if (i == n)
                return true;
        }
 
        while (i < n) {
            int end = src.indexOf(':', i);
            boolean last = (end < 0);
            if (last) end = n;
 
            if (end == i)
                return false;
 
            if (last && src.indexOf('.', i) >= 0) {
                if (!isIPv4(src, i, end))
                    return false;
                groups += 2;
            }
            else {
                if (!isHexGroup(src, i, end))
                    return false;
                groups++;
            }
            if (groups > 8)
                return false;
            if (last)
                break;
 
            i = end + 1;
            if (i == n)
                return false;
            if (src.charAt(i) == ':') {
                if (compressed)
                    return false;
                compressed = true;
                i++;
                if (i == n)
                    break;
            }
        }
 
        return compressed ? groups <= 7 : groups == 8;
    }
 
    private static boolean isHexGroup(String s, int from, int to) {
        int len = to - from;
        if (len < 1 || len > 4)
            return false;
        for (int k = from; k < to; k++) {
            if (Character.digit(s.charAt(k), 16) < 0)
                return false;
        }
        return true;
    }
 
    private static boolean isIPv4(String s, int from, int to) {
        int parts = 0;
        int k = from;
        while (k <= to) {
            int dot = s.indexOf('.', k);
            int partEnd = (dot < 0 || dot > to) ? to : dot;
            int len = partEnd - k;
            if (len < 1 || len > 3)
                return false;
            int v = 0;
            for (int m = k; m < partEnd; m++) {
                char c = s.charAt(m);
                if (c < '0' || c > '9')
                    return false;
                v = v * 10 + (c - '0');
            }
            if (len > 1 && s.charAt(k) == '0')
                return false;
            if (v > 255)
                return false;
            parts++;
            if (partEnd == to)
                break;
            k = partEnd + 1;
            if (k == to)
                return false;
        }
        return parts == 4;
    }
}
