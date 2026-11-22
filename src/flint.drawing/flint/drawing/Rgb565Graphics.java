package flint.drawing;

import java.io.InputStream;

public class Rgb565Graphics extends Graphics {
    private static GraphicsFactory factory;

    private Rgb565Graphics(int width, int height, byte[] buff) {
        super(width, height, buff);
        if ((width * height * 2) > buff.length)
            throw new IllegalArgumentException("The buffer is not large enough to create graphics of the specified size");
    }

    public static synchronized GraphicsFactory getGraphicsFactory() {
        if (factory == null)
            factory = new Factory();
        return factory;
    }

    @Override
    public native void clear();

    @Override
    public native void clear(int color);

    @Override
    public native void clear(int x, int y, int w, int h);

    @Override
    public native void clear(int color, int x, int y, int w, int h);

    @Override
    public native void drawLine(int color, int x1, int y1, int x2, int y2);

    @Override
    public native void drawRect(int color, int x, int y, int w, int h);

    @Override
    public native void fillRect(int color, int x, int y, int w, int h);

    @Override
    public native void drawRoundRect(int color, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    @Override
    public native void fillRoundRect(int color, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    @Override
    public native void drawEllipse(int color, int x, int y, int w, int h);

    @Override
    public native void fillEllipse(int color, int x, int y, int w, int h);

    @Override
    public native void drawArc(int color, int x, int y, int w, int h, float startAngle, float sweepAngle);

    @Override
    public native void fillArc(int color, int x, int y, int w, int h, float startAngle, float sweepAngle);

    @Override
    public native void drawPolygon(int color, Point[] points);

    @Override
    public native void fillPolygon(int color, Point[] points);

    @Override
    public native void drawString(String str, Font font, int color, int x, int y);

    @Override
    public native void drawImage(Image img, int x, int y);

    @Override
    public native void drawImage(Image img, int x, int y, int w, int h);

    @Override
    public native void drawRgb(int[] rgbData, int offset, int scanlength, int x, int y, int width, int height, boolean processAlpha);

    @Override
    public native void drawRegion(Image src, int sx, int sy, int sw, int sh, int transform, int dx, int dy);

    private static class Factory implements GraphicsFactory {
        Factory() {

        }

        @Override
        public Graphics createGraphic(int width, int height, byte[] buff) {
            return new Rgb565Graphics(width, height, buff);
        }

        @Override
        public Image createImage(int width, int height) {
            return new Rgb565Image(width, height);
        }

        @Override
        public Image createImage(byte[] imageData, int off, int len) {
            return ImageDecoder.decodeToRgb565(imageData, off, len);
        }

        @Override
        public native Image createImage(int[] rgb, int w, int h, boolean processAlpha);

        @Override
        public native Image createImage(Image src, int x, int y, int w, int h, int transform);
    }
}
