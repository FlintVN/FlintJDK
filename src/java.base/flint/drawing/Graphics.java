package flint.drawing;

public class Graphics {
    private final int colorMode;
    private final byte[] colorBuffer;
    private int x;
    private int y;
    private int width;
    private int clipX;
    private int clipY;
    private int clipWidth;
    private int clipHeight;

    private static final int COLOR_MODE_RGB555 = 0;
    private static final int COLOR_MODE_RGB565 = 1;
    private static final int COLOR_MODE_BGR565 = 2;
    private static final int COLOR_MODE_RGB888 = 3;

    public Graphics(int x, int y, int width, int height) {
        this(x, y, width, height, ColorMode.BGR565);
    }

    public Graphics(int x, int y, int width, int height, ColorMode colorMode) {
        this.colorMode = switch(colorMode) {
            case RGB555 -> COLOR_MODE_RGB555;
            case RGB565 -> COLOR_MODE_RGB565;
            case BGR565 -> COLOR_MODE_BGR565;
            case RGB888 -> COLOR_MODE_RGB888;
        };
        this.colorBuffer = new byte[width * height * getPixelSize()];
        this.x = x;
        this.y = y;
        this.width = width;
        this.clipX = x;
        this.clipY = y;
        this.clipWidth = width;
        this.clipHeight = height;
    }

    private int getPixelSize() {
        return (this.colorMode == COLOR_MODE_RGB888) ? 3 : 2;
    }

    public byte[] getColorBuffer() {
        return this.colorBuffer;
    }

    public void setOrigin(int x, int y) {
        this.clipX += x - this.x;
        this.clipY += y - this.x;
        this.x = x;
        this.y = y;
    }

    public void translate(int x, int y) {
        this.x += x;
        this.y += y;
        this.clipX += x;
        this.clipY += y;
    }

    public void clipRect(int x, int y, int width, int height) {
        int xEnd1 = this.clipWidth + this.clipX;
        int yEnd1 = this.clipHeight + this.clipY;
        int xEnd2 = width + x;
        int yEnd2 = height + y;
        this.clipX = x > this.clipX ? x : this.clipX;
        this.clipY = y > this.clipY ? y : this.clipY;
        this.clipWidth = ((xEnd2 < xEnd1) ? xEnd2 : xEnd1) - this.clipX;
        this.clipHeight = ((yEnd2 < yEnd1) ? yEnd2 : yEnd1) - this.clipY;
    }

    public void setClip(int x, int y, int width, int height) {
        int xEnd1 = this.width + this.x;
        int yEnd1 = colorBuffer.length / this.width / getPixelSize() + this.y;
        int xEnd2 = width + x;
        int yEnd2 = height + y;
        this.clipX = x > this.x ? x : this.x;
        this.clipY = y > this.y ? y : this.y;
        this.clipWidth = ((xEnd2 < xEnd1) ? xEnd2 : xEnd1) - this.clipX;
        this.clipHeight = ((yEnd2 < yEnd1) ? yEnd2 : yEnd1) - this.clipY;
    }

    public native void clear();

    public native void drawLine(Color color, int x1, int y1, int x2, int y2);

    public void drawLine(Color color, Point p1, Point p2) {
        drawLine(color, p1.x, p1.y, p2.x, p2.y);
    }

    public native void drawRect(Color color, int x, int y, int width, int height);

    public void drawRect(Color color, Rectangle rect) {
        drawRect(color, rect.x, rect.y, rect.width, rect.height);
    }

    public native void fillRect(Color color, int x, int y, int width, int height);

    public void fillRect(Color color, Rectangle rect) {
        fillRect(color, rect.x, rect.y, rect.width, rect.height);
    }

    public native void drawRoundRect(Color color, int x, int y, int width, int height, int r1, int r2, int r3, int r4);

    public void drawRoundRect(Color color, int x, int y, int width, int height, int radius) {
        drawRoundRect(color, x, y, width, height, radius, radius, radius, radius);
    }

    public native void fillRoundRect(Color color, int x, int y, int width, int height, int r1, int r2, int r3, int r4);

    public void fillRoundRect(Color color, int x, int y, int width, int height, int radius) {
        fillRoundRect(color, x, y, width, height, radius, radius, radius, radius);
    }

    public native void drawEllipse(Color color, int x, int y, int width, int height);

    public void drawEllipse(Color color, Rectangle rect) {
        drawEllipse(color, rect.x, rect.y, rect.width, rect.height);
    }

    public native void fillEllipse(Color color, int x, int y, int width, int height);

    public void fillEllipse(Color color, Rectangle rect) {
        fillEllipse(color, rect.x, rect.y, rect.width, rect.height);
    }

    public native void drawArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle);

    public void drawArc(Color color, Rectangle rect, int startAngle, int arcAngle) {
        drawArc(color, rect.x, rect.y, rect.width, rect.height, startAngle, arcAngle);
    }

    public native void fillArc(Color color, int x, int y, int width, int height, int startAngle, int arcAngle);

    public void fillArc(Color color, Rectangle rect, int startAngle, int arcAngle) {
        fillArc(color, rect.x, rect.y, rect.width, rect.height, startAngle, arcAngle);
    }

    public native void drawPolyline(Color color, int[] xPoints, int[] yPoints, int nPoints);

    public native void drawPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints);

    public void drawPolygon(Color color, Polygon polygon) {
        drawPolygon(color, polygon.xpoints, polygon.ypoints, polygon.npoints);
    }

    public native void fillPolygon(Color color, int[] xPoints, int[] yPoints, int nPoints);

    public void fillPolygon(Color color, Polygon p) {
        fillPolygon(color, p.xpoints, p.ypoints, p.npoints);
    }

    public native void drawString(String str, Font font, Color color, int x, int y);

    public native void drawImage(Image img, int x, int y);

    public native void drawImage(Image img, int x, int y, int width, int height);

    public void drawImage(Image img, Point p) {
        drawImage(img, p.x, p.y);
    }

    public void drawImage(Image img, Rectangle rect) {
        drawImage(img, rect.x, rect.y, rect.width, rect.height);
    }
}
