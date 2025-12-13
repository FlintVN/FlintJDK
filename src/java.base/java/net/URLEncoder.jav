package java.net;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class URLEncoder {
    private static byte[] DONT_NEED_ENCODING;
    private static final int CASE_DIFF = ('a' - 'A');

    static {
        DONT_NEED_ENCODING = new byte[128 / 8];
        int i;
        for(i = 'a'; i <= 'z'; i++)
            dontNeedEncodingSet(i);
        for(i = 'A'; i <= 'Z'; i++)
            dontNeedEncodingSet(i);
        for(i = '0'; i <= '9'; i++)
            dontNeedEncodingSet(i);
        dontNeedEncodingSet(' '); /* encoding a space to a + is done in the encode() method */
        dontNeedEncodingSet('-');
        dontNeedEncodingSet('_');
        dontNeedEncodingSet('.');
        dontNeedEncodingSet('*');
    }

    private URLEncoder() {

    }

    private static void dontNeedEncodingSet(int c) {
        DONT_NEED_ENCODING[c / 8] |= (byte)(1 << (c % 8));
    }

    private static boolean isDontNeedEncoding(int c) {
        if(c < 0 || c >= DONT_NEED_ENCODING.length)
            return false;
        return (DONT_NEED_ENCODING[c / 8] & (1 << (c % 8))) != 0;
    }

    public static String encode(String s) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(s.length());

        for(int i = 0; i < s.length(); i++) {
            int c = (int)s.charAt(i);
            if(isDontNeedEncoding(c)) {
                if(c == ' ')
                    c = '+';
                out.write(c);
            }
            else {
                out.write('%');
                out.write(Character.forDigit((c >> 4) & 0xF, 16));
                out.write(Character.forDigit(c & 0xF, 16));
            }
        }

        return out.toString();
    }
}
