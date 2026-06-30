package flint.drawing;

public abstract class Graphics {
    static GraphicsFactory factory;

    protected int x;
    protected int y;

    protected final int width;
    protected final int height;

    protected int clipX;
    protected int clipY;
    protected int clipWidth;
    protected int clipHeight;

    @SuppressWarnings("this-escape")
    protected Graphics(int width, int height) {
        this.x = 0;
        this.y = 0;
        this.width = width;
        this.height = height;
        setClip(0, 0, width, height);
    }

    public static synchronized void setGraphicsFactory(GraphicsFactory factory) {
        if(Graphics.factory != null)
            throw new IllegalStateException("Graphics factory has been previously configured and cannot be reconfigured");
        Graphics.factory = factory;
    }

    public static Graphics create(int width, int height, byte[] buff) {
        ensureFactoryNotNull();
        return factory.createGraphic(width, height, buff);
    }

    public static Graphics create(Image img) {
        ensureFactoryNotNull();
        return factory.createGraphic(img.width, img.height, img.data);
    }

    static GraphicsFactory getGraphicsFactory() {
        ensureFactoryNotNull();
        return factory;
    }

    private static synchronized void ensureFactoryNotNull() {
        if(factory == null)
            factory = Rgb565Graphics.getGraphicsFactory();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getClipX() {
        return clipX;
    }

    public int getClipY() {
        return clipY;
    }

    public int getClipWidth() {
        return clipWidth;
    }

    public int getClipHeight() {
        return clipHeight;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public native boolean isVisible(int x, int y, int w, int h);

    public void setTransform(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void translate(int x, int y) {
        this.x += x;
        this.y += y;
    }

    public void resetClip() {
        setClip0(0, 0, width, height, ClipMode.REPLACE.value);
    }

    public void setClip(Rectangle clip) {
        setClip0(clip.x, clip.y, clip.width, clip.height, ClipMode.REPLACE.value);
    }

    public void setClip(Rectangle clip, ClipMode mode) {
        setClip0(clip.x, clip.y, clip.width, clip.height, mode.value);
    }

    public void setClip(int x, int y, int w, int h) {
        setClip0(x, y, w, h, ClipMode.REPLACE.value);
    }

    public void setClip(int x, int y, int w, int h, ClipMode mode) {
        setClip0(x, y, w, h, mode.value);
    }

    private native void setClip0(int x, int y, int w, int h, int mode);

    public abstract void clear();

    public abstract void clear(Color c);

    public void drawLine(Color color, int thickness, Rectangle rect) {
        drawLine(color, thickness, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void drawLine(Color color, int thickness, int x1, int y1, int x2, int y2);

    public void drawRect(Color color, int thickness, Rectangle rect) {
        drawRect(color, thickness, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void drawRect(Color color, int thickness, int x, int y, int w, int h);

    public void fillRect(Color c, Rectangle rect) {
        fillRect(c, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void fillRect(Color c, int x, int y, int w, int h);

    public void drawRoundRect(Color color, int thickness, Rectangle rect, int r) {
        drawRoundRect(color, thickness, rect.x, rect.y, rect.width, rect.height, r, r, r, r);
    }

    public void drawRoundRect(Color color, int thickness, Rectangle rect, int r1, int r2, int r3, int r4) {
        drawRoundRect(color, thickness, rect.x, rect.y, rect.width, rect.height, r1, r2, r3, r4);
    }

    public void drawRoundRect(Color color, int thickness, int x, int y, int w, int h, int r) {
        drawRoundRect(color, thickness, x, y, w, h, r, r, r, r);
    }

    public abstract void drawRoundRect(Color color, int thickness, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    public void fillRoundRect(Color c, Rectangle rect, int r) {
        fillRoundRect(c, rect.x, rect.y, rect.width, rect.height, r, r, r, r);
    }

    public void fillRoundRect(Color c, Rectangle rect, int r1, int r2, int r3, int r4) {
        fillRoundRect(c, rect.x, rect.y, rect.width, rect.height, r1, r2, r3, r4);
    }

    public void fillRoundRect(Color c, int x, int y, int w, int h, int r) {
        fillRoundRect(c, x, y, w, h, r, r, r, r);
    }

    public abstract void fillRoundRect(Color c, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

    public void drawEllipse(Color color, int thickness, Rectangle rect) {
        drawEllipse(color, thickness, rect);
    }

    public abstract void drawEllipse(Color color, int thickness, int x, int y, int w, int h);

    public void fillEllipse(Color c, Rectangle rect) {
        fillEllipse(c, rect);
    }

    public abstract void fillEllipse(Color c, int x, int y, int w, int h);

    public void drawArc(Color color, int thickness, Rectangle rect, float startAngle, float sweepAngle) {
        drawArc(color, thickness, rect.x, rect.y, rect.width, rect.height, startAngle, sweepAngle);
    }

    public abstract void drawArc(Color color, int thickness, int x, int y, int w, int h, float startAngle, float sweepAngle);

    public void fillArc(Color c, Rectangle rect, float startAngle, float sweepAngle) {
        fillArc(c, rect.x, rect.y, rect.width, rect.height, startAngle, sweepAngle);
    }

    public abstract void fillArc(Color c, int x, int y, int w, int h, float startAngle, float sweepAngle);

    public abstract void drawPolygon(Color color, int thickness, Point[] points);

    public abstract void fillPolygon(Color c, Point[] points);

    public void drawString(String str, Font font, Color c, Point p) {
        drawString(str, font, c, p.x, p.y);
    }

    public static native Size measureString(String str, Font font);

    public static native int measureStringWidth(String str, Font font);

    public static native int measureStringHeight(String str, Font font);

    public abstract void drawString(String str, Font font, Color c, int x, int y);

    public void drawImage(Image img, Point p) {
        drawImage(img, p.x, p.y, img.width, img.height);
    }

    public void drawImage(Image img, int x, int y) {
        drawImage(img, x, y, img.width, img.height);
    }

    public abstract void drawImage(Image img, int x, int y, int w, int h);
}
