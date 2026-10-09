package flint.drawing;

import java.io.InputStream;

public final class ImageDecoder {
    private ImageDecoder() {

    }

    public static Rgb565Image decodeToRgb565(byte[] imageData, int off, int len) {
        if (imageData == null)
            throw new NullPointerException("imageData cannot be null");
        if (off < 0 || len > imageData.length - off)
            throw new IndexOutOfBoundsException();

        int c1 = imageData[off + 0];
        int c2 = imageData[off + 1];
        int c3 = imageData[off + 2];
        int c4 = imageData[off + 3];
        int c5 = imageData[off + 4];
        int c6 = imageData[off + 5];
        int c7 = imageData[off + 6];
        int c8 = imageData[off + 7];

        if (c1 == '#' && c2 == 'd' && c3 == 'e' && c4 == 'f')
            return decodeBmpToRgb565(imageData, off, len);
        if (c1 == -119 && c2 == 80 && c3 == 78 && c4 == 71 && c5 == 13 && c6 == 10 && c7 == 26 && c8 == 10)
            return decodePngToRgb565(imageData, off, len);
        else
            throw new UnsupportedOperationException("Image format not supported");
    }

    private static native Rgb565Image decodeBmpToRgb565(byte[] imageData, int off, int len);

    private static native Rgb565Image decodePngToRgb565(byte[] imageData, int off, int len);
}
