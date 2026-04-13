package flint.drawing;

public class Argb565Graphics extends Graphics {
    private static GraphicsFactory factory;

    private byte[] data;

    private Argb565Graphics(int width, int height, byte[] buff) {
        if((width * height * 2) > buff.length)
            throw new IllegalArgumentException("The buffer is not large enough to create graphics of the specified size");
        super(width, height);
        this.data = buff;
    }

    public static synchronized GraphicsFactory getGraphicsFactory() {
        if(factory == null)
            factory = new Factory();
        return factory;
    }

    @Override
    public native void drawLine(Pen pen, int x1, int y1, int x2, int y2);

    @Override
    public native void drawRect(Pen pen, int x, int y, int w, int h);

    @Override
    public native void fillRect(Color c, int x, int y, int w, int h);

    @Override
    public native void drawRoundRect(Pen pen, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    @Override
    public native void fillRoundRect(Color c, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    @Override
    public native void drawEllipse(Pen pen, int x, int y, int w, int h);

    @Override
    public native void fillEllipse(Color c, int x, int y, int w, int h);

    @Override
    public native void drawArc(Pen pen, int x, int y, int w, int h, float startAngle, float sweepAngle);

    @Override
    public native void fillArc(Color c, int x, int y, int w, int h, float startAngle, float sweepAngle);

    @Override
    public native void drawPolygon(Pen pen, Point[] points);

    @Override
    public native void fillPolygon(Color c, Point[] points);

    @Override
    public native void drawString(String str, Font font, Color c, int x, int y);

    @Override
    public native void drawImage(Image img, int x, int y, int w, int h);

    private static class Factory implements GraphicsFactory {
        Factory() {

        }

        @Override
        public Graphics createGraphic(int width, int height, byte[] buff) {
            return new Argb565Graphics(width, height, buff);
        }

        @Override
        public Color createColor(int a, int r, int g, int b) {
            return new Argb565(a, r, g, b);
        }
    }

    private static class Argb565 extends Color {
        private int value;

        Argb565(int a, int r, int g, int b) {
            if(a < 0 || a > 1)
                throw new IllegalArgumentException("The alpha value is within the range of 0 to 1");
            int tmp = (r << 8) & 0xF800;
            tmp |= (g << 3) & 0x07E0;
            tmp |= (b >>> 3) & 0x1F;
            tmp = (tmp >>> 8) | ((tmp & 0xFF) << 8);
            tmp |= a << 24;
            value = tmp;
        }

        @Override
        public int getValue() {
            return value;
        }

        @Override
        public int getRed() {
            return (value >> 8) & 0x1F;
        }

        @Override
        public int getGreen() {
            return ((value >>> 13) & 0x07) | ((value << 3) & 0x38);
        }

        @Override
        public int getBlue() {
            return (value >>> 3) & 0x1F;
        }

        @Override
        public float getAlpha() {
            return (float)(value >>> 24) / 255;
        }
    }
}
