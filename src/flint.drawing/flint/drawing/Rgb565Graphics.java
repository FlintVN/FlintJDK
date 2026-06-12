package flint.drawing;

public class Rgb565Graphics extends Graphics {
    private static GraphicsFactory factory;

    private byte[] data;

    private Rgb565Graphics(int width, int height, byte[] buff) {
        super(width, height);
        if((width * height * 2) > buff.length)
            throw new IllegalArgumentException("The buffer is not large enough to create graphics of the specified size");
        this.data = buff;
    }

    public static synchronized GraphicsFactory getGraphicsFactory() {
        if(factory == null)
            factory = new Factory();
        return factory;
    }

    @Override
    public native void clear();

    @Override
    public native void clear(Color c);

    @Override
    public native void drawLine(Color color, int thickness, int x1, int y1, int x2, int y2);

    @Override
    public native void drawRect(Color color, int thickness, int x, int y, int w, int h);

    @Override
    public native void fillRect(Color c, int x, int y, int w, int h);

    @Override
    public native void drawRoundRect(Color color, int thickness, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    @Override
    public native void fillRoundRect(Color c, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    @Override
    public native void drawEllipse(Color color, int thickness, int x, int y, int w, int h);

    @Override
    public native void fillEllipse(Color c, int x, int y, int w, int h);

    @Override
    public native void drawArc(Color color, int thickness, int x, int y, int w, int h, float startAngle, float sweepAngle);

    @Override
    public native void fillArc(Color c, int x, int y, int w, int h, float startAngle, float sweepAngle);

    @Override
    public native void drawPolygon(Color color, int thickness, Point[] points);

    @Override
    public native void fillPolygon(Color c, Point[] points);

    @Override
    public native void drawString(String str, Font font, Color c, int x, int y);

    @Override
    public native void drawImage(Image img, int x, int y);

    @Override
    public native void drawImage(Image img, int x, int y, int w, int h);

    private static class Factory implements GraphicsFactory {
        Factory() {

        }

        @Override
        public Graphics createGraphic(int width, int height, byte[] buff) {
            return new Rgb565Graphics(width, height, buff);
        }
    }
}
