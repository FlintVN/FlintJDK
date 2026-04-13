package flint.drawing;

public abstract class Graphics {
    static GraphicsFactory factory;

    protected final int width;
    protected final int height;

    protected int clipX;
    protected int clipY;
    protected int clipWidth;
    protected int clipHeight;

    @SuppressWarnings("this-escape")
    protected Graphics(int width, int height) {
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
            factory = Argb565Graphics.getGraphicsFactory();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void resetClip() {
        setClip(0, 0, width, height);
    }

    public void setClip(Rectangle clip) {
        setClip(clip.x, clip.y, clip.width, clip.height, ClipMode.REPLACE);
    }

    public void setClip(Rectangle clip, ClipMode mode) {
        setClip(clip.x, clip.y, clip.width, clip.height, mode);
    }

    public void setClip(int x, int y, int w, int h) {
        setClip(x, y, w, h, ClipMode.REPLACE);
    }

    public void setClip(int x, int y, int w, int h, ClipMode mode) {
        if(width < 0 || height < 0)
            throw new IllegalArgumentException("Width and height cannot be negative");
        if(mode.value == ClipMode.REPLACE.value) {
            int xend = x + w;
            int yend = y + h;
            clipX = x > 0 ? x : 0;
            clipY = y > 0 ? y : 0;
            clipWidth = (xend < width ? xend : width) - clipX;
            clipHeight = (yend < height ? yend : height) - clipY;
        }
        else {
            int xend1 = clipX + clipWidth;
            int yend1 = clipY + clipHeight;
            int xend2 = x + w;
            int yend2 = y + h;
            clipX = x > clipX ? x : clipX;
            clipY = y > clipY ? y : clipY;
            clipWidth = ((xend2 < xend1) ? xend2 : xend1) - clipX;
            clipHeight = ((yend2 < yend1) ? yend2 : yend1) - clipY;
        }
    }

    public void drawLine(Pen pen, Rectangle rect) {
        drawLine(pen, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void drawLine(Pen pen, int x1, int y1, int x2, int y2);

    public void drawRect(Pen pen, Rectangle rect) {
        drawRect(pen, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void drawRect(Pen pen, int x, int y, int w, int h);

    public void fillRect(Color c, Rectangle rect) {
        fillRect(c, rect.x, rect.y, rect.width, rect.height);
    }

    public abstract void fillRect(Color c, int x, int y, int w, int h);

    public void drawRoundRect(Pen pen, Rectangle rect, int r) {
        drawRoundRect(pen, rect.x, rect.y, rect.width, rect.height, r, r, r, r);
    }

    public void drawRoundRect(Pen pen, Rectangle rect, int r1, int r2, int r3, int r4) {
        drawRoundRect(pen, rect.x, rect.y, rect.width, rect.height, r1, r2, r3, r4);
    }

    public void drawRoundRect(Pen pen, int x, int y, int w, int h, int r) {
        drawRoundRect(pen, x, y, w, h, r, r, r, r);
    }

    public abstract void drawRoundRect(Pen pen, int x, int y, int w, int h, int r1, int r2, int r3, int r4);

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

    public void drawEllipse(Pen pen, Rectangle rect) {
        drawEllipse(pen, rect);
    }

    public abstract void drawEllipse(Pen pen, int x, int y, int w, int h);

    public void fillEllipse(Color c, Rectangle rect) {
        fillEllipse(c, rect);
    }

    public abstract void fillEllipse(Color c, int x, int y, int w, int h);

    public void drawArc(Pen pen, Rectangle rect, float startAngle, float sweepAngle) {
        drawArc(pen, rect.x, rect.y, rect.width, rect.height, startAngle, sweepAngle);
    }

    public abstract void drawArc(Pen pen, int x, int y, int w, int h, float startAngle, float sweepAngle);

    public void fillArc(Color c, Rectangle rect, float startAngle, float sweepAngle) {
        fillArc(c, rect.x, rect.y, rect.width, rect.height, startAngle, sweepAngle);
    }

    public abstract void fillArc(Color c, int x, int y, int w, int h, float startAngle, float sweepAngle);

    public abstract void drawPolygon(Pen pen, Point[] points);

    public abstract void fillPolygon(Color c, Point[] points);

    public void drawString(String str, Font font, Color c, Point p) {
        drawString(str, font, c, p.x, p.y);
    }

    public abstract void drawString(String str, Font font, Color c, int x, int y);

    public void drawImage(Image img, Point p) {
        drawImage(img, p.x, p.y, img.width, img.height);
    }

    public void drawImage(Image img, int x, int y) {
        drawImage(img, x, y, img.width, img.height);
    }

    public abstract void drawImage(Image img, int x, int y, int w, int h);
}
